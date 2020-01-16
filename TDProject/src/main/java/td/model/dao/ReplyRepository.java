package td.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import td.model.domain.ReplyDTO;

public interface ReplyRepository extends ElasticsearchRepository<ReplyDTO, String> {

//	ReplyDTO findByRepBoardId(String userId, String repBoardId);
	
	long countByRepBoardId(String repBoardId);

	Page<ReplyDTO> findByRepBoardId(Pageable pageable, String repBoardId);

	ReplyDTO findByUserIdAndRepBoardId(String userId, String repBoardId);

	Iterable<ReplyDTO> findByPlusHeartUserId(String plusHeartUserId);

}