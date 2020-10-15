package app.snapmate.facebook;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.ashudevs.facebookurlextractor.FacebookExtractor;
import com.ashudevs.facebookurlextractor.FacebookFile;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class DownloadForgroundService extends Service {

   private  WindowManager wm;
   private LinearLayout ll;
   private String url;
   private String readURL;
    private static final String ANDROID_CHANNEL_ID = "channel_id";
    int DOWNLOAD_NOTIFICATION_ID = (int) System.currentTimeMillis() % 10000;

    private String downloadPath;
    private  DownloadManager downloadManager;
    private long enq;

    public DownloadForgroundService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createChannelNotification();
        if (intent.hasExtra("ACTION_STOP")) {
            if (intent.getBooleanExtra("ACTION_STOP", false)) {
                stopForeground(true);
                stopSelf();
            }
        }

        Intent notificationIntent=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);

        Intent StopIntent = new Intent(getApplicationContext(), DownloadForgroundService.class);
        StopIntent.putExtra("ACTION_STOP", true);
        PendingIntent stopIntent = PendingIntent.getService(this, 0, StopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action stopServiceBtn = new NotificationCompat.Action(
                R.drawable.downloadicon,
                getResources().getString(R.string.stop),
                stopIntent);

        Notification notification = new NotificationCompat.Builder(this, ANDROID_CHANNEL_ID)
                .setContentTitle("FB Video Downloader is Active")
                .setContentText("Copy Video Link to Instant download")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentIntent(pendingIntent)
                .addAction(stopServiceBtn)
                .build();

        startForeground(1, notification);
        /*-----------------------------getting clipborad Data----------------------*/
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = clipBoard.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);
                url = item.getText().toString();
                if (url.contains("facebook.com")) {
                    if(url.contains("story_fbid")) {
                        String result = url.substring(url.indexOf("story_fbid=") + 11, url.indexOf("&"));
                        readURL = "https://www.facebook.com/"+result;
                        System.out.println("url checking"+ readURL);
                    } else  {
                        readURL = url;
                    }

                    //Toast.makeText(DownloadForgroundService.this, "its Facebook video url" + url, Toast.LENGTH_SHORT).show();
                      floatingViewbtn();
                    new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            wm.removeView(ll);
                        }
                    }.start();

                } else {
                    Toast.makeText(DownloadForgroundService.this, "PLz use Facebook Video Url", Toast.LENGTH_SHORT).show();
                }

                // Access your context here using YourActivityName.this
            }
        });


        return START_NOT_STICKY;

    }

    private void floatingViewbtn() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        ll = new LinearLayout(DownloadForgroundService.this);
        ll.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(layoutParams);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM| Gravity.RIGHT;
//        params.horizontalMargin=30;
        params.x = 0;
        params.y = 0;
        ImageView openapp = new ImageView(DownloadForgroundService.this);
        openapp.setImageResource(R.drawable.floatingbutnicon);
        ViewGroup.LayoutParams butnparams = new ViewGroup.LayoutParams(
                250, 250);
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.blink);
        openapp.setLayoutParams(butnparams);
        openapp.startAnimation(animation);

        ll.addView(openapp);
        wm.addView(ll, params);

        openapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startdownload();
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    private void startdownload() {
        new FacebookExtractor(this,readURL,false)
        {
            @Override
            protected void onExtractionComplete(FacebookFile facebookFile) {
                Log.e("TAG","---------------------------------------");
                Log.e("TAG","facebookFile AutherName :: "+facebookFile.getAuthor());
                Log.e("TAG","facebookFile FileName :: "+facebookFile.getFilename());
                Log.e("TAG","facebookFile Ext :: "+facebookFile.getExt());
                Log.e("TAG","facebookFile SD :: "+facebookFile.getSdUrl());
                Log.e("TAG","facebookFile HD :: "+facebookFile.getHdUrl());
                Log.e("TAG","---------------------------------------");
                String SD = facebookFile.getSdUrl();
                String HD = facebookFile.getHdUrl();
                if (HD != null) {
                    downloadPath = HD;
                } else {
                    downloadPath = SD;
                }

                System.out.println("SD url" + facebookFile.getSdUrl());
                System.out.println("HD url" + facebookFile.getHdUrl());

                downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                Uri uri2 = Uri.parse(downloadPath);
                DownloadManager.Request request1 = new DownloadManager.Request(uri2);
                Toast.makeText(DownloadForgroundService.this, "Video is Downloading", Toast.LENGTH_SHORT).show();
                request1.setVisibleInDownloadsUi(true);
                request1.setAllowedOverMetered(true);
                request1.setAllowedOverRoaming(true);
                request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request1.allowScanningByMediaScanner();
                request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/SnapMate", "SnapMate-" + uri2.getLastPathSegment());
                enq=downloadManager.enqueue(request1);
                registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
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

                                TastyToast.makeText(DownloadForgroundService.this,"Download Successful",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);

                                //TODO : Use this local uri and launch intent to open file
                            }

                        }
                    }
                }
            };

            @Override
            protected void onExtractionFail(Exception error) {
                Log.e("Error", "Error :: " + error.getMessage());
                TastyToast.makeText(DownloadForgroundService.this,""+error.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                error.printStackTrace();
            }
        };
    }
    public void sendonChannel(String notificationstring) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "notif_download")
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.downloadicon)
                .setContentTitle("Download in Progress")
                .setContentText(notificationstring)
                .setOngoing(true); // Again, THIS is the important line


        // .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager nomanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent appActivityIntent = new Intent(this, MainActivity.class
        );

        PendingIntent contentAppActivityIntent =
                PendingIntent.getActivity(
                        this,  // calling from Activity
                        0,
                        appActivityIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setContentIntent(contentAppActivityIntent);

        nomanager.notify(1, notification.build());

    }

    private File getDownloadLocation() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File youtubeDLDir = new File(downloadsDir, "SnapMate");
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir();
        return youtubeDLDir;
    }



    public void createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serveiceChannel = new NotificationChannel(
                    ANDROID_CHANNEL_ID,
                    "Forground Service Channel",
                    IMPORTANCE_DEFAULT
            );
            serveiceChannel.setSound(null, null);
            serveiceChannel.enableVibration(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serveiceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

