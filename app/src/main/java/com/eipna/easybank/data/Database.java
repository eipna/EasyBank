package com.eipna.easybank.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import com.eipna.easybank.util.DateUtil;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    public Database(@Nullable Context context) {
        super(context, "easybank.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createAccountTable = "CREATE TABLE IF NOT EXISTS accounts(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "full_name TEXT NOT NULL, " +
                "balance REAL DEFAULT 0, " +
                "contact_number TEXT NOT NULL);";

        String createTransactionTable = "CREATE TABLE IF NOT EXISTS transactions(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "account_id INTEGER NOT NULL, " +
                "amount REAL NOT NULL, " +
                "type TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE);";

        sqLiteDatabase.execSQL(createAccountTable);
        sqLiteDatabase.execSQL(createTransactionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS accounts;");
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public boolean registerAccount(String email, String password, String fullName, String contactNumber) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("email", email);
        values.put("password", password);
        values.put("full_name", fullName);
        values.put("contact_number", contactNumber);

        long result = database.insert("accounts", null, values);
        database.close();
        return result != -1;
    }

    public boolean loginAccount(String email, String password) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM accounts WHERE email = ? AND password = ?", new String[]{email, password});

        if (cursor.moveToFirst()) {
            cursor.close();
            database.close();
            return true;
        }
        cursor.close();
        database.close();
        return false;
    }

    public int getUserID(String email, String password) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM accounts WHERE email = ? AND password = ?", new String[]{String.valueOf(email), String.valueOf(password)});

        if (cursor.moveToFirst()) {
            int userID = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            cursor.close();
            database.close();
            return userID;
        }
        cursor.close();
        database.close();
        return -1;
    }

    public ArrayList<Account> getAllAccounts() {
        ArrayList<Account> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM accounts", null);

        if (cursor.moveToFirst()) {
            do {
                Account account = new Account();
                account.setID(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                account.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                account.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
                account.setContactNumber(cursor.getString(cursor.getColumnIndexOrThrow("contact_number")));
                account.setBalance(cursor.getDouble(cursor.getColumnIndexOrThrow("balance")));
                list.add(account);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }

    public Account getAccount(int accountID) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM accounts WHERE id = ?", new String[]{String.valueOf(accountID)});

        if (cursor.moveToFirst()) {
            Account account = new Account();
            account.setID(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            account.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            account.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
            account.setBalance(cursor.getDouble(cursor.getColumnIndexOrThrow("balance")));
            account.setContactNumber(cursor.getString(cursor.getColumnIndexOrThrow("contact_number")));
            account.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));

            cursor.close();
            database.close();
            return account;
        }
        cursor.close();
        database.close();
        return null;
    }

    public void updateAccount(Account account) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("full_name", account.getFullName());
        values.put("email", account.getEmail());
        values.put("password", account.getPassword());
        values.put("contact_number", account.getContactNumber());
        database.update("accounts", values, "id = ?", new String[]{String.valueOf(account.getID())});
        database.close();
    }

    public ArrayList<Transaction> getTransactions(int accountID) {
        ArrayList<Transaction> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM transactions WHERE account_id = ?", new String[]{String.valueOf(accountID)});

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setID(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                transaction.setAccountID(cursor.getInt(cursor.getColumnIndexOrThrow("account_id")));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                transaction.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                transaction.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                list.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }

    public void deposit(int accountID, double amount) {
        SQLiteDatabase database = getWritableDatabase();
        SQLiteStatement statement = database.compileStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
        statement.bindDouble(1, amount);
        statement.bindLong(2, accountID);
        statement.executeUpdateDelete();

        ContentValues values = new ContentValues();
        values.put("account_id", accountID);
        values.put("amount", amount);
        values.put("type", "Deposit");
        values.put("date", DateUtil.getDate(System.currentTimeMillis()));
        database.insert("transactions", null, values);
        database.close();
    }

    public void withdraw(int accountID, double amount) {
        SQLiteDatabase database = getWritableDatabase();
        SQLiteStatement statement = database.compileStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
        statement.bindDouble(1, amount);
        statement.bindLong(2, accountID);
        statement.executeUpdateDelete();

        ContentValues values = new ContentValues();
        values.put("account_id", accountID);
        values.put("amount", amount);
        values.put("type", "Withdraw");
        values.put("date", DateUtil.getDate(System.currentTimeMillis()));
        database.insert("transactions", null, values);
        database.close();
    }

    public boolean fundTransfer(int accountID, String email , double amount) {
        SQLiteDatabase database = getWritableDatabase();
        if (doesAccountExists(email)) {
            SQLiteStatement statement = database.compileStatement("UPDATE accounts SET balance = balance + ? WHERE email = ?");
            statement.bindDouble(1, amount);
            statement.bindString(2, email);
            statement.executeUpdateDelete();

            statement = database.compileStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
            statement.bindDouble(1, amount);
            statement.bindLong(2, accountID);
            statement.executeUpdateDelete();
            database.close();
            return true;
        }
        database.close();
        return false;
    }

    public boolean doesAccountExists(String email) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM accounts WHERE email = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public void payBill(int accountID, double amount) {
        SQLiteDatabase database = getWritableDatabase();
        SQLiteStatement statement = database.compileStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
        statement.bindDouble(1, amount);
        statement.bindLong(2, accountID);
        statement.executeUpdateDelete();
        database.close();
    }
}