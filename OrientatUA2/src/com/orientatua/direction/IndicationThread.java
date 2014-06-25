package com.orientatua.direction;

public class IndicationThread extends Thread {
	private boolean running;
	private long timer;
	private Step step;		
	
	public IndicationThread() {
		running=false;
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
		while(running) {
			try {
				
				
				Thread.sleep(timer*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
