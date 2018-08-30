package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoSample;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

// TODO: NOT USED. WE ARE USING SPECIMENREPOSITORY

public interface SampleRepository extends PagingAndSortingRepository<NeoSample, Long> {
	// count number of studies
	String queryCount = "MATCH (s:NeoSpecimen) RETURN count(s)";
	@Query(queryCount)
	int countSamples();
	
	// return study samples using study acronym and design samples (case or control)
	String queryCase = "MATCH (s:NeoStudy {acronym: {acronym}}) -[r1:HAS_SAMPLE]-> (p:NeoSample) -[r2:HAS_CHARACTER]-> (c:NeoCharacter {name: {name}}) RETURN p";
	@Query(queryCase)
	Collection<NeoSample> samplesByDesign(@Param("acronym") String acronym, @Param("name") String name);
	
	// return samples
	Collection<NeoSample> findAll();
	
	// find samples by character
	String queryCharacter = "MATCH (c: NeoCharacter {name: {name}}) <-[r:HAS_CHARACTER]- (s: NeoSample) RETURN s, c, r";
	@Query(queryCharacter)
	Collection<NeoSample> samplesByCharacter(@Param("name") String name);
	
	// find samples by gender
	String queryGender = "MATCH (g: NeoGender {name: {name}}) <-[r:HAS_GENDER]- (s: NeoSample) RETURN s, g, r";
	@Query(queryGender)
	Collection<NeoSample> samplesByGender(@Param("name") String name);
	
	// find samples by country
	String queryCountry = "MATCH (c: NeoCountry {name: {name}}) <-[r:HAS_COUNTRY]- (s:NeoSample) RETURN s, c, r";
	@Query(queryCountry)
	Collection<NeoSample> samplesByCountry(@Param("name") String name);
	
	// find samples by specimen type
	String querySpecType = "MATCH (t: NeoSpecType {name: {name}}) <-[r:HAS_SPECTYPE]- (s:NeoSample) RETURN s, t, r";
	@Query(querySpecType)
	Collection<NeoSample> samplesBySpecType(@Param("name") String name);
}
