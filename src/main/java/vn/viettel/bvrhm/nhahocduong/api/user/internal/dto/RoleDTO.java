package vn.viettel.bvrhm.nhahocduong.api.user.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author: longlb1
 * @since: 19-Sep-23
 */
public record RoleDTO(
    String id, String code, String name, @JsonIgnore Boolean status, String description) {}
