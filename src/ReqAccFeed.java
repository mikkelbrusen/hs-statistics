import java.io.ByteArrayInputStream;
import java.util.List;

import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

public class ReqAccFeed extends Thread {
	
	final String reqAcc = "/space/FriendReqAcc";
	
	Template t1,t2,t3,t4;

	private boolean result1;
	private boolean result2;
	private boolean result3;
	private boolean result4;

	
	static Semaphore multEx = new Semaphore(1);
	
	public void run()
	{
		try {
			while (true)
			{
				t1 = new Template(reqAcc,"?");
				System.out.println("Looking for some file...");
				try { multEx.P(); } catch (InterruptedException e) {} // The threads should not get the same file.
				t1.get();
				multEx.V();
				System.out.println("Found file " + t1.name);
				
				String[] parts1 = t1.name.split("\\.");
				String[] parts = parts1[0].split("_");
				
				if (parts[1].equals(parts[2]))			//A user cannot add himself
				{
					System.out.println("A user cannot add himself.");
					continue;
				}
				
				t1 = new Template(getUserReq(parts[1]),parts[2]);
				t2 = new Template(getUserReq(parts[2]),parts[1]);
				t3 = new Template(getUserAcc(parts[1]),parts[2]);
				t4 = new Template(getUserAcc(parts[2]),parts[1]);
				
				if (!t1.doesExist() || !t2.doesExist()){
					System.out.println("User does not exist.");
					continue;
				}

				
				multEx.P();		//Search also has to be atomic because action is based on the result.
				
				result1 = t1.searchFor();
				result2 = t2.searchFor();
				result3 = t3.searchFor();
				result4 = t4.searchFor();

				if (parts[0].equals("REQ"))
				{
					if (result2 && !result3 && !result4) //The opposite client has received a req aswell
					{
						t1.path = getUserReq(parts[2]) + "/" + parts[1];
						t1.remove();

						Template t2 = new Template(getUserAcc(parts[1]) + "/" + parts[2], parts[2]); 
						t2.put(); 			//Make access file
						Template t3 = new Template(getUserAcc(parts[2]) + "/" + parts[1], parts[1]); 
						t3.put(); 			//Make access file
						System.out.println(parts[1] + " and " + parts[2] + " are now friends.");
					}
					else if (result1 || result3 || result4) 	//Dont send the req further, since it exists already
					{
						if(result1){
							System.out.println("Request already exists.");
						}
						else{
							System.out.println("Already friends.");
						}
					}
					else //Move request file to user req folder
					{
						Template t2 = new Template(getUserReq(parts[1]) + "/" + parts[2], parts[2]);
						t2.put();		
					}
				}
				multEx.V();

				
			}
			
		} catch (Exception e){ e.printStackTrace(); }
	}

	private String getUserReq(String user)
	{
		return "/space/Users/" + user + "/Request";
	}
	private String getUserAcc(String user)
	{
		return "/space/Users/" + user + "/Access";
	}
	



}
