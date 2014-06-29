package com.orientatua.direction;

import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.widget.Toast;

import com.orientatua.GPSManager;
import com.orientatua.VoiceManager;

public class IndicationThread extends TimerTask {
	private boolean running;
	private long timer;
	private Step step;		
	private GPSManager gps;	
	private float distance;
	private VoiceManager voicer;
	private Context context;	
	private boolean change;
	
	private int amount;
		
	public IndicationThread(GPSManager _gps,Step _step,VoiceManager _voicer, Context _context) {
		context=_context;		
		running=true;
		distance=0;
		change=false;
		amount=0;
		try {			
			step=_step;
			timer=step.getDuration().getValue();
			gps=_gps;
			voicer=_voicer;					
		}
		catch(NullPointerException ex) {
			Toast.makeText(context, "Nullpointer Ex", Toast.LENGTH_SHORT).show();
		}
	}
		
	public void setRunning(boolean _running)  {
		running=_running;
	}
	
	public void setTimer(long _timer) {
		timer=_timer;
	}
	
	public void setStep(Step _step) {
		step=_step;
	}
	
	public boolean isChanged() {
		return change;
	}
	//final Handler handler=new Handler();
	
	@Override	
	public void run() {
		if(running) {
			try {
				float[] results=new float[3];
				Location current;
				current=gps.getCurrentLocation();		
				Location.distanceBetween(current.getLatitude(), current.getLongitude(), step.getLocation().getLatitude(), step.getLocation().getLongitude(), results);		
				distance=results[0];
				
				amount++;
				
				if(amount==3) {
					change=true;
					voicer.speak("Para");
					amount=0;
				}
				else
					voicer.speak("Continúa "+(int)distance);
			}										
			catch(Exception ex) {
				voicer.speak("Excepcionaca");
			}
		}		
	}
}
