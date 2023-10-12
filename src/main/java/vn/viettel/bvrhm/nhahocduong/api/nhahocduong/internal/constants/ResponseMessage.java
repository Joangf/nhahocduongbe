package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants;

/**
 * @author: longlb1
 * @since: 11-Oct-23
 */
public final class ResponseMessage {
    // Organization
    public final static String ORGANIZATION_DUPLICATE_CLASS = "Duplicate class(es): ";
    public final static String ORGANIZATION_NOT_FOUND_WITH_ID = "Can't find organization with ID ";
    public final static String ORGANIZATION_CANT_DELETE_HAS_STUDENT ="Can't delete organization which has student";
    public final static String ORGANIZATION_CANT_DELETE_CLASS_HAS_STUDENT = "Can't delete class which has student";

    // Exam
    public final static String EXAM_NOT_FOUND_WITH_ID = "Can't find exam with ID ";
    public final static String EXAM_NOT_FOUND_WITH_PATIENT_ID = "Can't find exam for the student with ID ";
}
