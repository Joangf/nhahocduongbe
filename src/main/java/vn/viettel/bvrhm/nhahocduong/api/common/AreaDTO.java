package vn.viettel.bvrhm.nhahocduong.api.common;

import jakarta.validation.constraints.NotNull;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.Area;

import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link Area} entity
 */
public record AreaDTO(
  Integer id,
  @NotNull String code,
  String name,
  Integer type,
  List<AreaDTO> ancestor
) implements Serializable {}