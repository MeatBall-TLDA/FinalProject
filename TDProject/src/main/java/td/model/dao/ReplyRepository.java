package td.model.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import td.model.domain.ReplyDTO;

public interface ReplyRepository extends ElasticsearchRepository<ReplyDTO, String> {

}
