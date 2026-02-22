package com.example.moneymanager.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        // Validate title
        if (title.isEmpty()) {
            etTitle.setError("Title cannot be empty");
            etTitle.requestFocus();
            return;
        }

        // Validate amount
        if (amountStr.isEmpty()) {
            etAmount.setError("Amount cannot be empty");
            etAmount.requestFocus();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            transaction.setReason(title);
            transaction.setAmount(amount);

            dbHelper.updateTransaction(transaction);

            Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK);
            finish();

        } catch (NumberFormatException e) {
            etAmount.setError("Please enter a valid number");
            etAmount.requestFocus();
        }
    }

    private void deleteTransaction() {
        dbHelper.deleteTransaction(transactionId);
        Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}