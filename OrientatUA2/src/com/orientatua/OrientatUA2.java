package com.orientatua;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.orientatua2.R;

import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
 

public class OrientatUA2 extends Activity {
	private GPSManager gps;	
	private VoiceManager voicer;
	private DirectionsManager directions;
	
	private static final int REQUEST_CODE = 1234;
	private ListView wordsList;
	private String destination;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		voicer=VoiceManager.getInstance(OrientatUA2.this);
		voicer.speak("Bienvenido, �qu� desea hacer?");
		voicer.waitSpeaking();
		gps = new GPSManager(OrientatUA2.this);
		
		Button btSpeak=(Button)findViewById(R.id.btSpeak);			
		wordsList = (ListView) findViewById(R.id.listView1);
		
		PackageManager pm = getPackageManager();
	    List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
	    if (activities.size() == 0)
	    {
	        btSpeak.setEnabled(false);	        
	    	Toast.makeText(getApplicationContext(), "Servicio de micr�fono desactivado", Toast.LENGTH_LONG).show();
	    	voicer.speak("Micr�fono desactivado");			
	    }		
	    else {
	    	voicer.setType(0);
	    	startVoiceRecognitionActivity();
	    }
	    
	    btSpeak.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				voicer.setType(0);
				startVoiceRecognitionActivity();
				
			}
		});
		//***** Fin de prueba de speechToText
	    
	    Button btExit = (Button)findViewById(R.id.btExit);
		btExit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	    
		Button btCurrent = (Button)findViewById(R.id.btCurrent);
		
		btCurrent.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {						
				getCurrentLocation();				
			}
		});		
	}
	
	private void startVoiceRecognitionActivity() //0 general, 1 destino
	{
		voicer.waitSpeaking();			
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");	    
	    startActivityForResult(intent, REQUEST_CODE);	
			    
	}
	
	public void getCurrentLocation() {		
		if(gps.canGetLocation()) {
			Location current=gps.getCurrentLocation();
			
			if(current!=null) {
				double latitude = current.getLatitude();
				double longitude = current.getLongitude();
				String address="Usted se encuentra en "+gps.getAddress(latitude,longitude);
				
				Toast.makeText(getApplicationContext(), "Coordenadas: \nLatitud: " + latitude + "\nLongitud: " + longitude, Toast.LENGTH_LONG).show();
				Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
				
				voicer.speak(address);	
			}						
			else {
				//Toast.makeText(getApplicationContext(), "No se ha podido obtener la ubicación", Toast.LENGTH_LONG).show();
				voicer.speak("No se ha podido obtener la ubicación");	
			}						
		}
		else
			gps.setSettings();
	}
	
	public void getRoute() {	
		directions=new DirectionsManager(getApplicationContext());
		
		
		float results[]= new float[3];		
		ArrayList<String> distance=new ArrayList<String>();				
		
		Address address=gps.getCoordinates("Plaza Santa Faz 03690 San Vicente del Raspeig Alicante Espa�a");
		Address address2=gps.getCoordinates("Calle San Pablo 13 03690 San Vicente del Raspeig Alicante Espa�a");
		
		//String error=directions.makeRequest("Calle San Pablo 13 03690 San Vicente del Raspeig Alicante Espa�a", "Plaza Santa Faz 03690 San Vicente del Raspeig Alicante Espa�a");
		directions.makeRequest(address.getLatitude()+","+address.getLongitude(),address2.getLatitude()+","+address2.getLongitude());
		
		//Parte real, comentada para probar la request
		/*Location current=gps.getCurrentLocation();
		//String address=gps.getAddress(gps.getCurrentLocation().getLatitude(), gps.getCurrentLocation().getLongitude());//gps.getCoordinates("Plaza Santa Faz, 03690 San Vicente del Raspeig, Alicante");
		Address address=gps.getCoordinates("Plaza Santa Faz 03690 San Vicente del Raspeig Alicante España");
		
		
		
		if(address!=null) {
			distance.add("Address ok");
			//distance.add(gps.getCurrentLocation().getLatitude()+","+gps.getCurrentLocation().getLongitude());
			distance.add(address.getLatitude()+","+address.getLongitude());
			//distance.add(gps.getAddress(current.getLatitude(), current.getLongitude()));
			Location.distanceBetween(gps.getCurrentLocation().getLatitude(), gps.getCurrentLocation().getLongitude(), address.getLatitude(), address.getLongitude(), results);			
		
			if(results!=null && results.length>0) {				
				for(int i=0; i<results.length; i++)
					distance.add(results[i]+"");		
			}		
			String error=directions.makeRequest("Calle San Pablo 13 03690 San Vicente del Raspeig Alicante España", address.toString());
			distance.add(error);
			
		}
		else
			distance.add("Address null");*/
		
		wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,distance));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	
		int type=voicer.getType();
		Toast.makeText(getApplicationContext(), "Tipo:"+type, Toast.LENGTH_SHORT).show();
		
	    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
	    {	        
	        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,matches));	     
	        
	        switch(type) {
	        	case 0: //Ordenes generales
	        			voicer.resetResults();
	        			for(String word: matches) {	        	
				        	if(word.toLowerCase(Locale.getDefault()).contains("donde estoy") || word.toLowerCase(Locale.getDefault()).contains("d�nde estoy"))
				        		getCurrentLocation();
				        	
				        	else if(word.toLowerCase(Locale.getDefault()).contains("ir a destino")) {				        		
				        		voicer.speak("Diga el destino");	
				        		//voicer.waitSpeaking();	 
				        		voicer.setType(1);
				        		startVoiceRecognitionActivity();	
				        		break;
				        	}
				        	else if(word.equalsIgnoreCase("Salir"))
				        		finish();	        	
				        }
			        	break;
	        	case 1: //Orden ir a destino, preguntamos confirmacion
	        			
	        			voicer.setResults( data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
	        			destination=voicer.getResult();	  
	        			
	        			if(destination!=null) {	        			
		        			voicer.speak("�Ha dicho "+destination+"?");
		        			voicer.setType(2);
		        			startVoiceRecognitionActivity();
	        			}
	        			else {
	        				voicer.resetResults();
	        				voicer.speak("Por favor, repita el destino");
	        				voicer.setType(1);
	        				startVoiceRecognitionActivity();
	        			}
	        			
	        			
	        			break;
	        			
	        	case 2: //Confirmacion de destino
	        			String answer=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
	        			
	        			if(answer.toLowerCase(Locale.getDefault()).contains("s�") || answer.toLowerCase(Locale.getDefault()).contains("si")) 
	        				getRoute();	        			
	        			else if(answer.toLowerCase(Locale.getDefault()).contains("no")) {
	        				voicer.setType(1);
	        				startVoiceRecognitionActivity();
	        			}
	        			break;
	        			
	        }
	    }
	    else {
	    	Toast.makeText(getApplicationContext(), "Code: "+resultCode, Toast.LENGTH_SHORT).show();
	    	voicer.speak("No se ha podido reconocer el destino, por favor, repita. O diga Terminar.");
	    	voicer.setType(1);
	    	startVoiceRecognitionActivity();
	    }
	    
	    super.onActivityResult(requestCode, resultCode, data);	
	}
}
