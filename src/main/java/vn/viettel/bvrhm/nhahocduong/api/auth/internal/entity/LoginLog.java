package vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "login_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username")
  private String username;

  @Column(name = "login_time")
  private LocalDateTime loginTime;

  @Column(name = "status")
  private Boolean status; // SUCCESS or FAILED

  @Column(name = "logout_time")
  private LocalDateTime logoutTime;

  @Column(name = "phone_number")
  private String phoneNumber;
}
