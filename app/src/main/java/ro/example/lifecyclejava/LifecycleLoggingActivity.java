package ro.example.lifecyclejava;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class LifecycleLoggingActivity extends Activity {
    private static final String TAG = "ActivityLifecycle";

    private void logLifecycle(String callback) {
        String message = getClass().getSimpleName() + ": " + callback;
        Log.e(TAG, "ERROR " + message);
        Log.w(TAG, "WARNING " + message);
        Log.d(TAG, "DEBUG " + message);
        Log.i(TAG, "INFO " + message);
        Log.v(TAG, "VERBOSE " + message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        logLifecycle("onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        logLifecycle("onResume()");
    }

    @Override
    protected void onPause() {
        logLifecycle("onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        logLifecycle("onStop()");
        super.onStop();
    }
}