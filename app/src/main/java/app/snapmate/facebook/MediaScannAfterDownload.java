package app.snapmate.facebook;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

public class MediaScannAfterDownload implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection mediaScannerConnection;
    private File singleFile;

    public MediaScannAfterDownload(Context mcontext, File file) {
        singleFile = file;
        mediaScannerConnection = new MediaScannerConnection(mcontext, this);
        mediaScannerConnection.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mediaScannerConnection.scanFile(singleFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mediaScannerConnection.disconnect();
    }
}
