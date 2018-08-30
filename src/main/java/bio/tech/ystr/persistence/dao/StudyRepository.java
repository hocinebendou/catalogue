package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoStudy;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public interface StudyRepository extends PagingAndSortingRepository<NeoStudy, Long> {

	List<NeoStudy> findAll();
	NeoStudy findByAcronym(String title);

	// query count studies
	String queryCount = "MATCH (s:NeoStudy) RETURN count(s)";
	@Query(queryCount)
	int countStudies();

	@Query( "MATCH (s:NeoStudy {acronym: {acronym}})-[:HAS_DATASET]->(d:NeoDataSet) WITH d " +
			"MATCH (d)-[:HAS_FILETYPE]->(f:NeoFileType) " +
			"MATCH (d)-[:HAS_DATAGEN]->(g:NeoDataGen) " +
			"MATCH (d)-[:HAS_TECHNOLOGY]->(t:NeoTechnology) " +
			"RETURN d, f, g, t" )
    Iterable<Map<String,Object>> studyDataSets(@Param("acronym") String acronym);

	/* ----------------------------------------------------------------------------------- */

    // query by acronyms
	@Query( "MATCH (s:NeoStudy) " +
			"WHERE s.acronym IN {acronyms} " +
			"RETURN s" )
    List<NeoStudy> findByAcronyms(@Param("acronyms") List<String> acronyms);

	// query by diseases
	@Query( "MATCH (s:NeoStudy) " +
			"WHERE s.disease IN {diseases} " +
			"RETURN s.acronym" )
	List<String> findByDiseases(@Param("diseases") List<String> diseases);

	// query by designs
	@Query( "MATCH (s:NeoStudy)-[:STUDY_DESIGN]->(d:NeoDesign) " +
			"WHERE d.name IN {names} " +
			"RETURN s.acronym" )
	List<String> findByDesigns(@Param("names") List<String> names);

	// query by acronyms and diseases
	@Query( "MATCH (s:NeoStudy) " +
			"WHERE s.acronym IN {acronyms} AND " +
			"s.disease IN {diseases} " +
			"RETURN s.acronym")
	List<String> findByAcronymsAndDiseases(@Param("acronyms") List<String> acronyms,
                                           @Param("diseases") List<String> diseases);

	// query by acronyms and designs
	@Query( "MATCH (s:NeoStudy) " +
			"WHERE s.acronym IN {acronyms} " +
			"MATCH (s)-[]->(d:NeoDesign) " +
			"WHERE d.name IN {designs} " +
			"RETURN s.acronym")
	List<String> findByAcronymsAndDesigns(@Param("acronyms") List<String> acronyms,
                                          @Param("designs") List<String> names);

	// query by diseases and designs
	@Query( "MATCH (s:NeoStudy) " +
			"WHERE s.disease IN {diseases} " +
			"MATCH (s)-[:STUDY_DESIGN]->(d:NeoDesign) " +
			"WHERE d.name IN {names} " +
			"RETURN s.acronym" )
    List<String> findByDiseasesAndDesigns(@Param("diseases") List<String> diseases,
                                          @Param("names") List<String> names);

	// query by acronyms, diseases and designs
	@Query( "MATCH (s:NeoStudy)" +
			"WHERE s.acronym IN {acronyms} AND " +
			"s.disease IN {diseases}" +
			"MATCH (s)-[]->(d:NeoDesign) " +
			"WHERE d.name IN {designs} " +
			"RETURN s.acronym")
	List<String> findByAcronymsAndDiseasesAndDesigns(@Param("acronyms") List<String> acronyms,
                                                     @Param("diseases") List<String> diseases,
                                                     @Param("designs") List<String> designs);

	/*------------------------------------------------------------------------------------------*/

    // query by ethnicity
    String queryByEthnicity = "MATCH (s: NeoStudy)-[]->(p:NeoParticipant)-[]->(e:NeoEthnicity) " +
                              "WHERE e.name IN {names} " +
                              "RETURN s";
    @Query(queryByEthnicity)
    Collection<NeoStudy> findStudiesByEthnicity(@Param("names") List<String> names);

    // query all acronyms
	@Query("MATCH (s:NeoStudy) " +
			"RETURN s.acronym")
	List<String> findAllStudyAcronyms();

	// query all disease names
	@Query( "MATCH (s:NeoStudy) " +
			"RETURN DISTINCT s.disease ")
    List<String> findAllStudyDiseases();

}
