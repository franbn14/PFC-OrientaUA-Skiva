package com.orientaua2;

import java.util.Locale;
import android.os.Bundle;
import android.app.Activity;
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
					double latitude = gps.getLatitude();
					double longitude = gps.getLongitude();
					
					Toast.makeText(getApplicationContext(), "Coordenadas: \nLatitud: " + latitude + "\nLongitud: " + longitude, Toast.LENGTH_LONG).show();
					Toast.makeText(getApplicationContext(), gps.getAddress(latitude,longitude), Toast.LENGTH_LONG).show();
				}
				else
					gps.setSettings();
				
			}
		});
		
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
						Toast.makeText(getApplicationContext(), gps.getAddress(latitude,longitude), Toast.LENGTH_LONG).show();
					}
				}
				else
					gps.setSettings();				
			}
		});
	}

	@Override
	public void onInit(int status) {
		Locale lang = new Locale("es");
		tts.setLanguage(lang);		
		
		/*if(tts.isLanguageAvailable(lang)){
			tts.setLanguage(lang);
		}*/
		//if ( status == TextToSpeech.LANG_MISSING_DATA | status == TextToSpeech.LANG_NOT_SUPPORTED )
			//Toast.makeText( this, "ERROR LANG_MISSING_DATA | LANG_NOT_SUPPORTED", Toast.LENGTH_SHORT ).show();       		
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
