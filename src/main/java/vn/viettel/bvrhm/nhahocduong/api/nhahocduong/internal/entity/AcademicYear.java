package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.AcademicYearStatus;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter.AcademicYearStatusConverter;

@Data
@Entity
@Table(name = "academic_year")
public class AcademicYear {

  @Id
  @GeneratedValue(generator = "academic_year_id_generator")
  @SequenceGenerator(
      name = "academic_year_id_generator",
      sequenceName = "academic_year_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @Convert(converter = AcademicYearStatusConverter.class)
  @Column(name = "status", nullable = false)
  private AcademicYearStatus status = AcademicYearStatus.UPCOMING;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "updated_date")
  private LocalDateTime updatedDate;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_by")
  private String updatedBy;
}
