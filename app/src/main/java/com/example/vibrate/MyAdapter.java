package com.example.vibrate;

import static com.example.vibrate.R.drawable.email;
import static com.example.vibrate.R.drawable.national_tax_svgrepo_com;
import static com.example.vibrate.R.drawable.selecter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;
    ArrayList<String> years = new ArrayList<>();

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_tem, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataClass dataClass = dataList.get(position);
        holder.TitleText.setText(dataClass.getTitle());
        holder.BilltypeText.setText(dataClass.getBill());
        holder.DescText.setText(dataClass.getDescription());
        holder.CatagoryText.setText(dataClass.getCatagory());
        holder.emaiText.setText(dataClass.getEmail());

        Calendar c = Calendar.getInstance();
        int dates = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        String Date = String.valueOf(dates) + "/" +  String.valueOf(month) + "/" +String.valueOf(year);
        String yesterday = String.valueOf(dates-1) + "/" +  String.valueOf(month) + "/" +String.valueOf(year);



        if (years.contains(dataList.get(position).getTimestamp())) {
            years.add("");
        } else {
            years.add(dataList.get(position).getTimestamp());
        }

        for (String yea: years) {
            if (yea.equals(Date)){
                holder.year.setText("Today");
            } else if (yea.equals(yesterday)) {
                holder.year.setText("Yesterday");
            }else if (yea.equals("")){
                holder.year.setText(yea);
                holder.year.setVisibility(View.GONE);
            } else {
                holder.year.setVisibility(View.VISIBLE);
                holder.year.setText(yea);
            }
        }


        String type = holder.BilltypeText.getText().toString();

        if (type.equals("Income")) {
            holder.AmountText.setText("+" + dataClass.getAmount());
            holder.logo.setImageResource(national_tax_svgrepo_com);

        } else if (type.equals("Expense")) {
            holder.AmountText.setText("-" + dataClass.getAmount());
            holder.AmountText.setTextColor(Color.parseColor("#e34f4f"));
            holder.logo.setImageResource(R.drawable.postage_expense_svgrepo_com);
        }

        holder.reccard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.sheet_dialog_layout);

                TextView TextTitle = bottomSheetDialog.findViewById(R.id.bilTypetext);
                TextView userName = bottomSheetDialog.findViewById(R.id.userNameText);
                TextView title = bottomSheetDialog.findViewById(R.id.dialogText);
                TextView description = bottomSheetDialog.findViewById(R.id.dialogDEsc);
                TextView uniqueID = bottomSheetDialog.findViewById(R.id.dummyText);
                TextView Amount = bottomSheetDialog.findViewById(R.id.AmountTextofBill);
                Button delete = bottomSheetDialog.findViewById(R.id.DeleteButton);
                Button Cancel = bottomSheetDialog.findViewById(R.id.CancelButton);
                ProgressBar progressBar = bottomSheetDialog.findViewById(R.id.progress);

                TextTitle.setText(dataList.get(holder.getAdapterPosition()).getBill());
                userName.setText(dataList.get(holder.getAdapterPosition()).getCatagory());
                title.setText(dataList.get(holder.getAdapterPosition()).getTitle());
                description.setText(dataList.get(holder.getAdapterPosition()).getDescription());
                uniqueID.setText(dataList.get(holder.getAdapterPosition()).getUniqueID());
                progressBar.setVisibility(View.INVISIBLE);

                String AmountofBill = dataList.get(holder.getAdapterPosition()).getAmount();

                if (type.equals("Income")) {
                    float TotalExpense = Float.parseFloat(dataList.get(holder.getAdapterPosition()).getAmount());
                    NumberFormat formate = NumberFormat.getCurrencyInstance();
                    formate.setCurrency(Currency.getInstance("LKR"));
                    String newExpense = formate.format(TotalExpense);
                    Amount.setText("+" + newExpense);
                    Amount.setTextColor(Color.parseColor("#006400"));

                } else if (type.equals("Expense")) {
                    float TotalExpense = Float.parseFloat(dataList.get(holder.getAdapterPosition()).getAmount());
                    NumberFormat formate = NumberFormat.getCurrencyInstance();
                    formate.setCurrency(Currency.getInstance("LKR"));
                    String newExpense = formate.format(TotalExpense);
                    Amount.setText("-" + newExpense);
                    Amount.setTextColor(Color.parseColor("#e34f4f"));

                }

                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });



                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                        String path = dataList.get(holder.getAdapterPosition()).getEmail();
                        String email = path.replaceAll("[-+.^:,]","");
                        String key = uniqueID.getText().toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(email);
                        Query query = ref.orderByChild("uniqueID").equalTo(key);

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot item: snapshot.getChildren()) {
                                    item.getRef().removeValue();

                                    String bill = dataList.get(holder.getAdapterPosition()).getBill();
                                    FirebaseDatabase.getInstance().getReference(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChild(bill)) {
                                                String PreviousAmountString = snapshot.child(bill).getValue(String.class);
                                                float NowAmount = Float.parseFloat(AmountofBill);
                                                float PreviousAmount = Float.parseFloat(PreviousAmountString);
                                                float newAmount = PreviousAmount - NowAmount;
                                                String newAmountString = String.valueOf(newAmount);
                                                FirebaseDatabase.getInstance().getReference(email).child(bill).setValue(newAmountString);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(bottomSheetDialog.getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(bottomSheetDialog.getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                delete.setVisibility(View.VISIBLE);
                            }
                        });
                        Toast.makeText(bottomSheetDialog.getContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();
            }

        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView logo;
    TextView TitleText, BilltypeText, AmountText, DescText, emaiText, CatagoryText, year;
    CardView reccard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        logo = itemView.findViewById(R.id.imageLogo);
        TitleText = itemView.findViewById(R.id.titleTxt);
        BilltypeText = itemView.findViewById(R.id.BillTypeTextList);
        AmountText = itemView.findViewById(R.id.AmountofBill);
        DescText = itemView.findViewById(R.id.IteDescription);
        emaiText = itemView.findViewById(R.id.EmailText);
        CatagoryText = itemView.findViewById(R.id.Catagory_Text);
        reccard = itemView.findViewById(R.id.recycleCard);
        year = itemView.findViewById(R.id.year);


    }


}
