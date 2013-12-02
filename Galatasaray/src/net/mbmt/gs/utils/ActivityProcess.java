package net.mbmt.gs.utils;

import android.app.ProgressDialog;


public class ActivityProcess {
	private Exception failEx;
	private IActivityProcessContext aph;
	private String waitTitle;
	private String waitMessage;
	private ProgressDialog pd;
	
	public ActivityProcess(IActivityProcessContext activityProcessHandler,
			String waitTitle, String waitMessage) {
		this.aph = activityProcessHandler;
		this.waitTitle = waitTitle;
		this.waitMessage = waitMessage;
	}
	
	public void execute() {
		pd = ProgressDialog.show(Global.Activity, waitTitle, waitMessage);
		new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					aph.process();
					Global.Activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							aph.success();							
						}
					});
				}
				catch (GSException ex) {
					failEx = ex;
					Global.Activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							aph.fail(failEx);							
						}
					});
				}
				finally {
					pd.dismiss();
				}
			}
		}).start();
	}
	
	public static void execute(IActivityProcessContext activityProcessHandler,
			String waitTitle, String waitMessage) {
		new ActivityProcess(activityProcessHandler, waitTitle, waitMessage).execute();
	}
}
