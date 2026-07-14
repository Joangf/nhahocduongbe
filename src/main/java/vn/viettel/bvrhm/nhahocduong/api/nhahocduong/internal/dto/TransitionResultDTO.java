package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TransitionResultDTO {
  private String sessionId;
  private Long oldYearId;
  private Long newYearId;
  private String newYearName;
  private int promotedCount;
  private int graduatedCount;
  private List<String> warnings = new ArrayList<>();
  private boolean success;
  private String message;
}
