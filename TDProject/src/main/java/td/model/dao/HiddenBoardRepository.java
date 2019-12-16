package td.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import td.model.domain.HiddenBoardDTO;

public interface HiddenBoardRepository extends ElasticsearchRepository<HiddenBoardDTO, String> {
	
	Page<HiddenBoardDTO> findAllOrderByHeart(Pageable pageable);
	long count();
}
