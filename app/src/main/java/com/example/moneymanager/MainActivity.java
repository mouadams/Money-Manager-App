package com.example.moneymanager;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.activity.AddTransactionActivity;
import com.example.moneymanager.activity.TransactionDetailsActivity;
import com.example.moneymanager.adapter.TransactionAdapter;
import com.example.moneymanager.database.DatabaseHelper;
import com.example.moneymanager.model.Transaction;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TransactionAdapter.OnDeleteClickListener, TransactionAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView tvBalance;
    private FloatingActionButton fabAdd;
    private MaterialButton btnToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        loadTransactions();
        setupFab();
        setupThemeToggle();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvBalance = findViewById(R.id.tvBalance);
        fabAdd = findViewById(R.id.fabAdd);
        btnToggle = findViewById(R.id.btnThemeToggle);
        dbHelper = new DatabaseHelper(this);
    }

    private void setupThemeToggle() {
        btnToggle.setOnClickListener(v -> {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                // Switch to Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                // Switch to Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            recreate();
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(this, Collections.emptyList(), this, this);
        recyclerView.setAdapter(adapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Transaction transaction = adapter.getTransactionList().get(position);
                deleteTransaction(transaction, position);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void loadTransactions() {
        List<Transaction> transactions = dbHelper.getAllTransactions();
        adapter.updateData(transactions);
        updateBalance();
    }

    private void updateBalance() {
        double balance = dbHelper.getTotalBalance();
        tvBalance.setText(String.format("Total Balance: $%.2f", balance));
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });
    }

    private void deleteTransaction(Transaction transaction, int position) {
        dbHelper.deleteTransaction(transaction.getId());
        adapter.removeItem(position);
        updateBalance();

        Snackbar.make(recyclerView, "Transaction deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> {

                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions();
    }

    @Override
    public void onDeleteClick(int position) {
        Transaction transaction = adapter.getTransactionList().get(position);
        deleteTransaction(transaction, position);
    }

    @Override

    public void onItemClick(Transaction transaction) {

        Intent intent = new Intent(MainActivity.this, TransactionDetailsActivity.class);

        // Pass transaction ID to details screen
        intent.putExtra("transaction_id", transaction.getId());

        startActivity(intent);
    }
}
