public class Feed extends Thread{

	final String feed = "/space/Feed";
	

	Template t1,t2,t3,semaphore;
	String semaphorePath = "/space/FeedSemaphore";

	static Semaphore multEx = new Semaphore(1);
	
	public void run() 
	{
		try {
			while(true){
				
				t1 = new Template(feed,"?");
                semaphore = new Template(semaphorePath, "feedSemaphore");
				System.out.println("Looking for some file...");
                try { semaphore.get(); } catch (Exception e) {continue;} // The threads should not get the same file.				t1.get();
				t1.get();
                semaphore.path = semaphorePath + "/feedSemaphore";
				semaphore.put();
				System.out.println("Found file " + t1.name);
				
				String[] parts = t1.name.split("_");

				if(parts[0].equals("REQ")) {
					Template t2 = new Template("/space/FriendReqAcc/" + t1.name, t1.name);
					t2.put();
				} else {
					t2 = new Template("/space/GeneralData/" + t1.name,t1.name);
					System.out.println("Putting the file in its corresponding subfolder...");
					t2.put();
					t3 = new Template("/space/UserData/" + t1.name,t1.name);
					System.out.println("Putting the file in its corresponding subfolder...");
					t3.put();
				}
			}

		} catch (Exception e) { e.printStackTrace(); }
	}
}