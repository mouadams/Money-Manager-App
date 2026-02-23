package com.example.moneymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.moneymanager.model.Transaction;
import com.example.moneymanager.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MoneyManager.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_REASON = "reason";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_USER_FULL_NAME = "full_name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create transactions table
        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_REASON + " TEXT, " +
                COLUMN_DATE + " TEXT)";
        db.execSQL(createTransactionsTable);

        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_FULL_NAME + " TEXT NOT NULL, " +
                COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(createUsersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Create users table for new version
            String createUsersTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_FULL_NAME + " TEXT NOT NULL, " +
                    COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_USER_PASSWORD + " TEXT NOT NULL)";
            db.execSQL(createUsersTable);
        }
    }

    public boolean addTransaction(String type, double amount, String reason, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_REASON, reason);
        values.put(COLUMN_DATE, date);

        long result = db.insert(TABLE_TRANSACTIONS, null, values);
        return result != -1;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRANSACTIONS, null, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                String reason = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REASON));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));

                Transaction transaction = new Transaction(id, type, amount, reason, date);
                transactionList.add(transaction);
            }
            cursor.close();
        }
        return transactionList;
    }

    public Transaction getTransactionById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM transactions WHERE id=?",
                new String[]{String.valueOf(id)});    if (cursor.moveToFirst()) {
            Transaction transaction = new Transaction(
                    cursor.getInt(0),      // COLUMN_ID at index 0
                    cursor.getString(1),   // COLUMN_TYPE at index 1
                    cursor.getDouble(2),   // COLUMN_AMOUNT at index 2
                    cursor.getString(3),   // COLUMN_REASON at index 3
                    cursor.getString(4)    // (FIX) Add COLUMN_DATE at index 4
            );
            cursor.close();
            return transaction;
        }

        cursor.close();
        return null;
    }

    public int updateTransaction(Transaction transaction) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("type", transaction.getType());
        values.put("reason", transaction.getReason());
        values.put("amount", transaction.getAmount());
        values.put("date", transaction.getDate());

        int rowsAffected = db.update(
                "transactions",            // table name
                values,
                "id = ?",                  // where clause
                new String[]{String.valueOf(transaction.getId())}
        );

        db.close();

        return rowsAffected;
    }

    public double getTotalBalance() {
        double balance = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(CASE WHEN type = 'Deposit' THEN amount ELSE -amount END) FROM " + TABLE_TRANSACTIONS, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                balance = cursor.getDouble(0);
            }
            cursor.close();
        }
        return balance;
    }

    public boolean deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TRANSACTIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    // User authentication methods
    public long registerUser(String fullName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FULL_NAME, fullName);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, hashPassword(password));

        long result = db.insert(TABLE_USERS, null, values);
        return result;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);
        
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_USER_FULL_NAME, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD},
                COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?",
                new String[]{email, hashedPassword},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                    ""
            );
            cursor.close();
        }
        return user;
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_USER_FULL_NAME, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD},
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                    ""
            );
            cursor.close();
        }
        return user;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }
}