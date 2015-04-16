package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by kaisers on 4/15/15.
 */
public class FilterImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    private ProgressBar mProgressBar;

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
        setContentView(R.layout.filter_image_activity);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        // Get the URL associated with the Intent data.
        // @@ TODO -- you fill in here.
        Intent intent = getIntent();
        final Uri url = intent.getData();

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.


        // TODO: get the command to run from the thread pool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new FilterTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        } else {
            new FilterTask().execute(url);
        }

    }

    private class FilterTask extends AsyncTask<Uri, Integer, Uri> {

        private final int progressStates[] = {10,70};
        private int progressIndex = 0;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "in FilterTask.preExecute");
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Uri doInBackground(Uri... urls) {
            Log.d(TAG, "in FilterTask.doInBackground");
            publishProgress();
            Uri pathToFile = Utils.downloadImage(getApplicationContext(), urls[0]);
            publishProgress();
            return Utils.grayScaleFilter(getApplicationContext(), pathToFile);
        }

        @Override
        protected void onPostExecute(Uri pathToFile) {
            Log.d(TAG, "in FilterTask.postExecute");
            Intent result = new Intent();
            result.putExtra(Intent.EXTRA_TEXT, pathToFile.toString());
            setResult(Activity.RESULT_OK, result);
            mProgressBar.setVisibility(View.INVISIBLE);
            finish();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, "in FilterTask.progressUpdate");
            Log.d(TAG, progressStates[progressIndex]+"");
            mProgressBar.setProgress(progressStates[progressIndex]);
            progressIndex++;
        }
    }
}
