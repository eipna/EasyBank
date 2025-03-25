package com.eipna.easybank.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.icu.text.NumberFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.easybank.R;
import com.eipna.easybank.data.Transaction;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    Context context;
    ArrayList<Transaction> transactions;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.type.setText(transaction.getType());

        if (transaction.getType().equals("Deposit")) {
            holder.amount.setTextColor(ColorStateList.valueOf(Color.rgb(46, 205, 28)));
            holder.amount.setText(String.format("+₱%s", NumberFormat.getInstance().format(transaction.getAmount())));
        } else {
            holder.amount.setTextColor(ColorStateList.valueOf(Color.RED));
            holder.amount.setText(String.format("-₱%s", NumberFormat.getInstance().format(transaction.getAmount())));
        }

        holder.date.setText(transaction.getDate());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView type, amount, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.transaction_type);
            amount = itemView.findViewById(R.id.transaction_amount);
            date = itemView.findViewById(R.id.transaction_date);
        }
    }
}