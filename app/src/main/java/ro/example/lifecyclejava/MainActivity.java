package ro.example.lifecyclejava;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends LifecycleLoggingActivity {
    private static final int REQUEST_ADD_ARICI = 10;
    private static final int REQUEST_ARICI_LIST = 11;
    private static final String STATE_ARICI_LIST = "state_arici_list";

    private TextView ariciDetails;
    private ArrayList<AriciIIC> ariciList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            ArrayList<AriciIIC> savedList = getAriciList(savedInstanceState, STATE_ARICI_LIST);
            if (savedList != null) {
                ariciList = savedList;
            }
        }

        ariciDetails = findViewById(R.id.text_arici_details);
        Button addButton = findViewById(R.id.button_add_arici);
        addButton.setOnClickListener(view ->
                startActivityForResult(
                        new Intent(MainActivity.this, AddAriciActivity.class),
                        REQUEST_ADD_ARICI
                ));

        Button listButton = findViewById(R.id.button_view_arici_list);
        listButton.setOnClickListener(view -> openAriciList());
        updateAriciPreview();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        if (requestCode == REQUEST_ADD_ARICI) {
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
                ariciList.add(arici);
                updateAriciPreview();
                Toast.makeText(this, "Ariciul „" + arici + "” a fost adăugat.", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_ARICI_LIST) {
            ArrayList<AriciIIC> updatedList = getAriciList(data.getExtras(), ThirdActivity.EXTRA_ARICI_LIST);
            if (updatedList != null) {
                ariciList = updatedList;
                updateAriciPreview();
            }
        }
    }

    private void openAriciList() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ThirdActivity.EXTRA_ARICI_LIST, ariciList);
        bundle.putBoolean(ThirdActivity.EXTRA_SHOW_ARICI_LIST, true);

        Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_ARICI_LIST);
    }

    private void updateAriciPreview() {
        if (ariciList.isEmpty()) {
            ariciDetails.setText(R.string.empty_collection);
            return;
        }

        AriciIIC lastArici = ariciList.get(ariciList.size() - 1);
        ariciDetails.setText(getString(R.string.arici_count, ariciList.size())
                + "\n\n" + lastArici.toDisplayText());
    }

    @SuppressWarnings("unchecked")
    private ArrayList<AriciIIC> getAriciList(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return bundle.getSerializable(key, ArrayList.class);
        }
        return (ArrayList<AriciIIC>) bundle.getSerializable(key);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_ARICI_LIST, ariciList);
        super.onSaveInstanceState(outState);
    }

}
