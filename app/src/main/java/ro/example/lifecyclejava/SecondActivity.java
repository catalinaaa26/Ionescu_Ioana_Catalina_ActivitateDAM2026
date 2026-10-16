package ro.example.lifecyclejava;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class SecondActivity extends LifecycleLoggingActivity {
    private static final int REQUEST_THIRD_ACTIVITY = 1;

    public static final String EXTRA_MESSAGE = "ro.example.lifecyclejava.EXTRA_MESSAGE";
    public static final String EXTRA_FIRST_VALUE = "ro.example.lifecyclejava.EXTRA_FIRST_VALUE";
    public static final String EXTRA_SECOND_VALUE = "ro.example.lifecyclejava.EXTRA_SECOND_VALUE";
    public static final String EXTRA_REPLY_MESSAGE = "ro.example.lifecyclejava.EXTRA_REPLY_MESSAGE";
    public static final String EXTRA_SUM = "ro.example.lifecyclejava.EXTRA_SUM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Button button = findViewById(R.id.button_open_third);
        button.setOnClickListener(view -> openThirdActivity());
    }

    private void openThirdActivity() {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MESSAGE, "Salut din activitatea a doua!");
        bundle.putInt(EXTRA_FIRST_VALUE, 12);
        bundle.putInt(EXTRA_SECOND_VALUE, 8);

        Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_THIRD_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_THIRD_ACTIVITY && resultCode == RESULT_OK && data != null) {
            Bundle reply = data.getExtras();
            if (reply != null) {
                String message = reply.getString(EXTRA_REPLY_MESSAGE, "Mesaj primit.");
                int sum = reply.getInt(EXTRA_SUM, 0);
                Toast.makeText(
                        this,
                        message + " Suma valorilor este " + sum + ".",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}
