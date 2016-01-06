public class Feed extends Thread{

	final String feed = "/space/Feed";
	
	Template t1;
	Template t2;
	Template t3;
	
	public void run() 
	{
		try {
			while(true){
				
				t1 = new Template(feed,"?");
				System.out.println("Looking for some file...");
				t1.get();
				System.out.println("Found file " + t1.name);

				t2 = new Template("/space/GeneralData/" + t1.name,t1.name);
				System.out.println("Putting the file in its corresponding subfolder...");
				t2.put();
				t3 = new Template("/space/UserData/" + t1.name,t1.name);
				System.out.println("Putting the file in its corresponding subfolder...");
				t3.put();
			}

		} catch (Exception e) { e.printStackTrace(); }
	}
}