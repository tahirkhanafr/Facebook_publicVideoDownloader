package app.snapmate.facebook;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class HowToDownload extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_download);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
