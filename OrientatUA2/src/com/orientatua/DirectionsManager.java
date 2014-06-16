package com.orientatua;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class DirectionsManager {
	private static String key="AIzaSyBwu6y14fhrXhLiqNCaGD4vByhOUOtyr2Y";
	private List<String> directions;
	private Context context;
	
	public DirectionsManager(Context _context) {
		context=_context;
	}
	
	public String makeRequest(String address1, String address2) {
		String str="https://maps.googleapis.com/maps/api/directions/json";
		str+="?origin="+address1;
		str+="&destination="+address2;
		str+="&key="+key;
		str+="&mode=walking";
		//str="https://maps.googleapis.com/maps/api/directions/json?origin=Calle%20San%20Pablo%2013%2003690%20San%20Vicente%20del%20RaspeigAlicanteEspa%C3%B1a&destination=Plaza%20Santa%20Faz03690SanVicentedelRaspeig%20Alicante%20Espa%C3%B1a&mode=walking";
		Toast.makeText(context, str, Toast.LENGTH_SHORT);
		Log.i("URL",str);
		System.out.println(str);
		try {
			URL url= new URL(str);
  		    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	  		InputStream response = new BufferedInputStream(connection.getInputStream());
	  	    
	  		if(response!=null && !response.equals("")) {
	  			boolean res=parseJSON(response.toString());	  				  		
	  			connection.disconnect();
	  			if(res)
	  				return "OK";
	  			else
	  				return "Error en parser";
	  		}
  			else
  				return "Error: "+connection.getErrorStream().toString();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Error: excepción inesperada";
		}		
	}
	
	public boolean parseJSON(String result) {
		try {
			JSONObject json = new JSONObject(result);
			String status=json.getString("status");
			
			if(status.equals("OK")) {
				
				return true;
			}
			else
				return false;
			
		} catch (JSONException e) {
			return false;
		}
		
	}
}
