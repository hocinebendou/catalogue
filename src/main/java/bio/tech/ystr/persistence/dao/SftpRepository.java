package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoSftp;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface SftpRepository extends PagingAndSortingRepository<NeoSftp, Long> {

    NeoSftp findNeoSftpByUsername(@Param("username") String username);

}
