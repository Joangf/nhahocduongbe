package vn.viettel.bvrhm.nhahocduong.api.auth.internal.object;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@NoArgsConstructor
public class UserAuthDetails implements UserDetails, CredentialsContainer {

  private static final Logger logger = LoggerFactory.getLogger(UserAuthDetails.class);
  private Long userId;
  private String username;
  private Set<GrantedAuthority> authorities;
  private boolean accountNonExpired;
  private boolean accountNonLocked;
  private boolean credentialsNonExpired;
  private boolean enabled;
  private Boolean registerStatus;
  private String password;

  public UserAuthDetails(
      Long userId,
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities) {
    this(userId, username, password, true, true, true, true, authorities);
  }

  public UserAuthDetails(
      Long userId,
      String username,
      String password,
      boolean accountNonExpired,
      boolean accountNonLocked,
      boolean credentialsNonExpired,
      boolean enabled,
      Collection<? extends GrantedAuthority> authorities) {
    this.userId = userId;
    this.username = username;
    this.password = password;
    this.accountNonExpired = accountNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.credentialsNonExpired = credentialsNonExpired;
    this.enabled = enabled;
    // FIXME: maybe need to be sorted like in org.springframework.security.core.userdetails.User
    this.authorities = Collections.unmodifiableSet((Set<GrantedAuthority>) authorities);
  }

  @Override
  public Collection<GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  public Long getUserId() {
    return this.userId;
  }

  @Override
  public String getPassword() {
    return this.username;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public void eraseCredentials() {
    this.password = null;
  }
}
