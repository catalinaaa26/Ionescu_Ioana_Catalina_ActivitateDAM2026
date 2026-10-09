package ro.example.lifecyclejava;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SecondActivity extends LifecycleLoggingActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Button button = findViewById(R.id.button_open_third);
        button.setOnClickListener(view ->
                startActivity(new Intent(SecondActivity.this, ThirdActivity.class)));
    }
}