package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Grade;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.OrganizationType;

import java.util.List;
import java.util.Map;

@Data
public class OrganizationDTO{
    private Long id;
    private String name;
    private String address;
    private String code;
    private String areaCode;
    private Map<Grade, List<String>> classes;
    private OrganizationType type = OrganizationType.SCHOOL;
    @JsonIgnore
    private Boolean status = true;
    @JsonIgnore
    public List<String> getFlattenClassList() {
        if (this.getClasses().isEmpty()) return null;
        return this.getClasses()
                .values()
                .stream()
                .reduce((classList, classList2) -> {
                    classList.addAll(classList2);
                    return classList;
                })
                .orElse(null);
    }
}