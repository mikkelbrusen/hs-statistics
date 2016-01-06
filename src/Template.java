import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;
import java.io.*;

public class Template {
	String path;
	String name;
	
	public Template(String path, String name) {
		this.path = path;
		this.name = name;
	}
	
	public void get() throws DbxException, IOException{	
		DbxEntry.WithChildren listing;
		String name_aux=name;

		// Repeat until one file/tuple is found
		while(true){
			listing = ConnectionInit.client.getMetadataWithChildren(path);
			for (DbxEntry child : listing.children) {
				
					name_aux=child.name;
					name=name_aux;									
					ByteArrayOutputStream out=new ByteArrayOutputStream();
					ConnectionInit.client.getFile(child.path,null,out);
					out.close();
					ConnectionInit.client.delete(child.path);
					return;
				       	
			}
			// Simple implementation based on busy-wait
			// We hence insert a delay of 10 seconds to minimise unsucessful checks
			System.out.println("Blocking operation (qry/get) was unsucessful, sleeping for a while...");
			try {
				Thread.sleep(10000);
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}
	public void put() throws DbxException, IOException{
		ByteArrayInputStream in = null;
		ConnectionInit.client.uploadFile(path, DbxWriteMode.add(), -1, in);
		return;
	}
	
}
