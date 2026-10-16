package ro.example.lifecyclejava;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class ThirdActivity extends LifecycleLoggingActivity {
    private int firstValue;
    private int secondValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        Bundle received = getIntent().getExtras();
        String message = "Nu a fost primit niciun mesaj.";
        if (received != null) {
            message = received.getString(SecondActivity.EXTRA_MESSAGE, message);
            firstValue = received.getInt(SecondActivity.EXTRA_FIRST_VALUE, 0);
            secondValue = received.getInt(SecondActivity.EXTRA_SECOND_VALUE, 0);
        }

        Toast.makeText(
                this,
                message + " Valorile primite sunt " + firstValue + " și " + secondValue + ".",
                Toast.LENGTH_LONG
        ).show();

        Button returnButton = findViewById(R.id.button_return_to_second);
        returnButton.setOnClickListener(view -> sendResultToSecondActivity());
    }

    private void sendResultToSecondActivity() {
        Bundle reply = new Bundle();
        reply.putString(SecondActivity.EXTRA_REPLY_MESSAGE, "Mesaj de la activitatea a treia:");
        reply.putInt(SecondActivity.EXTRA_SUM, firstValue + secondValue);

        Intent result = new Intent();
        result.putExtras(reply);
        setResult(RESULT_OK, result);
        finish();
    }
}
