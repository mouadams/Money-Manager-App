package com.example.moneymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.R;
import com.example.moneymanager.model.Transaction;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter
        extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final Context context;
    private List<Transaction> transactionList;
    private final OnDeleteClickListener deleteClickListener;
    private final OnItemClickListener itemClickListener;

    // ===== Interfaces =====
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    // ===== Constructor (UNCHANGED) =====
    public TransactionAdapter(Context context,
                              List<Transaction> transactionList,
                              OnDeleteClickListener deleteClickListener,
                              OnItemClickListener itemClickListener) {
        this.context = context;
        this.transactionList = transactionList;
        this.deleteClickListener = deleteClickListener;
        this.itemClickListener = itemClickListener;
    }

    // ===== Adapter Methods =====
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.tvType.setText(transaction.getType());
        holder.tvReason.setText(transaction.getReason());
        holder.tvDate.setText(transaction.getDate());

        holder.tvAmount.setText(
                String.format(Locale.getDefault(), "$%.2f", transaction.getAmount())
        );

        // Clean string comparison (safe way)
        if ("Deposit".equalsIgnoreCase(transaction.getType())) {
            holder.tvAmount.setTextColor(
                    ContextCompat.getColor(context, R.color.green)
            );
        } else {
            holder.tvAmount.setTextColor(
                    ContextCompat.getColor(context, R.color.red)
            );
        }

        // Delete click
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    deleteClickListener.onDeleteClick(adapterPosition);
                }
            }
        });

        // Item click
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(transactionList.get(adapterPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionList != null ? transactionList.size() : 0;
    }

    // ===== Public Methods =====
    public void updateData(List<Transaction> newTransactions) {
        if (newTransactions != null) {
            this.transactionList = newTransactions;
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position) {
        if (transactionList != null &&
                position >= 0 &&
                position < transactionList.size()) {

            transactionList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, transactionList.size());
        }
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    // ===== ViewHolder =====
    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView tvType, tvReason, tvDate, tvAmount;
        ImageButton btnDelete;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tvType);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}