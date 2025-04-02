package com.eipna.easybank.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eipna.easybank.R;
import com.eipna.easybank.data.Transaction;
import com.eipna.easybank.databinding.ActivityTransactionBinding;
import com.eipna.easybank.ui.adapters.TransactionAdapter;

import java.util.ArrayList;

public class TransactionActivity extends BaseActivity {

    private ActivityTransactionBinding binding;
    private ArrayList<Transaction> transactions;
    private TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        transactions = new ArrayList<>(databaseInstance.getTransactions(preferenceUtil.getLoggedInUserID()));
        transactionAdapter = new TransactionAdapter(TransactionActivity.this, transactions);

        binding.emptyIndicator.setVisibility(transactions.isEmpty() ? View.VISIBLE : View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(transactionAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
