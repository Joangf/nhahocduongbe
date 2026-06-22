package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants;

/**
 * @author: longlb1
 * @since: 11-Oct-23
 */
public final class ResponseMessage {
  // Organization
  public static final String ORGANIZATION_DUPLICATE_CLASS = "Duplicate class(es): ";
  public static final String ORGANIZATION_NOT_FOUND_WITH_ID = "Can't find organization with ID ";
  public static final String ORGANIZATION_NOT_FOUND_WITH_CODE =
      "Can't find organization with code ";
  public static final String ORGANIZATION_CANT_DELETE_HAS_STUDENT =
      "Can't delete organization which has student";
  public static final String ORGANIZATION_CANT_DELETE_CLASS_HAS_STUDENT =
      "Can't delete class which has student";

  public static final String ORGANIZATION_CANT_FOUND_CLASS_OF_SCHOOL =
      "Not found any class of school with Code ";
  public static final String ORGANIZATION_CANT_FOUND_CLASS = "Can't found class ";

  // Exam
  public static final String EXAM_NOT_FOUND_WITH_ID = "Can't find exam with ID ";
  public static final String EXAM_NOT_FOUND_WITH_PATIENT_ID =
      "Can't find exam for the student with ID ";

  // Treatment Record
  public static final String TREATMENT_RECORD_NOT_FOUND_WITH_ID =
      "Can't find treatment record with ID ";

  // Patient
  public static final String PATIENT_CANT_DELETE_HAS_EXAMS = "Can't delete student who has exams";

  // User
  public static final String USER_WEAK_PASSWORD = "Weak password";
  public static final String USER_DUPLICATE_PHONE_NUMBER = "Phone number already exists";
}
