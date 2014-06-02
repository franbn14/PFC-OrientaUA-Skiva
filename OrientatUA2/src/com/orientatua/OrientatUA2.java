package com.orientatua;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.orientatua2.R;

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
	
	private VoiceManager manager;
	
	private static final int REQUEST_CODE = 1234;
	private ListView wordsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		manager=VoiceManager.getInstance(OrientatUA2.this);
		manager.speak("Bienvenido");
		gps = new GPSManager(OrientatUA2.this);
		
		Button btSpeak=(Button)findViewById(R.id.btSpeak);			
		wordsList = (ListView) findViewById(R.id.listView1);
		
		PackageManager pm = getPackageManager();
	    List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
	    if (activities.size() == 0)
	    {
	        btSpeak.setEnabled(false);	        
	    	Toast.makeText(getApplicationContext(), "Servicio de micrófono desactivado", Toast.LENGTH_LONG).show();
	    	manager.speak("Micrófono desactivado");			
	    }	
		
	    
	    btSpeak.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//manager.startVoiceRecognition();
				startVoiceRecognitionActivity(0);
				
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
		/*
		
		Button btRoute = (Button)findViewById(R.id.btRoute);
		
		btRoute.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getRoute();			
			}
		});
				
		// Fin de la simulaci�n
		
		// Parte de pruebas no reales
		Button btCoords = (Button)findViewById(R.id.btCoords);
		
		btCoords.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String address = ((TextView)findViewById(R.id.tbAddress)).getText().toString();
				
				if(gps.canGetLocation()) {
					if(address!=null && !address.equals("")) {
						Toast.makeText(getApplicationContext(), gps.getCoordinates(address), Toast.LENGTH_LONG).show();
						Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
					}
				}
				else
					gps.setSettings();
			}
		});		
		
		Button btPlay = (Button)findViewById(R.id.btPlay);
		
		btPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {				
				String address = ((TextView)findViewById(R.id.tbAddress)).getText().toString();							
				//tts.speak(address,  TextToSpeech.QUEUE_FLUSH, null);			
			}
		});
		
		Button btAddress = (Button)findViewById(R.id.btAddress);
		
		btAddress.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Double latitude = Double.parseDouble((((TextView)findViewById(R.id.tbLatitude)).getText()).toString());
				Double longitude = Double.parseDouble((((TextView)findViewById(R.id.tbLongitude)).getText()).toString());
				
				if(gps.canGetLocation()) {						
					if(latitude!=null && longitude!=null) {				
						//Toast.makeText(getApplicationContext(), gps.getAddress(latitude,longitude), Toast.LENGTH_LONG).show();
						 Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345")); 
						 startActivity(intent);
						 Toast.makeText(getApplicationContext(), intent.getDataString(), Toast.LENGTH_SHORT);
					}
				}
				else
					gps.setSettings();				
			}
		});
		// Fin parte de pruebas		
		 */
	}
	
	private void startVoiceRecognitionActivity(int type) //0 general, 1 destino
	{
		//waitSpeaking();				
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
				
				manager.speak(address);	
			}						
			else {
				//Toast.makeText(getApplicationContext(), "No se ha podido obtener la ubicación", Toast.LENGTH_LONG).show();
				manager.speak("No se ha podido obtener la ubicación");	
			}						
		}
		else
			gps.setSettings();
	}
	
	public void getRoute() {
		manager.speak("Diga el destino");	
		startVoiceRecognitionActivity(1);
	}
	
	public void waitSpeaking() {
		/*boolean speaking;
		do {
			speaking=tts.isSpeaking();
		} while(speaking);*/
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
	    {
	        // Populate the wordsList with the String values the recognition engine thought it heard
	        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,matches));	     
	        
	        for(String word: matches) {	        	
	        	if(word.toLowerCase(Locale.getDefault()).contains("donde estoy"))
	        		getCurrentLocation();
	        	
	        	else if(word.toLowerCase(Locale.getDefault()).contains("ir a destino"))
	        		getRoute();
	        	
	        	else if(word.equalsIgnoreCase("Salir"))
	        		finish();	        	
	        }
	    }
	    else {	    	
	    	manager.speak("No se ha podido reconocer el destino, por favor, repita. O diga Terminar.");
	    	startVoiceRecognitionActivity(1);
	    }
	    
	    super.onActivityResult(requestCode, resultCode, data);	
	}
/*
	@Override
	public void onInit(int status) {
		Locale lang = Locale.getDefault();
		//int result=//tts.setLanguage(lang);		
		
		if (status == TextToSpeech.SUCCESS) {
			//tts.setLanguage(Locale.getDefault());
			
			//tts.speak("Bienvenido, ¿qué desea hacer?", TextToSpeech.QUEUE_FLUSH, null);	
			startVoiceRecognitionActivity(0);	
		}
		else
			Toast.makeText( this, "Error al inicializar TextToSpeech", Toast.LENGTH_SHORT ).show();
		
	}
	
	  @Override
      protected void onDestroy()
      {
	      if ( tts != null ) {
	    	  //tts.stop();
	          //tts.shutdown();
	      }
	      super.onDestroy();
      }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

	

}
