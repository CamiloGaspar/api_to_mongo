package com.jcga.mean_social.middleware;

import java.io.IOException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jcga.mean_social.models.User;
import com.jcga.mean_social.util.ObjectToJson;

public class AuthMiddleware {

	public static User isValid(String token){
		User user = null;

		try{
			DecodedJWT decodeToken =  JWT.decode(token);

			if(decodeToken.getExpiresAt().getTime() > System.currentTimeMillis()){
				user = ObjectToJson.toObject(User.class, decodeToken.getSubject());
			}
		} catch(JWTDecodeException e){
			
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return user;
	}
}
