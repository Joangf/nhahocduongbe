package vn.viettel.bvrhm.nhahocduong.api.system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.viettel.bvrhm.nhahocduong.api.auth.JwtService;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private JwtService jwtService;

  @Autowired
  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    final String jwtString;
    final String userId;

    // No JWT in Authorization header -> continue down the chain
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    jwtString = authHeader.substring(7);

    // Invalid JWT token -> continue down the chain
    if (!jwtService.isTokenValid(jwtString)) {
      filterChain.doFilter(request, response);
      return;
    }

    userId = jwtService.extractUserId(jwtString);
    // TODO extract roles
    // Option 1: get from JWT token => Use this for now
    // Option 2: load from database => Revise later
    List<String> authorityStrList = jwtService.extractRoles(jwtString);

    List<Authority> authorityList = authorityStrList.stream().map(Authority::fromName).toList();

    // populate Authentication object into SecurityContext
    Authentication authentication = new AuthenticationToken(userId, null, authorityList);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }
}
