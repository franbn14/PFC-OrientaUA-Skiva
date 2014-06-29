package com.orientatua;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import com.orientatua.direction.Compass;
import com.orientatua.direction.IndicationThread;
import com.orientatua.direction.Step;
import com.orientatua2.R;

import android.hardware.SensorManager;
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
	private IndicationThread thread;
	private Timer timer;
	
	private static final int REQUEST_CODE = 1234;
	private ListView wordsList;
	private String destination;
	private Compass compass; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		voicer=VoiceManager.getInstance(getApplicationContext());		
		gps = new GPSManager(getApplicationContext());
		compass=new Compass(getApplicationContext(), (SensorManager)getSystemService(SENSOR_SERVICE));
		
		Button btSpeak=(Button)findViewById(R.id.btSpeak);			
		wordsList = (ListView) findViewById(R.id.listView1);
		
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
				//timer.cancel();
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
				String address=gps.getAddress(latitude,longitude);
				
				address=(address==null?address="No se ha podido encontrar su localizaciÃ³n":"Usted se encuentra en "+address);
				
				//Toast.makeText(getApplicationContext(), "Coordenadas: \nLatitud: " + latitude + "\nLongitud: " + longitude, Toast.LENGTH_LONG).show();
				//Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
				
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
			//thread=new IndicationThread(gps, directioner.getDirection().getSteps().get(0), voicer, getApplicationContext());
			voicer.speak(directioner.getIndication(directioner.getIndex(),compass.getCardinalPoint()));
			
			//timer = new Timer();
			//timer.schedule(thread, 1000, (directioner.getDirection().getSteps().get(0).getDuration().getValue()/4)*1000);
			//timer.schedule(thread, 1000, 5000);
			
			voicer.setType(3);
			startVoiceRecognitionActivity();
			//Toast.makeText(getApplicationContext(), "Address OK", Toast.LENGTH_SHORT).show();
		}/*
		else
			Toast.makeText(getApplicationContext(), "Address null", Toast.LENGTH_SHORT).show();*/
		
		wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,distance));
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	
		int type=voicer.getType();
		String answer="";				
		
	    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
	    {	        
	    	boolean found=false;
	        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,matches));	     
	        
	        switch(type) {
	        	case 0: //Ordenes generales
	        			voicer.resetResults();
	        			found=false;
	        			
	        			for(String word: matches) {	        	
				        	if(word.toLowerCase(Locale.getDefault()).contains("donde estoy") || word.toLowerCase(Locale.getDefault()).contains("dónde estoy")) {
				        		found=true;
				        		getCurrentLocation();
				        		break;
				        	}				        	
				        	else if(word.toLowerCase(Locale.getDefault()).contains("ir a destino")) {
				        		found=true;
				        		voicer.speak("Diga el destino");					        		
				        		voicer.setType(1);				        		
				        		break;
				        	}
				        	else if(word.equalsIgnoreCase("Salir")) {
				        		//timer.cancel();
				        		finish();	        	
				        	}				        
				        }
	        			if(!found)
	        				voicer.speak("No le he entendido, repita.");
	        			
	        			startVoiceRecognitionActivity();	
			        	break;
	        	case 1: //Orden ir a destino, preguntamos confirmacion
	        			
	        			voicer.setResults(matches);
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
	        			//answer=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);	        			
	        			//if(answer.toLowerCase(Locale.getDefault()).contains("sí") || answer.toLowerCase(Locale.getDefault()).contains("si"))
	        			found=false;
	        			for(String word : matches) {
	        				if(word.toLowerCase(Locale.getDefault()).contains("sí") || word.toLowerCase(Locale.getDefault()).contains("si")) {
	        					found=true;
	        					getRoute();
	        					break;
	        				}
	        				else if(word.toLowerCase(Locale.getDefault()).contains("no")) {
	        					found=true;
		        				voicer.setType(1);
		        				startVoiceRecognitionActivity();
		        				break;
		        			}
	        			}	        		
	        			if(!found) {
	        				voicer.speak("Repita la respuesta.");
	        				startVoiceRecognitionActivity();
	        			}
	        			break;
	        			
	        	case 3:
	        			/*if(thread.isChanged()) {
	        				thread.setRunning(false);
	        				timer.cancel();
	        				directioner.nextIndex();
	        				answer=directioner.getIndication(directioner.getIndex(),compass.getCardinalPoint());
	        				voicer.speak(answer);	        				
	        				thread=new IndicationThread(gps, directioner.getDirection().getSteps().get(directioner.getIndex()), voicer, getApplicationContext());	        					        			
	        				timer=new Timer();	        				
	        				//timer.schedule(thread, 1000, (directioner.getDirection().getSteps().get(directioner.getIndex()).getDuration().getValue()/4)*1000);
	        				timer.schedule(thread, 6000, 5000);
	        			}
	        			else {*/
	        				int situation=directioner.checkPosition(gps.getCurrentLocation(),voicer);
	        				situation=0;
	        				if(situation==2) {
	        					directioner.nextIndex();
		        				answer=directioner.getIndication(directioner.getIndex(),compass.getCardinalPoint());
		        				voicer.speak(answer);	        				
	        				}
	        				else if(situation==0) {	        					
	        					answer=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);			        		
				        		
			        			if(answer.toLowerCase(Locale.getDefault()).contains("siguiente")) {//Siguiente indicaciÃ³n	        				
			        				answer=directioner.getIndication(directioner.getIndex()+1,compass.getCardinalPoint());	        				        				
			        				voicer.speak(answer);		        				
			        			}	        			
			        			else if(answer.toLowerCase(Locale.getDefault()).contains("repetir")) { //Repetir indicaciÃ³n actual
			        				answer=directioner.getIndication(directioner.getIndex(),compass.getCardinalPoint());
			        				voicer.speak(answer);
			        			}
			        			else if(answer.toLowerCase(Locale.getDefault()).contains("salir")) { //Salir de la aplicaciÃ³n
			        				//timer.cancel();
			        				finish();	        				
			        			}
	        				}			        				        			        		  
	        			//}
	        			voicer.setType(3);	        		    	
        		    	startVoiceRecognitionActivity();
	        			break;	        			
	        }
	    }
	    else {		 
	    	checkMissUnderstood(type);
	    	/*if(voicer.getType()!=3) {
	    		voicer.speak("No se ha podido reconocer el destino, por favor, repita. O diga Terminar.");
	    		voicer.setType(1);	    		
	    	}
	    	else
	    		voicer.setType(3);*/	    	
	    	startVoiceRecognitionActivity();
	    }	    
	    super.onActivityResult(requestCode, resultCode, data);	
	}
	
	public void checkMissUnderstood(int type) {
		String answer="";
		switch(type) {
			case 0: 
					answer="No le he entendido, repita, por favor.";
					break;
			
			case 2:
					answer="Por favor, repita la respuesta.";
					break;
			
		}
		voicer.speak(answer);
		//startVoiceRecognitionActivity();
	}
}
