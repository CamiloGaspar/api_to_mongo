package com.jcga.mean_social.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcga.mean_social.models.User;

public class TokenGenerate {

	private static final Logger logger = LogManager.getLogger(TokenGenerate.class);
	static final long ONE_MINUTE_IN_MILLIS=60000;

	public static String getToken(User user){

		String token = null;

		
		try{
			String userJson = ObjectToJson.toJson(user);

			Algorithm algorithmHS = Algorithm.HMAC256("secret");

			token = JWT.create()
					.withSubject(userJson)
					.withExpiresAt(new Date(System.currentTimeMillis()+(ONE_MINUTE_IN_MILLIS*30)))
					.sign(algorithmHS);



		} catch(UnsupportedEncodingException e){

		} catch (JWTCreationException  e) {

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return token;
	}

	public static User decodeUserToken (String token){
		ObjectMapper mapper = new ObjectMapper();

		User user = null;
		try {

			DecodedJWT decodeToken =  JWT.decode(token);

			String userJson = decodeToken.getSubject();

			user = mapper.readValue(userJson, User.class);

		}  catch (JWTDecodeException e){
			logger.error("Error al decodificar el token",e);
		} catch (Exception e) {
			logger.error("Error al mapear el JSON a objeto User");
		}

		return user;
	}
	
	public static boolean isValid(DecodedJWT token){
		
		if(token.getExpiresAt().getTime() > System.currentTimeMillis()){
			return true;
		}
		
		return false;
	}
}
