package com.eipna.easybank.ui.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;

import com.eipna.easybank.R;
import com.eipna.easybank.data.Account;
import com.eipna.easybank.databinding.ActivityBillsBinding;
import com.eipna.easybank.util.DateUtil;
import com.eipna.easybank.util.NotificationUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

public class BillsActivity extends BaseActivity {

    private ActivityBillsBinding binding;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBillsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        account = databaseInstance.getAccount(preferenceUtil.getLoggedInUserID());

        binding.utilityBills.setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(BillsActivity.this).inflate(R.layout.dialog_utility_bills, null, false);

            TextInputEditText fieldAmount = dialogView.findViewById(R.id.field_bill_amount);
            MaterialAutoCompleteTextView fieldBill = dialogView.findViewById(R.id.autoComplete_utility_bills);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(BillsActivity.this)
                    .setTitle("Utility bill")
                    .setView(dialogView)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Pay", null);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
                String amountString = fieldAmount.getText().toString();
                String billType = fieldBill.getText().toString();

                if (amountString.isEmpty() || billType.isEmpty()) {
                    Toast.makeText(BillsActivity.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountString);
                if (amount == 0) {
                    Toast.makeText(BillsActivity.this, "Payment amount cannot be 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (amount > account.getBalance()) {
                    Toast.makeText(this, "Your account has insufficient balance, please add funds to your account.", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseInstance.payBill(account.getID(), amount);

                if (account.getBalance() < 1000) {
                    String title = getString(R.string.notification_error_balance_title);
                    String text = getString(R.string.notification_error_balance_text);
                    NotificationUtil.createNotification(BillsActivity.this, title, text, 0);
                }

                String title = getString(R.string.notification_bill_title);
                String textString = getString(R.string.notification_bill_text);
                String notificationText = String.format(textString, billType, amount);

                NotificationUtil.createNotification(BillsActivity.this, title, notificationText, 3);
                dialog.dismiss();
            }));
            dialog.show();
        });

        binding.creditCard.setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(BillsActivity.this).inflate(R.layout.dialog_credit_card_bill, null, false);

            TextInputEditText fieldAmount = dialogView.findViewById(R.id.field_card_amount);
            TextInputEditText fieldCardNumber = dialogView.findViewById(R.id.field_card_number);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(BillsActivity.this)
                    .setTitle("Credit card bill")
                    .setView(dialogView)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Pay", null);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view2 -> {
                String amountString = fieldAmount.getText().toString();
                String cardNumberString = fieldCardNumber.getText().toString();

                if (amountString.isEmpty() || cardNumberString.isEmpty()) {
                    Toast.makeText(BillsActivity.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cardNumberString.length() != 16) {
                    Toast.makeText(this, "Please enter a valid credit card number", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountString);
                if (amount > account.getBalance()) {
                    Toast.makeText(BillsActivity.this, "Your account has insufficient balance, please add funds to your account.", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseInstance.payBill(account.getID(), amount);

                if (account.getBalance() < 1000) {
                    String title = getString(R.string.notification_error_balance_title);
                    String text = getString(R.string.notification_error_balance_text);
                    NotificationUtil.createNotification(BillsActivity.this, title, text, 0);
                }

                String date = DateUtil.getDate(System.currentTimeMillis());
                String title = "Credit Card Bill";

                @SuppressLint("DefaultLocale")
                String text = String.format("Your credit card bill has been successfully paid! Your payment of ₱%.2f hase been processed on %s.", amount, date);

                NotificationUtil.createNotification(BillsActivity.this, title, text, 3);
                dialog.dismiss();
            }));
            dialog.show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}