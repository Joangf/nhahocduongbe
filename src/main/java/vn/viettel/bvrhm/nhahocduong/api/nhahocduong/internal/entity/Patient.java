package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Ethnic;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter.EthnicJpaConverter;

@Entity
@Data
@Table(name = "nhahocduong_patient")
public class Patient {
  @Id
  @GeneratedValue(generator = "patient_id_generator")
  @SequenceGenerator(
      name = "patient_id_generator",
      sequenceName = "nhahocduong_patient_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "code")
  private String code;

  @Column(name = "health_insurance_number")
  private String healthInsuranceNumber;

  @Column(name = "gender")
  private Integer gender;

  @Column(name = "birthdate")
  private LocalDate birthDate;

  @Convert(converter = EthnicJpaConverter.class)
  @Column(name = "ethnic")
  private Ethnic ethnic;

  @Column(name = "area_type")
  private String areaType; // TODO change to localeClassification

  // TODO nationality

  @Column(name = "address_line")
  private String addressLine;

  @Column(name = "phone_number")
  private String phoneNumber;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "nhahocduong_patient_disease",
      joinColumns = {@JoinColumn(name = "patient_id")},
      inverseJoinColumns = {@JoinColumn(name = "disease_id")})
  private List<Disease> chronicConditions;

  @ManyToOne
  @JoinColumn(name = "organization")
  private Organization organization;

  @Column(name = "school_class")
  private String schoolClass;

  @Column(name = "national_id_num")
  private String nationalIdNum;

  @Column(name = "care_taker")
  private String careTaker;

  @Column(name = "status")
  private Boolean status;
}
