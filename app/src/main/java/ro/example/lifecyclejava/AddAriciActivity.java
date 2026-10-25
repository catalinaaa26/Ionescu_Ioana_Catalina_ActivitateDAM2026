package ro.example.lifecyclejava;

import android.content.Intent;
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

        AriciIIC arici = new AriciIIC(
                name,
                age,
                habitatSpinner.getSelectedItem().toString(),
                selectedSex.getText().toString(),
                shelterCheckBox.isChecked(),
                friendshipRating.getRating(),
                nocturnalSwitch.isChecked()
        );

        Bundle resultBundle = new Bundle();
        resultBundle.putSerializable(EXTRA_ARICI, arici);
        Intent resultIntent = new Intent();
        resultIntent.putExtras(resultBundle);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
