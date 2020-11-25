package app.snapmate.facebook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

//import com.google.android.gms.ads.AdView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.OutputStream;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public final static int REQUEST_CODE = 39;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Switch aSwitch;
    private EditText editText, videourl;
    private Bitmap bitmap;
    private View btnDownload;
    private  ImageView imageView, imginfo,iv;
    private ClipboardManager clipboardManager;
    private ClipData clipData;
    private Button Rateus,Share;
    private  String sharelink;
    private String readURL;

    private ProgressBar progressBar;
    private TextView tvDownloadStatus;
    private ProgressBar pbLoading;
    CardView cardView1;
   // private AdView mAdView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private  OutputStream fos;
    LinearLayout showuprelativeLayout;



    String url;
    private static final String TAG = "DownloadingExample";
    private String downloadPath;
    private long enq;
    private  DownloadManager downloadManager;

    private static final int PROGRESS_DELAY = 1000;


    /*_____________________________ ON CREATE_______________________*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        tvDownloadStatus = findViewById(R.id.tv_status);
        showuprelativeLayout=findViewById(R.id.popupRl);
        iv=findViewById(R.id.iv);
        //mAdView = findViewById(R.id.adView);
        // pbLoading=findViewById(R.id.pb_status);

        /*-----------Hide CardView For OS  Q 10----------*/
        cardView1=findViewById(R.id.cardview1);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            cardView1.setVisibility(View.GONE);
        }
        else {
            cardView1.setVisibility(View.VISIBLE);
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        // Log.d(TAG, "Default value: " + mFirebaseRemoteConfig.getString(VERSION_CODE_KEY));

//        /* -------------------ads Start-----------------------------*/

//        MobileAds.initialize(this, String.valueOf(R.string.Application_ID));
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        /*-------------------- Hooks--------------*/
        /*------------------------shareIntent-------------*/
        // getting the intent that started your app
        Intent myapp = getIntent();
        // getting the action associated with that intent
        String actionOfTheIntentThatStartedMyApp = myapp.getAction();
        // checking the intent action
//        if(actionOfTheIntentThatStartedMyApp.equals(Intent.ACTION_SEND))
        if (Intent.ACTION_SEND.equals(actionOfTheIntentThatStartedMyApp)){
            Bundle extras = getIntent().getExtras();
            sharelink = extras.getString(Intent.EXTRA_TEXT);
        }

        /*--------------------fireBase Notification----------*/
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("MyNotifi","MyNotifi",NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
//        FirebaseMessaging.getInstance().subscribeToTopic("general")
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        String msg = "Successfull";
//                        if (!task.isSuccessful()) {
//                            msg = "Failed";
//                        }
//                        Log.d(TAG,msg);
//                    }
//                });


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        aSwitch = findViewById(R.id.switchh);
        editText = findViewById(R.id.edcopyurl);
        editText.setText(sharelink);
        imageView=findViewById(R.id.dirctcopyurl);
        imginfo=findViewById(R.id.info);
        imginfo.bringToFront();
        imageView.bringToFront();
        Rateus =findViewById(R.id.Rateus);
        Share=findViewById(R.id.Share);


        clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipData pData = clipboardManager.getPrimaryClip();
                if (pData != null) {
                    ClipData.Item item = pData.getItemAt(0);
                    String txtpaste = item.getText().toString();
                    editText.setText(txtpaste);
                }
                else { editText.setError(getString(R.string.url_empty));
                }
            }
        });
        imginfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    startActivity(new Intent(MainActivity.this,HowtoDownlaod10Q.class));
                }
                else {
                    Intent intent=new Intent(MainActivity.this,HowtoDownloadReview.class);
                    startActivity(intent);

                }

            }
        });

        /*-------------------- Toolbar--------------*/
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(null);
        }


        /*-------------------- Navigation Drawer Menu--------------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);


        if (isServiceRunning(getApplicationContext(), DownloadForgroundService.class)) {
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    isStoragePermissionGranted();
                    checkDrawOverlayPermission();
                    Toast.makeText(MainActivity.this, "Instant Download is On", Toast.LENGTH_SHORT).show();
                    startService();
                } else {
                    stopService();
                }
            }
        });
        Rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TastyToast.makeText(MainActivity.this,"Thanks for like our App",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
            }
        });
        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent sharelink =new Intent(Intent.ACTION_SEND);
                    sharelink.setType("text/plain");
                    sharelink.putExtra(Intent.EXTRA_SUBJECT,"Share App");
                    String shareMessage="https://play.google.com/store/apps/details?id=app.snapmate.facebook.video.downloader\n\n";
                    sharelink.putExtra(Intent.EXTRA_TEXT,shareMessage);
                    startActivity(Intent.createChooser(sharelink,"share by"));
                }
                catch (Exception e){
                    TastyToast.makeText(MainActivity.this,"Somthing went Wrong..",TastyToast.LENGTH_SHORT,TastyToast.ERROR);
                }


            }
        });
    }




    /*-------------------------------Menu Items------------------------------------------*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_home:
                break;
            case R.id.nav_MyDownloads:
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(intent);
                break;
//            case R.id.nav_BuyMeCoffee:
//                Intent intent1 = new Intent(MainActivity.this, buyMeCoffee.class);
//              startActivity(intent1);
//                break;
            case R.id.nav_FollowUs:
                String YourPageURL = "https://www.facebook.com/SnapMate-102591188251619";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YourPageURL));

                startActivity(browserIntent);
                break;

            case R.id.nav_HowToDownload:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    startActivity(new Intent(MainActivity.this, HowtoDownlaod10Q.class));
                }
                else {
                    Intent intent2=new Intent(MainActivity.this,HowtoDownloadReview.class);
                    startActivity(intent2);
                }
                break;
            case R.id.nav_PrivacyPolicy:
                Intent intent3=new Intent(MainActivity.this,PrivacyPolicy.class);
                startActivity(intent3);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /*-------------------option menu----------------------------------------*/
    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_meanu2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.howtodownload:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    startActivity(new Intent(MainActivity.this,HowtoDownlaod10Q.class));

                }
                else {
                    Intent intent=new Intent(MainActivity.this,HowtoDownloadReview.class);
                    startActivity(intent);
                }


                // do your code
                return true;
            case R.id.privacypolicy:
                Intent intent2=new Intent(MainActivity.this,PrivacyPolicy.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*---------------------------------------- Main Layout---------------------------------*/
    public void process(View view) {
        switch (view.getId()) {
            case R.id.switchh:
                if (aSwitch.isChecked()) {

                } else {
                    stopService();
                }
                break;
            case R.id.btnDownload:
                Uri uri = Uri.parse(editText.getText().toString());
                //saveVideo();
                startDownload();
                break;

        }
    }


    private static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }

        }
        return false;
    }

    private void startService() {
        Intent myService = new Intent(MainActivity.this, DownloadForgroundService.class);
        ContextCompat.startForegroundService(MainActivity.this, myService);
    }

    private void stopService() {
        stopService(new Intent(this, DownloadForgroundService.class));

    }


    @SuppressLint("StaticFieldLeak")
    public void startDownload() {
        if (!isStoragePermissionGranted()) {
            Toast.makeText(MainActivity.this, "grant storage permission and retry", Toast.LENGTH_LONG).show();
            return;
        }
        //editText.setText(sharelink);
        url = editText.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            editText.setError(getString(R.string.url_error));
            return;
        }
        if (url.contains("fb.watch"))
        {
            readURL=url;
            new FBVideoExtractor(this, readURL, false) {
                @Override
                protected void onExtractionComplete(FacebookFile facebookFile) {
//                    Log.e("TAG", "---------------------------------------");
//                    Log.e("TAG", "facebookFile AutherName :: " + facebookFile.getAuthor());
//                    Log.e("TAG", "facebookFile FileName :: " + facebookFile.getFilename());
//                    Log.e("TAG", "facebookFile Ext :: " + facebookFile.getExt());
//                    Log.e("TAG", "facebookFile SD :: " + facebookFile.getSdUrl());
//                    Log.e("TAG", "facebookFile HD :: " + facebookFile.getHdUrl());
//                    Log.e("TAG", "---------------------------------------");
                    String SD = facebookFile.getSdUrl();
                    String HD = facebookFile.getHdUrl();
                    if (HD != null) {
                        downloadPath = HD;
                    } else {
                        downloadPath = SD;
                    }

                    // System.out.println("SD url" + facebookFile.getSdUrl());
                    // System.out.println("HD url" + facebookFile.getHdUrl());

                    downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                    Uri uri2 = Uri.parse(downloadPath);
                    DownloadManager.Request request1 = new DownloadManager.Request(uri2);
                    Toast.makeText(MainActivity.this, "Video is Downloading", Toast.LENGTH_SHORT).show();
                    showuprelativeLayout.setVisibility(View.VISIBLE);
                    iv.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    tvDownloadStatus.setVisibility(View.VISIBLE);
//                    startProgressChecker();
                    request1.setVisibleInDownloadsUi(true);

                    request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request1.allowScanningByMediaScanner();
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            request1.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS + "/SnapMate", "SnapMate-" + uri2.getLastPathSegment());
                        request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS , "SnapMate-" + uri2.getLastPathSegment());
                    } else {
                        request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/SnapMate", "SnapMate-" + uri2.getLastPathSegment());
                    }
                    enq = downloadManager.enqueue(request1);
                    registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


                    final int UPDATE_PROGRESS = 5020;

                    @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == UPDATE_PROGRESS) {
                                int downloadedbyte = msg.arg1;
                                // System.out.println("Crsh" + msg.arg1);

                                int total = msg.arg2;
                                //String status = downloaded+"/"+total;
                                try {
                                    int percentage = (int) ((downloadedbyte * 100L) / total);
                                    tvDownloadStatus.setText(percentage + "%");
                                } catch (Exception e) {
                                    //  System.out.println("Crsh" + e.getMessage());
                                }
                            }
                            super.handleMessage(msg);
                        }
                    };
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean downloading = true;
                            while (downloading) {
                                DownloadManager.Query q = new DownloadManager.Query();
                                q.setFilterById(enq);
                                Cursor cursor = downloadManager.query(q);
                                cursor.moveToFirst();
                                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                    downloading = false;
                                }
                                //Post message to UI Thread
                                Message msg = handler.obtainMessage();
                                msg.what = UPDATE_PROGRESS;
                                //msg.obj = statusMessage(cursor);
                                msg.arg1 = bytes_downloaded;
                                msg.arg2 = bytes_total;
                                handler.sendMessage(msg);
                                cursor.close();
                            }
                        }
                    }).start();


                }

                BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(enq);
                            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            Cursor c = downloadManager.query(query);
                            if (c.moveToFirst()) {
                                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                                    String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                                    TastyToast.makeText(MainActivity.this, "Download Successful", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                                    progressBar.setVisibility(View.GONE);
                                    tvDownloadStatus.setVisibility(View.GONE);
                                    iv.setVisibility(View.VISIBLE);

                                    //TODO : Use this local uri and launch intent to open file
                                }
                            }
                        }
                    }
                };

                @Override
                protected void onExtractionFail(Exception error) {
                    //Log.e("Error", "Error :: " + error.getMessage());
                    TastyToast.makeText(MainActivity.this, "" + error.getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    error.printStackTrace();
                }
            };
        }
        else {
            if (url.contains("facebook.com")) {
                if (url.contains("story_fbid")) {
                    String result = url.substring(url.indexOf("story_fbid=") + 11, url.indexOf("&"));
                    readURL = "https://www.facebook.com/" + result;
                } else {
                    readURL = url;
                }

                /*-----------fcebook url ectractor----------*/
                new FBVideoExtractor(this, readURL, false) {
                    @Override
                    protected void onExtractionComplete(FacebookFile facebookFile) {
//                        Log.e("TAG", "---------------------------------------");
//                        Log.e("TAG", "facebookFile AutherName :: " + facebookFile.getAuthor());
//                        Log.e("TAG", "facebookFile FileName :: " + facebookFile.getFilename());
//                        Log.e("TAG", "facebookFile Ext :: " + facebookFile.getExt());
//                        Log.e("TAG", "facebookFile SD :: " + facebookFile.getSdUrl());
//                        Log.e("TAG", "facebookFile HD :: " + facebookFile.getHdUrl());
//                        Log.e("TAG", "---------------------------------------");
                        String SD = facebookFile.getSdUrl();
                        String HD = facebookFile.getHdUrl();
                        if (HD != null) {
                            downloadPath = HD;
                        } else {
                            downloadPath = SD;
                        }

                        // System.out.println("SD url" + facebookFile.getSdUrl());
                        // System.out.println("HD url" + facebookFile.getHdUrl());

                        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);


                        Uri uri2 = Uri.parse(downloadPath);
                        DownloadManager.Request request1 = new DownloadManager.Request(uri2);
                        Toast.makeText(MainActivity.this, "Video is Downloading", Toast.LENGTH_SHORT).show();
                        showuprelativeLayout.setVisibility(View.VISIBLE);
                        iv.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        tvDownloadStatus.setVisibility(View.VISIBLE);
//                    startProgressChecker();
                        request1.setVisibleInDownloadsUi(true);

                        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request1.allowScanningByMediaScanner();
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            FileOutputStream out;


//                            ContentResolver contentResolver= getContentResolver();
//                            ContentValues contentValues=new ContentValues();
//                            contentValues.put(MediaStore.Downloads.DISPLAY_NAME,"SnapMate-" + uri2.getLastPathSegment());
//                            contentValues.put(MediaStore.Downloads.MIME_TYPE,"video/mp4");
//                            contentValues.put(MediaStore.Downloads.RELATIVE_PATH,Environment.DIRECTORY_DOWNLOADS);
//                            Uri video= contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,contentValues);
//                            try {
//                                 fos = contentResolver.openOutputStream(Objects.requireNonNull(video));
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            }

//                            ContentValues valuesvideos;
//                            valuesvideos = new ContentValues();
//
//                            valuesvideos.put(MediaStore.Video.Media.TITLE, "SnapMate-" + uri2.getLastPathSegment());
//                            valuesvideos.put(MediaStore.Video.Media.DISPLAY_NAME, "SomeName");
//                            valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
//                            valuesvideos.put(MediaStore.Video.Media.IS_PENDING, 1);
//                            ContentResolver resolver = getContentResolver();
//                            Uri collection = MediaStore.Video.Media.getContentUri("external");
////                            valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, "DOWNLOADS/" + "SnapMateho");
//                            valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS+"/Snapmateeeee");
//                            Uri uriSavedVideo = resolver.insert(collection, valuesvideos);


//                            request1.setDestinationInExternalFilesDir(getApplicationContext(),out);
//                            request1.allowScanningByMediaScanner();
//                            request1.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS + "/SnapMate", "SnapMate-" + uri2.getLastPathSegment());
                            request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS , "SnapMate-" + uri2.getLastPathSegment());

                        } else {
                            request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/SnapMate", "SnapMate-" + uri2.getLastPathSegment());
                        }
                        enq = downloadManager.enqueue(request1);
                        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));




                        final int UPDATE_PROGRESS = 5020;

                        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == UPDATE_PROGRESS) {
                                    int downloadedbyte = msg.arg1;
                                    // System.out.println("Crsh" + msg.arg1);

                                    int total = msg.arg2;
                                    //String status = downloaded+"/"+total;
                                    try {
                                        int percentage = (int) ((downloadedbyte * 100L) / total);
                                        tvDownloadStatus.setText(percentage + "%");
                                    } catch (Exception e) {
                                        //  System.out.println("Crsh" + e.getMessage());
                                    }
                                }
                                super.handleMessage(msg);
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean downloading = true;
                                while (downloading) {
                                    DownloadManager.Query q = new DownloadManager.Query();
                                    q.setFilterById(enq);
                                    Cursor cursor = downloadManager.query(q);
                                    cursor.moveToFirst();
                                    int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                        downloading = false;
                                    }
                                    //Post message to UI Thread
                                    Message msg = handler.obtainMessage();
                                    msg.what = UPDATE_PROGRESS;
                                    //msg.obj = statusMessage(cursor);
                                    msg.arg1 = bytes_downloaded;
                                    msg.arg2 = bytes_total;
                                    handler.sendMessage(msg);
                                    cursor.close();
                                }
                            }
                        }).start();


                    }

                    BroadcastReceiver receiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context,Intent intent) {
                            String action = intent.getAction();
                            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                                DownloadManager.Query query = new DownloadManager.Query();
                                query.setFilterById(enq);
                                downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                Cursor c = downloadManager.query(query);
                                if (c.moveToFirst()) {
                                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {


//                                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
//                                        String title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
////                                        String destination="content://com.android.externalstorage.documents/document/primary%3ADownload"+"/Snapmate";
//                                        String file="file:///storage/emulated/0/Android/data/app.snapmate.facebook.video.downloader/files/Download/SnapMate/";
//                                        String file1="/storage/emulated/0/Android/data/app.snapmate.facebook.video.downloader/files/Download/SnapMate/";
//                                        System.out.println("path"+ uriString);
//                                         System.out.println("Title"+ title);

                                        TastyToast.makeText(MainActivity.this, "Download Successful", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                                        progressBar.setVisibility(View.GONE);
                                        tvDownloadStatus.setVisibility(View.GONE);
                                        iv.setVisibility(View.VISIBLE);

                                        //TODO : Use this local uri and launch intent to open file
                                    }

                                }
                            }

                        }
                    };

                    @Override
                    protected void onExtractionFail(Exception error) {
                        // Log.e("Error", "Error :: " + error.getMessage());
                        TastyToast.makeText(MainActivity.this, "" + error.getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        error.printStackTrace();
                    }
                };

            } else {
                editText.setError("Please Enter Facebook Video Url");
            }
        }
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(this)) {
            ShowMessageDialog();
            return false;
        } else {
            return true;
        }
    }

    private void ShowMessageDialog() {

        //Alerat for showing OVERLAY_PERMISSION
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Note!");
        builder.setMessage("For Instant Download popup on facebook app.We need a permission DRAW_OVER_OTHER APP. Click OK and On the permission");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showstart() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        tvDownloadStatus.setVisibility(View.VISIBLE);
        tvDownloadStatus.setText("0 %");
        iv.setVisibility(View.GONE);

        // pbLoading.setVisibility(View.VISIBLE);
        //working on git
    }

}
