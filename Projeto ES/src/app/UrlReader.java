package app;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlReader {
	
	
	public static void main(String[] args) {
		
		try {
			
			URL link= new URL("http://fenix.iscte-iul.pt/publico/publicPersonICale"
					+ "ndar.do?method=iCalendar&username=tamos@iscte.pt&password=56eL4iwoIkDYcylyIO0CXH0q2pDe"
					+ "lEJxN1BxRPezjRT1QR3p2SwDmZgUO2b3oLG2ZTZ3Ivu9p1YqgIBhmq68WKHSlyobMvO9hDGka2jh2clSGvyMRzqXjdWjDag33ikg");
			HttpURLConnection linkConnected= (HttpURLConnection) link.openConnection();
			System.out.println(linkConnected.getResponseCode());
			
		} catch (IOException e) {
			System.out.println("URL não é valido");
			
			
		}
	}

}
