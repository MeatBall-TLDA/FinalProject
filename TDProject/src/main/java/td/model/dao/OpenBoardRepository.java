package td.model.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import td.model.domain.HiddenBoardDTO;
import td.model.domain.OpenBoardDTO;

public interface OpenBoardRepository extends ElasticsearchRepository<OpenBoardDTO, String> {

	Page<OpenBoardDTO> findAll(Pageable pageable);

	Page<OpenBoardDTO> findByHashtagContaining(Pageable pageable, String hastag);

	Page<OpenBoardDTO> findByCategoryContaining(Pageable pageable, String category);

	long countByHashtagContaining(String hastag);

	long countByCategoryContaining(String category);

	long count();
	
	Optional<OpenBoardDTO> findById(String id);
}
