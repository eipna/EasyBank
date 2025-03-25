package com.eipna.easybank.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.eipna.easybank.R;
import com.eipna.easybank.data.Account;
import com.eipna.easybank.databinding.ActivityHomeBinding;
import com.eipna.easybank.util.NotificationUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding binding;
    private Account account;

    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Do nothing
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            Snackbar.make(binding.getRoot(), "The application needs notification permission to send alerts, please enable it in the settings", Snackbar.LENGTH_SHORT).show();
        } else {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        account = databaseInstance.getAccount(preferenceUtil.getLoggedInUserID());

        binding.balanceText.setText(String.format("₱%s", NumberFormat.getInstance().format(account.getBalance())));

        binding.sendMoney.setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_send, null, false);
            
            TextInputEditText fieldEmail = dialogView.findViewById(R.id.field_transfer_account_id);
            TextInputEditText fieldAmount = dialogView.findViewById(R.id.field_transfer_amount);
            
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                    .setTitle("Fund Transfer")
                    .setView(dialogView)
                    .setNegativeButton("Cancel" , null)
                    .setPositiveButton("Send", null);
            
            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view3 -> {
                if (fieldAmount.getText().toString().isEmpty() || fieldEmail.getText().toString().isEmpty()) {
                    Toast.makeText(HomeActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(fieldAmount.getText().toString());
                if (amount == 0) {
                    Toast.makeText(HomeActivity.this, "Transfer amount cannot be 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = fieldEmail.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(HomeActivity.this, "Email field cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.equals(account.getEmail())) {
                    Toast.makeText(this, "Fund transfer does not work with the same email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amount > account.getBalance()) {
                    Toast.makeText(HomeActivity.this, "Transfer amount is too big", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (databaseInstance.fundTransfer(account.getID(), email, amount)) {
                    account = databaseInstance.getAccount(preferenceUtil.getLoggedInUserID());
                    binding.balanceText.setText(String.format("₱%s", NumberFormat.getInstance().format(account.getBalance())));

                    if (account.getBalance() < 1000) {
                        String title = getString(R.string.notification_error_balance_title);
                        String text = getString(R.string.notification_error_balance_text);
                        NotificationUtil.createNotification(this, title, text, 0);
                    }

                    String title = getString(R.string.notification_transfer_successful_title);
                    String textStr = getString(R.string.notification_transfer_successful_text);
                    String notificationBody = String.format(textStr, amount, email);
                    NotificationUtil.createNotification(HomeActivity.this, title, notificationBody, 2);

                    dialog.dismiss();
                } else {
                    String title = getString(R.string.notification_error_email_title);
                    String text = getString(R.string.notification_error_email_text);
                    NotificationUtil.createNotification(this, title, text, 1);
                    dialog.dismiss();
                }
            }));
            dialog.show();
        });

        binding.profile.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        binding.transaction.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, TransactionActivity.class)));

        binding.payBills.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, BillsActivity.class)));

        binding.deposit.setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_deposit_withrawal, null, false);

            TextInputEditText fieldAmount = dialogView.findViewById(R.id.field_transaction);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(HomeActivity.this)
                    .setTitle("Deposit money")
                    .setView(dialogView)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Deposit", null);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
                if (fieldAmount.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(fieldAmount.getText().toString());
                if (amount == 0) {
                    Toast.makeText(this, "Deposit amount cannot be 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseInstance.deposit(account.getID(), amount);
                account = databaseInstance.getAccount(account.getID());
                binding.balanceText.setText(String.format("₱%s", NumberFormat.getInstance().format(account.getBalance())));
                dialog.dismiss();
            }));
            dialog.show();
        });

        binding.withdraw.setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_deposit_withrawal, null, false);

            TextInputEditText fieldAmount = dialogView.findViewById(R.id.field_transaction);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(HomeActivity.this)
                    .setTitle("Withdraw money")
                    .setView(dialogView)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Withdraw", null);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view2 -> {
                if (fieldAmount.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(fieldAmount.getText().toString());
                if (amount == 0) {
                    Toast.makeText(this, "Deposit amount cannot be 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amount > account.getBalance()) {
                    Toast.makeText(this, "Withdraw amount is too big", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseInstance.withdraw(account.getID(), amount);
                account = databaseInstance.getAccount(account.getID());

                if (account.getBalance() < 1000) {
                    String title = getString(R.string.notification_error_balance_title);
                    String text = getString(R.string.notification_error_balance_text);
                    NotificationUtil.createNotification(this, title, text, 0);
                }

                binding.balanceText.setText(String.format("₱%s", NumberFormat.getInstance().format(account.getBalance())));
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