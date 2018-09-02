package bio.tech.catalog.persistence.dao;

import bio.tech.catalog.persistence.model.NeoSpecType;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpecTypeRepository extends PagingAndSortingRepository<NeoSpecType, Long> {

	List<NeoSpecType> findAll();

	NeoSpecType findNeoSpecTypeByName(@Param("name") String name);

	@Query( "MATCH (t: NeoSpecType) " +
			"RETURN t.name")
	List<String> findAllSpecimenTypeNames();
}
