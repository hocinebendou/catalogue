package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoStudy;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hocine on 2017/04/17.
 */
public interface UploadRepository extends PagingAndSortingRepository<NeoStudy, Long> {}
