package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

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
        new DownloadTask().execute(url);

    }

    private class DownloadTask extends AsyncTask<Uri, Void, Uri> {
        @Override
        protected Uri doInBackground(Uri... urls) {
            return Utils.downloadImage(getApplicationContext(), urls[0]);
        }

        @Override
        protected void onPostExecute(Uri pathToFile) {
            Intent result = new Intent();
            result.putExtra(Intent.EXTRA_TEXT, pathToFile.toString());
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }

}
