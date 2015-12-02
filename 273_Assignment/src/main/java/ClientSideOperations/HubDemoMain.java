package ClientSideOperations;

public class HubDemoMain {

	public static void main(String args[]){
		HubRunning hub = new HubRunning();
		HubRunMinute minute = new HubRunMinute();
		hub.start();
		minute.start();
	}
}
