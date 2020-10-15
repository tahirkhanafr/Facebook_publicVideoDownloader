package app.snapmate.facebook.ui.notifications;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;

import app.snapmate.facebook.R;

import static android.app.Activity.RESULT_OK;

public class NotificationsFragment extends Fragment {

    private View view;
    private Context mContext;
    private ImageView mImageView;
    Uri getimageUri;


    private static final int GELLERY_PICK_CODE=1;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        mContext = getContext();
        mImageView= view.findViewById(R.id.img_fragment);
        mImageView.setVisibility(View.VISIBLE);
        runTimePermission();
        return view;
    }

    private void runTimePermission() {
        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        gellaryIntent();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void gellaryIntent() {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
        //intent.setType("image/*");
        startActivityForResult(intent,GELLERY_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GELLERY_PICK_CODE && resultCode == RESULT_OK ){
            if (data != null){
                getimageUri=data.getData();
                if (getimageUri != null){
                    try {
                        InputStream inputStream=getActivity().getContentResolver().openInputStream(getimageUri);
                        Bitmap  bitmap= BitmapFactory.decodeStream(inputStream);
                        mImageView.setImageBitmap(bitmap);


                    }
                    catch (Exception exception)
                    {
                        Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } }
        }
    }

    }


    // private void pickImageFromGallery()  {

 // }
//        File file = new File(Environment.getExternalStorageDirectory(), "Download/Instalizer/");
//
//        File[] files = file.listFiles(new FilenameFilter() {
//
//            @Override
//            public boolean accept(File dir, String filename) {
//
//                return filename.contains(".png");
//            }
//        });
//        imageArray = files;
//        System.out.println("file work "+files);
////        Intent intent = new Intent(Intent.ACTION_PICK);
////        intent.setType("Download/Instalizer/*");
////        startActivityForResult(intent, IMAGE_PICK_CODE);
////        String internetUri = "https://homepages.cae.wisc.edu/~ece533/images/girl.png";
//
////
//        Glide.with(mContext)
//                .load(files)
//                .placeholder(R.drawable.ic_launcher_foreground)
//                .into();
//







