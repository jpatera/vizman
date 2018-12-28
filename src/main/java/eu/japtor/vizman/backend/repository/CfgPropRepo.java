package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.CfgProp;
import eu.japtor.vizman.backend.entity.Cin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgPropRepo extends JpaRepository<CfgProp, Long> {

    public CfgProp findByName(String name);

    public CfgProp findByNameOrderByOrd(String name);

}
