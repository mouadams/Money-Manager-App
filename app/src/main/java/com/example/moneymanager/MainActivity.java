package com.example.moneymanager;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.activity.AddTransactionActivity;
import com.example.moneymanager.activity.LoginActivity;
import com.example.moneymanager.activity.TransactionDetailsActivity;
import com.example.moneymanager.adapter.TransactionAdapter;
import com.example.moneymanager.database.DatabaseHelper;
import com.example.moneymanager.model.Transaction;
import com.example.moneymanager.helpers.AuthHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements TransactionAdapter.OnDeleteClickListener,
        TransactionAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView tvBalance;
    private FloatingActionButton fabAdd;
    private MaterialButton btnToggle;
    private MaterialButton btnLogout;
    private AuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authHelper = new AuthHelper(this);

        if (!authHelper.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupFab();
        setupThemeToggle();
        setupLogout();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvBalance = findViewById(R.id.tvBalance);
        fabAdd = findViewById(R.id.fabAdd);
        btnToggle = findViewById(R.id.btnThemeToggle);
        btnLogout = findViewById(R.id.btnLogout);

        dbHelper = new DatabaseHelper(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(this, Collections.emptyList(), this, this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Transaction transaction =
                                adapter.getTransactionList().get(position);

                        deleteTransaction(transaction, position);
                    }
                });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddTransactionActivity.class))
        );
    }

    private void setupThemeToggle() {
        btnToggle.setOnClickListener(v -> {
            int currentNightMode =
                    getResources().getConfiguration().uiMode
                            & Configuration.UI_MODE_NIGHT_MASK;

            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES);
            }
        });
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout", (dialog, which) -> {
                            authHelper.logout();
                            navigateToLogin();
                        })
                        .setNegativeButton("Cancel", null)
                        .show()
        );
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

    private void deleteTransaction(Transaction transaction, int position) {
        dbHelper.deleteTransaction(transaction.getId());
        adapter.removeItem(position);
        updateBalance();

        Snackbar.make(recyclerView,
                "Transaction deleted",
                Snackbar.LENGTH_LONG).show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions();
    }

    @Override
    public void onDeleteClick(int position) {
        Transaction transaction =
                adapter.getTransactionList().get(position);

        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) ->
                        deleteTransaction(transaction, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onItemClick(Transaction transaction) {
        Intent intent =
                new Intent(this, TransactionDetailsActivity.class);
        intent.putExtra("transaction_id", transaction.getId());
        startActivity(intent);
    }
}