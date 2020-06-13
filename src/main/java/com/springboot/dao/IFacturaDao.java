package com.springboot.dao;

import org.springframework.data.repository.CrudRepository;

import com.springboot.entity.Factura;

public interface IFacturaDao extends CrudRepository<Factura, Long> {

}
