package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.OrganizationType;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DentistWithUserDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Dentist;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.DentistRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.DentistServiceImpl;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;

@DisplayName("DentistServiceImpl — Unit Tests")
@ExtendWith(MockitoExtension.class)
class DentistServiceImplUnitTest {

  @Mock DentistRepository dentistRepository;
  @Mock UserRepository userRepository;
  @InjectMocks DentistServiceImpl service;

  private User user(Long id, String username, String firstName, String lastName,
      Long orgId, OrganizationType orgType, Boolean status, Boolean registerStatus) {
    User u = new User();
    u.setId(id);
    u.setUsername(username);
    u.setFirstName(firstName);
    u.setLastName(lastName);
    u.setPhoneNumber("0909000001");
    u.setStatus(status);
    u.setRegisterStatus(registerStatus);
    if (orgId != null) {
      Organization org = new Organization();
      org.setId(orgId);
      org.setType(orgType);
      u.setOrganization(org);
    }
    return u;
  }

  @Nested
  @DisplayName("TC-01 getAllDentistsWithUserInfo()")
  class GetAllDentistsWithUserInfo {

    @Test
    @DisplayName("Trả về danh sách bác sĩ hợp lệ")
    void shouldReturnDentistList() {
      User dentist = user(1L, "dentist1", "Bác sĩ", "Nguyễn", null, null, true, true);
      when(userRepository.findUsersByRoleCode("DENTIST")).thenReturn(List.of(dentist));
      when(dentistRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
      when(dentistRepository.save(any(Dentist.class))).thenAnswer(i -> i.getArgument(0));

      List<DentistWithUserDTO> result = service.getAllDentistsWithUserInfo();

      assertThat(result).hasSize(1);
      assertThat(result.get(0).userId()).isEqualTo(1L);
      assertThat(result.get(0).fullName()).contains("Nguyễn Bác sĩ");
    }

    @Test
    @DisplayName("Bỏ qua user đã bị lock")
    void shouldSkipLockedUser() {
      User locked = user(1L, "locked", "Locked", "User", null, null, false, true);
      when(userRepository.findUsersByRoleCode("DENTIST")).thenReturn(List.of(locked));

      List<DentistWithUserDTO> result = service.getAllDentistsWithUserInfo();

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Bỏ qua user chưa được duyệt")
    void shouldSkipUnapprovedUser() {
      User unapproved = user(1L, "pending", "Pending", "User", null, null, true, false);
      when(userRepository.findUsersByRoleCode("DENTIST")).thenReturn(List.of(unapproved));

      List<DentistWithUserDTO> result = service.getAllDentistsWithUserInfo();

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Bỏ qua user thuộc trường học")
    void shouldSkipSchoolUser() {
      User schoolUser = user(1L, "teacher", "Teacher", "School", 1L, OrganizationType.SCHOOL, true, true);
      when(userRepository.findUsersByRoleCode("DENTIST")).thenReturn(List.of(schoolUser));

      List<DentistWithUserDTO> result = service.getAllDentistsWithUserInfo();

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Dùng lại dentist record nếu đã tồn tại")
    void shouldUseExistingDentistRecord() {
      User dentist = user(1L, "dentist1", "Bác sĩ", "Nguyễn", null, null, true, true);
      Dentist existing = new Dentist();
      existing.setId(5L);
      existing.setUserId(1L);

      when(userRepository.findUsersByRoleCode("DENTIST")).thenReturn(List.of(dentist));
      when(dentistRepository.findByUserId(1L)).thenReturn(List.of(existing));
      // Không gọi save vì đã có record

      List<DentistWithUserDTO> result = service.getAllDentistsWithUserInfo();

      assertThat(result).hasSize(1);
      assertThat(result.get(0).dentistId()).isEqualTo(5L);
      verify(dentistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Bỏ qua user bị null status")
    void shouldSkipUserWithNullStatus() {
      User u = new User();
      u.setId(1L);
      u.setUsername("test");
      u.setFirstName("Test");
      u.setLastName("User");
      u.setPhoneNumber("0909");
      u.setStatus(null);
      u.setRegisterStatus(true);
      when(userRepository.findUsersByRoleCode("DENTIST")).thenReturn(List.of(u));

      List<DentistWithUserDTO> result = service.getAllDentistsWithUserInfo();

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Bỏ qua user có registerStatus null")
    void shouldSkipUserWithNullRegisterStatus() {
      User u = new User();
      u.setId(1L);
      u.setUsername("test");
      u.setFirstName("Test");
      u.setLastName("User");
      u.setPhoneNumber("0909");
      u.setStatus(true);
      u.setRegisterStatus(null);
      when(userRepository.findUsersByRoleCode("DENTIST")).thenReturn(List.of(u));

      List<DentistWithUserDTO> result = service.getAllDentistsWithUserInfo();

      assertThat(result).isEmpty();
    }
  }
}
