package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.io.Serializable;

public record DiseaseDTO(
  Long id,
  String code,
  String name) implements Serializable {
}