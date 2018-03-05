package com.jcga.mean_social.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Clase que convierte objetos en json y convierte json a objetos de una clase dada
 * @author camilo.gaspar10@gmail.com
 *
 */
public class ObjectToJson {

	/**
	 * Método encargado de convertir en un string con formato JSON un objeto recibido por parámetro
	 * @param objeto Objeto a convertir en JSON
	 * @return String en formato JSON con información del objeto
	 * @throws JsonProcessingException
	 */
	public static String toJson(Object objeto) throws JsonProcessingException{
		
		ObjectMapper mapper = new ObjectMapper();
		
		String json = mapper.writeValueAsString(objeto);
		
		return json;		
	}
	
	/**
	 * Método encargado de convertir un String con formato JSON a un Objeto de tipo dado por parámetro 
	 * @param clase Tipo que tendrá el objeto resultante
	 * @param json String en formato Json con la información que tendrá el objeto
	 * @return Objeto con la información del Json seteada
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T toObject(Class<T> clase, String json) throws JsonParseException, JsonMappingException, IOException{
		
		ObjectMapper mapper = new ObjectMapper();
		
		T objeto = mapper.readValue(json, clase);
		
		return objeto;
		
	}
}
