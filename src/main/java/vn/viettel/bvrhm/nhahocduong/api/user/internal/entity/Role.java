package vn.viettel.bvrhm.nhahocduong.api.user.internal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.BaseEntity;

import java.util.List;

/**
 * @author: longlb1
 * @since: 19-Sep-23
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_ROLE")
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(generator = "role_id_generator")
    @SequenceGenerator(
            name = "role_id_generator",
            sequenceName = "user_role_id_seq",
            allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "roleList")
    List<User> userList;
}
