package vn.viettel.bvrhm.nhahocduong.api.user.internal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import lombok.EqualsAndHashCode;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.BaseEntity;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.RefreshToken;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_USER")
public class User extends BaseEntity {
  @ManyToMany
  @JoinTable(
      name = "user_role_mapping",
      joinColumns = {@JoinColumn(name = "user_id")},
      inverseJoinColumns = {@JoinColumn(name = "role_id")})
  List<Role> roleList;

  @Id
  @GeneratedValue(generator = "user_id_generator")
  @SequenceGenerator(
      name = "user_id_generator",
      sequenceName = "user_user_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "username")
  private String username;

  @Column(name = "email")
  private String email;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "password")
  private String password;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "birthdate")
  private LocalDate birthdate;

  @ManyToOne
  @JoinColumn(name = "organization")
  private Organization organization;

  @OneToMany(mappedBy = "user")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<RefreshToken> refreshTokens;

  @Column(name = "register_status")
  private Boolean registerStatus;
}
