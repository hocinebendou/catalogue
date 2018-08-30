package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoRequest;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;

/**
 * Created by hocine on 2017/12/17.
 */
public interface RequestRepository extends PagingAndSortingRepository<NeoRequest, Long> {

    Collection<NeoRequest> findAllByStatus(String status);

    Collection<NeoRequest> findAll();
}
