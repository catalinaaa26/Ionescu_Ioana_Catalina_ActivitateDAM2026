package ro.example.lifecyclejava;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AddAriciActivity extends LifecycleLoggingActivity {
    public static final String EXTRA_ARICI = "ro.example.lifecyclejava.EXTRA_ARICI";

    private EditText nameInput;
    private EditText ageInput;
    private RadioGroup sexGroup;
    private Spinner habitatSpinner;
    private CheckBox shelterCheckBox;
    private RatingBar friendshipRating;
    private Switch nocturnalSwitch;
    private AriciIIC editingArici;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_arici);

        nameInput = findViewById(R.id.edit_arici_name);
        ageInput = findViewById(R.id.edit_arici_age);
        sexGroup = findViewById(R.id.radio_group_sex);
        habitatSpinner = findViewById(R.id.spinner_habitat);
        shelterCheckBox = findViewById(R.id.checkbox_shelter);
        friendshipRating = findViewById(R.id.rating_friendship);
        nocturnalSwitch = findViewById(R.id.switch_nocturnal);

        Button saveButton = findViewById(R.id.button_save_arici);
        saveButton.setOnClickListener(view -> saveArici());

        Bundle received = getIntent().getExtras();
        editingArici = getParcelable(received, EXTRA_ARICI);
        if (editingArici != null) {
            TextView title = findViewById(R.id.text_form_title);
            title.setText(R.string.edit_form_title);
            saveButton.setText(R.string.update_arici);
            if (savedInstanceState == null) {
                populateFields(editingArici);
            }
        }
    }

    private void populateFields(AriciIIC arici) {
        nameInput.setText(arici.getNume());
        ageInput.setText(String.valueOf(arici.getVarstaAni()));
        shelterCheckBox.setChecked(arici.hasAdapostSigur());
        friendshipRating.setRating(arici.getNivelPrietenie());
        nocturnalSwitch.setChecked(arici.isActivNoaptea());

        int zonePosition = -1;
        for (int position = 0; position < habitatSpinner.getCount(); position++) {
            if (arici.getZona().equals(habitatSpinner.getItemAtPosition(position).toString())) {
                zonePosition = position;
                break;
            }
        }
        if (zonePosition >= 0) {
            habitatSpinner.setSelection(zonePosition);
        }

        if (getString(R.string.sex_female).equals(arici.getSex())) {
            sexGroup.check(R.id.radio_female);
        } else if (getString(R.string.sex_male).equals(arici.getSex())) {
            sexGroup.check(R.id.radio_male);
        }
    }

    private void saveArici() {
        String name = nameInput.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Introdu un nume pentru arici.");
            nameInput.requestFocus();
            return;
        }

        String ageText = ageInput.getText().toString().trim();
        if (TextUtils.isEmpty(ageText)) {
            ageInput.setError("Introdu vârsta în ani.");
            ageInput.requestFocus();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException exception) {
            ageInput.setError("Vârsta trebuie să fie un număr întreg.");
            ageInput.requestFocus();
            return;
        }
        if (age < 0) {
            ageInput.setError("Vârsta nu poate fi negativă.");
            ageInput.requestFocus();
            return;
        }

        int selectedId = sexGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Selectează o variantă pentru sex.", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedSex = findViewById(selectedId);

        AriciIIC enteredArici = new AriciIIC(
                name,
                age,
                habitatSpinner.getSelectedItem().toString(),
                selectedSex.getText().toString(),
                shelterCheckBox.isChecked(),
                friendshipRating.getRating(),
                nocturnalSwitch.isChecked()
        );

        AriciIIC resultArici = enteredArici;
        if (editingArici != null) {
            editingArici.updateFrom(enteredArici);
            resultArici = editingArici;
        }

        Bundle resultBundle = new Bundle();
        resultBundle.putParcelable(EXTRA_ARICI, resultArici);
        Intent resultIntent = new Intent();
        resultIntent.putExtras(resultBundle);
        setResult(RESULT_OK, resultIntent);
        finish();
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
}
