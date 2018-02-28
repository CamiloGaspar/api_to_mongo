package com.jcga.mean_social.persistence.mongodb;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.jcga.mean_social.models.User;
import com.mongodb.MongoClient;

public class MongoDAO {

	private static MongoDAO INSTANCE;
	private MongoClient mongoClient;
	private Datastore ds;

	private MongoDAO(){
		this.mongoClient = new MongoClient();

		final Morphia morphia = new Morphia();

		// tell Morphia where to find your classes
		// can be called multiple times with different packages or classes
		morphia.mapPackage("com.jcga.mean_social");

		// create the Datastore connecting to the default port on the local host
		this.ds = morphia.createDatastore(this.mongoClient, "curso_mean_social");
		ds.ensureIndexes();
	}


	public static MongoDAO getInstance(){
		if(INSTANCE == null){
			INSTANCE = new MongoDAO();
		}

		return INSTANCE;
	}

	public User getUserById(String id){
		User user = null;
		ObjectId userId = new ObjectId(id);
		user = ds.get(User.class, userId);


		return user;
	}
	
	public User getUserBy(String campo, String valor){
		User user = null;
		Query<User> query = ds.createQuery(User.class);
		
		query.field(campo).equal(valor);
		
		user= query.get();
		
		return user;
	}
	
	public User saveUser(User user){
		 ds.save(user);
		 return user;
	}
	
	
}
