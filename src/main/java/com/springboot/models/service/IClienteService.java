package com.springboot.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.entity.Cliente;
import com.springboot.entity.Factura;
import com.springboot.entity.Producto;

public interface IClienteService {

	public List<Cliente> findAll();
	
	public Page<Cliente> findAll(Pageable pageable);

	public void save(Cliente cliente);
	
	public Cliente findOne(Long id);
	
	public void delete(Long id);
	
	// lista de productos buscado por nombre para autocomplete
	public List<Producto> findByNombre(String term);
	
	public void saveFactura(Factura factura);
}
