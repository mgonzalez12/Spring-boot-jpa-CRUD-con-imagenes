package com.springboot.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.entity.Cliente;
import com.springboot.entity.Factura;
import com.springboot.entity.Producto;
import com.springboot.models.service.IClienteService;

@Controller
@RequestMapping("factura")
@SessionAttributes("factura")
public class FacturaController {

	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/form/{clienteId}")
	public String crear(@PathVariable(value="clienteId") Long clienteId, Model model, RedirectAttributes flash) {
		 
		Cliente cliente = clienteService.findOne(clienteId);
		
		if(cliente == null) {
			flash.addAttribute("error","El cliente no existe en la base de datos");
			return "redirect:/listar";
		}
		
		 Factura factura = new Factura();
		 factura.setCliente(cliente);
		 
		 model.addAttribute("factura", factura);
		 model.addAttribute("titulo","Crear Factura");
		
		
		return "factura/form";
	}
	
	@GetMapping(value = "/cargar-productos/{term}", produces = { "application/json" })
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String term) {
		return clienteService.findByNombre(term);
	}
}
