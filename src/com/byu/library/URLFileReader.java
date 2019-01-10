package com.byu.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class URLFileReader {

	/**
	 * Create the URL and open the HTTP connection
	 * @param urlPath The URL file path to retrieve data
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public HttpURLConnection getHttpURLConnection(String urlPath) throws MalformedURLException, IOException {
		HttpURLConnection connection = null;
		URL url = new URL(urlPath); 
		connection = (HttpURLConnection)url.openConnection();
	    
	    return connection;
	}

	/**
	 * Get the URL content for the file from the connection
	 * @param conn
	 * @return The file data for the URL
	 * @throws IOException
	 */
	private String getContent(HttpURLConnection conn) throws IOException {
		StringBuffer content = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String input;
			while ((input = br.readLine()) != null) {
				//System.out.println(input);
				content.append(input+"\n");
			}
		} finally {
			if( br != null ) br.close();
		}
		
		return content.toString();
	}

	/**
	 * Call the methods to create the HTTP connection for the file
	 * @param urlPath
	 * @return The file content
	 */
	protected String retrieveUrlPathData(String urlPath) {
		HttpURLConnection urlConnection = null;
		String fileData = null;
		try {
			urlConnection = getHttpURLConnection(urlPath);
			if( urlConnection != null ) {
				int responseCode = urlConnection.getResponseCode();
				//System.out.printf("Response Code=%d", responseCode);
				if( responseCode == 200 ) {
					fileData = getContent(urlConnection);
				} else {
					System.out.printf("Unable to retrieve data for URL path %s, response code=%d. \n\n", urlPath, responseCode);
				}
			}
		} catch(Exception ex) {
			System.out.printf("Unable to retrieve word count data for url path %s: %s \n\n", urlPath, ex.getMessage());
		}
		
		return fileData;
	}
	
	/**
	 * Get the contents of the file from the URL path
	 * @param urlPath
	 * @return the contents
	 */
	public String getWordDataForUrlPath(String urlPath) {
		return retrieveUrlPathData(urlPath);
	}
}
