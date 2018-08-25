package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.Privilege;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PrivilegeRepository extends PagingAndSortingRepository<Privilege, Long> {

    Privilege findByName(String name);

    @Override
    void delete(Privilege privilege);
}
