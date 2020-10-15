package app.snapmate.facebook;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class RecievedDownload extends AppCompatActivity {

    ImageView recivevid;
    Button downloadbutton;
    TextView test;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recieved_download);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recivevid = findViewById(R.id.recievdvid);
        downloadbutton = findViewById(R.id.downloadbutton);
        test = findViewById(R.id.tv);


        downloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    VideoInfo videoInfo1= YoutubeDL.getInstance().getInfo(url);
//                    System.out.println(videoInfo1.getExt());
//                    System.out.println(videoInfo1.getDisplayId());
//                    System.out.println(videoInfo1.getFormats());
//                    System.out.println(videoInfo1.getWebpageUrl());
//                    System.out.println(videoInfo1.getHttpHeaders().toString());
//                } catch (YoutubeDLException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("Link");
        test.setText(url);
        Glide
                .with(getApplicationContext())
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(recivevid);

    }
}
