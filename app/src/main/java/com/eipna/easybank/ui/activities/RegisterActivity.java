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
import com.eipna.easybank.databinding.ActivityRegisterBinding;

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        binding.login.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        binding.register.setOnClickListener(view -> {
            String fullName = binding.fieldFullName.getText().toString();
            String phoneNumber = binding.fieldPhoneNumber.getText().toString();
            String email = binding.fieldEmailAddress.getText().toString();
            String password = binding.fieldPassword.getText().toString();
            String confirmPassword = binding.fieldConfirmPassword.getText().toString();

            if (fullName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Password must match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (databaseInstance.registerAccount(email, password, fullName, phoneNumber)) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Account already exist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}