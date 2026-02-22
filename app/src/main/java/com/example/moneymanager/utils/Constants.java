package com.example.moneymanager.utils;


public final class Constants {


    private Constants() {}


    public static final String DATABASE_NAME = "money_manager.db";
    public static final int DATABASE_VERSION = 1;


    public static final String TABLE_TRANSACTIONS = "transactions";


    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_REASON = "reason";
    public static final String COLUMN_DATE = "date";


    public static final String TYPE_DEPOSIT = "deposit";
    public static final String TYPE_WITHDRAWAL = "withdrawal";


    public static final String EXTRA_TRANSACTION_ID = "transaction_id";
    public static final String EXTRA_TRANSACTION_TYPE = "transaction_type";


    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";


    public static final double DEFAULT_BALANCE = 0.0;
}