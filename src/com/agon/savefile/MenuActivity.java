package com.agon.savefile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MenuActivity extends Activity implements OnInitListener {
	private static final String TAG = "MenuActivity";
	private TextToSpeech tts;
	private boolean mAttachedToWindow;
	private boolean mTTSSelected;
	
	String filename = "sensorData"; 
	FileOutputStream outputStream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTTSSelected = false;
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		mAttachedToWindow = true;
		openOptionsMenu();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mAttachedToWindow = false;
	}	

	@Override
	public void openOptionsMenu() {
		if (mAttachedToWindow) {
			super.openOptionsMenu();
		}
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.stop:
			stopService(new Intent(this, AppService.class));
			return true;

		case R.id.tts:
			mTTSSelected = true;
			tts = new TextToSpeech(this, this);  
			tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
				@Override
				public void onDone(String utteranceId) {
					Log.w(TAG, "onDone");
					if (tts != null) {
						tts.stop();
						tts.shutdown();
					}                  
					finish();
				}

				@Override
				public void onError(String utteranceId) {
					Log.w(TAG, "onError");
				}

				@Override
				public void onStart(String utteranceId) {
					Log.w(TAG, "onStart");
				}
			});
			return true;

		case R.id.asr:
			Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			startActivityForResult(i, 0);  
			return true;
		
		case R.id.save:
			try {
		          outputStream = openFileOutput(filename, Context.MODE_APPEND);
		          OutputStreamWriter outputStreamWriter = new OutputStreamWriter (outputStream);
		          outputStreamWriter.write(filename);
		          outputStreamWriter.close();
		        } 
		       catch (Exception e) {
		          e.printStackTrace();
		        }
			return true;
			
		case R.id.load:

		   //Printing the file in logcat just to verify the contents 

		      FileInputStream inputStream;

		      try
		        {
		            inputStream = openFileInput(filename);

		            InputStreamReader inputStreamReader = new InputStreamReader (inputStream);

		            char[] buffer = new char[filename.length()];

		            inputStreamReader.read(buffer);

		            String input = buffer.toString();

		            Log.i("Salida",input);

		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		        }
		    return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public void onOptionsMenuClosed(Menu menu) {
		if (!mTTSSelected) 
			finish();
	}


	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"helloID");				
				tts.speak("Hello Glass!", TextToSpeech.QUEUE_FLUSH, map);
			}
		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}    
}
