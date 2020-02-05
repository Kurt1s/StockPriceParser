package financial.analysis.main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.*;

public class JSONparser {
	public static final int LOW = 0;
	public static final int VOL = 1;
	public static final int OPEN = 2;
	public static final int HIGH = 3;
	public static final int CLOSE = 4;

	public static void main(String[] args) throws Exception {
		URL url = new URL("https://www.alphavantage.co/"
				+ "query?function=TIME_SERIES_INTRADAY"
				+ "&symbol=MSFT"
				+ "&interval=5min"
				+ "&outputsize=full"
				+ "&apikey=demo");
		
		//Parse URL into HttpURLConnection in order to open the connection in order to get the JSON data
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		//Set the request to GET or POST as per the requirements
		conn.setRequestMethod("GET");
		//Use the connect method to create the connection bridge
		conn.connect();
		//Get the response status of the Rest API
		int responsecode = conn.getResponseCode();

		if(responsecode != 200) {
			System.err.println("BAD RESPONSE: " + responsecode);
			System.exit(1);
		}
		
		System.out.println("Connected ...\n");
		InputStream stream = conn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String response = "";
		String line = "";
		while((line = br.readLine() )!= null)
			//System.out.println(line);
			response += line;

		System.out.println(response.substring(0,2000));

		JSONObject responseJson = new JSONObject(response);
		JSONObject data = responseJson.getJSONObject("Time Series (5min)");

		String[] dates = JSONObject.getNames(data);

		ArrayList<FinanceWrapper> dataList = new ArrayList<FinanceWrapper>();

		for(String date : dates) { //traverse all dates, get each entry
			FinanceWrapper point = new FinanceWrapper();
			JSONObject entry = data.getJSONObject(date);
			String entry_data = "";

			point.setDate(date);

			Iterator<String> iterator = entry.keys();

			for(int i = 0;  iterator.hasNext(); i = (i+1)%5) { //traverse all fields
				entry_data = (String) entry.get(iterator.next());

				Double entry_data_d = Double.parseDouble(entry_data);

				switch( i ) {
				case LOW:
					point.setLow(entry_data_d);
					break;
				case VOL:
					Integer entry_data_i = Integer.parseInt(entry_data);
					point.setVolume(entry_data_i);
					break;
				case OPEN:
					point.setOpen(entry_data_d);
					break;
				case HIGH:
					point.setHigh(entry_data_d);
					break;
				case CLOSE: 
					point.setClose(entry_data_d);
					dataList.add(point);
					break;
				}

			}

		}

		dataList.sort((o1,o2) -> o1.getDate().compareTo(o2.getDate()));
		
		for(FinanceWrapper entry : dataList) System.out.println(entry);
		
	}


}


