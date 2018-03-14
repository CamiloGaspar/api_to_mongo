package com.jcga.mean_social.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jcga.mean_social.exceptions.AuthException;
import com.jcga.mean_social.exceptions.UserFindException;
import com.jcga.mean_social.exceptions.UserNoInsertException;
import com.jcga.mean_social.middleware.AuthMiddleware;
import com.jcga.mean_social.models.PruebaTO;
import com.jcga.mean_social.models.RespuestaTO;
import com.jcga.mean_social.models.TokenTO;
import com.jcga.mean_social.models.User;
import com.jcga.mean_social.persistence.mongodb.MongoDAO;
import com.jcga.mean_social.util.ObjectToJson;
import com.jcga.mean_social.util.TokenGenerate;

/**
 * Clase encargada de recibir las peticiones por HTTP 
 * @author camilo.gaspar10@gmail.com
 *
 */
@RestController
public class ApiController {

	/**
	 * Instancia encargada de escribir en el log
	 */
	private static final Logger logger = LogManager.getLogger(ApiController.class);


	/**
	 * Método encargado de registrar un usuario en la base de datos
	 * @param user Usuario que se quiere registrar 
	 * @return Usuario que se registró o mensaje en caso de que se generara algún error
	 */
	@RequestMapping(value="/register", method= RequestMethod.POST, consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody String registro(User user){

		//TODO Validar token
		logger.debug("Se accedió al método de registro por la ruta /register mediante un método post");
		String jRespuesta = null;
		try {
			try{
				if(user.getName() != null && user.getSurname() != null && user.getEmail() != null 
						&& user.getNick() != null && user.getPassword() != null){

					MongoDAO dao = MongoDAO.getInstance();

					User exist = dao.getUserBy("email", user.getEmail());

					if(exist == null){
						user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
						user.setRole("ROLE_USER");
						user.setImage(null);
						User userSave = dao.saveUser(user);
						userSave.setPassword(null);

						jRespuesta = ObjectToJson.toJson(userSave);

					} else{

						jRespuesta = this.respuesta(500, "No llegaron los datos necesarios del usuarios a registrar");
					}
				} else {

					jRespuesta = this.respuesta(500, "No llegaron todos los datos del usuarios a registrar");
				}
			} catch (UserNoInsertException e) {

				jRespuesta = this.respuesta(500, e.getMessage());
			} catch (UserFindException e) {

				jRespuesta = this.respuesta(500, e.getMessage());
			}
		} catch (JsonProcessingException e) {
			logger.error("Error al convertir en JSON el objeto ErrorTO");
			jRespuesta = "Error al convertir en JSON el objeto ErrorTO";
		} 
		return jRespuesta;
	}

	/**
	 * Método encargado de verificar si el usuario dado existe en base de datos y sus credenciales son correctas.
	 * <br>
	 *  En caso de ser correctas devuelve un token con la información del usuario
	 * @param user Usuario que intenta ingresar
	 * @return Token que contiene la información del usuario o mensaje en caso de que se genere algún error 
	 */
	@RequestMapping(value="/login", method=RequestMethod.POST, consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody String loginUser(User user){

		//TODO Validar token
		String jRespuesta = null;
		try{
			try {
				if(user != null && user.getEmail() != null && user.getPassword() != null){
					MongoDAO dao = MongoDAO.getInstance();

					User userLogin = dao.getUserBy("email", user.getEmail());
					if(userLogin != null){
						if(BCrypt.checkpw(user.getPassword(), userLogin.getPassword())){

							TokenTO token = new TokenTO();

							token.setToken(TokenGenerate.getToken(userLogin));
							jRespuesta = ObjectToJson.toJson(token);
						} else {

							jRespuesta = this.respuesta(500, "Contraseña incorrecta");
						}
					} else {

						jRespuesta = this.respuesta(500, "El usuario "+user.getEmail()+" no existe en la base de datos");
					} 
				} else { 

					jRespuesta = this.respuesta(500, "No se ha suministrado el correo o la contraseña");
				}

			} catch (UserFindException e) {

				jRespuesta = this.respuesta(500, e.getMessage());
			}

		} catch(JsonProcessingException e){
			logger.error("Error al convertir en JSON el objeto ErrorTO");
			jRespuesta = "Error al convertir en JSON el objeto ErrorTO";
		}
		return jRespuesta;		
	}

	/**
	 * Método encargado de obtener la información del usuario solicitado
	 * @param id Id del usuario que se desea consultar en base de datos 
	 * @param actUserToken Token del usuario que hace la petición
	 * @return Usuario que se consulto o mensaje en caso de que se genere algún error
	 */
	@RequestMapping(value="/user/{id}", method=RequestMethod.GET )
	public @ResponseBody String getUser(@PathVariable("id") String id, @RequestHeader(value="Authorization") String actUserToken){

		String jRespuesta = null;
		try{
			if(actUserToken != null){
				try{
					User userToken = AuthMiddleware.isValid(actUserToken);
					if(userToken != null){

						if(id != null){

							MongoDAO dao = MongoDAO.getInstance();

							User user = dao.getUserById(id);
							if(user != null){

								jRespuesta = ObjectToJson.toJson(user);
							} else {

								jRespuesta = this.respuesta(500, "No existe el usuario con id: "+id);
							}
						} else {

							jRespuesta = this.respuesta(500, "No se ha suministrado la id del usuario");
						}
					} else {

						jRespuesta = this.respuesta(500, "Token inválido");
					}
				} catch(UserFindException e){

					jRespuesta = this.respuesta(500, e.getMessage());
				} catch (AuthException e) {

					jRespuesta = this.respuesta(500, e.getMessage());
				}

			} else {

				jRespuesta = this.respuesta(500, "La petición no tiene la cabecera de autenticación");
			}
		} catch(JsonProcessingException e){

			logger.error("Error al convertir en JSON el objeto ErrorTO");
			jRespuesta = "Error al convertir en JSON el objeto ErrorTO";
		} 
		return jRespuesta;
	}

	/*
	@RequestMapping(value="/prueba", method= RequestMethod.POST, consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody String prueba(PruebaTO prueba, @RequestHeader(value="Authorization") String actUserToken){

		String respuesta = null;
		RespuestaTO error = null;

		try{


			User user = AuthMiddleware.isValid(actUserToken);
			if(user != null){
				if(dao.getUserBy("email", user.getEmail()) != null){

				} else {
					error = new RespuestaTO();
					error.setStatus(500);
					error.setMessage("El usuario que intenta hacer la prueba no existe en la base de datos");
					respuesta =  ObjectToJson.toJson(error);
				} 
			} else {
				error = new RespuestaTO();
				error.setStatus(404);
				error.setMessage("El token no es valido");
				respuesta =  ObjectToJson.toJson(error);
			}
		}catch(JsonProcessingException e){
			logger.error("Error al convertir en JSON el objeto ErrorTO");
			respuesta = "Error al convertir en JSON el objeto ErrorTO";
		}


		return respuesta;
	}
	 */


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




	/**
	 * Método encargado de almacenar una imagen en un FS definido y de agregarla a un usuario
	 * @param file Imagen que se desea almacenar
	 * @param id Id del usuario al que se le desea agregar la imagen
	 * @return Mensaje de respuesta a la petición
	 */
	@RequestMapping(value="/upload-image/{id}", method=RequestMethod.POST, consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public String singleFileUpload(@RequestParam("image") MultipartFile file, @PathVariable("id") String id) {

		RespuestaTO respuesta = null;
		String jRespuesta = null;

		try{
			if (file.isEmpty()) {
				respuesta = new RespuestaTO();
				respuesta.setStatus(400);
				respuesta.setMessage("No se adjunto imagen");
				jRespuesta = ObjectToJson.toJson(respuesta);
			} else{

				try {

					String extFile = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));

					if(extFile.toUpperCase().equals(".PNG") || extFile.toUpperCase().equals(".JPG") 
							|| extFile.toUpperCase().equals(".JPEG") || extFile.toUpperCase().equals(".GIF")){


						// Obtiene el archivo y lo guarda en la ruta especificada
						byte[] bytes = file.getBytes();
						Path path = Paths.get("C:\\Users\\dell\\Desktop\\" + file.getOriginalFilename());
						Files.write(path, bytes);

						respuesta = new RespuestaTO();
						respuesta.setStatus(200);
						respuesta.setMessage("Se adjuntó correctamente la imagen.");
						jRespuesta = ObjectToJson.toJson(respuesta);
					} else{

						respuesta = new RespuestaTO();
						respuesta.setStatus(400);
						respuesta.setMessage("Formato de la imagen: "+extFile+" no es valido");
						jRespuesta = ObjectToJson.toJson(respuesta);
					}


				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch(JsonProcessingException e){
			logger.error("Error al convertir en JSON el objeto ErrorTO");
			jRespuesta = "Error al convertir en JSON el objeto ErrorTO";
		}
		return jRespuesta;
	}

	private String respuesta(int status, String message) throws JsonProcessingException{

		String jRespuesta;

		RespuestaTO respuesta = new RespuestaTO();
		respuesta.setStatus(status);
		respuesta.setMessage(message);
		jRespuesta = ObjectToJson.toJson(respuesta);

		return jRespuesta;
	}

}