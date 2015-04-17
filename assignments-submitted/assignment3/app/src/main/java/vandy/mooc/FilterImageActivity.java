package vandy.mooc;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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
    private RetainedFragment mRetainedFragment;

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


        FragmentManager fm = getFragmentManager();
        mRetainedFragment = (RetainedFragment) fm.findFragmentByTag("data");
        // create the fragment and data the first time
        if (mRetainedFragment == null) {
            Log.d(TAG, "First time in!");
            // add the fragment
            mRetainedFragment = new RetainedFragment();
            fm.beginTransaction().add(mRetainedFragment, "data").commit();
            // stash URL for first time.
            mRetainedFragment.setUrl(getIntent().getData());
            Log.d(TAG, "XXXXXXXXX url is: " + mRetainedFragment.getUrl());
        } else {
            // Try to retrieve imagepath from previous download
            Uri pathToImage = mRetainedFragment.getImagePath();

            // If we have it, we're done!
            if (pathToImage != null) {
                Log.d(TAG, "We're done: " + pathToImage);
                Intent result = new Intent();
                result.putExtra(Intent.EXTRA_TEXT, pathToImage.toString());
                setResult(Activity.RESULT_OK, result);
                finish();
            } else {
                Log.d(TAG, "Continuing");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FilterTask downloadTask = mRetainedFragment.getDownloadTask();
        mProgressBar.setVisibility(View.VISIBLE);

        if (downloadTask == null) {
            Log.d(TAG, "Creating new asyncTask");
            downloadTask = new FilterTask();
            mRetainedFragment.setDownloadTask(downloadTask);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mRetainedFragment.getUrl());
            } else {
                downloadTask.execute(mRetainedFragment.getUrl());
            }
        } else {
            Log.d(TAG, "Reusing asyncTask");
        }
    }

    private class FilterTask extends AsyncTask<Uri, Integer, Uri> {

        private final int progressStates[] = {10,70};
        private int progressIndex = 0;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "in FilterTask.preExecute");
        }

        @Override
        protected Uri doInBackground(Uri... urls) {
            Log.d(TAG, "in FilterTask.doInBackground");
            //publishProgress();
            try {
                Uri pathToImage = Utils.downloadImage(getApplicationContext(), urls[0]);
              //  publishProgress();
                mRetainedFragment.setImagePath(Utils.grayScaleFilter(getApplicationContext(), pathToImage));
            } catch (Exception e) {
                Log.d(TAG, "Caught exception downloading image");
            }
            return mRetainedFragment.getImagePath();
        }

        @Override
        protected void onPostExecute(Uri pathToFile) {
            Log.d(TAG, "in FilterTask.postExecute");
            Intent result = new Intent();
            result.putExtra(Intent.EXTRA_TEXT, pathToFile.toString());
            setResult(Activity.RESULT_OK, result);
            //mProgressBar.setVisibility(View.INVISIBLE);
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




    public static class RetainedFragment extends Fragment {

        // data object we want to retain
        private Uri mUrl;
        private Uri mImagePath;
        private FilterTask mDownloadTask;


        // this method is only called once for this fragment
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // retain this fragment
            setRetainInstance(true);
        }

        public void setUrl(Uri url) {
            mUrl = url;
        }

        public Uri getUrl() {
            return mUrl;
        }


        public Uri getImagePath() {
            return mImagePath;
        }

        public void setImagePath(Uri imagePath) {
            mImagePath = imagePath;
        }

        public FilterTask getDownloadTask() {
            return mDownloadTask;
        }

        public void setDownloadTask(FilterTask downloadTask) {
            mDownloadTask = downloadTask;
        }

    }
}
