package td.model.dao;


import org.springframework.data.repository.CrudRepository;

import td.model.domain.ClientDTO;

public interface ClientRepository  extends CrudRepository<ClientDTO, String> {
   Iterable<ClientDTO> findAll();
   
   ClientDTO findByServiceName(String serviceName);
   
   long count();
}