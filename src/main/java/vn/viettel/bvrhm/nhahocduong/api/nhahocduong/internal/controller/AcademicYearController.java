package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.AcademicYearDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TransitionResultDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.YearTransitionRequest;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.AcademicYearService;

@RestController
@RequestMapping("/api/academic-years")
public class AcademicYearController {

  @Autowired
  private AcademicYearService academicYearService;

  // ── CRUD ──
  @GetMapping
  public ResponseEntity<List<AcademicYearDTO>> getAll() {
    return ResponseEntity.ok(academicYearService.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<AcademicYearDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(academicYearService.getById(id));
  }

  @GetMapping("/current")
  public ResponseEntity<AcademicYearDTO> getCurrentYear() {
    return ResponseEntity.ok(academicYearService.getCurrentYear());
  }

  @PostMapping
  public ResponseEntity<AcademicYearDTO> create(@RequestBody AcademicYearDTO dto) {
    return ResponseEntity.ok(academicYearService.create(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<AcademicYearDTO> update(@PathVariable Long id, @RequestBody AcademicYearDTO dto) {
    return ResponseEntity.ok(academicYearService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    academicYearService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // ── Transition ──
  @PostMapping("/validate/{currentYearId}")
  public ResponseEntity<List<String>> validateBeforeTransition(@PathVariable Long currentYearId) {
    return ResponseEntity.ok(academicYearService.validateBeforeTransition(currentYearId));
  }

  @PostMapping("/transition")
  public ResponseEntity<TransitionResultDTO> transition(@RequestBody YearTransitionRequest request) {
    return ResponseEntity.ok(academicYearService.transitionToNewYear(request));
  }

  @PostMapping("/rollback/{sessionId}")
  public ResponseEntity<TransitionResultDTO> rollback(@PathVariable String sessionId) {
    return ResponseEntity.ok(academicYearService.rollbackTransition(sessionId));
  }
}
