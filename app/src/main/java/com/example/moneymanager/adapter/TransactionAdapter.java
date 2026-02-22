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

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;
    private final Context context;
    private final OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    private final OnItemClickListener onItemClickListener;

    public TransactionAdapter(Context context,
                              List<Transaction> transactionList,
                              OnDeleteClickListener deleteListener,
                              OnItemClickListener itemClickListener) {
        this.context = context;
        this.transactionList = transactionList;
        this.onDeleteClickListener = deleteListener;
        this.onItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.tvType.setText(transaction.getType());
        holder.tvReason.setText(transaction.getReason());
        holder.tvDate.setText(transaction.getDate());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "$%.2f", transaction.getAmount()));

        if (transaction.getType().equals("Deposit")) {
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void updateData(List<Transaction> newTransactions) {
        this.transactionList = newTransactions;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        transactionList.remove(position);
        notifyItemRemoved(position);
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvReason, tvDate, tvAmount;
        ImageButton btnDelete;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onDeleteClickListener.onDeleteClick(position);
                    }
                }
            });

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(transactionList.get(position));
                    }
                }
            });
        }
    }
}
