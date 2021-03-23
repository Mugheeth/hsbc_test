package hsbc_parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class Runner {

	// Creating connection to connect to URL
	private static HttpURLConnection connection;
	// Creating logger
	public final static Logger logger = Logger.getLogger(Runner.class);

	public static void main(String[] args) {

		// Response retrieved is an input stream
		// Buffer reader to handle the input stream from get request
		BufferedReader bufferedReader;
		// String to read every line
		String line;
		// To concatenate each line from JSON
		StringBuffer responseContent = new StringBuffer();

		try {
			// Defining URL - JSON end point
			URL url = new URL(
					"https://samples.openweathermap.org/data/2.5/box/city?bbox=12,32,15,37,10&appid=b6907d289e10d714a6e88b30761fae22");

			// Start/Open connection to URL
			connection = (HttpURLConnection) url.openConnection();

			// Set request method to retrieve information
			connection.setRequestMethod("GET");

			// Close connection after 5s if cannot connect or read
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			// Getting the response code (200 successful)
			int status = connection.getResponseCode();

			// If connection not established
			if (status != 200) {
				// Log error in log file
				logger.error("Error: connection not established - connection response code: " + status);
				// Get the error message from the end point input stream and output it
				bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				bufferedReader.close();

			} else {
				// Get the data input stream from end point
				bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				// Loop until there is no more content to read / no next line
				while ((line = bufferedReader.readLine()) != null) {
					// Build response content from end point
					responseContent.append(line);
				}
				bufferedReader.close();
			}

			// Pass the concatenated response content from the end point into parse function
			// which parses and returns number of occurrences of names starting with T
			int numberofCitiesStartingWithT = parse(responseContent.toString());
			// Log the number to file
			// File location C:\temp\logging.log - in log4j.properties file
			logger.info(numberofCitiesStartingWithT);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Close connection at end of application
			connection.disconnect();
		}
	}

	public static int parse(String completeResponseFromEndPoint) {
		// Counter to count number of cities starting with T
		int citiesCount = 0;
		// JSON parser object to parse retrieved response data
		JSONParser parse = new JSONParser();
		try {
			// Parse data and store into a JSON object because of end point file structure
			JSONObject jsonObj = (JSONObject) parse.parse(completeResponseFromEndPoint);
			// Target the list and store values in JSON array
			JSONArray jsonarr = (JSONArray) jsonObj.get("list");
			// Loop through the array
			for (int i = 0; i < jsonarr.size(); i++) {
				// Get each object and store in a JSON object
				JSONObject o = (JSONObject) jsonarr.get(i);
				// Access name value using key
				String name = (String) o.get("name");
				// Check if starts with T
				if (name.startsWith("T")) {
					//System.out.println(name);
					// Increment count
					citiesCount++;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// Return cities starting with T count
		return citiesCount;
	}

}