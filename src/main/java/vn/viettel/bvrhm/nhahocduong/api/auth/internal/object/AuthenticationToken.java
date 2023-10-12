package vn.viettel.bvrhm.nhahocduong.api.auth.internal.object;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class AuthenticationToken extends AbstractAuthenticationToken {
  private final Object principal;

  private Object credentials;

  /**
   * Creates a token with the supplied array of authorities.
   *
   * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal represented
   *     by this authentication object.
   * @param principal
   * @param credentials
   */
  public AuthenticationToken(
      Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    // TODO maybe review why sometimes authenticated need to be false
    setAuthenticated(true);
  }

  public static AuthenticationToken authenticated(
      Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    return new AuthenticationToken(principal, credentials, authorities);
  }

  @Override
  public Object getCredentials() {
    return this.credentials;
  }

  @Override
  public Object getPrincipal() {
    return this.principal;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    // TODO check why the default is so weird
    super.setAuthenticated(isAuthenticated);
  }

  @Override
  public void eraseCredentials() {
    super.eraseCredentials();
    this.credentials = null;
  }
}
