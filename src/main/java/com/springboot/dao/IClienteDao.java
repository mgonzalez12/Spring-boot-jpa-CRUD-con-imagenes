package com.springboot.dao;


import org.springframework.data.repository.PagingAndSortingRepository;

import com.springboot.entity.Cliente;

public interface IClienteDao extends  PagingAndSortingRepository<Cliente, Long>{
	


}
