package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.config;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.PlaqueCondition;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.TartarCondition;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.*;

@Component
public class SpringDataRestConfig implements RepositoryRestConfigurer {

  public void configureRepositoryRestConfiguration(
      RepositoryRestConfiguration config, CorsRegistry cors) {
    config.setRepositoryDetectionStrategy(
        RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED);
    config.setDefaultMediaType(MediaType.APPLICATION_JSON);
    config.useHalAsDefaultJsonMediaType(false);
    config.exposeIdsFor(
        Dentist.class,
        Exam.class,
        Medication.class,
        Organization.class,
        Patient.class,
        PlaqueCondition.class,
        PlaqueRecord.class,
        TartarCondition.class,
        TartarRecord.class,
        TeethRecord.class);
  }
}
