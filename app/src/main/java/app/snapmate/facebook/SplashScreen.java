package app.snapmate.facebook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.Constants;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIME_OUT = 1000;
    private final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    private HashMap<String, Object> firebaseDefaultMap;
    String VERSION_CODE_KEY = "snapmate_setup_build_1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this will bind your MainActivity.class file with activity_main.
        setContentView(R.layout.activity_splash_screen);

        boolean connected = Connectivity.isConnectedWifi(getApplicationContext());
        boolean connectedmobiledata = Connectivity.isConnectedMobile(getApplicationContext());
        if (connected) {
            TastyToast.makeText(getApplicationContext(), "CONNECTED TO WIFI", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
        } else if (connectedmobiledata) {
            TastyToast.makeText(getApplicationContext(), "CONNECTED TO MOBILE", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();

        } else {

            TastyToast.makeText(getApplicationContext(), "NOT CONNECTED PLZ CONNECT TO INTERNET", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            //Setting message manually and performing action on button click
            builder.setMessage("Please Connect to the Internet, Then Retry")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Toast.makeText(getApplicationContext(),"Please connect to WIFI or Mobile data Manually", Toast.LENGTH_LONG).show();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //  Action for 'NO' Button
                    finish();
                    dialog.cancel();

                }
            });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Network Error ");
            alert.show();
            return;
            }







        /*------------------------Check Intro screen shared Preference------------------------*/
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
//  Create a new boolean and preference and set it to true
        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
//  If the activity has never started before...
        if (isFirstStart) {
            //  Launch app intro
            Intent i = new Intent(SplashScreen.this, IntroActivity.class);
            startActivity(i);
            //  Make a new preferences editor
            SharedPreferences.Editor e = getPrefs.edit();
            //  Edit preference to make it false because we don't want this to run again
            e.putBoolean("firstStart", false);
            //  Apply changes
            e.apply();
        }
        /*---------------------Remote Config----------------------*/
        //This is default Map
        firebaseDefaultMap = new HashMap<>();
        //Setting the Default Map Value with the current version code
        firebaseDefaultMap.put(VERSION_CODE_KEY, getCurrentVersionCode());

        //Setting that default Map to Firebase Remote Config
        mFirebaseRemoteConfig.setDefaultsAsync(firebaseDefaultMap);
        //Setting Developer Mode enabled to fast retrieve the values
        mFirebaseRemoteConfig.setConfigSettingsAsync(
                new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(TimeUnit.HOURS.toSeconds(60))
                        .build());
        //Fetching the values here
        mFirebaseRemoteConfig.fetch().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mFirebaseRemoteConfig.activate();
                //Log.d(TAG,"Fetched value: " + mFirebaseRemoteConfig.getString(VERSION_CODE_KEY));
                //calling function to check if new version is available or not
//                    checkForUpdate();
//                    private void checkForUpdate() {
                try {
                    JSONObject obj = new JSONObject(mFirebaseRemoteConfig.getString("snapmate_setup_build_1"));

                    int latestAppVersion = obj.getInt("current_version_code");
                    String message = obj.getString("message");
                    String updateLink = obj.getString("updateLink");
                    if (latestAppVersion > getCurrentVersionCode()) {
                        Intent intent = new Intent(SplashScreen.this, UpdateApp.class);
                        //Create the bundle
                        Bundle bundle = new Bundle();
                        //Add your data to bundle
                        bundle.putString("message", message);
                        bundle.putString("update", updateLink);
                        //Add the bundle to the intent
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        splashcsreen();
                    }

                } catch (Exception e) {
                    // System.out.println(e.getMessage());
                }
            } else
                TastyToast.makeText(SplashScreen.this, "Please Connect to the Internet ", TastyToast.ERROR, TastyToast.LENGTH_LONG).show();
//                Toast.makeText(SplashScreen.this,"Someting went wrong please try again",Toast.LENGTH_SHORT).show();
        });


//
//        //This is default Map
//        firebaseDefaultMap = new HashMap<>();
//        //Setting the Default Map Value with the current version code
//        firebaseDefaultMap.put(VERSION_CODE_KEY, getCurrentVersionCode());
//
//        //Setting that default Map to Firebase Remote Config
//        mFirebaseRemoteConfig.setDefaultsAsync(firebaseDefaultMap);
//
//        //Setting Developer Mode enabled to fast retrieve the values
//        mFirebaseRemoteConfig.setConfigSettingsAsync(
//                new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(TimeUnit.HOURS.toSeconds(12))
//                        .build());
//
//        //Fetching the values here
//        mFirebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
//
//
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    mFirebaseRemoteConfig.activate();
//                    //Log.d(TAG,"Fetched value: " + mFirebaseRemoteConfig.getString(VERSION_CODE_KEY));
//                    //calling function to check if new version is available or not
//                    checkForUpdate();
//                }else
//                    Toast.makeText(SplashScreen.this,"Someting went wrong please try again",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//       // Log.d(TAG, "Default value: " + mFirebaseRemoteConfig.getString(VERSION_CODE_KEY));


    }





    private void splashcsreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(SplashScreen.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);

    }
    /*---------------------Remote Config-----------------------------*/

    private int getCurrentVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
}



