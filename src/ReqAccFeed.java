import java.io.ByteArrayInputStream;
import java.util.List;

import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

public class ReqAccFeed extends Thread {
	
	final String reqAcc = "/space/FriendReqAcc";
	
	Template t1,t2,t3;
	String path1,path2,path3,path4;

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
				
				if (!doesExist(parts[1]) || !doesExist(parts[2])){
					System.out.println("User does not exist");
					return;
				}
				
				if (parts[1].equals(parts[2]))			//Cant add yourself
				{
					return;
				}

				multEx.P();
				result1 = searchFor(getUserReq(parts[1]),parts[2]);
				result2 = searchFor(getUserReq(parts[2]),parts[1]);
				result3 = searchFor(getUserAcc(parts[1]),parts[2]);
				result4 = searchFor(getUserAcc(parts[2]),parts[1]);

				if (parts[0].equals("REQ"))
				{
					if (result2 && !result3 && !result4) //The opposite client has received a req aswell
					{
						ConnectionInit.client.delete(getUserReq(parts[2]) + "/" + parts[1]);

						Template t2 = new Template(getUserAcc(parts[1]) + "/" + parts[2], parts[2]); 
						t2.put(); //Make access file
						Template t3 = new Template(getUserAcc(parts[2]) + "/" + parts[1], parts[1]); 
						t3.put(); //Make access file
						System.out.println(parts[1] + " and " + parts[2] + " are now friends.");
						multEx.V();
						continue;
					}
					else if (result1 || result3 || result4) //Dont send the req further, since it exists already
					{
						multEx.V();
						continue;
					}
					else //Move request file to user req folder
					{
						Template t2 = new Template(getUserReq(parts[1]) + "/" + parts[2], parts[2]);
						t2.put();		
						multEx.V();
						continue;
					}
				} 
				else
				{
					multEx.V();
				}
				
			}
			
		} catch (Exception e){ e.printStackTrace(); }
	}
	private boolean doesExist(String user) throws DbxException {
		List<DbxEntry> result;
		result = ConnectionInit.client.searchFileAndFolderNames("/space/Users/" + user, "Statistics");
		return !result.isEmpty();
	}
	private boolean searchFor(String path, String user) throws DbxException {
		List<DbxEntry> result;
		result = ConnectionInit.client.searchFileAndFolderNames(path, user);
		for (DbxEntry f : result)
		{
			return f.name.equals(user);
		}		
		return false;
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
