package com.example.vibrate;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class ColorAdapter extends RecyclerView.Adapter<MyViewHolder1> {

    private Context context;
    private ArrayList<listModel> dataList;

    public ColorAdapter(Context context, ArrayList<listModel> colorDict) {
        this.context = context;
        this.dataList = colorDict;
    }


    @NonNull
    @Override
    public MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.colorview, parent, false);
        return new MyViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder1 holder, int position) {
        holder.Text.setText(dataList.get(position).getTitle());
        holder.logo.setBackgroundColor(Color.parseColor(dataList.get(position).getColor()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class MyViewHolder1 extends RecyclerView.ViewHolder {

    View logo;
    TextView Text;


    public MyViewHolder1(@NonNull View itemView) {
        super(itemView);

        Text = itemView.findViewById(R.id.Text);
        logo = itemView.findViewById(R.id.colorView);



    }


}
