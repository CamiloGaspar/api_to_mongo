package com.jcga.mean_social.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jcga.mean_social.models.ErrorTO;
import com.jcga.mean_social.models.PruebaTO;
import com.jcga.mean_social.models.User;
import com.jcga.mean_social.persistence.mongodb.MongoDAO;
import com.jcga.mean_social.util.ObjectToJson;
import com.jcga.mean_social.util.TokenGenerate;


@RestController
public class ApiController {

	private static final Logger logger = LogManager.getLogger(ApiController.class);
	private MongoDAO dao;
	ObjectToJson otj = ObjectToJson.getInstance();
	
	public  ApiController() {
		this.dao = MongoDAO.getInstance();
	}

	@RequestMapping(value="/prueba", method= RequestMethod.POST, consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody String prueba(PruebaTO prueba, @RequestHeader(value="Authorization") String actUserToken){
		
		
		User user = TokenGenerate.decodeUserToken(actUserToken);
		
		if(dao.getUserBy("email", user.getEmail()) != null){
			
		} else {
			try{
			ErrorTO error = new ErrorTO();
			error.setStatus(500);
			error.setMessage("El usuario que intenta hacer la prueba no existe en la base de datos");
			return otj.toJson(error);
			} catch(JsonProcessingException e){
				logger.error("Error al convertir en JSON el objeto ErrorTO");
				return "Error al convertir en JSON el objeto ErrorTO";
			}
		}
		String respuesta = null;
		if(prueba != null){
			respuesta = prueba.getNombre()+"-"+prueba.getApellido();
		}
		return respuesta;
	}

	@RequestMapping(value="/register", method= RequestMethod.POST, consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody User registro(User user){
		
		logger.debug("Se accedió al método de registro por la ruta /register mediante un método post");

		user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
		user.setRole("USER_ROLE");
		user.setImage(null);

		MongoDAO dao = MongoDAO.getInstance();

		User userSave = dao.saveUser(user);
		
		user.setPassword(null);

		return userSave;

	}

	@RequestMapping(value="/updateUser/{id}", method= RequestMethod.POST, consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody User updateUser(User user, @PathVariable("id") String id){
		
		User resUser = null;
		
		if(user.getPassword() != null){
			user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
		}
		user.setId(new ObjectId(id));
		user.setRole("USER_ROLE");
		user.setImage("");

		MongoDAO dao = MongoDAO.getInstance();

		User userUpdate = dao.getUserById(id);
		
		if(user.getName() != null) userUpdate.setName(user.getName()); 
		if(user.getSurname() != null) userUpdate.setSurname(user.getSurname()); 
		if(user.getEmail() != null) userUpdate.setEmail(user.getEmail()); 
		if(user.getImage() != null) userUpdate.setImage(user.getImage()); 
		
		resUser = dao.saveUser(userUpdate);
		
		resUser.setPassword(null);
		
		return resUser;

	}

	@RequestMapping(value="/user/{id}", method=RequestMethod.GET )
	public @ResponseBody User getUser(@PathVariable("id") String id){

		MongoDAO dao = MongoDAO.getInstance();

		User user = dao.getUserById(id);

		return user;
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST, consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody String loginUser(User user){
		
		MongoDAO dao = MongoDAO.getInstance();
		String token = null;
		
		User userLogin = dao.getUserBy("email", user.getEmail());
		
		if(BCrypt.checkpw(user.getPassword(), userLogin.getPassword())){
			token = TokenGenerate.getToken(userLogin);
		}
				
		return token;		
	}
	
	public @ResponseBody User UploadImagen(){
		
		//TODO
		
		return null;
		
	}

}