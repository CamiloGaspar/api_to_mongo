package com.jcga.mean_social.middleware;

import java.io.IOException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jcga.mean_social.exceptions.AuthException;
import com.jcga.mean_social.models.User;
import com.jcga.mean_social.util.ObjectToJson;

/**
 * Clase encargada de validar el token de Autenticación
 * @author camilo.gaspar10@gmail.com
 *
 */
public class AuthMiddleware {

	/**
	 * Método encargado de validar si el token es valido
	 * @param token Token que contiene la información del usuario
	 * @return Usuario que está incluido en el token
	 * @throws AuthException En caso de que se genere algún error al validar el token 
	 */
	public static User isValid(String token) throws AuthException{
		User user = null;

		try{
			DecodedJWT decodeToken =  JWT.decode(token);

			if(decodeToken.getExpiresAt().getTime() > System.currentTimeMillis()){
				user = ObjectToJson.toObject(User.class, decodeToken.getSubject());
			}
		} catch(JWTDecodeException e){
			throw new AuthException("Error decodificando el token",e);
		} catch (JsonParseException e) {
			throw new AuthException("Error convirtiendo en usuario el token",e);
		} catch (JsonMappingException e) {
			throw new AuthException("Error convirtiendo en usuario el token",e);
		} catch (IOException e) {
			throw new AuthException("Error convirtiendo en usuario el token",e);
		}
		return user;
	}
}
