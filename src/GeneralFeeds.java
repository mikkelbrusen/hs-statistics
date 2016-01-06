// Include the Dropbox SDK.
import com.dropbox.core.*;
import java.io.*;
import java.util.Locale;

public class GeneralFeeds {

	// Some global variables
	static DbxAppInfo appInfo;
	static DbxRequestConfig config;
	static DbxClient client;

	// This is a very simple implementation of tuple-based operations for files as tuples
	// Note that we only allow name and extension to be binders
	public class Template {
		String path;		// File folder
		String name;		// File name (without extension), "?" denotes any value
		byte[] content;		// File content (as byte array)

		public Template(String p, String n, byte[] c) {
			this.path=p;		
			this.name=n;		
			this.content=c;		
		}

		public void getAny() throws DbxException, IOException{	
			DbxEntry.WithChildren listing;
			String name_aux=name;

			// Repeat until one file/tuple is found
			while(true){				
				listing = client.getMetadataWithChildren(path);

				for (DbxEntry child : listing.children) {				
						name_aux=child.name;
						name=name_aux;
						System.out.println(name);
						ByteArrayOutputStream out=new ByteArrayOutputStream();
						client.getFile(child.path,null,out);
						content = out.toByteArray(); 
						out.close();
						client.delete(child.path);
						return;				       	
				} 

				System.out.println("Blocking operation (qry/get) was unsucessful, sleeping for a while...");
				try {
					Thread.sleep(10000);
				} catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
		}
		public void getByName() throws DbxException, IOException{	
			DbxEntry.WithChildren listing;
			String name_aux=name;

			// Repeat until one file/tuple is found
			while(true){				
				listing = client.getMetadataWithChildren(path);

				for (DbxEntry child : listing.children) {				
						name_aux=child.name;
						name=name_aux;
						System.out.println(name);
						ByteArrayOutputStream out=new ByteArrayOutputStream();
						client.getFile(child.path,null,out);
						content = out.toByteArray(); 
						out.close();
						client.delete(child.path);
						return;				       	
				} 

				System.out.println("Blocking operation (qry/get) was unsucessful, sleeping for a while...");
				try {
					Thread.sleep(10000);
				} catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
		}


		public void put() throws DbxException, IOException{
			ByteArrayInputStream in = new ByteArrayInputStream(content);
			client.uploadFile(path, DbxWriteMode.add(), -1, in);
			return;
		}
	}
	
	

	public static void main(String[] args) throws IOException, DbxException {
		GeneralFeeds me = new GeneralFeeds();

		// Get your app key and secret from the Dropbox developers website and insert them below
		final String APP_KEY = "75ozb45t6z1tq24";
		final String APP_SECRET = "xv6js8mv9li623h";
		appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
		config = new DbxRequestConfig("JavaTutorial/1.0",Locale.getDefault().toString());

		// Insert here the access token from your app.
		final String accessToken = "RUVKadBuhlAAAAAAAAAAEMs2wc1EUw5gU8nDyquO-9ZDnDa7p5qKxhjB0Ivn4NRz";

		client = new DbxClient(config, accessToken);

		// Name of the shared folder to be used as shared space
		final String feed = "/space/GeneralData";
		final String general = "/space/General";


		// Two tuple templates for the main loop of the app
		Template t1;
		Template t2;
		Template t3;
		
		// Main loop that processes files in the shared space
		while(true){
			
			// Template t1 represents "any file in the space folder"
			t1 = me.new Template(feed,"?",null);
			System.out.println("Looking for some file...");
			t1.get();
			System.out.println("Found file " + t1.name);
			
			String[] parts = t1.name.split("_");
			String[] endPart = parts[3].split("\\.");
			String winCon = endPart[0];
			
			
			t2 = me.new Template(general,parts[1],null);
			System.out.println("Looking for some file...");
			t2.get();
			
			String[] parts2 = t2.name.split("_");
			
			int winCount = Integer.parseInt(parts2[2]);
			int lossCount = Integer.parseInt(parts2[1]);
			
			if(winCon == "Win"){
				winCount++;
			} else {
				lossCount++;
			}
			
			t3 = me.new Template("/space/General/" + parts2[0] + "_" + winCount + "_" + lossCount,t2.name,t2.content);
			System.out.println("Putting the file in its corresponding subfolder...");
			t3.put();
		
//			if(parts[1] != null) {
//				
//				if(parts[3].equals("W")){
//					name = parts[1] + "_" + "1" + "_" + "0";
//				} else {
//					name = parts[1] + "_" + "0" + "_" + "1";
//				}
//			}


			// t2 is a tuple representing the previously retreived file in a new folder (named after the extension of the file)
			//t2 = me.new Template("/space/GeneralData/" + t1.name,t1.name,t1.content);
			//System.out.println("Putting the file in its corresponding subfolder...");
			//t2.put();
			//t3 = me.new Template("/space/UserData/" + t1.name,t1.name,t1.content);
			//System.out.println("Putting the file in its corresponding subfolder...");
			//t3.put();
		}
	}
}