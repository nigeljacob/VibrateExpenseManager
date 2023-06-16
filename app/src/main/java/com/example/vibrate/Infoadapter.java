package com.example.vibrate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

public class Infoadapter extends RecyclerView.Adapter<MyViewHolder2>{

    private Context context;
    private ArrayList<listModel> dataList;

    public Infoadapter(Context context, ArrayList<listModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.infoview, parent, false);
        return new MyViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 holder, @SuppressLint("RecyclerView") int position) {
        holder.Text.setText(dataList.get(position).getTitle());
        holder.logo.setBackgroundColor(Color.parseColor(dataList.get(position).getColor()));
        holder.perc.setText(dataList.get(position).getPercentage() + "%");

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.perc.getText().toString().equals(dataList.get(position).getPercentage() + "%")){
                    String amountString = dataList.get(position).getAmount();
                    float amount = Float.parseFloat(amountString);
                    NumberFormat formate = NumberFormat.getCurrencyInstance();
                    formate.setCurrency(Currency.getInstance("LKR"));
                    String newIncome = formate.format(amount);
                    holder.perc.setText(newIncome);
                } else {

                    holder.perc.setText(dataList.get(position).getPercentage() + "%");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class MyViewHolder2 extends RecyclerView.ViewHolder {

    View logo;
    TextView Text;
    TextView perc;
    ConstraintLayout constraintLayout;


    public MyViewHolder2(@NonNull View itemView) {
        super(itemView);

        Text = itemView.findViewById(R.id.Text01);
        logo = itemView.findViewById(R.id.colorView01);
        perc  = itemView.findViewById(R.id.percentageSalaryView);
        constraintLayout = itemView.findViewById(R.id.COLayout);



    }


}
