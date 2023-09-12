package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import lombok.Data;

import java.util.List;

@Data
public class ToothCondition {
  // Có 2 trường hợp thông tin răng:
  // Trường hợp 1: Răng chỉ có 1 vấn đề (chọn 1), sau đó chọn vị trí xảy ra vấn đề (chọn nhiều cho 1
  // vấn đề)
  // sau đó chọn 1 phương án điều trị (chọn 1 cho 1 vấn đề)
  // Trường hợp 2: Răng có nhiều vấn đề (chọn nhiều), với mỗi vấn đề chọn nhiều vị trí xảy ra
  // (chọn nhiều), với mỗi vấn đề chọn 1 phương án điều trị
  // BS BVRHM confirm ngày 8/6/2023 chọn phương án 1.

  private ToothProblem problem;
  private List<ToothSide> locations;

  // Điều trị
  private ToothTreatment treatment = ToothTreatment.CROWN;
}
