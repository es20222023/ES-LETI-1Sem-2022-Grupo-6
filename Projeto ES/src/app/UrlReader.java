package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlReader {
	
	
	public static void main(String[] args) {
		
		try {
			
			URL link= new URL("https://fenix.iscte-iul.pt/publico/publicPersonICale"
					+ "ndar.do?method=iCalendar&username=tamos@iscte.pt&password=56eL4iwoIkDYcylyIO0CXH0q2pDe"
					+ "lEJxN1BxRPezjRT1QR3p2SwDmZgUO2b3oLG2ZTZ3Ivu9p1YqgIBhmq68WKHSlyobMvO9hDGka2jh2clSGvyMRzqXjdWjDag33ikg");
			HttpURLConnection linkConnected= (HttpURLConnection) link.openConnection();
			System.out.println(linkConnected.getResponseCode()); //codigo https ==201
			
			if(linkConnected.getResponseCode()==200) {
				InputStream is= linkConnected.getInputStream();
				StringBuffer sb= new StringBuffer();
				BufferedReader br= new BufferedReader(new InputStreamReader(is));
				FileOutputStream file= new FileOutputStream("files/text_files/Horario.txt");
				BufferedWriter bw= new BufferedWriter(new OutputStreamWriter(file));
				String line= br.readLine();
				while(line!=null) {
					System.out.println(line);
					bw.write(line);
					bw.newLine();
					bw.flush();
					line= br.readLine();
				}
			}
			
		} catch (IOException e) {
			System.out.println("URL não é valido");
			
			
		}
	}

}

