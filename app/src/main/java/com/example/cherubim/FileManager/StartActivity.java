package com.example.cherubim.FileManager;


import android.app.*;
import android.content.*;
import android.os.*;
import android.telephony.*;
import android.view.*;

public class StartActivity extends Activity
{

	
    @Override
    public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(new StartView(this));
		
        Handler x = new Handler();
        x.postDelayed(new splashhandler(), 3000);

    }
	
	

	class splashhandler implements Runnable{

        public void run() {
            Intent i = new Intent(StartActivity.this,MainActivity.class);
			overridePendingTransition(R.anim.fade, R.anim.hold);
		    startActivity(i);
            finish();
			
        }
    }
}
