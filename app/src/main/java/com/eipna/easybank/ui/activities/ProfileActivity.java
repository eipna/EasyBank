package com.eipna.easybank.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.eipna.easybank.R;
import com.eipna.easybank.data.Account;
import com.eipna.easybank.databinding.ActivityProfileBinding;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        account = databaseInstance.getAccount(preferenceUtil.getLoggedInUserID());

        binding.fieldAccountFullName.setText(account.getFullName());
        binding.fieldAccountEmail.setText(account.getEmail());
        binding.fieldAccountPassword.setText(account.getPassword());
        binding.fieldAccountPhoneNumber.setText(account.getContactNumber());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        binding.save.setOnClickListener(view -> {
            String fullName = binding.fieldAccountFullName.getText().toString();
            String email = binding.fieldAccountEmail.getText().toString();
            String contactNumber = binding.fieldAccountPhoneNumber.getText().toString();
            String password = binding.fieldAccountPassword.getText().toString();
            
            if (fullName.isEmpty() || email.isEmpty() || contactNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            account.setFullName(fullName);
            account.setEmail(email);
            account.setPassword(password);
            account.setContactNumber(contactNumber);
            databaseInstance.updateAccount(account);

            Toast.makeText(this, "Account has been updated", Toast.LENGTH_SHORT).show();
        });

        binding.logout.setOnClickListener(view -> {
            Toast.makeText(ProfileActivity.this, "Logging out", Toast.LENGTH_SHORT).show();
            preferenceUtil.setLoggedInUserID(-1);

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}