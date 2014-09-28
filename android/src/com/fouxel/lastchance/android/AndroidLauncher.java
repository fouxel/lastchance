package com.fouxel.lastchance.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fouxel.lastchance.MyGame;

public class AndroidLauncher extends AndroidApplication {
	


    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    

    double mLatitude=0;
    double mLongitude=0;
    
    MyGame game;
	
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		game = new MyGame();
		initialize(game, config);
        
        
	}
	
	@Override
	public void onBackPressed()
	{
		game.onBackPressed();
	}

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    @Override
    public void onStop() {

        super.onStop();
    }
    @Override
    public void onPause() {
    	
        super.onPause();
    }
    @Override
    public void onStart() {

        super.onStart();

    }
    @Override
    public void onResume() {
        super.onResume();

    }
    

}
