package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoGender;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;

public interface GenderRepository extends PagingAndSortingRepository<NeoGender, Long> {
	
	// Return country nodes
	Collection<NeoGender> findAll();

	// query all gender names
	String queryGenderNames = "MATCH (g: NeoGender) " +
                              "WHERE g.name IS NOT NULL " +
							  "RETURN g.name";
	@Query(queryGenderNames)
	Collection<String> allGenderNames();
}
