package ro.example.lifecyclejava;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends LifecycleLoggingActivity {
    private static final int REQUEST_ADD_ARICI = 10;

    private TextView ariciDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ariciDetails = findViewById(R.id.text_arici_details);
        Button addButton = findViewById(R.id.button_add_arici);
        addButton.setOnClickListener(view ->
                startActivityForResult(
                        new Intent(MainActivity.this, AddAriciActivity.class),
                        REQUEST_ADD_ARICI
                ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_ADD_ARICI || resultCode != RESULT_OK || data == null) {
            return;
        }

        Bundle result = data.getExtras();
        if (result == null) {
            Toast.makeText(this, "Nu au fost primite datele ariciului.", Toast.LENGTH_SHORT).show();
            return;
        }

        AriciIIC arici;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arici = result.getSerializable(AddAriciActivity.EXTRA_ARICI, AriciIIC.class);
        } else {
            arici = (AriciIIC) result.getSerializable(AddAriciActivity.EXTRA_ARICI);
        }

        if (arici != null) {
            ariciDetails.setText(arici.toDisplayText());
            Toast.makeText(this, "Ariciul „" + arici.toDisplayText().split("\\n")[0]
                    + "” a fost adăugat.", Toast.LENGTH_LONG).show();
        }
    }
}
