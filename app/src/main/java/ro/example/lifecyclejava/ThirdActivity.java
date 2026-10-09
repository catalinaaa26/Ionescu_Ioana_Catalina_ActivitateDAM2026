package ro.example.lifecyclejava;

import android.os.Bundle;

public class ThirdActivity extends LifecycleLoggingActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
    }
}