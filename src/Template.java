import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

import java.io.*;
import java.util.List;

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

		while(true){
			listing = ConnectionInit.client.getMetadataWithChildren(path);
			for (DbxEntry child : listing.children) {

				name_aux=child.name;
				name=name_aux;									

				ConnectionInit.client.delete(child.path);
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
	
	public void remove() throws DbxException{
		ConnectionInit.client.delete(path);
	}

	public boolean existsWithPrefix() throws DbxException{	
		DbxEntry.WithChildren listing;
		String name_aux=name;

		listing = ConnectionInit.client.getMetadataWithChildren(path);
		if(listing != null){
			for (DbxEntry child : listing.children) {	
				if(child.name.startsWith(name_aux)){				
					name_aux=child.name;
					name=name_aux;
					path = child.path;
					return true;	
				}
			} 
		}
		return false;
	}

	public void put() throws DbxException, IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {'1'}); //uploadFile must have a file content.
		ConnectionInit.client.uploadFile(path, DbxWriteMode.add(), -1, in);
		return;
	}
	public boolean doesExist() throws DbxException {
		List<DbxEntry> result;
		result = ConnectionInit.client.searchFileAndFolderNames("/space/Users/" + name, "Statistics");
		return !result.isEmpty();
	}
	public boolean searchFor() throws DbxException {
		List<DbxEntry> result;
		result = ConnectionInit.client.searchFileAndFolderNames(path, name);
		for (DbxEntry f : result)
		{
			if (f.name.equals(name))
			{
				return true;
			}
		}		
		return false;
	}

}
