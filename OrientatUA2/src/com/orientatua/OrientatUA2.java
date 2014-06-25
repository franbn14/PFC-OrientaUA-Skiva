package com.orientatua;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.orientatua.direction.Step;
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
	private DirectionManager directioner;
	
	private static final int REQUEST_CODE = 1234;
	private ListView wordsList;
	private String destination;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		voicer=VoiceManager.getInstance(OrientatUA2.this);
		voicer.speak("Bienvenido, ¿qué desea hacer?");
		voicer.waitSpeaking();
		gps = new GPSManager(OrientatUA2.this);
		
		Button btSpeak=(Button)findViewById(R.id.btSpeak);			
		wordsList = (ListView) findViewById(R.id.listView1);
		
		PackageManager pm = getPackageManager();
	    List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
	    if (activities.size() == 0)
	    {
	        btSpeak.setEnabled(false);	        
	    	Toast.makeText(getApplicationContext(), "Servicio de micrï¿½fono desactivado", Toast.LENGTH_LONG).show();
	    	voicer.speak("Micrï¿½fono desactivado");			
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
				//Toast.makeText(getApplicationContext(), "No se ha podido obtener la ubicaciÃ³n", Toast.LENGTH_LONG).show();
				voicer.speak("No se ha podido obtener la ubicaciÃ³n");	
			}						
		}
		else
			gps.setSettings();
	}
	
	public void getRoute() {	
		directioner=new DirectionManager(getApplicationContext());
		
		
		float results[]= new float[3];		
		ArrayList<String> distance=new ArrayList<String>();				
				
		Location current=gps.getCurrentLocation();		
		Address address=gps.getCoordinates(destination+" Universidad de Alicante, Alicante, España");
					
		if(address!=null) {
			distance.add("Address ok");
			directioner.makeRequest(current.getLatitude()+","+current.getLongitude(),address.getLatitude()+","+address.getLongitude());							
			voicer.speak(directioner.getIndication(directioner.getIndex()));
			voicer.setType(3);
			startVoiceRecognitionActivity();
		}
		else
			distance.add("Address null");
		
		wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,distance));
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	
		int type=voicer.getType();
		String answer;				
		
	    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
	    {	        
	        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,matches));	     
	        
	        switch(type) {
	        	case 0: //Ordenes generales
	        			voicer.resetResults();
	        			for(String word: matches) {	        	
				        	if(word.toLowerCase(Locale.getDefault()).contains("donde estoy") || word.toLowerCase(Locale.getDefault()).contains("dï¿½nde estoy"))
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
		        			voicer.speak("¿Ha dicho "+destination+"?");
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
	        			answer=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
	        			
	        			if(answer.toLowerCase(Locale.getDefault()).contains("sí") || answer.toLowerCase(Locale.getDefault()).contains("si")) 
	        				getRoute();	        			
	        			else if(answer.toLowerCase(Locale.getDefault()).contains("no")) {
	        				voicer.setType(1);
	        				startVoiceRecognitionActivity();
	        			}
	        			break;
	        			
	        	case 3:
		        		answer=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
	        			
	        			if(answer.toLowerCase(Locale.getDefault()).contains("siguiente")) {//Siguiente indicaciÃ³n	        				
	        				answer=directioner.getIndication(directioner.getIndex()+1);	        				        				
	        				voicer.speak(answer);
	        			}	        			
	        			else if(answer.toLowerCase(Locale.getDefault()).contains("repetir")) { //Repetir indicaciÃ³n actual
	        				answer=directioner.getIndication(directioner.getIndex());
	        				voicer.speak(answer);
	        			}
	        			else if(answer.toLowerCase(Locale.getDefault()).contains("salir")) { //Salir de la aplicaciÃ³n	        				
	        				finish();	        				
	        			}
	        			voicer.setType(3);	        		    	
        		    	startVoiceRecognitionActivity();        		    	
	        			break;	        			
	        }
	    }
	    else {		    		    	
	    	if(voicer.getType()!=3) {
	    		voicer.speak("No se ha podido reconocer el destino, por favor, repita. O diga Terminar.");
	    		voicer.setType(1);	    		
	    	}
	    	else
	    		voicer.setType(3);
	    	
	    	startVoiceRecognitionActivity();
	    }	    
	    super.onActivityResult(requestCode, resultCode, data);	
	}		
}
