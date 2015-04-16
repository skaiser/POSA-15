package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by kaisers on 4/15/15.
 */
public class FilterImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    //private ProgressBar mProgressBar

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
        new FilterTask().execute(url);

    }

    private class FilterTask extends AsyncTask<Uri, Void, Uri> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "in FilterTask.preExecute");
        }

        @Override
        protected Uri doInBackground(Uri... urls) {
            Log.d(TAG, "in FilterTask.doInBackground");
            Uri pathToFile = Utils.downloadImage(getApplicationContext(), urls[0]);
            return Utils.grayScaleFilter(getApplicationContext(), pathToFile);
        }

        @Override
        protected void onPostExecute(Uri pathToFile) {
            Log.d(TAG, "in FilterTask.postExecute");
            Intent result = new Intent();
            result.putExtra(Intent.EXTRA_TEXT, pathToFile.toString());
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }
}
