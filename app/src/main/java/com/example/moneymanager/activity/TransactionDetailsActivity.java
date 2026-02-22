package com.example.moneymanager.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneymanager.R;
import com.example.moneymanager.database.DatabaseHelper;
import com.example.moneymanager.model.Transaction;

public class TransactionDetailsActivity extends AppCompatActivity {

    private EditText etTitle, etAmount;
    private Button btnUpdate, btnDelete;
    private DatabaseHelper dbHelper;
    private int transactionId;
    private Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        dbHelper = new DatabaseHelper(this);

        transactionId = getIntent().getIntExtra("transaction_id", -1);

        if (transactionId != -1) {
            transaction = dbHelper.getTransactionById(transactionId);

            if (transaction != null) {
                etTitle.setText(transaction.getReason());
                etAmount.setText(String.valueOf(transaction.getAmount()));
            }
        }

        btnUpdate.setOnClickListener(v -> updateTransaction());
        btnDelete.setOnClickListener(v -> deleteTransaction());
    }

    private void updateTransaction() {

        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Title cannot be empty");
            etTitle.requestFocus();
            return;
        }

        if (amountStr.isEmpty()) {
            etAmount.setError("Amount cannot be empty");
            etAmount.requestFocus();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Update")
                .setMessage("Are you sure you want to update this transaction?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    try {
                        double amount = Double.parseDouble(amountStr);

                        transaction.setReason(title);
                        transaction.setAmount(amount);

                        dbHelper.updateTransaction(transaction);

                        setResult(RESULT_OK);
                        finish();

                    } catch (NumberFormatException e) {
                        etAmount.setError("Invalid number");
                        etAmount.requestFocus();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTransaction() {

        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {

                    dbHelper.deleteTransaction(transactionId);

                    setResult(RESULT_OK);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}