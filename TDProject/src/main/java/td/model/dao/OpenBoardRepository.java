package td.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import td.model.domain.OpenBoardDTO;

public interface OpenBoardRepository extends ElasticsearchRepository<OpenBoardDTO, String> {

	Page<OpenBoardDTO> findAll(Pageable pageable);
	long count();
}
