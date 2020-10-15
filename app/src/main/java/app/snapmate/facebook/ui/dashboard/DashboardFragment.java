package app.snapmate.facebook.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.snapmate.facebook.R;

public class DashboardFragment extends Fragment {

    private View view;
    private static final int GELLERY_PICK_CODE = 1;
    Uri imageUri;
    private Cursor videocursor;
    private int columnIndex;
    GridView gv;
    private int mGalleryItemBackground = 2;
    ArrayList<String> list;
    private Bitmap bitmap;


    private int video_column_index;
    int count;
    String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};
    String thumbPath;
    private Context mContext;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
//         mimageView=view.findViewById(R.id.img_fragment);
//        mimageView.setVisibility(View.VISIBLE);
//        videoView=view.findViewById(R.id.videoview);
//        videoView.setVisibility(view.VISIBLE);
//        MediaController mediaController=new MediaController(getContext());
//        mediaController.setAnchorView(videoView);
//        videoView.setMediaController(mediaController);
         mContext = getContext();
        gv = (GridView) view.findViewById(R.id.gridview);
        init_phone_video_grid();


        // runTimePermission();

        return view;
    }

    private void init_phone_video_grid() {
        System.gc();
        String[] proj = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE};
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%FACEBOOK VIDEO DOWNLOADER%"};
        videocursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                proj, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
        count = videocursor.getCount();

        gv.setAdapter(new VideoAdapter(getContext()));
        gv.setOnItemClickListener(videogridlistener);
    }

    private AdapterView.OnItemClickListener videogridlistener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            System.gc();
            video_column_index = videocursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videocursor.moveToPosition(position);
            String filename = videocursor.getString(video_column_index);
            /*   Intent intent = new Intent(MainActivity.this, ViewVideo.class);
                  intent.putExtra("videofilename", filename);
                  startActivity(intent);*/
//            Toast.makeText(getContext(), filename, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(filename), "video/mp4");
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
            return videocursor.getCount();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.gc();
            View listItemRow;
            listItemRow = LayoutInflater.from(vContext).inflate(R.layout.gridview, parent, false);
            ImageView thumbImage = (ImageView) listItemRow.findViewById(R.id.icon);

            videocursor.moveToPosition(position);
            // Get the current value for the requested column
                int videoId = videocursor.getInt(videocursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                Cursor videoThumbnailCursor = getActivity().getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + videoId, null, null);

                if (videoThumbnailCursor.moveToFirst()) {
                    thumbPath = videoThumbnailCursor.getString(videoThumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                    Log.i("ThumbPath: ", thumbPath);

                }
            Glide.with(mContext)
                    .load(thumbPath).into(thumbImage);
//                thumbImage.setImageURI(Uri.parse(thumbPath));
                    return listItemRow;

        }
    }
}





//    private void runTimePermission() {
//        Dexter.withContext(getContext())
//                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        galleryIntent();
//
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//                        TastyToast.makeText(getContext(),"Please give a permission!",TastyToast.LENGTH_LONG,TastyToast.WARNING);
//                    }
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken token) {
//                        token.continuePermissionRequest();
//
//                    }
//                }).check();
//    }

//    private void galleryIntent() {
////        Intent intent=new Intent(Intent.ACTION_PICK);
////        intent.setType("video/*");
////        startActivityForResult(intent,GELLERY_PICK_CODE);
//
//        // request only the image ID to be returned
//        String[] projection = {MediaStore.Images.Media._ID};
//// Create the cursor pointing to the SDCard
//        cursor = getActivity().getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                projection,
//                MediaStore.Images.Media.DATA + " like ? ",
//                new String[] {"%Instalizer%"},
//                null);
//// Get the column index of the image ID
//        columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
//
//
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK){
//            if (requestCode==GELLERY_PICK_CODE){
//                imageUri=data.getData();
//                videoView.setVideoURI(imageUri);
//            }
//        }
//    }
    


