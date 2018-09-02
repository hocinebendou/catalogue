package bio.tech.catalog.persistence.dao;

import bio.tech.catalog.persistence.model.NeoDesign;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;

public interface DesignRepository extends PagingAndSortingRepository<NeoDesign, Long> {
	
	Collection<NeoDesign> findAll();

	// query study design names
    String queryDesignNames = "MATCH (d: NeoDesign) " +
                              "RETURN d.name";
    @Query(queryDesignNames)
    Collection<String> allStudyDesignNames();
}
