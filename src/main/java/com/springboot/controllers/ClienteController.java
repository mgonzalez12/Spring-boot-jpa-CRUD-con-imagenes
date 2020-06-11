package com.springboot.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.entity.Cliente;
import com.springboot.models.service.IClienteService;
import com.springboot.util.PageRender;
//import com.sun.javafx.collections.MappingChange.Map;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename){
		Path pathFoto = Paths.get("uploads").resolve(filename).toAbsolutePath();
		Resource recurso = null;
		try {
			recurso = new UrlResource(pathFoto.toUri());
			if(!recurso.exists() && !recurso.isReadable()) {
				throw new RuntimeException("Error: No se puede cargar la imagen: " + pathFoto.toString());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=\""+ recurso.getFilename()+"\"")
				.body(recurso);
	}
	
	
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value="id") Long id, Map<String,Object> model,RedirectAttributes flash) {
		
		Cliente cliente= clienteService.findOne(id);
		if(cliente == null) {
			flash.addFlashAttribute("error","El Cliente no existe");
			return "redirect:/listar";
		}
		
		model.put("cliente",cliente);
		model.put("titulo","Detalle Cliente");
		
		return "ver";
	}

	@RequestMapping(value = "/listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name="page", defaultValue = "0") int page, Model model) {
		
		Pageable pageRequest = PageRequest.of(page,4);
		
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender = new PageRender<>("/listar",clientes);
				
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page",pageRender);
		return "listar";
	}
	
	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model) {

		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");
		return "form";
	}
	
	@RequestMapping(value="/form/{id}")
	public String editar(@PathVariable(value="id") Long id, Map<String, Object> model,RedirectAttributes attribute) {
		
		Cliente cliente = null;
		
		if(id > 0) {
			cliente = clienteService.findOne(id);
			if(cliente == null) {
				attribute.addFlashAttribute("error","El ID del cliente no existe en la base de datos");
				return "redirect:/listar";
			}
		} else {
			attribute.addFlashAttribute("error","El ID del cliente no puede ser cero");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar Cliente");
		return "form";
	}
	
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente,@RequestParam("file") MultipartFile foto, RedirectAttributes attribute, BindingResult result, Model model, SessionStatus status) {
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}
		
		if (!foto.isEmpty()) {
			
			String uniqueFilename = UUID.randomUUID().toString() + "_"+foto.getOriginalFilename();
			
			Path rootPath = Paths.get("uploads").resolve(uniqueFilename) ;
			
			Path rootAbsolutPath = rootPath.toAbsolutePath();

			try {
				
				Files.copy(foto.getInputStream(),rootAbsolutPath);
				
				attribute.addFlashAttribute("info","ha subido correctamente '"+ uniqueFilename +"'");

				cliente.setFoto(uniqueFilename);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		String mensajeFlash = (cliente.getId() != null)?"Cliente editado con exito" : "Cliente creado con exito";
		
		clienteService.save(cliente);
		status.setComplete();
		attribute.addFlashAttribute("success",mensajeFlash);
		return "redirect:listar";
	}
	
	@RequestMapping(value="/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id,RedirectAttributes attribute) {
		
		if(id > 0) {
			clienteService.delete(id);
			attribute.addFlashAttribute("success","Cliente Eliminado con exito");
		}
		
		return "redirect:/listar";
	}
}
