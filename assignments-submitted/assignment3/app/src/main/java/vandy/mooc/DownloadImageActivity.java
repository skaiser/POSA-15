package vandy.mooc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        // @@ TODO -- you fill in here.
        super.onCreate(savedInstanceState);

        // Get the URL associated with the Intent data.
        // @@ TODO -- you fill in here.
        Intent intent = getIntent();
        final Uri url = intent.getData();

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.


        // TODO: get the command to run from the thread pool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new DownloadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        } else {
            new DownloadTask().execute(url);
        }

    }

    private class DownloadTask extends AsyncTask<Uri, Void, Uri> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "in DownloadTask.preExecute");
            Utils.showToast(getApplicationContext(), "Downloading normal image...");
        }

        @Override
        protected Uri doInBackground(Uri... urls) {
            Log.d(TAG, "in DownloadTask.doInBackground");
            return Utils.downloadImage(getApplicationContext(), urls[0]);
        }

        @Override
        protected void onPostExecute(Uri pathToFile) {
            Log.d(TAG, "in DownloadTask.postExecute");
            Intent result = new Intent();
            result.putExtra(Intent.EXTRA_TEXT, pathToFile.toString());
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }

}
