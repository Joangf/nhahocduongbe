package vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.BaseEntity;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.Role;

import java.util.List;

/**
 * @author: longlb1
 * @since: 20-Sep-23
 */
@Entity
@Data
@NoArgsConstructor
public class Rule extends BaseEntity {
    @Id
    @GeneratedValue(generator = "rule_id_generator")
    @SequenceGenerator(
            name = "rule_id_generator",
            sequenceName = "nhahocduong_rule_id_seq",
            allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "rule_role_mapping",
            joinColumns = {@JoinColumn(name = "rule_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    List<Role> roles;

    @ManyToOne
    private Policy policy;
}