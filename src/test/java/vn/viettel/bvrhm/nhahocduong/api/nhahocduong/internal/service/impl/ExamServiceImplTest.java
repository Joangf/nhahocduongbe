package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.ResponseMessage;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.ExamSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.AssessmentUpdateDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ImageUpdateDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Tooth;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothProblem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExamServiceImpl Unit Tests")
class ExamServiceImplTest {

    @Mock private ExamRepository examRepository;
    @Mock private DiseaseRepository diseaseRepository;
    @Mock private ExamMapper examMapper;
    @Mock private PatientRepository patientRepository;
    @Mock private TartarRecordRepository tartarRecordRepository;
    @Mock private TeethRecordRepository teethRecordRepository;
    @Mock private PlaqueRecordRepository plaqueRecordRepository;
    @Mock private ExamCampaignRepository examCampaignRepository;

    @InjectMocks private ExamServiceImpl examService;

    @Captor private ArgumentCaptor<Exam> examCaptor;

    // ─── Helper methods ────────────────────────────────────────────────

    private Exam createMockExam() {
        Exam exam = new Exam();
        exam.setId(1L);
        exam.setDate(LocalDate.of(2026, 3, 15));
        exam.setSchoolClass("1A");
        exam.setYear("2025-2026");
        exam.setStatus(true);
        exam.setPathologyAssessment("Initial assessment");
        exam.setTreatmentNote("No treatment needed");
        exam.setImageUpperUrl("https://example.com/upper.jpg");
        exam.setImageUpperTime(LocalDateTime.of(2026, 3, 15, 10, 0));
        exam.setImageLowerUrl("https://example.com/lower.jpg");
        exam.setImageLowerTime(LocalDateTime.of(2026, 3, 15, 10, 5));
        return exam;
    }

    private ExamDTO createMockExamDTO() {
        ExamDTO dto = new ExamDTO();
        dto.setId(1L);
        dto.setPatientId(10L);
        dto.setPatientName("Nguyen Van A");
        dto.setDentistId(5L);
        dto.setDentistName("Dr. Tran");
        dto.setOrganizationId(20L);
        dto.setOrganizationName("Test School");
        dto.setSchoolClass("1A");
        dto.setYear("2025-2026");
        dto.setDate(LocalDate.of(2026, 3, 15));
        dto.setStatus(true);
        dto.setPathologyAssessment("Initial assessment");
        dto.setTreatmentNote("No treatment needed");
        dto.setImageUpperUrl("https://example.com/upper.jpg");
        dto.setImageUpperTime(LocalDateTime.of(2026, 3, 15, 10, 0));
        dto.setImageLowerUrl("https://example.com/lower.jpg");
        dto.setImageLowerTime(LocalDateTime.of(2026, 3, 15, 10, 5));
        return dto;
    }

    private TeethRecord createMockTeethRecord() {
        TeethRecord tr = new TeethRecord();
        tr.setId(100L);
        return tr;
    }

    private PlaqueRecord createMockPlaqueRecord() {
        PlaqueRecord pr = new PlaqueRecord();
        pr.setId(200L);
        return pr;
    }

    private TartarRecord createMockTartarRecord() {
        TartarRecord tr = new TartarRecord();
        tr.setId(300L);
        return tr;
    }

    // ─── createExam() Tests ────────────────────────────────────────────

    @Nested
    @DisplayName("createExam()")
    class CreateExamTests {

        @Test
        @DisplayName("TC-EXAM-01: Happy path — saves new exam and returns ExamDTO")
        void createExam_happyPath_savesAndReturnsDTO() {
            // Arrange
            ExamDTO inputDTO = createMockExamDTO();
            Exam newExam = createMockExam();
            Exam savedExam = createMockExam();
            savedExam.setId(1L);
            ExamDTO expectedDTO = createMockExamDTO();

            when(examMapper.toEntity(inputDTO)).thenReturn(newExam);
            when(examRepository.save(any(Exam.class))).thenReturn(savedExam);
            when(examMapper.toDto(savedExam)).thenReturn(expectedDTO);

            // Act
            ExamDTO result = examService.createExam(inputDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getPatientName()).isEqualTo("Nguyen Van A");

            // Verify ID was set to null before save (to force INSERT)
            verify(examRepository).save(examCaptor.capture());
            assertThat(examCaptor.getValue().getId()).isNull();
        }
    }

    // ─── updateAssessment() Tests ──────────────────────────────────────

    @Nested
    @DisplayName("updateAssessment()")
    class UpdateAssessmentTests {

        @Test
        @DisplayName("Happy path — patches only non-null fields")
        void updateAssessment_patchesOnlyNonNullFields() {
            // Arrange
            Exam existingExam = createMockExam();
            existingExam.setPathologyAssessment("Old assessment");
            existingExam.setTreatmentNote("Old note");

            AssessmentUpdateDTO dto = new AssessmentUpdateDTO();
            dto.setPathologyAssessment("New assessment");
            dto.setTreatmentNote(null); // Should NOT overwrite

            when(examRepository.findById(1L)).thenReturn(Optional.of(existingExam));
            when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));

            ExamDTO expectedDTO = createMockExamDTO();
            expectedDTO.setPathologyAssessment("New assessment");
            expectedDTO.setTreatmentNote("Old note");
            when(examMapper.toDto(any(Exam.class))).thenReturn(expectedDTO);

            // Act
            ExamDTO result = examService.updateAssessment(1L, dto);

            // Assert
            verify(examRepository).save(examCaptor.capture());
            Exam saved = examCaptor.getValue();
            assertThat(saved.getPathologyAssessment()).isEqualTo("New assessment");
            assertThat(saved.getTreatmentNote()).isEqualTo("Old note"); // NOT overwritten

            assertThat(result.getPathologyAssessment()).isEqualTo("New assessment");
        }

        @Test
        @DisplayName("Both fields provided — both updated")
        void updateAssessment_bothFieldsProvided_bothUpdated() {
            // Arrange
            Exam existingExam = createMockExam();
            AssessmentUpdateDTO dto = new AssessmentUpdateDTO("Updated pathology", "Updated note");

            when(examRepository.findById(1L)).thenReturn(Optional.of(existingExam));
            when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));
            when(examMapper.toDto(any(Exam.class))).thenReturn(createMockExamDTO());

            // Act
            examService.updateAssessment(1L, dto);

            // Assert
            verify(examRepository).save(examCaptor.capture());
            Exam saved = examCaptor.getValue();
            assertThat(saved.getPathologyAssessment()).isEqualTo("Updated pathology");
            assertThat(saved.getTreatmentNote()).isEqualTo("Updated note");
        }

        @Test
        @DisplayName("Both fields null — existing values preserved")
        void updateAssessment_allFieldsNull_preservesExisting() {
            // Arrange
            Exam existingExam = createMockExam();
            existingExam.setPathologyAssessment("Keep this");
            existingExam.setTreatmentNote("Keep this too");
            AssessmentUpdateDTO dto = new AssessmentUpdateDTO(null, null);

            when(examRepository.findById(1L)).thenReturn(Optional.of(existingExam));
            when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));
            when(examMapper.toDto(any(Exam.class))).thenReturn(createMockExamDTO());

            // Act
            examService.updateAssessment(1L, dto);

            // Assert
            verify(examRepository).save(examCaptor.capture());
            assertThat(examCaptor.getValue().getPathologyAssessment()).isEqualTo("Keep this");
            assertThat(examCaptor.getValue().getTreatmentNote()).isEqualTo("Keep this too");
        }

        @Test
        @DisplayName("Exam not found — throws 404")
        void updateAssessment_examNotFound_throws404() {
            // Arrange
            when(examRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> examService.updateAssessment(999L, new AssessmentUpdateDTO()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).contains(ResponseMessage.EXAM_NOT_FOUND_WITH_ID);
                });
        }
    }

    // ─── updateTeethRecordIdOfExam() Tests ─────────────────────────────

    @Nested
    @DisplayName("updateTeethRecordIdOfExam()")
    class UpdateTeethRecordIdTests {

        @Test
        @DisplayName("Happy path — updates teeth record reference on exam")
        void updateTeethRecordIdOfExam_updatesRecordId() {
            // Arrange
            Exam exam = createMockExam();
            TeethRecord teethRecord = createMockTeethRecord();
            Exam updatedExam = createMockExam();
            updatedExam.setTeethRecord(teethRecord);
            ExamDTO expectedDTO = createMockExamDTO();
            expectedDTO.setTeethRecordId(100L);

            when(examRepository.getReferenceById(1L)).thenReturn(exam);
            when(teethRecordRepository.getReferenceById(100L)).thenReturn(teethRecord);
            when(examRepository.save(exam)).thenReturn(updatedExam);
            when(examMapper.toDto(updatedExam)).thenReturn(expectedDTO);

            // Act
            ExamDTO result = examService.updateTeethRecordIdOfExam(1L, 100L);

            // Assert
            assertThat(result.getTeethRecordId()).isEqualTo(100L);
            verify(examRepository).save(exam);
            assertThat(exam.getTeethRecord()).isEqualTo(teethRecord);
        }
    }

    // ─── updatePlaqueRecordIdOfExam() Tests ────────────────────────────

    @Nested
    @DisplayName("updatePlaqueRecordIdOfExam()")
    class UpdatePlaqueRecordIdTests {

        @Test
        @DisplayName("Happy path — updates plaque record reference on exam")
        void updatePlaqueRecordIdOfExam_updatesRecordId() {
            // Arrange
            Exam exam = createMockExam();
            PlaqueRecord plaqueRecord = createMockPlaqueRecord();
            ExamDTO expectedDTO = createMockExamDTO();
            expectedDTO.setPlaqueRecordId(200L);

            when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
            when(plaqueRecordRepository.getReferenceById(200L)).thenReturn(plaqueRecord);
            when(examRepository.save(exam)).thenReturn(exam);
            when(examMapper.toDto(exam)).thenReturn(expectedDTO);

            // Act
            ExamDTO result = examService.updatePlaqueRecordIdOfExam(1L, 200L);

            // Assert
            assertThat(result.getPlaqueRecordId()).isEqualTo(200L);
            verify(examRepository).save(exam);
            assertThat(exam.getPlaqueRecord()).isEqualTo(plaqueRecord);
        }

        @Test
        @DisplayName("Exam not found — throws NoSuchElementException")
        void updatePlaqueRecordIdOfExam_examNotFound_throwsException() {
            // Arrange
            when(examRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> examService.updatePlaqueRecordIdOfExam(999L, 200L))
                .isInstanceOf(NoSuchElementException.class);
        }
    }

    // ─── updateTartarRecordIdOfExam() Tests ────────────────────────────

    @Nested
    @DisplayName("updateTartarRecordIdOfExam()")
    class UpdateTartarRecordIdTests {

        @Test
        @DisplayName("Happy path — updates tartar record reference on exam")
        void updateTartarRecordIdOfExam_updatesRecordId() {
            // Arrange
            Exam exam = createMockExam();
            TartarRecord tartarRecord = createMockTartarRecord();
            ExamDTO expectedDTO = createMockExamDTO();
            expectedDTO.setTartarRecordId(300L);

            when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
            when(tartarRecordRepository.getReferenceById(300L)).thenReturn(tartarRecord);
            when(examRepository.save(exam)).thenReturn(exam);
            when(examMapper.toDto(exam)).thenReturn(expectedDTO);

            // Act
            ExamDTO result = examService.updateTartarRecordIdOfExam(1L, 300L);

            // Assert
            assertThat(result.getTartarRecordId()).isEqualTo(300L);
            verify(examRepository).save(exam);
            assertThat(exam.getTartarRecord()).isEqualTo(tartarRecord);
        }

        @Test
        @DisplayName("Exam not found — throws NoSuchElementException")
        void updateTartarRecordIdOfExam_examNotFound_throwsException() {
            // Arrange
            when(examRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> examService.updateTartarRecordIdOfExam(999L, 300L))
                .isInstanceOf(NoSuchElementException.class);
        }
    }

    // ─── clearImage() Tests ────────────────────────────────────────────

    @Nested
    @DisplayName("clearImage()")
    class ClearImageTests {

        @Test
        @DisplayName("TC-EXAM-04: Clear 'upper' — nullifies upper image, preserves lower")
        void clearImage_upper_clearsUpperPreservesLower() {
            // Arrange
            Exam exam = createMockExam();
            // Pre-set both images
            exam.setImageUpperUrl("https://example.com/upper.jpg");
            exam.setImageUpperTime(LocalDateTime.of(2026, 3, 15, 10, 0));
            exam.setImageLowerUrl("https://example.com/lower.jpg");
            exam.setImageLowerTime(LocalDateTime.of(2026, 3, 15, 10, 5));

            when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
            when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));

            ExamDTO expectedDTO = createMockExamDTO();
            expectedDTO.setImageUpperUrl(null);
            expectedDTO.setImageUpperTime(null);
            when(examMapper.toDto(any(Exam.class))).thenReturn(expectedDTO);

            // Act
            examService.clearImage(1L, "upper");

            // Assert
            verify(examRepository).save(examCaptor.capture());
            Exam saved = examCaptor.getValue();

            // Upper should be cleared
            assertThat(saved.getImageUpperUrl()).isNull();
            assertThat(saved.getImageUpperTime()).isNull();

            // Lower should be UNTOUCHED
            assertThat(saved.getImageLowerUrl()).isEqualTo("https://example.com/lower.jpg");
            assertThat(saved.getImageLowerTime()).isEqualTo(LocalDateTime.of(2026, 3, 15, 10, 5));
        }

        @Test
        @DisplayName("TC-EXAM-04: Clear 'lower' — nullifies lower image, preserves upper")
        void clearImage_lower_clearsLowerPreservesUpper() {
            // Arrange
            Exam exam = createMockExam();
            exam.setImageUpperUrl("https://example.com/upper.jpg");
            exam.setImageUpperTime(LocalDateTime.of(2026, 3, 15, 10, 0));
            exam.setImageLowerUrl("https://example.com/lower.jpg");
            exam.setImageLowerTime(LocalDateTime.of(2026, 3, 15, 10, 5));

            when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
            when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));
            when(examMapper.toDto(any(Exam.class))).thenReturn(createMockExamDTO());

            // Act
            examService.clearImage(1L, "lower");

            // Assert
            verify(examRepository).save(examCaptor.capture());
            Exam saved = examCaptor.getValue();

            // Lower should be cleared
            assertThat(saved.getImageLowerUrl()).isNull();
            assertThat(saved.getImageLowerTime()).isNull();

            // Upper should be UNTOUCHED
            assertThat(saved.getImageUpperUrl()).isEqualTo("https://example.com/upper.jpg");
            assertThat(saved.getImageUpperTime()).isEqualTo(LocalDateTime.of(2026, 3, 15, 10, 0));
        }

        @Test
        @DisplayName("Case-insensitive — 'UPPER' clears upper image")
        void clearImage_caseInsensitive_clearsUpper() {
            // Arrange
            Exam exam = createMockExam();
            when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
            when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));
            when(examMapper.toDto(any(Exam.class))).thenReturn(createMockExamDTO());

            // Act
            examService.clearImage(1L, "UPPER");

            // Assert
            verify(examRepository).save(examCaptor.capture());
            assertThat(examCaptor.getValue().getImageUpperUrl()).isNull();
            assertThat(examCaptor.getValue().getImageUpperTime()).isNull();
        }

        @Test
        @DisplayName("Unknown side — neither image is cleared, entity is still saved")
        void clearImage_unknownSide_nothingCleared() {
            // Arrange
            Exam exam = createMockExam();
            when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
            when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));
            when(examMapper.toDto(any(Exam.class))).thenReturn(createMockExamDTO());

            // Act
            examService.clearImage(1L, "left");

            // Assert — both images are still present
            verify(examRepository).save(examCaptor.capture());
            Exam saved = examCaptor.getValue();
            assertThat(saved.getImageUpperUrl()).isNotNull();
            assertThat(saved.getImageLowerUrl()).isNotNull();
        }

        @Test
        @DisplayName("Exam not found — throws 404")
        void clearImage_examNotFound_throws404() {
            // Arrange
            when(examRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> examService.clearImage(999L, "upper"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND));
        }
    }

    // ─── updateImages() Tests ──────────────────────────────────────────

    @Nested
    @DisplayName("updateImages()")
    class UpdateImagesTests {

        @Test
        @DisplayName("Only upper image provided — updates upper, leaves lower untouched")
        void updateImages_onlyUpper_updatesUpperPreservesLower() {
            // Arrange
            Exam exam = createMockExam();
            ImageUpdateDTO dto = new ImageUpdateDTO();
            dto.setImageUpperUrl("https://new-upper.jpg");
            dto.setImageUpperTime(LocalDateTime.of(2026, 7, 1, 12, 0));
            // Lower fields are null — should NOT overwrite

            when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
            when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));
            when(examMapper.toDto(any(Exam.class))).thenReturn(createMockExamDTO());

            // Act
            examService.updateImages(1L, dto);

            // Assert
            verify(examRepository).save(examCaptor.capture());
            Exam saved = examCaptor.getValue();
            assertThat(saved.getImageUpperUrl()).isEqualTo("https://new-upper.jpg");
            // Lower preserved
            assertThat(saved.getImageLowerUrl()).isEqualTo("https://example.com/lower.jpg");
        }

        @Test
        @DisplayName("Only lower image provided — updates lower, leaves upper untouched")
        void updateImages_onlyLower_updatesLowerPreservesUpper() {
            // Arrange
            Exam exam = createMockExam();
            ImageUpdateDTO dto = new ImageUpdateDTO();
            dto.setImageLowerUrl("https://new-lower.jpg");
            dto.setImageLowerTime(LocalDateTime.of(2026, 7, 1, 12, 0));
            // Upper fields are null — should NOT overwrite

            when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
            when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));
            when(examMapper.toDto(any(Exam.class))).thenReturn(createMockExamDTO());

            // Act
            examService.updateImages(1L, dto);

            // Assert
            verify(examRepository).save(examCaptor.capture());
            Exam saved = examCaptor.getValue();
            assertThat(saved.getImageLowerUrl()).isEqualTo("https://new-lower.jpg");
            assertThat(saved.getImageLowerTime()).isEqualTo(LocalDateTime.of(2026, 7, 1, 12, 0));
            // Upper preserved
            assertThat(saved.getImageUpperUrl()).isEqualTo("https://example.com/upper.jpg");
            assertThat(saved.getImageUpperTime()).isEqualTo(LocalDateTime.of(2026, 3, 15, 10, 0));
        }

        @Test
        @DisplayName("All four fields provided — all updated")
        void updateImages_allFourFields_updatesAll() {
            // Arrange
            Exam exam = createMockExam();
            ImageUpdateDTO dto = new ImageUpdateDTO();
            dto.setImageUpperUrl("https://new-upper.jpg");
            dto.setImageUpperTime(LocalDateTime.of(2026, 8, 1, 14, 0));
            dto.setImageLowerUrl("https://new-lower.jpg");
            dto.setImageLowerTime(LocalDateTime.of(2026, 8, 1, 14, 30));

            when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
            when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));
            when(examMapper.toDto(any(Exam.class))).thenReturn(createMockExamDTO());

            // Act
            examService.updateImages(1L, dto);

            // Assert
            verify(examRepository).save(examCaptor.capture());
            Exam saved = examCaptor.getValue();
            assertThat(saved.getImageUpperUrl()).isEqualTo("https://new-upper.jpg");
            assertThat(saved.getImageUpperTime()).isEqualTo(LocalDateTime.of(2026, 8, 1, 14, 0));
            assertThat(saved.getImageLowerUrl()).isEqualTo("https://new-lower.jpg");
            assertThat(saved.getImageLowerTime()).isEqualTo(LocalDateTime.of(2026, 8, 1, 14, 30));
        }

        @Test
        @DisplayName("Exam not found — throws 404")
        void updateImages_examNotFound_throws404() {
            // Arrange
            when(examRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> examService.updateImages(999L, new ImageUpdateDTO()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND));
        }
    }

    // ─── delete() Tests ────────────────────────────────────────────────

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("Happy path — soft-deletes exam (sets status=false)")
        void delete_existingExam_softDeletes() {
            // Arrange
            Exam exam = createMockExam();
            when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

            // Act
            boolean result = examService.delete(1L);

            // Assert
            assertThat(result).isTrue();
            verify(examRepository).save(examCaptor.capture());
            assertThat(examCaptor.getValue().getStatus()).isFalse();
        }

        @Test
        @DisplayName("Exam not found — throws 404")
        void delete_examNotFound_throws404() {
            // Arrange
            when(examRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> examService.delete(999L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
        }
    }

    // ─── updateExam() Tests ────────────────────────────────────────────

    @Nested
    @DisplayName("updateExam()")
    class UpdateExamTests {

        @Test
        @DisplayName("Happy path — finds exam by id+patientId, partial updates, returns DTO")
        void updateExam_existingExam_updatesAndReturnsDTO() {
            // Arrange
            ExamDTO inputDTO = createMockExamDTO();
            Exam existingExam = createMockExam();

            when(examRepository.findExamByIdAndPatientId(1L, 10L)).thenReturn(existingExam);
            when(examRepository.save(existingExam)).thenReturn(existingExam);
            when(examMapper.toDto(existingExam)).thenReturn(inputDTO);

            // Act
            ExamDTO result = examService.updateExam(inputDTO);

            // Assert
            assertThat(result).isNotNull();
            verify(examMapper).partialUpdate(inputDTO, existingExam);
            verify(examRepository).save(existingExam);
        }

        @Test
        @DisplayName("Exam not found for patient — throws 404")
        void updateExam_examNotFoundForPatient_throws404() {
            // Arrange
            ExamDTO inputDTO = createMockExamDTO();
            when(examRepository.findExamByIdAndPatientId(1L, 10L)).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> examService.updateExam(inputDTO))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).contains(ResponseMessage.EXAM_NOT_FOUND_WITH_PATIENT_ID);
                });
        }
    }

    // ─── getExamsByPatientIdAndStatus() Tests ──────────────────────────

    @Nested
    @DisplayName("getExamsByPatientIdAndStatus()")
    class GetExamsByPatientIdTests {

        @Test
        @DisplayName("Returns list of ExamDTOs for a patient")
        void getExamsByPatientIdAndStatus_returnsList() {
            // Arrange
            Exam exam = createMockExam();
            List<ExamDTO> dtoList = List.of(createMockExamDTO());

            when(examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(10L, true))
                .thenReturn(List.of(exam));
            when(examMapper.toDtoList(List.of(exam))).thenReturn(dtoList);

            // Act
            List<ExamDTO> result = examService.getExamsByPatientIdAndStatus(10L, true);

            // Assert
            assertThat(result).hasSize(1);
        }
    }

    // ─── getExamByIdAndPatientIdAndStatus() Tests ──────────────────────

    @Nested
    @DisplayName("getExamByIdAndPatientIdAndStatus()")
    class GetExamByIdAndPatientIdTests {

        @Test
        @DisplayName("Returns ExamDTO when exam exists")
        void getExamByIdAndPatientIdAndStatus_exists_returnsDTO() {
            // Arrange
            Exam exam = createMockExam();
            ExamDTO dto = createMockExamDTO();

            when(examRepository.findExamByIdAndPatientIdAndStatus(1L, 10L, true)).thenReturn(exam);
            when(examMapper.toDto(exam)).thenReturn(dto);

            // Act
            ExamDTO result = examService.getExamByIdAndPatientIdAndStatus(1L, 10L, true);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Returns null when exam does not exist")
        void getExamByIdAndPatientIdAndStatus_notExists_returnsNull() {
            // Arrange
            when(examRepository.findExamByIdAndPatientIdAndStatus(1L, 10L, true)).thenReturn(null);

            // Act
            ExamDTO result = examService.getExamByIdAndPatientIdAndStatus(1L, 10L, true);

            // Assert
            assertThat(result).isNull();
        }
    }

    // ─── search() Tests ────────────────────────────────────────────────

    @Nested
    @DisplayName("search()")
    class SearchTests {

        @Test
        @DisplayName("Returns paged results from repository")
        void search_returnsPagedResults() {
            // Arrange
            ExamSearchCriteria criteria = new ExamSearchCriteria();
            Pageable pageable = PageRequest.of(0, 10);
            Exam exam = createMockExam();
            Page<Exam> examPage = new PageImpl<>(List.of(exam), pageable, 1);
            ExamDTO dto = createMockExamDTO();

            when(examRepository.search(criteria, pageable)).thenReturn(examPage);
            when(examMapper.toDto(exam)).thenReturn(dto);

            // Act
            Page<ExamDTO> result = examService.search(criteria, pageable);

            // Assert
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
        }
    }

    // ─── updateChronicDiseasesCodesByExamId() Tests ────────────────────

    @Nested
    @DisplayName("updateChronicDiseasesCodesByExamId()")
    class UpdateChronicDiseasesTests {

        @Test
        @DisplayName("Updates chronic disease list on exam")
        void updateChronicDiseases_updatesAndReturnsCodes() {
            // Arrange
            Exam exam = createMockExam();
            Disease d1 = new Disease();
            d1.setCode("D01");
            Disease d2 = new Disease();
            d2.setCode("D02");

            when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
            when(diseaseRepository.getByCode("D01")).thenReturn(d1);
            when(diseaseRepository.getByCode("D02")).thenReturn(d2);

            Exam savedExam = createMockExam();
            savedExam.setChronicConditions(List.of(d1, d2));
            when(examRepository.save(exam)).thenReturn(savedExam);

            // Act
            List<String> result = examService.updateChronicDiseasesCodesByExamId(1L, List.of("D01", "D02"));

            // Assert
            assertThat(result).containsExactly("D01", "D02");
        }

        @Test
        @DisplayName("Exam not found — throws NoSuchElementException")
        void updateChronicDiseases_examNotFound_throwsException() {
            // Arrange
            when(examRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> examService.updateChronicDiseasesCodesByExamId(999L, List.of("D01")))
                .isInstanceOf(NoSuchElementException.class);
        }
    }

    // ─── getChronicDiseasesCodesByExamId() Tests ──────────────────────

    @Nested
    @DisplayName("getChronicDiseasesCodesByExamId()")
    class GetChronicDiseasesCodesTests {

        @Test
        @DisplayName("Happy path — exam with chronic conditions returns disease codes")
        void getChronicDiseasesCodes_happyPath_returnsCodes() {
            // Arrange
            Exam exam = createMockExam();
            Disease d1 = new Disease();
            d1.setCode("D01");
            Disease d2 = new Disease();
            d2.setCode("D02");
            exam.setChronicConditions(List.of(d1, d2));

            when(examRepository.getReferenceById(1L)).thenReturn(exam);

            // Act
            List<String> result = examService.getChronicDiseasesCodesByExamId(1L);

            // Assert
            assertThat(result).containsExactly("D01", "D02");
        }

        @Test
        @DisplayName("Empty chronic conditions — returns empty list")
        void getChronicDiseasesCodes_emptyList_returnsEmpty() {
            // Arrange
            Exam exam = createMockExam();
            exam.setChronicConditions(Collections.emptyList());

            when(examRepository.getReferenceById(1L)).thenReturn(exam);

            // Act
            List<String> result = examService.getChronicDiseasesCodesByExamId(1L);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    // ─── getDashboardStats() Tests ─────────────────────────────────────

    @Nested
    @DisplayName("getDashboardStats()")
    class GetDashboardStatsTests {

        @Test
        @DisplayName("Returns aggregated statistics")
        void getDashboardStats_returnsStats() {
            // Arrange
            when(examCampaignRepository.count()).thenReturn(10L);
            when(examCampaignRepository.countByStatus(true)).thenReturn(5L);
            when(patientRepository.count()).thenReturn(200L);
            when(examRepository.countTotalExamined()).thenReturn(150L);

            // Act
            var stats = examService.getDashboardStats();

            // Assert
            assertThat(stats.getTotalCampaigns()).isEqualTo(10L);
            assertThat(stats.getActiveCampaigns()).isEqualTo(5L);
            assertThat(stats.getTotalStudents()).isEqualTo(200L);
            assertThat(stats.getTotalExamined()).isEqualTo(150L);
        }
    }

    // ─── getReExams() Tests ───────────────────────────────────────────

    @Nested
    @DisplayName("getReExams()")
    class GetReExamsTests {

        private TeethRecord createTeethRecordWithProblem(ToothProblem problem) {
            TeethRecord tr = new TeethRecord();
            Map<Tooth, ToothCondition> recordMap = new HashMap<>();
            ToothCondition tc = new ToothCondition();
            tc.setProblem(problem);
            recordMap.put(Tooth._11, tc);
            tr.setRecord(recordMap);
            return tr;
        }

        private TeethRecord createTeethRecordNoProblem() {
            return createTeethRecordWithProblem(ToothProblem.NO_PROBLEM);
        }

        @Test
        @DisplayName("Exam with caries — included in result with correct reExamDate")
        void getReExams_examWithCaries_includedInResult() {
            // Arrange
            Exam exam = createMockExam();
            exam.setDate(LocalDate.of(2026, 3, 15));
            exam.setTeethRecord(createTeethRecordWithProblem(ToothProblem.CARIES));

            when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));
            ExamDTO mockDto = createMockExamDTO();
            when(examMapper.toDto(exam)).thenReturn(mockDto);

            // Act
            List<ExamDTO> result = examService.getReExams();

            // Assert
            assertThat(result).hasSize(1);
            ExamDTO resultDto = result.get(0);
            assertThat(resultDto.getReExamDate()).isEqualTo(LocalDate.of(2026, 9, 15));
            assertThat(resultDto.getReExamNote()).isEqualTo("Cần tái khám điều trị sâu răng");
        }

        @Test
        @DisplayName("Exam without caries — excluded from result")
        void getReExams_examWithoutCaries_excludedFromResult() {
            // Arrange
            Exam exam = createMockExam();
            exam.setTeethRecord(createTeethRecordNoProblem());

            when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));

            // Act
            List<ExamDTO> result = examService.getReExams();

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Exam with null teethRecord — excluded from result")
        void getReExams_nullTeethRecord_excludedFromResult() {
            // Arrange
            Exam exam = createMockExam();
            exam.setTeethRecord(null);

            when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));

            // Act
            List<ExamDTO> result = examService.getReExams();

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Exam with null record map — excluded from result")
        void getReExams_nullRecordMap_excludedFromResult() {
            // Arrange
            Exam exam = createMockExam();
            TeethRecord tr = new TeethRecord();
            tr.setRecord(null);
            exam.setTeethRecord(tr);

            when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));

            // Act
            List<ExamDTO> result = examService.getReExams();

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Exam with null date — reExamDate = now + 6 months")
        void getReExams_nullDate_usesNowPlusSixMonths() {
            // Arrange
            Exam exam = createMockExam();
            exam.setDate(null);
            exam.setTeethRecord(createTeethRecordWithProblem(ToothProblem.CARIES));

            when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));

            ExamDTO mockDto = new ExamDTO();
            when(examMapper.toDto(exam)).thenReturn(mockDto);

            // Act
            List<ExamDTO> result = examService.getReExams();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getReExamDate()).isEqualTo(LocalDate.now().plusMonths(6));
            assertThat(result.get(0).getReExamNote()).isEqualTo("Cần tái khám điều trị sâu răng");
        }

        @Test
        @DisplayName("Exam with valid date — reExamDate = date + 6 months")
        void getReExams_validDate_usesDatePlusSixMonths() {
            // Arrange
            Exam exam = createMockExam();
            exam.setDate(LocalDate.of(2026, 1, 20));
            exam.setTeethRecord(createTeethRecordWithProblem(ToothProblem.CARIES));

            when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));

            ExamDTO mockDto = new ExamDTO();
            when(examMapper.toDto(exam)).thenReturn(mockDto);

            // Act
            List<ExamDTO> result = examService.getReExams();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getReExamDate()).isEqualTo(LocalDate.of(2026, 7, 20));
            assertThat(result.get(0).getReExamNote()).isEqualTo("Cần tái khám điều trị sâu răng");
        }
    }
}
