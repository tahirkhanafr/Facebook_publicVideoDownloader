package app.snapmate.facebook;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class GalleryActivity extends AppCompatActivity {

    private Cursor videocursor;
    GridView gv;
    private int video_column_index;
    int count;
    String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};
    String thumbPath;
    private Context mContext;
    private ArrayList<String> f=new ArrayList<String>();
    private File[] listFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       mContext=getApplicationContext();
        gv = (GridView) findViewById(R.id.gridview);
        File file= new File(Environment.getExternalStorageDirectory()+ "/Download/SnapMate" );
        if (file.isDirectory()){
            listFile=file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.contains(".mp4");
                }
            });
            GridItem item;
            if (listFile != null){
                if (listFile.length != 0){
                    Arrays.sort(listFile, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
                    for (File value : listFile){
                        item= new GridItem();
                        item.setImage(value.getAbsolutePath());
                        f.add(value.getAbsolutePath());
                    }
                    gv.setAdapter(new VideoAdapter(getApplicationContext()));
                    gv.setOnItemClickListener(itemGridlistener);


                }else {
//                    noPermssionVIEW.setVisibility(View.GONE);
//                    noContent.setVisisbility(View.GONE);
                }
            }

        }

    }

//    private void init_phone_video_grid() {
//        System.gc();
//        String[] proj = {MediaStore.Video.Media._ID,
//                MediaStore.Video.Media.DATA,
//                MediaStore.Video.Media.DISPLAY_NAME,
//                MediaStore.Video.Media.SIZE};
//        String selection = MediaStore.Video.Media.DATA + " like?";
//        String[] selectionArgs = new String[]{"%SnapMate%"};
//        videocursor = getApplication().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                proj, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
//        count = videocursor.getCount();
//
//        gv.setAdapter(new VideoAdapter(getApplicationContext()));
//        gv.setOnItemClickListener(videogridlistener);
//    }

   private AdapterView.OnItemClickListener itemGridlistener=new AdapterView.OnItemClickListener() {
       @Override
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           String file=f.get(position);
           Intent intent = new Intent(Intent.ACTION_VIEW);
           intent.setDataAndType(Uri.parse(file), "video/mp4");
           startActivity(intent);

       }
   };

    public class VideoAdapter extends BaseAdapter {
        private Context vContext;
        int layoutResourceId;

        public VideoAdapter(Context c) {
            vContext = c;
        }

        public int getCount() {
            return f.size();
        }

        public Object getItem(int position) {
            return position;
        }
        public String getPicture( int position){
            return f.get(position);
        }

        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.gc();
            View listItemRow;
            listItemRow = LayoutInflater.from(vContext).inflate(R.layout.gridview, parent, false);
            if (convertView == null)
                listItemRow = View.inflate(vContext, R.layout.gridview, null);
                ImageView thumbImage = (ImageView) listItemRow.findViewById(R.id.icon);

                Glide.with(mContext)
                        .load(getPicture(position))
                        .into(thumbImage);
//                thumbImage.setImageURI(Uri.parse(thumbPath));

            return listItemRow;

        }
    }

}
