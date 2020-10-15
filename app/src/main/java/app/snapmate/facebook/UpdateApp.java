package app.snapmate.facebook;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateApp extends AppCompatActivity {
    TextView updatemessage,notnow;
    Button updatebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);
        getSupportActionBar().hide(); //hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        updatemessage=findViewById(R.id.updatemessage);
        updatebutton=findViewById(R.id.updatebutton);
        notnow=findViewById(R.id.notnow);

        updatemessage.setText(getIntent().getStringExtra("message"));
        String txt= new String(getIntent().getStringExtra("update"));
        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(txt)));
                }catch (ActivityNotFoundException e){
//                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse()));

                }
            }
        });
        notnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateApp.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
