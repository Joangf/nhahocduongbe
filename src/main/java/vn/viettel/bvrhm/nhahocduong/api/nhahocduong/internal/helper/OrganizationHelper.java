package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.helper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.OrganizationRepository;

/**
 * @author: longlb1
 * @since: 29-Sep-23
 */
@Component
public class OrganizationHelper {
  @Autowired OrganizationRepository organizationRepository;

  public List<String> getFlattenClassList(OrganizationDTO organizationDTO) {
    if (organizationDTO.getClasses() == null || organizationDTO.getClasses().isEmpty()) return null;
    return organizationDTO.getClasses().values().stream()
        .reduce(
            (classList, classList2) -> {
              classList.addAll(classList2);
              return classList;
            })
        .orElse(null);
  }

  public List<String> getDuplicateClassList(OrganizationDTO organizationDTO) {
    List<String> flattenClassList = getFlattenClassList(organizationDTO);
    Set<String> classes = new HashSet<>();

    if (isNull(flattenClassList) || flattenClassList.isEmpty()) return null;
    return flattenClassList.stream()
        .filter(clazz -> !classes.add(clazz.trim().toLowerCase()))
        .toList();
  }

  public String generateCode(OrganizationDTO organizationDTO) {
    StringBuilder codeBuilder = new StringBuilder();
    codeBuilder.append(String.format("%03d", Integer.parseInt(organizationDTO.getAreaCode())));

    // Get latest org code and increase 1, if not exist start with xxx001
    Organization latestOrganization =
        organizationRepository.findFirstByAreaCodeOrderByCodeDesc(organizationDTO.getAreaCode());
    int orgOrderNumber;
    if (nonNull(latestOrganization)) {
      orgOrderNumber = Integer.parseInt(latestOrganization.getCode().substring(3, 6));
    } else {
      orgOrderNumber = 0;
    }
    codeBuilder.append(String.format("%03d", orgOrderNumber + 1));

    return codeBuilder.toString();
  }
}
