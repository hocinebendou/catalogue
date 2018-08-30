package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoCountry;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends PagingAndSortingRepository<NeoCountry, Long> {

	List<NeoCountry> findAll();

	NeoCountry findNeoCountryByName(@Param("country") String country);

	@Query( "MATCH (c: NeoCountry) " +
			"RETURN c.name")
	List<String> findAllCountryNames();
}
