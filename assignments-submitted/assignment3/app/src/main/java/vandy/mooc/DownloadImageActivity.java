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
        setContentView(R.layout.download_image_activity);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        // Get the URL associated with the Intent data.
        // @@ TODO -- you fill in here.
        //Intent intent = getIntent();
        //final Uri url = intent.getData();


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
        DownloadTask downloadTask = mRetainedFragment.getDownloadTask();

        if (downloadTask == null) {
            Log.d(TAG, "Creating new asyncTask");
            downloadTask = new DownloadTask();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "Destroying downloadActivity imagepath: " + mRetainedFragment.getImagePath());
        //Log.d(TAG, "Destroying downloadActivity url: " + mRetainedFragment.getUrl());
        // store the data in the fragment
        //mRetainedFragment.setUrl(mUri);
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
            try {
                Uri pathToImage = Utils.downloadImage(getApplicationContext(), urls[0]);
                mRetainedFragment.setImagePath(pathToImage);
            } catch (Exception e) {
                Log.d(TAG, "Caught exception downloading image");
            }
            return mRetainedFragment.getImagePath();
            //return Utils.downloadImage(getApplicationContext(), urls[0]);
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

    public static class RetainedFragment extends Fragment {

        // data object we want to retain
        private Uri mUrl;
        private Uri mImagePath;
        private DownloadTask mDownloadTask;


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

        public DownloadTask getDownloadTask() {
            return mDownloadTask;
        }

        public void setDownloadTask(DownloadTask downloadTask) {
            mDownloadTask = downloadTask;
        }

    }

}
