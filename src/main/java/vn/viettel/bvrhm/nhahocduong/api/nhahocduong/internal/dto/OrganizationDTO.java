package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

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

}