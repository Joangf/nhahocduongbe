package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import java.util.Map;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.AcademicYearDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TransitionResultDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.YearTransitionRequest;

public interface AcademicYearService {

  // CRUD
  List<AcademicYearDTO> getAll();
  AcademicYearDTO getById(Long id);
  AcademicYearDTO getCurrentYear();
  AcademicYearDTO create(AcademicYearDTO dto);
  AcademicYearDTO update(Long id, AcademicYearDTO dto);
  void delete(Long id);

  // Transition workflow
  List<String> validateBeforeTransition(Long currentYearId);
  TransitionResultDTO transitionToNewYear(YearTransitionRequest request);
  TransitionResultDTO rollbackTransition(String sessionId);

  // History
  List<Map<String, Object>> getTransitionHistory();
}
