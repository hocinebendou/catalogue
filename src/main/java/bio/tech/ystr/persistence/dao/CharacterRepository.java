package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoCharacter;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CharacterRepository extends PagingAndSortingRepository<NeoCharacter, Long> {
	
	// Return country nodes
	Collection<NeoCharacter> findAll();
	
}
