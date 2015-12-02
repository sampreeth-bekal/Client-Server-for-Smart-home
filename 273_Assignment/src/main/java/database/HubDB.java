package database;

import java.net.UnknownHostException;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import Models.Events;
import Models.HubProperties;
import Models.ServerObject;

public class HubDB {

	MongoClient mongoClient;
	DB db;
	DBCollection coll;
	
	public HubDB(){
		try {
			mongoClient = new MongoClient("localhost",27017);
			db = mongoClient.getDB("HubDB");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void saveHubInfo(HubProperties hub, ServerObject server){
		if(!db.collectionExists("HubDeviceInfo")){
			BasicDBObject options = new BasicDBObject().append("capped", true).append("size", 100).append("max", 1);
			coll = db.createCollection("HubDeviceInfo", options);
		}
		else coll = db.getCollection("HubDeviceInfo");
		
		Gson gson = new Gson();
		BasicDBObject obj = (BasicDBObject) JSON.parse(gson.toJson(hub));
		obj.put("server",(BasicDBObject) JSON.parse(gson.toJson(server))); 
		coll.insert(obj);
	}
	
	public List<Object> retrieveHub(){
		List<Object> list = new ArrayList<Object>();
		
		coll = db.getCollection("HubDeviceInfo");
		DBObject cursor = coll.findOne();
		HubProperties hub = new Gson().fromJson(cursor.toString(), HubProperties.class);
		ServerObject server = new Gson().fromJson(cursor.get("server").toString(), ServerObject.class);
		
		list.add(hub);
		list.add(server);
		return list;
	}
	
	public int retriveLifetime(){
		coll = db.getCollection("HubDeviceInfo");
		DBObject cursor = coll.findOne();
		return (int) cursor.get("lifetime");
	}
	
	public void updateLifetime(long lifetime){
		coll = db.getCollection("HubDeviceInfo");
		DBObject cursor = coll.findOne();
		System.out.println(cursor.toString());
		BasicDBObject newDoc = new BasicDBObject();
		newDoc.append("$set", new BasicDBObject().append("lifetime", lifetime));
		coll.update(cursor, newDoc);
	}
	
	public void updateResource(int resourceValue){
		if(!db.collectionExists("HubResourceInfo")){
			coll = db.createCollection("HubResourceInfo",null);
			BasicDBObject obj = new BasicDBObject().append("resource", 10);
			coll.insert(obj);
		}
		else coll = db.getCollection("HubResourceInfo");
		
		DBObject cursor = coll.findOne();
		BasicDBObject newDoc = new BasicDBObject();
		newDoc.append("$set", new BasicDBObject().append("resource", resourceValue));
		coll.update(cursor, newDoc);
		
	}
	
	public int retriveResource(){
		coll = db.getCollection("HubResourceInfo");
		DBObject cursor = coll.findOne();
		return (int)cursor.get("resource");
	}
	
	public void registerEvent(Events event){
		if(!db.collectionExists("EventsInfo")){
			coll = db.createCollection("EventsInfo",null);
		}
		else coll = db.getCollection("EventsInfo");
		
		BasicDBObject obj = (BasicDBObject) JSON.parse(new Gson().toJson(event));
		coll.insert(obj);
		
	}
	
	public Map<List<ObjectId>,List<Events>> retriveEvents(){
		Map<List<ObjectId>,List<Events>> map = new HashMap<List<ObjectId>,List<Events>>();
		List<ObjectId> idList = new ArrayList<>();
		List<Events> eventList = new ArrayList<>();
	
		coll = db.getCollection("EventsInfo");
		DBCursor cursor = coll.find(new BasicDBObject("eventId",new BasicDBObject("$ne",1)));
		cursor.sort(new BasicDBObject("startDate", 1).append("startTime", 1));
		
		while(cursor.hasNext()){
			idList.add((ObjectId) cursor.next().get("_id"));
			eventList.add(new Gson().fromJson(cursor.curr().toString(), Events.class));
		}
		map.put(idList,eventList);
		return map;
	}
	
	public Map<List<ObjectId>,List<Events>> retrieveInstantEvent(){
		Map<List<ObjectId>,List<Events>> map = new HashMap<List<ObjectId>,List<Events>>();
		List<ObjectId> idList = new ArrayList<>();
		List<Events> eventList = new ArrayList<>();
		coll = db.getCollection("EventsInfo");
		DBCursor cursor = coll.find( new BasicDBObject("eventId",1));
		
		while(cursor.hasNext()){
			idList.add((ObjectId) cursor.next().get("_id"));
			eventList.add(new Gson().fromJson(cursor.curr().toString(), Events.class));
			
		}	
		map.put(idList, eventList);
		return map;
	}
	
	public void deleteInstantEvent(ObjectId id){
		coll = db.getCollection("EventsInfo");
		BasicDBObject doc = new BasicDBObject();
		doc.put("_id",id);
		coll.remove(doc);
	}
}
