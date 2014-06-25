package com.orientatua;

import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orientatua.direction.*;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import java.lang.reflect.Type;
import android.text.Html;

public class DirectionManager {
	private static String key="AIzaSyBwu6y14fhrXhLiqNCaGD4vByhOUOtyr2Y";
	public Direction direction;
	private Context context;
	private String destination;
	private int index;	
	
	public DirectionManager(Context _context) {
		context=_context;
	}
	
	public void makeRequest(String address1, String address2) {
		try {
			RequestTask request=new RequestTask();
			request.execute(address1,address2);
			String result=request.get();
			
			if(result!=null) {
				Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
				if(!parseJSON(result))  
					Toast.makeText(context, "Result null", Toast.LENGTH_SHORT).show();
			}
			else {
				Log.i("Result","NULL");				
			}									
		} catch (InterruptedException e) {		
			e.printStackTrace();
		} catch (ExecutionException e) {			
			e.printStackTrace();
		}
	}
	public Direction getDirection() {
		return direction;
	}
	
	public void setDestination(String destination) {
		this.destination=destination;
	}
	
	public boolean parseJSON(String result) {
		//try {
			//JSONObject json = new JSONObject(result);
			
			Gson gson = new Gson();
            Type collectionType = new TypeToken<Direction>(){}.getType();
			direction=gson.fromJson(result, collectionType);
			
			if(direction.getStatus().equals("OK")) { //El status lo reconoce
				
				if(direction.getRoutes()==null) //Fallo con routes
					Toast.makeText(context, "Vacio", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(context, "No vacio", Toast.LENGTH_SHORT).show();
				
			Toast.makeText(context,direction.getRoutes().get(0).getLegs().get(0).getSteps().get(0).getInstruction(),Toast.LENGTH_SHORT).show();
				return true;
			}
			else
				return false;
			
		/*} catch (JSONException e) {
			return false;
		}*/
		
	}
	
	public String getIndication(int hopedIndex) {
		String indication="";
				
		if(hopedIndex<direction.getSteps().size()) {			
			indication=Html.fromHtml(direction.getSteps().get(hopedIndex).getInstruction()).toString();
			indication+=" y contin�a "+direction.getSteps().get(hopedIndex).getDistance().getValue()+" metros";			
		}
		else if(hopedIndex==index)
			indication="Ha llegado a su destino";
		else
			indication="El pr�ximo punto es su destino";
		
		return indication;
	}	
	
	public int getIndex() {
		return index;
	}
	
	public void nextIndex() {
		index++;
	}
	
	private class RequestTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String str="https://maps.googleapis.com/maps/api/directions/json";
		    str+="?origin="+params[0];
			str+="&destination="+params[1];
			str+="&key="+key;
			str+="&mode=walking";
			str+="&language=es";
			//str="https://maps.googleapis.com/maps/api/directions/json?origin=Calle%20San%20Pablo%2013%2003690%20San%20Vicente%20del%20RaspeigAlicanteEspa%C3%B1a&destination=Plaza%20Santa%20Faz03690SanVicentedelRaspeig%20Alicante%20Espa%C3%B1a&mode=walking";
			Log.i("URL",str);
			try {
				URL url= new URL(str);
	  		    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	  		    connection.setRequestMethod("GET");
	  		    connection.setRequestProperty("Content-length", "0");
	            connection.setUseCaches(false);
	            connection.setAllowUserInteraction(false);	            
	  		    connection.connect();
		  		
	  		    int status=connection.getResponseCode();
	  		    Log.i("Status",status+"");
	  		    if(status>=200 ||  status<=202) {
	  		    	BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));	  		    	
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line+"\n");	                
	                }
	                br.close();
	                return sb.toString();
	  		    }	  		    
				
			} catch (Exception ex) {			
				return null;
			}		
			return null;
		}
		
	}
}
