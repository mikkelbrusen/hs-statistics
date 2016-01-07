public class Feed extends Thread{

	final String feed = "/space/Feed";
	
	Template t1;
	static Semaphore multEx = new Semaphore(1);
	
	public void run() 
	{
		try {
			while(true){
				
				t1 = new Template(feed,"?");
				System.out.println("Looking for some file...");
				try { multEx.P(); } catch (InterruptedException e) {} // The threads should not get the same file.
				t1.get();
				multEx.V();
				System.out.println("Found file " + t1.name);
				
				String[] parts = t1.name.split("_");
				String[] last = parts[2].split("\\.");
				
				if(parts[0].equals("REQ")) {
					Template t2 = new Template("/space/Users/" + last[0] + "/Request/" + parts[1], parts[1]);
					t2.put();
				}
				else if(parts[0].equals("ACC")) {
					Template t2 = new Template("/space/Users/" + last[0] + "/Access/" + parts[1], parts[1]);
					t2.put();
					Template t3 = new Template("/space/Users/" + parts[1] + "/Access/" + last[1], last[0]);
					t3.put();
					
				} else {
					Template t2 = new Template("/space/GeneralData/" + t1.name,t1.name);
					System.out.println("Putting the file in its corresponding subfolder...");
					t2.put();
					Template t3 = new Template("/space/UserData/" + t1.name,t1.name);
					System.out.println("Putting the file in its corresponding subfolder...");
					t3.put();
				}
			}

		} catch (Exception e) { e.printStackTrace(); }
	}
}