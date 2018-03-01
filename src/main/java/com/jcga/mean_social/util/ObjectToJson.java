package com.jcga.mean_social.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectToJson {

	private static ObjectToJson INSTANCE;
	private ObjectMapper mapper;
	
	private ObjectToJson(){
		if(this.mapper == null){
			this.mapper = new ObjectMapper();
		}
	}
	
	public static ObjectToJson getInstance(){
		if(INSTANCE == null){
			INSTANCE = new ObjectToJson();
		}
		return INSTANCE;
	}
	
	public String toJson(Object objeto) throws JsonProcessingException{
		
		String json = this.mapper.writeValueAsString(objeto);
		
		return json;		
	}
	
	public <T> T toObject(Class<T> clase, String json) throws JsonParseException, JsonMappingException, IOException{
		
		T objeto = mapper.readValue(json, clase);
		
		return objeto;
		
	}
}
