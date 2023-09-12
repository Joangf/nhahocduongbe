// package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
// import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.*;
//
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;
//
// public class ExamMapper {
//
//  @Autowired private PatientRepository patientRepository;
//  @Autowired private DentistRepository dentistRepository;
//  @Autowired private OrganizationRepository organizationRepository;
//  @Autowired private TeethRecordRepository teethRecordRepository;
//  @Autowired private PlaqueRecordRepository plaqueRecordRepository;
//  @Autowired private TartarRecordRepository tartarRecordRepository;
//  public Exam toEntity(ExamDTO examDTO) {
//    if ( examDTO == null ) {
//      return null;
//    }
//
//    if (examDTO.patientId() == null) {
//      return null;
//    }
//
//    Exam exam = new Exam();
//
//    exam.setId( examDTO.id() );
//    exam.setSchoolClass( examDTO.schoolClass() );
//    exam.setYear( examDTO.year() );
//    exam.setProfileNumber( examDTO.profileNumber() );
//    exam.setDate( examDTO.date() );
//
//    if (examDTO.dentistId() != null) {
//      var dentist = dentistRepository.getReferenceById(examDTO.dentistId());
//      exam.setDentist(dentist);
//    }
//
//    if (examDTO.organizationId() != null) {
//      var organization
//    }
//    return exam;
//  }
//
//  public List<Exam> toEntityList(List<ExamDTO> examDTOList) {
//    if ( examDTOList == null ) {
//      return null;
//    }
//
//    List<Exam> list = new ArrayList<Exam>( examDTOList.size() );
//    for ( ExamDTO examDTO : examDTOList ) {
//      list.add( toEntity( examDTO ) );
//    }
//
//    return list;
//  }
//
//  public ExamDTO toDto(Exam exam) {
//    if ( exam == null ) {
//      return null;
//    }
//
//    Long id = null;
//    String schoolClass = null;
//    String year = null;
//    Long profileNumber = null;
//    LocalDate date = null;
//
//    id = exam.getId();
//    schoolClass = exam.getSchoolClass();
//    year = exam.getYear();
//    profileNumber = exam.getProfileNumber();
//    date = exam.getDate();
//
//    Long patientId = null;
//    Long dentistId = null;
//    Long organizationId = null;
//    Long teethRecordId = null;
//    Long plaqueRecordId = null;
//    Long tartarRecordId = null;
//    List<Long> prescriptionItemIds = null;
//
//    ExamDTO examDTO = new ExamDTO( id, patientId, dentistId, organizationId, schoolClass, year,
// profileNumber, date, teethRecordId, plaqueRecordId, tartarRecordId, prescriptionItemIds );
//
//    return examDTO;
//  }
//
//  public List<ExamDTO> toDtoList(List<Exam> examList) {
//    if ( examList == null ) {
//      return null;
//    }
//
//    List<ExamDTO> list = new ArrayList<ExamDTO>( examList.size() );
//    for ( Exam exam : examList ) {
//      list.add( toDto( exam ) );
//    }
//
//    return list;
//  }
//
//  public Exam partialUpdate(ExamDTO examDTO, Exam exam) {
//    if ( examDTO == null ) {
//      return exam;
//    }
//
//    if ( examDTO.id() != null ) {
//      exam.setId( examDTO.id() );
//    }
//    if ( examDTO.schoolClass() != null ) {
//      exam.setSchoolClass( examDTO.schoolClass() );
//    }
//    if ( examDTO.year() != null ) {
//      exam.setYear( examDTO.year() );
//    }
//    if ( examDTO.profileNumber() != null ) {
//      exam.setProfileNumber( examDTO.profileNumber() );
//    }
//    if ( examDTO.date() != null ) {
//      exam.setDate( examDTO.date() );
//    }
//
//    return exam;
//  }
// }
