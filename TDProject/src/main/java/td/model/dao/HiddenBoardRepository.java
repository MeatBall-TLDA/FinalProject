package td.model.dao;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import td.model.domain.HiddenBoardDTO;

public interface HiddenBoardRepository extends ElasticsearchRepository<HiddenBoardDTO, String> {
   
   Page<HiddenBoardDTO> findAll(Pageable pageable);
   
   long count();

   Page<HiddenBoardDTO> findByHashtagContaining(Pageable pageable, String hastag);
   
   Page<HiddenBoardDTO> findByCategoryContaining(Pageable pageable, String category);
   
   long countByHashtagContaining(String hastag);
   
   long countByCategoryContaining(String category);
   
   List<HiddenBoardDTO> findByOpenDate(String today);

   Optional<HiddenBoardDTO> findById(String id);
   
}

