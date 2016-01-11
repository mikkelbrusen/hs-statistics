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

	private List<DbxEntry> result1;
	private List<DbxEntry> result2;
	private List<DbxEntry> result3;
	private List<DbxEntry> result4;

	
	static Semaphore multEx = new Semaphore(1);
	
	private String getUserReq(String user)
	{
		return "/space/Users/" + user + "/Request";
	}
	private String getUserAcc(String user)
	{
		return "/space/Users/" + user + "/Access";
	}

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
				
				path1 = getUserReq(parts[1]);
				path2 = getUserReq(parts[2]);
				path3 = getUserAcc(parts[1]);
				path4 = getUserAcc(parts[2]);

				multEx.P();
				result1 = ConnectionInit.client.searchFileAndFolderNames(path1, parts[2]);
				result2 = ConnectionInit.client.searchFileAndFolderNames(path2, parts[1]);
				result3 = ConnectionInit.client.searchFileAndFolderNames(path3, parts[2]);
				result4 = ConnectionInit.client.searchFileAndFolderNames(path4, parts[1]);

				
				if (parts[0].equals("REQ"))
				{
					if (!result2.isEmpty() && result3.isEmpty() && result4.isEmpty()) //The opposite client has received a req aswell
					{
						for (DbxEntry f : result2)
						{
							ConnectionInit.client.delete(f.path);
						}
						Template t2 = new Template(getUserAcc(parts[1]) + "/" + parts[2], parts[2]); 
						t2.put(); //Make access file
						Template t3 = new Template(getUserAcc(parts[2]) + "/" + parts[1], parts[1]); 
						t3.put(); //Make access file
						System.out.println(parts[1] + " and " + parts[2] + " are now friends.");
						multEx.V();
						continue;
					}
					else if (!result1.isEmpty() || !result3.isEmpty() || !result4.isEmpty()) //Dont send the req further, since it exists already
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


}
