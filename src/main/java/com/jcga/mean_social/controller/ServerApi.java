package com.jcga.mean_social.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.jcga.mean_social.persistence.mongodb.MongoDAO;

@SpringBootApplication
public class ServerApi {

	public static void main(String[] args) {
		SpringApplication.run(ServerApi.class, args);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	           MongoDAO.getInstance().close();
	        }
	    }, "Shutdown-thread"));

	}

}
