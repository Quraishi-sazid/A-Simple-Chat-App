package com.Project.Online_Chat_App;
import com.parse.Parse; 
import android.app.Application;

import com.parse.Parse;

/**
 * The Class ChattApp is the Main Application class of this app. The onCreate
 * method of this class initializes the Parse.
 */
public class ChattApp extends Application
{

	@Override
	public void onCreate()
	{
		super.onCreate();

		Parse.initialize(this, "g1wxdi0J2GtXjnRTTvtMU8Ls8tdZiU5iU20xWc3j",
				"AG7R3Gv4650cNXAH3o5yaPwuNQ8rMAHmT5o3BNvy");

	}
}
