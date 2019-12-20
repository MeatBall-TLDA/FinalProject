package td.model.dao;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import td.model.domain.HiddenBoardDTO;
import td.model.domain.ReplyDTO;

public interface HiddenBoardRepository extends ElasticsearchRepository<HiddenBoardDTO, String> {
	
	Page<HiddenBoardDTO> findAllOrderByHeart(Pageable pageable);
	long count();


	Page<HiddenBoardDTO> findByHashtagContaining(Pageable pageable, String hastag);
	
	Page<HiddenBoardDTO> findByCategoryContaining(Pageable pageable, String category);
	
//	@Query(value = "{\"query\":{\"match_all\":{}},\"_source\":[\"hashtag\"]}")
//	String findAllHashtag();

}

