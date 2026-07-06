package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DentistWithUserDTO;

public interface DentistService {

  /** Lấy danh sách tất cả bác sĩ kèm thông tin User (tên, SĐT) */
  List<DentistWithUserDTO> getAllDentistsWithUserInfo();
}
