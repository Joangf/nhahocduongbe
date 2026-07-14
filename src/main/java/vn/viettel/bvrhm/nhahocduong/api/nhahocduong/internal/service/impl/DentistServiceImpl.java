package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.OrganizationType;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DentistWithUserDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Dentist;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.DentistRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.DentistService;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;

@Service
public class DentistServiceImpl implements DentistService {

  @Autowired private DentistRepository dentistRepository;
  @Autowired private UserRepository userRepository;

  @Override
  @Transactional
  public List<DentistWithUserDTO> getAllDentistsWithUserInfo() {
    // Lấy tất cả user có role DENTIST và status = true (chưa bị khóa)
    List<User> dentistUsers = userRepository.findUsersByRoleCode("DENTIST");

    List<DentistWithUserDTO> result = new ArrayList<>();
    for (User user : dentistUsers) {
      // Lọc bỏ các tài khoản đã bị khóa (status != true) hoặc chưa được duyệt
      if (user.getStatus() == null || !user.getStatus()) {
        continue;
      }
      if (user.getRegisterStatus() == null || !user.getRegisterStatus()) {
        continue;
      }
      // Lọc bỏ tài khoản thuộc trường học (không phải bác sĩ thực sự)
      if (user.getOrganization() != null
          && OrganizationType.SCHOOL.equals(user.getOrganization().getType())) {
        continue;
      }

      // Kiểm tra xem user đã có record trong nhahocduong_dentist chưa
      List<Dentist> existingDentists = dentistRepository.findByUserId(user.getId());
      Dentist dentist;
      if (existingDentists.isEmpty()) {
        // Tự động tạo record dentist mới cho user này
        dentist = new Dentist();
        dentist.setUserId(user.getId());
        dentist.setTitle(user.getLastName() + " " + user.getFirstName());
        dentist = dentistRepository.save(dentist);
      } else {
        dentist = existingDentists.get(0);
      }

      // Tạo fullName và phoneNumber từ User
      String firstName = user.getFirstName() != null ? user.getFirstName() : "";
      String lastName = user.getLastName() != null ? user.getLastName() : "";
      String fullName = (lastName + " " + firstName).trim();
      String phoneNumber = user.getPhoneNumber() != null ? user.getPhoneNumber() : "";

      result.add(new DentistWithUserDTO(
          dentist.getId(),
          user.getId(),
          fullName,
          phoneNumber));
    }

    return result;
  }
}
