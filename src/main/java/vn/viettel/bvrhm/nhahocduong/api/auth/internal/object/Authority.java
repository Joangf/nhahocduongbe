package vn.viettel.bvrhm.nhahocduong.api.auth.internal.object;

import org.springframework.security.core.GrantedAuthority;

public class Authority implements GrantedAuthority {

  private final String authorityName;

  public Authority(String authorityName) {
    this.authorityName = authorityName;
  }

  public static Authority fromName(String authorityName) {
    return new Authority(authorityName);
  }

  @Override
  public String getAuthority() {
    return this.authorityName;
  }
}
