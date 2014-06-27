package com.orientatua.direction;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.orientatua.GPSManager;
import com.orientatua.VoiceManager;

public class IndicationThread extends Thread {
	private boolean running;
	private long timer;
	private Step step;		
	private GPSManager gps;	
	private float distance;
	private VoiceManager voicer;
	private Context context;	
		
	public IndicationThread(GPSManager _gps,Step _step,VoiceManager _voicer, Context _context) {
		context=_context;		
		running=true;
		distance=0;
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
	
	@Override	
	public void run() {
		float[] results=new float[3];
		Location current;
		while(running) {
			try {		
				current=gps.getCurrentLocation();														
				Location.distanceBetween(current.getLatitude(), current.getLongitude(), step.getLocation().getLatitude(), step.getLocation().getLongitude(), results);
				distance=results[0];
				Toast.makeText(context, "Quedan: "+distance, Toast.LENGTH_SHORT).show();
				Thread.sleep(5000);
				running=false;
				
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (NullPointerException ex)  {
				Toast.makeText(context, "Nullpointer", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
