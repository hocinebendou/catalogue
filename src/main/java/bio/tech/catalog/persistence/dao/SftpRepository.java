package bio.tech.catalog.persistence.dao;

import bio.tech.catalog.persistence.model.NeoSftp;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface SftpRepository extends PagingAndSortingRepository<NeoSftp, Long> {

    NeoSftp findNeoSftpByUsername(@Param("username") String username);

}
