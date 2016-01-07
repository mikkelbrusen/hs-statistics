import java.util.Locale;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxRequestConfig;


public class ConnectionInit {
	
	public static DbxAppInfo appInfo;
	public static DbxRequestConfig config;
	public static DbxClient client;
	static Feed[] feed;
	
	public static void main(String args[]) {
		init();
		
		feed = new Feed[2];
		for (int i = 0; i < 2; i++) 
		{
			feed[i] = new Feed();
			feed[i].start();
		}
		GeneralFeeds generalFeeds = new GeneralFeeds();
		generalFeeds.start();
	}
	
	public static void init() {
		final String APP_KEY = "75ozb45t6z1tq24";
		final String APP_SECRET = "xv6js8mv9li623h";
		
		appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
		config = new DbxRequestConfig("JavaTutorial/1.0",Locale.getDefault().toString());
		
		final String accessToken = "RUVKadBuhlAAAAAAAAAAEMs2wc1EUw5gU8nDyquO-9ZDnDa7p5qKxhjB0Ivn4NRz";

		client = new DbxClient(config, accessToken);
	}
}
