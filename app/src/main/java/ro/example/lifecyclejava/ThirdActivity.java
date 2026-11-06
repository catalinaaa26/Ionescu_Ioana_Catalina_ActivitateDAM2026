package ro.example.lifecyclejava;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ThirdActivity extends LifecycleLoggingActivity {
    public static final String EXTRA_ARICI_LIST = "ro.example.lifecyclejava.EXTRA_ARICI_LIST";
    public static final String EXTRA_SHOW_ARICI_LIST = "ro.example.lifecyclejava.EXTRA_SHOW_ARICI_LIST";
    private static final String STATE_ARICI_LIST = "state_arici_list";
    private static final String STATE_EDITING_POSITION = "state_editing_position";
    private static final int REQUEST_EDIT_ARICI = 12;

    private int firstValue;
    private int secondValue;
    private boolean showingAriciList;
    private ArrayList<AriciIIC> ariciList = new ArrayList<>();
    private AriciAdapter ariciAdapter;
    private TextView ariciCount;
    private int editingPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        Bundle received = getIntent().getExtras();
        showingAriciList = received != null && received.getBoolean(EXTRA_SHOW_ARICI_LIST, false);

        ListView listView = findViewById(R.id.list_arici);
        TextView emptyView = findViewById(R.id.text_list_empty);
        ariciCount = findViewById(R.id.text_list_count);
        Button actionButton = findViewById(R.id.button_return_to_second);

        if (showingAriciList) {
            initializeAriciList(received, savedInstanceState);
            showAriciList(listView, emptyView, actionButton);
            return;
        }

        showLegacyMessageFlow(received, listView, emptyView, actionButton);
    }

    private void initializeAriciList(Bundle received, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            editingPosition = savedInstanceState.getInt(STATE_EDITING_POSITION, -1);
        }
        ArrayList<AriciIIC> restoredList = getAriciList(savedInstanceState, STATE_ARICI_LIST);
        if (restoredList != null) {
            ariciList = restoredList;
        } else {
            ArrayList<AriciIIC> passedList = getAriciList(received, EXTRA_ARICI_LIST);
            if (passedList != null) {
                ariciList = passedList;
            }
        }
    }

    private void showAriciList(ListView listView, TextView emptyView, Button actionButton) {
        TextView title = findViewById(R.id.text_third_title);
        title.setText(R.string.list_title);

        ariciAdapter = new AriciAdapter(this, ariciList);
        listView.setAdapter(ariciAdapter);
        listView.setEmptyView(emptyView);
        updateAriciCount();

        listView.setOnItemClickListener((parent, view, position, id) -> openEditForm(position));

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            AriciIIC removed = ariciList.remove(position);
            ariciAdapter.notifyDataSetChanged();
            updateAriciCount();
            Toast.makeText(this, "Arici șters: " + removed.getNume(), Toast.LENGTH_LONG).show();
            return true;
        });

        actionButton.setText(R.string.back_to_collection);
        actionButton.setOnClickListener(view -> finishAriciList());
    }

    private void openEditForm(int position) {
        editingPosition = position;
        Bundle editData = new Bundle();
        editData.putParcelable(AddAriciActivity.EXTRA_ARICI, ariciList.get(position));

        Intent intent = new Intent(this, AddAriciActivity.class);
        intent.putExtras(editData);
        startActivityForResult(intent, REQUEST_EDIT_ARICI);
    }

    private void showLegacyMessageFlow(Bundle received, ListView listView,
                                       TextView emptyView, Button actionButton) {
        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        ariciCount.setVisibility(View.GONE);

        String message = "Nu a fost primit niciun mesaj.";
        if (received != null) {
            message = received.getString(SecondActivity.EXTRA_MESSAGE, message);
            firstValue = received.getInt(SecondActivity.EXTRA_FIRST_VALUE, 0);
            secondValue = received.getInt(SecondActivity.EXTRA_SECOND_VALUE, 0);
        }

        TextView title = findViewById(R.id.text_third_title);
        title.setText(R.string.legacy_third_title);

        Toast.makeText(
                this,
                message + " Valorile primite sunt " + firstValue + " și " + secondValue + ".",
                Toast.LENGTH_LONG
        ).show();

        actionButton.setText(R.string.legacy_return_button);
        actionButton.setOnClickListener(view -> sendResultToSecondActivity());
    }

    private void updateAriciCount() {
        ariciCount.setText(getString(R.string.arici_count, ariciList.size()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_EDIT_ARICI) {
            return;
        }

        if (resultCode == RESULT_OK && data != null
                && editingPosition >= 0 && editingPosition < ariciList.size()) {
            AriciIIC editedArici = getParcelable(data.getExtras(), AddAriciActivity.EXTRA_ARICI);
            if (editedArici != null) {
                ariciList.get(editingPosition).updateFrom(editedArici);
                ariciAdapter.notifyDataSetChanged();
                updateAriciCount();
                Toast.makeText(this, R.string.arici_updated, Toast.LENGTH_SHORT).show();
            }
        }
        editingPosition = -1;
    }

    private AriciIIC getParcelable(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return bundle.getParcelable(key, AriciIIC.class);
        }
        return bundle.getParcelable(key);
    }

    private ArrayList<AriciIIC> getAriciList(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return bundle.getParcelableArrayList(key, AriciIIC.class);
        }
        return bundle.getParcelableArrayList(key);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (showingAriciList) {
            outState.putParcelableArrayList(STATE_ARICI_LIST, ariciList);
            outState.putInt(STATE_EDITING_POSITION, editingPosition);
        }
        super.onSaveInstanceState(outState);
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

    private void finishAriciList() {
        Bundle resultBundle = new Bundle();
        resultBundle.putParcelableArrayList(EXTRA_ARICI_LIST, ariciList);
        Intent result = new Intent();
        result.putExtras(resultBundle);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (showingAriciList) {
            finishAriciList();
        } else {
            super.onBackPressed();
        }
    }
}
