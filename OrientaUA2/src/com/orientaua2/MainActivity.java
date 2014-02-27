package com.orientaua2;

import java.util.Locale;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class MainActivity extends Activity implements OnInitListener {
	private GPSManager gps;
	private TextToSpeech tts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gps = new GPSManager(MainActivity.this);
		
		//Inicializamos el speaker, con idioma español					
		tts=new TextToSpeech(this, this);
		
		Button btCurrent = (Button)findViewById(R.id.btCurrent);
		
		btCurrent.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {											
				if(gps.canGetLocation()) {
					Location current=gps.getCurrentLocation();
					
					double latitude = current.getLatitude();
					double longitude = current.getLongitude();
					String address="Usted se encuentra en "+gps.getAddress(latitude,longitude);
					
					Toast.makeText(getApplicationContext(), "Coordenadas: \nLatitud: " + latitude + "\nLongitud: " + longitude, Toast.LENGTH_LONG).show();
					Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
					
					tts.speak(address, TextToSpeech.QUEUE_FLUSH, null);		
				}
				else
					gps.setSettings();
				
			}
		});
		
		Button btRoute = (Button)findViewById(R.id.btRoute);
		
		btRoute.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tts.speak("Diga el destino", TextToSpeech.QUEUE_FLUSH, null);				
			}
		});
		
		Button btExit = (Button)findViewById(R.id.btExit);
		btExit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		// Fin de la simulación
		
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
				tts.speak(address,  TextToSpeech.QUEUE_FLUSH, null);			
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

					}
				}
				else
					gps.setSettings();				
			}
		});
		// Fin parte de pruebas
	}

	@Override
	public void onInit(int status) {
		Locale lang = Locale.getDefault();
		//int result=tts.setLanguage(lang);		
		
		if (status == TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.getDefault());
			
			tts.speak("Bienvenido, ¿qué desea hacer?", TextToSpeech.QUEUE_FLUSH, null);		
			/*if(result==TextToSpeech.LANG_AVAILABLE)
				tts.setLanguage(lang);
			else
				Toast.makeText( this, "Error: Idioma no soportado", Toast.LENGTH_SHORT ).show();*/   
		}
		else
			Toast.makeText( this, "Error al inicializar TextToSpeech", Toast.LENGTH_SHORT ).show();
		
	}
	
	  @Override
      protected void onDestroy()
      {
	      if ( tts != null ) {
	    	  tts.stop();
	          tts.shutdown();
	      }
	      super.onDestroy();
      }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	

}
