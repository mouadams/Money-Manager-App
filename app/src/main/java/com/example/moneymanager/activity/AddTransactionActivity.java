package com.example.moneymanager.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneymanager.R;
import com.example.moneymanager.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etAmount, etReason, etDate;
    private RadioGroup radioGroupType;
    private RadioButton rbDeposit, rbWithdrawal;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private String selectedDate;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        initViews();
        setupDatePicker();
        setupSaveButton();
    }

    private void initViews() {
        etAmount = findViewById(R.id.etAmount);
        etReason = findViewById(R.id.etReason);
        etDate = findViewById(R.id.etDate);
        radioGroupType = findViewById(R.id.radioGroupType);
        rbDeposit = findViewById(R.id.rbDeposit);
        rbWithdrawal = findViewById(R.id.rbWithdrawal);
        btnSave = findViewById(R.id.btnSave);
        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();


        updateDateField();
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    updateDateField();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateField() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = dateFormat.format(calendar.getTime());
        etDate.setText(selectedDate);
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveTransaction();
            }
        });
    }

    private boolean validateInput() {
        String amountStr = etAmount.getText().toString().trim();
        String reason = etReason.getText().toString().trim();

        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("Amount cannot be empty");
            return false;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount <= 0) {
            etAmount.setError("Amount must be greater than zero");
            return false;
        }

        if (TextUtils.isEmpty(reason)) {
            etReason.setError("Reason cannot be empty");
            return false;
        }

        return true;
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString().trim();
        String reason = etReason.getText().toString().trim();
        String type = rbDeposit.isChecked() ? "Deposit" : "Withdrawal";
        double amount = Double.parseDouble(amountStr);

        boolean success = dbHelper.addTransaction(type, amount, reason, selectedDate);

        if (success) {
            Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving transaction", Toast.LENGTH_SHORT).show();
        }
    }
}
