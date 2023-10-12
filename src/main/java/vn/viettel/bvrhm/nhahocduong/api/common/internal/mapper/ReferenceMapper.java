package vn.viettel.bvrhm.nhahocduong.api.common.internal.mapper;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.ObjectFactory;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Component;

/**
 * @author: longlb1
 * @since: 05-Oct-23
 */
@Component
public class ReferenceMapper {

  @PersistenceContext private EntityManager entityManager;

  @ObjectFactory
  public <T> T map(@NonNull final Long id, @TargetType Class<T> type) {
    return entityManager.getReference(type, id);
  }
}
