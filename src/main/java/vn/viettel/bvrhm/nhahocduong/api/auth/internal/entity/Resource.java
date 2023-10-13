package vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.BaseEntity;

/**
 * @author: longlb1
 * @since: 19-Sep-23
 */
@Entity
@Data
@NoArgsConstructor
public class Resource extends BaseEntity {
    @Id
    @GeneratedValue(generator = "resource_id_generator")
    @SequenceGenerator(
            name = "resource_id_generator",
            sequenceName = "nhahocduong_resource_id_seq",
            allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "api_base_path")
    private String apiBasePath;

    @Column(name = "ui_base_path")
    private String uiBasePath;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;
}
