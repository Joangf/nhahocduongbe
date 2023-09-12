package vn.viettel.bvrhm.nhahocduong.api;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModulithArchitectureTest {
  ApplicationModules modules = ApplicationModules.of(ApiApplication.class);

  @Test
  void verifiesModularStructure() {
    modules.verify();
  }

  @Test
  void createModuleDocumentation() {
    new Documenter(modules).writeDocumentation().writeIndividualModulesAsPlantUml();
  }

  @Test
  void createApplicationModuleModel() {
    modules.forEach(System.out::println);
  }
}
