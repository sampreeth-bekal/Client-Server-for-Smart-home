package ClientSideOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;

import com.google.gson.Gson;

import Models.Events;
import database.HubDB;

public class HubRunning implements Runnable {
		static Thread t;
		public void start(){
			if (t==null){
				t = new Thread(this);
			}
			t.start();
		}
	@Override
	public void run() {
			while(true){
				try {	Thread.sleep(2*1000);
						performEventList();
				}
				catch (InterruptedException e) {	e.printStackTrace();	}
			}
		}
	public void performEventList(){
	
		Map<List<ObjectId>,List<Events>> map = new HubDB().retrieveInstantEvent();
		List<ObjectId> idList = new ArrayList<>();
		List<Events> eventList = new ArrayList<>();
	
		for(Entry<List<ObjectId>, List<Events>> entry : map.entrySet()){
			idList = entry.getKey();
			eventList = entry.getValue();
		}
	
		for(int i =0;i<idList.size();i++){
			System.out.println("_ID :"+idList.get(i)+"  "+new Gson().toJson(eventList.get(i)));
	//		new HubDB().deleteInstantEvent(idList.get(i));
		}
	}
}	


class HubRunMinute implements Runnable{

	Thread t;

	public void start(){
		if (t==null){
			t = new Thread(this);
		}
		t.start();
	}
	@Override
	public void run() {
				while(true){
					try {	Thread.sleep(2*1000);	
							performEventMinute();
					} catch (InterruptedException e) {	e.printStackTrace();	}
				}
	}
	
	public void performEventMinute(){
		Map<List<ObjectId>,List<Events>> map = new HubDB().retriveEvents();
		List<ObjectId> idList = new ArrayList<>();
		List<Events> eventList = new ArrayList<>();
	
		for(Entry<List<ObjectId>, List<Events>> entry : map.entrySet()){
			idList = entry.getKey();
			eventList = entry.getValue();
		}
		for(int i =0;i<idList.size();i++){
			System.out.println("_ID :"+idList.get(i)+"  "+new Gson().toJson(eventList.get(i)));
	
			
			//Code to Count time for the next events and Pass information to Lights
		}
				
	}
}