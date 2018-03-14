package com.jcga.mean_social.persistence.mongodb;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.jcga.mean_social.exceptions.UserFindException;
import com.jcga.mean_social.exceptions.UserNoInsertException;
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

	public User getUserById(String id) throws UserFindException{
		User user = null;
		
		try{
		ObjectId userId = new ObjectId(id);
		user = ds.get(User.class, userId);
		} catch (Exception e){
			throw new UserFindException("Error al buscar el usuario con id: "+ id, e);
		}

		return user;
	}

	public User getUserBy(String campo, String valor) throws UserFindException{

		User user = null;
		try{
			Query<User> query = ds.createQuery(User.class);

			query.field(campo).equal(valor);

			user= query.get();
		} catch (Exception e){
			throw new UserFindException("Error al buscar el usuario con "+campo+" = "+valor, e);
		}
		return user;
	}

	public User saveUser(User user) throws UserNoInsertException{
		try{
			ds.save(user);
		} catch (Exception e){
			throw new UserNoInsertException("No se ha agregado el usuario "+user.getEmail(), e);
		}
		return user;
	}

	public void close(){
		if(mongoClient != null){
			mongoClient.close();
		}
	}

}
