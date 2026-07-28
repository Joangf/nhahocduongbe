package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.PrescriptionItem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;

@DisplayName("StudentExamReportServiceImpl — Unit Tests")
@ExtendWith(MockitoExtension.class)
class StudentExamReportServiceImplTest {

  @Mock ExamRepository examRepository;
  @Mock PatientRepository patientRepository;
  @InjectMocks StudentExamReportServiceImpl service;

  // ── Helpers ──

  private Patient createPatient() {
    Patient p = new Patient();
    p.setId(1L);
    p.setFullName("Nguyen Van A");
    p.setCode("HS001");
    p.setBirthDate(LocalDate.of(2015, 5, 20));
    p.setGender(1); // Nam
    p.setSchoolClass("5A");

    Organization org = new Organization();
    org.setId(10L);
    org.setName("Trường Tiểu học ABC");
    p.setOrganization(org);

    return p;
  }

  private Exam createFullExam() {
    Exam exam = new Exam();
    exam.setId(1L);
    exam.setPatient(createPatient());
    exam.setDate(LocalDate.of(2026, 3, 15));
    exam.setProfileNumber(1L);
    exam.setPathologyAssessment("Sâu răng nhẹ");
    exam.setTreatmentNote("Cần trám răng số 16");

    // Dentist
    Dentist dentist = new Dentist();
    dentist.setTitle("BS. Nguyễn Văn B");
    exam.setDentist(dentist);

    // TeethRecord with conditions
    TeethRecord teethRecord = new TeethRecord();
    teethRecord.setId(100L);
    Map<Tooth, ToothCondition> conditions = new EnumMap<>(Tooth.class);

    ToothCondition caries = new ToothCondition();
    caries.setProblem(ToothProblem.CARIES);
    caries.setLocations(List.of(ToothSide.CHEW));
    caries.setTreatment(ToothTreatment.ONE_SIDE_FILLING);
    conditions.put(Tooth._16, caries);

    ToothCondition filling = new ToothCondition();
    filling.setProblem(ToothProblem.FILLING_NO_PROBLEM);
    filling.setLocations(List.of(ToothSide.OUTSIDE));
    conditions.put(Tooth._26, filling);

    ToothCondition noProblem = new ToothCondition();
    noProblem.setProblem(ToothProblem.NO_PROBLEM);
    conditions.put(Tooth._11, noProblem);

    ToothCondition lost = new ToothCondition();
    lost.setProblem(ToothProblem.LOST_CARIES);
    conditions.put(Tooth._46, lost);

    teethRecord.setRecord(conditions);
    exam.setTeethRecord(teethRecord);

    // PlaqueRecord
    PlaqueRecord plaque = new PlaqueRecord();
    plaque.setId(200L);
    plaque.set_17_16n(PlaqueCondition.TWO_THIRD);
    plaque.set_11n(PlaqueCondition.ONE_THIRD);
    exam.setPlaqueRecord(plaque);

    // TartarRecord
    TartarRecord tartar = new TartarRecord();
    tartar.setId(300L);
    tartar.set_17_16n(TartarCondition.ONE_THIRD);
    exam.setTartarRecord(tartar);

    // TreatmentRecords
    TreatmentRecord tr = new TreatmentRecord();
    tr.setId(400L);
    tr.setTooth(Tooth._16);
    tr.setService(ToothTreatment.ONE_SIDE_FILLING);
    tr.setDiagnosis("Sâu răng");
    exam.setTreatmentRecords(List.of(tr));

    return exam;
  }

  // ── Error Paths ──

  @Nested
  @DisplayName("generateExamReportPdf() — error paths")
  class ErrorPaths {

    @Test
    @DisplayName("Ném 404 khi exam không tồn tại")
    void shouldThrowWhenExamNotFound() {
      when(examRepository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.generateExamReportPdf(99L))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("Không tìm thấy phiếu khám");
    }

    @Test
    @DisplayName("Ném 404 khi patient null")
    void shouldThrowWhenPatientNull() {
      Exam exam = new Exam();
      exam.setId(1L);
      exam.setPatient(null);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      assertThatThrownBy(() -> service.generateExamReportPdf(1L))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("Không tìm thấy học sinh");
    }
  }

  // ── Null / Empty Edge Cases ──

  @Nested
  @DisplayName("generateExamReportPdf() — null and empty edge cases")
  class NullEmptyEdgeCases {

    @Test
    @DisplayName("Patient có gender=null → PDF hiển thị giới tính rỗng")
    void shouldGeneratePdfWithNullGender() {
      Exam exam = createFullExam();
      exam.getPatient().setGender(null);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
      assertThat(pdf[0]).isEqualTo((byte) 0x25);
    }

    @Test
    @DisplayName("TeethRecord map chứa giá trị condition=null → bỏ qua tooth đó")
    void shouldGeneratePdfWithNullConditionValueInTeethMap() {
      Exam exam = createFullExam();
      Map<Tooth, ToothCondition> conditions = exam.getTeethRecord().getRecord();
      // Add a tooth with null condition value
      conditions.put(Tooth._36, null);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("ToothCondition có problem=null → bỏ qua tooth đó")
    void shouldGeneratePdfWithNullProblem() {
      Exam exam = createFullExam();
      Map<Tooth, ToothCondition> conditions = exam.getTeethRecord().getRecord();
      ToothCondition nullProblem = new ToothCondition();
      nullProblem.setProblem(null);
      nullProblem.setLocations(List.of(ToothSide.CHEW));
      conditions.put(Tooth._36, nullProblem);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("TreatmentRecord có tooth=null → ô răng hiển thị rỗng")
    void shouldGeneratePdfWithNullToothInTreatment() {
      Exam exam = createFullExam();
      TreatmentRecord tr = new TreatmentRecord();
      tr.setId(500L);
      tr.setTooth(null);
      tr.setService(ToothTreatment.ONE_SIDE_FILLING);
      tr.setDiagnosis("Khám định kỳ");
      exam.setTreatmentRecords(List.of(tr));
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("TreatmentRecord có service=null → ô dịch vụ hiển thị rỗng")
    void shouldGeneratePdfWithNullServiceInTreatment() {
      Exam exam = createFullExam();
      TreatmentRecord tr = new TreatmentRecord();
      tr.setId(501L);
      tr.setTooth(Tooth._26);
      tr.setService(null);
      tr.setDiagnosis("Viêm nướu");
      exam.setTreatmentRecords(List.of(tr));
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("TreatmentRecord có prescription=rỗng (không null) → ô thuốc hiển thị rỗng")
    void shouldGeneratePdfWithEmptyPrescriptionList() {
      Exam exam = createFullExam();
      TreatmentRecord tr = new TreatmentRecord();
      tr.setId(502L);
      tr.setTooth(Tooth._16);
      tr.setService(ToothTreatment.ONE_SIDE_FILLING);
      tr.setDiagnosis("Sâu răng");
      tr.setPrescription(List.of()); // non-null but empty
      exam.setTreatmentRecords(List.of(tr));
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("ToothCondition có locations=rỗng (không null) → ô vị trí hiển thị rỗng")
    void shouldGeneratePdfWithEmptyLocationsList() {
      Exam exam = createFullExam();
      Map<Tooth, ToothCondition> conditions = new EnumMap<>(Tooth.class);
      ToothCondition emptyLocations = new ToothCondition();
      emptyLocations.setProblem(ToothProblem.CARIES);
      emptyLocations.setLocations(List.of()); // non-null but empty
      emptyLocations.setTreatment(ToothTreatment.ONE_SIDE_FILLING);
      conditions.put(Tooth._16, emptyLocations);
      exam.getTeethRecord().setRecord(conditions);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("pathologyAssessment và treatmentNote rỗng → nvl fallback 'Trống' được gọi")
    void shouldDisplayTrongForEmptyPathologyAndTreatmentNote() {
      Exam exam = createFullExam();
      exam.setPathologyAssessment("");
      exam.setTreatmentNote("");
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      // nvl("", "Trống") returns "Trống" because s.isEmpty() is true.
      // Verify the branch is exercised by asserting the PDF is valid.
      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
      assertThat(pdf[0]).isEqualTo((byte) 0x25);
      assertThat(pdf[1]).isEqualTo((byte) 0x50);
    }
  }

  // ── PDF Generation — Happy Paths ──

  @Nested
  @DisplayName("generateExamReportPdf() — happy paths")
  class HappyPaths {

    @Test
    @DisplayName("Tạo PDF thành công với đầy đủ dữ liệu")
    void shouldGeneratePdfWithFullData() {
      Exam exam = createFullExam();
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
      // PDF magic bytes: %PDF
      assertThat(pdf[0]).isEqualTo((byte) 0x25); // %
      assertThat(pdf[1]).isEqualTo((byte) 0x50); // P
      assertThat(pdf[2]).isEqualTo((byte) 0x44); // D
      assertThat(pdf[3]).isEqualTo((byte) 0x46); // F
    }

    @Test
    @DisplayName("Tạo PDF khi không có teethRecord")
    void shouldGeneratePdfWithoutTeethRecord() {
      Exam exam = createFullExam();
      exam.setTeethRecord(null);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
      assertThat(pdf[0]).isEqualTo((byte) 0x25);
    }

    @Test
    @DisplayName("Tạo PDF khi teethRecord có record null")
    void shouldGeneratePdfWithNullTeethRecordMap() {
      Exam exam = createFullExam();
      exam.getTeethRecord().setRecord(null);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("Tạo PDF khi không có treatmentRecords")
    void shouldGeneratePdfWithoutTreatments() {
      Exam exam = createFullExam();
      exam.setTreatmentRecords(null);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("Tạo PDF khi treatmentRecords rỗng")
    void shouldGeneratePdfWithEmptyTreatments() {
      Exam exam = createFullExam();
      exam.setTreatmentRecords(List.of());
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("Tạo PDF khi không có plaqueRecord và tartarRecord")
    void shouldGeneratePdfWithoutOhiRecords() {
      Exam exam = createFullExam();
      exam.setPlaqueRecord(null);
      exam.setTartarRecord(null);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("Tạo PDF khi plaqueRecord có tất cả 6 vị trí đều có giá trị → getDescription() được gọi cho mỗi vị trí")
    void shouldGeneratePdfWithAllPlaqueFieldsNonNull() {
      Exam exam = createFullExam();
      PlaqueRecord plaque = exam.getPlaqueRecord();
      plaque.set_17_16n(PlaqueCondition.TWO_THIRD);
      plaque.set_11n(PlaqueCondition.ONE_THIRD);
      plaque.set_26_27n(PlaqueCondition.CLEAN);
      plaque.set_47_46t(PlaqueCondition.TWO_THIRD_OR_MORE);
      plaque.set_31n(PlaqueCondition.TOOTH_MISSING);
      plaque.set_36_37t(PlaqueCondition.ONE_THIRD);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
      assertThat(pdf[0]).isEqualTo((byte) 0x25);
    }

    @Test
    @DisplayName("Tạo PDF khi tartarRecord có tất cả 6 vị trí đều có giá trị → getDescription() được gọi cho mỗi vị trí")
    void shouldGeneratePdfWithAllTartarFieldsNonNull() {
      Exam exam = createFullExam();
      TartarRecord tartar = exam.getTartarRecord();
      tartar.set_17_16n(TartarCondition.ONE_THIRD);
      tartar.set_11n(TartarCondition.CLEAN);
      tartar.set_26_27n(TartarCondition.TWO_THIRD);
      tartar.set_47_46t(TartarCondition.TWO_THIRD_OR_MORE);
      tartar.set_31n(TartarCondition.TOOTH_MISSING);
      tartar.set_36_37t(TartarCondition.ONE_THIRD);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
      assertThat(pdf[0]).isEqualTo((byte) 0x25);
    }

    @Test
    @DisplayName("Tạo PDF khi cả plaqueRecord và tartarRecord đều có đầy đủ 6 vị trí")
    void shouldGeneratePdfWithBothOhiRecordsFullyPopulated() {
      Exam exam = createFullExam();
      // Plaque — all 6 positions non-null
      PlaqueRecord plaque = exam.getPlaqueRecord();
      plaque.set_17_16n(PlaqueCondition.TWO_THIRD);
      plaque.set_11n(PlaqueCondition.ONE_THIRD);
      plaque.set_26_27n(PlaqueCondition.CLEAN);
      plaque.set_47_46t(PlaqueCondition.TWO_THIRD_OR_MORE);
      plaque.set_31n(PlaqueCondition.TOOTH_MISSING);
      plaque.set_36_37t(PlaqueCondition.ONE_THIRD);
      // Tartar — all 6 positions non-null
      TartarRecord tartar = exam.getTartarRecord();
      tartar.set_17_16n(TartarCondition.ONE_THIRD);
      tartar.set_11n(TartarCondition.CLEAN);
      tartar.set_26_27n(TartarCondition.TWO_THIRD);
      tartar.set_47_46t(TartarCondition.TWO_THIRD_OR_MORE);
      tartar.set_31n(TartarCondition.TOOTH_MISSING);
      tartar.set_36_37t(TartarCondition.ONE_THIRD);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
      assertThat(pdf[0]).isEqualTo((byte) 0x25);
    }

    @Test
    @DisplayName("Tạo PDF khi patient thiếu thông tin optional")
    void shouldGeneratePdfWithMinimalPatient() {
      Exam exam = createFullExam();
      Patient p = new Patient();
      p.setId(1L);
      p.setFullName("Trần Thị B");
      p.setGender(2); // Nữ
      // code, birthDate, organization, schoolClass = null
      exam.setPatient(p);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("Tạo PDF khi exam thiếu dentist, date, pathologyAssessment")
    void shouldGeneratePdfWithMinimalExam() {
      Exam exam = new Exam();
      exam.setId(1L);
      exam.setPatient(createPatient());
      // dentist, date, pathologyAssessment, treatmentNote = null
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("Tạo PDF với treatment có prescription")
    void shouldGeneratePdfWithPrescription() {
      Exam exam = createFullExam();
      TreatmentRecord tr = exam.getTreatmentRecords().get(0);
      PrescriptionItem item = new PrescriptionItem("AMOX500", 2);
      tr.setPrescription(List.of(item));
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      byte[] pdf = service.generateExamReportPdf(1L);

      assertThat(pdf).isNotEmpty();
    }
  }
}
