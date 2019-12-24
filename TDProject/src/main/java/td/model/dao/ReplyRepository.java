package td.model.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import td.model.domain.ReplyDTO;

public interface ReplyRepository extends ElasticsearchRepository<ReplyDTO, String> {

//	ReplyDTO findByRepBoardId(String userId, String repBoardId);

	Iterable<ReplyDTO> findByRepBoardId(String repBoardId);
	
	// findByNickName으로 바꿔야됨
	ReplyDTO findByUserIdAndRepBoardId(String userId, String repBoardId);
	Iterable<ReplyDTO> findByPlusHeartUserId(String plusHeartUserId);

}
