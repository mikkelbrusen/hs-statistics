public class GeneralFeeds extends Thread{


	final String feed = "/space/GeneralData";
	final String general = "/space/General";


	// Two tuple templates for the main loop of the app
	Template t1;
	Template t2;
	Template t3;
	
		public void run(){
			try{
				while(true){
					
					// Template t1 represents "any file in the space folder"
					t1 = new Template(feed,"?");
					System.out.println("Looking for some file...");
					t1.get();
					System.out.println("Found file " + t1.name);
					
					String[] parts = t1.name.split("_");
					String[] endPart = parts[3].split("\\.");
					String winCon = endPart[0];
					
					
					t2 = new Template(general,parts[1]);
					System.out.println("Looking for some file...");
					t2.getByPrefix();
					
					String[] parts2 = t2.name.split("_");
					
					int winCount = Integer.parseInt(parts2[1]);
					int lossCount = Integer.parseInt(parts2[2]);
					
					if(winCon.equals("Win")){
						winCount++;
					} else {
						lossCount++;
					}
					
					t3 = new Template("/space/General/" + parts2[0] + "_" + winCount + "_" + lossCount,t2.name);
					System.out.println("Putting the file in its corresponding subfolder...");
					t3.put();

				}
				
			}
			catch (Exception e) { e.printStackTrace();
		}

	}	
}
