package com.jcga.mean_social.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcga.mean_social.models.User;

public class TokenGenerate {


	public static String getToken(User user){

		String token = null;

		ObjectMapper mapper = new ObjectMapper();

		try{
			String userJson = mapper.writeValueAsString(user);

			Algorithm algorithmHS = Algorithm.HMAC256("secret");

			token = JWT.create()
					.withSubject(userJson)
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

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JWTDecodeException e){
			e.printStackTrace();
		}


		return user;
	}
}
