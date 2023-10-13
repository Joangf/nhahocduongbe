package vn.viettel.bvrhm.nhahocduong.api.user.internal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_USER")
public class User {
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

    @ManyToMany
    @JoinTable(
            name = "user_role_mapping",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    List<Role> roleList;
}
