package com.example.vibrate;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

public class ExpenseReportFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    List<DataClass> dataList;
    PieChart pieChart;
    TextView currency, currency2, nothing, MonthIncome, MonthText;
    Spinner spinner;
    Query query;
    ConstraintLayout constraintLayout;

    RecyclerView recyclerView, recyclerView2;

    ArrayList<listModel> listModels = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_expense_report, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        pieChart = v.findViewById(R.id.piechart2);
        currency = v.findViewById(R.id.currencyTextIncome002);
        currency2 = v.findViewById(R.id.currencyTextIncome001);
        spinner = v.findViewById(R.id.month_spinner);
        MonthIncome = v.findViewById(R.id.currencyTextExpense01);
        MonthText = v.findViewById(R.id.IncomeText001);

        nothing = v.findViewById(R.id.nothingText1);
        constraintLayout = v.findViewById(R.id.l02);

        recyclerView = v.findViewById(R.id.recycleViewForcolor);
        recyclerView2 = v.findViewById(R.id.recycleViewForInfo);

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        System.out.println(month);
        int year = c.get(Calendar.YEAR);

        spinner.setSelection(month, true);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pieChart.clearChart();
                pieChart.startAnimation();
                MonthText.setText(spinner.getSelectedItem() + " Expense");
                getData(spinner.getSelectedItemPosition() + 1, year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getData(spinner.getSelectedItemPosition() + 1, year);

        return v;
    }

    public void getData(int Month, int Year) {
        String email = currentUser.getEmail();
        String Path = email.replaceAll("[-+.^:,]","");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Path);
        if (spinner.getSelectedItem().equals("All")){
            query = databaseReference.orderByChild("bill").equalTo("Expense");
        } else {
            query = databaseReference.orderByChild("month").equalTo(String.valueOf(Month) + String.valueOf(Year));
        }


        ArrayList<listModel> finalList = new ArrayList<>();

        dataList = new ArrayList<>();
        String[] item = {"Restaurant", "Family", "Education", "Entertainment", "Personal", "travelling", "Functions", "Household", "Other"};

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getContext(), 1);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));

        ColorAdapter adapter = new ColorAdapter(getContext(), finalList);
        recyclerView.setAdapter(adapter);

        Infoadapter infoadapter = new Infoadapter(getContext(), finalList);
        recyclerView2.setAdapter(infoadapter);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Expense")) {
                    String totalIncomeString = snapshot.child("Expense").getValue(String.class);
                    float totalIncome = Float.parseFloat(totalIncomeString);
                    currency.setText(totalIncomeString);
                    NumberFormat formate = NumberFormat.getCurrencyInstance();
                    formate.setCurrency(Currency.getInstance("LKR"));
                    String newExpense = formate.format(totalIncome);
                    currency2.setText(newExpense);
                } else {
                    currency.setText("0.00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList<String> Catagory = new ArrayList<>();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                if (snapshot.exists()) {
                    nothing.setVisibility(View.INVISIBLE);
                    pieChart.setVisibility(View.VISIBLE);

                    final float[] total = {0.00F};

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                        if (dataClass.getBill().equals("Expense")) {
                        if (Catagory.contains(dataClass.getCatagory())) {
                            continue;

                        } else {
                            Catagory.add(dataClass.getCatagory());

                                HashMap<String, String> colorDict = new HashMap<String, String>();
                                colorDict.put("Restaurant", "#ea5545");
                                colorDict.put("Family", "#f46a9b");
                                colorDict.put("Education", "#ef9b20");
                                colorDict.put("Entertainment", "#edbf33");
                                colorDict.put("Personal", "#ede15b");
                                colorDict.put("travelling", "#bdcf32");
                                colorDict.put("Functions", "#87bc45");
                                colorDict.put("Household", "#27aeef");
                                colorDict.put("Other", "#b33dc6");

                                databaseReference.orderByChild("catagory").equalTo(dataClass.getCatagory()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        float catagory = 0.0F;
                                        float avg = 0.0F;
                                        dataList.clear();
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                                            dataList.add(dataClass);
                                            if (dataClass.getBill().equals("Expense")) {

                                                String Amountstring = dataClass.getAmount();
                                                float Amount = Float.parseFloat(Amountstring);
                                                catagory = catagory + Amount;


                                            }
                                        }


                                        total[0] = total[0] + catagory;
                                        if (spinner.getSelectedItem().equals("All")) {
                                            constraintLayout.setVisibility(View.GONE);
                                            setTextData(0.00F, MonthIncome);
                                            String TotalExpenseString = currency.getText().toString();
                                            float TotalExpense = Float.parseFloat(TotalExpenseString);
                                            avg = (catagory / TotalExpense) * 100;
                                        } else {
                                            constraintLayout.setVisibility(View.VISIBLE);
                                            setTextData(total[0], MonthIncome);
                                            String TotalExpenseString = currency.getText().toString();
                                            float TotalExpense = Float.parseFloat(TotalExpenseString);
                                            avg = (catagory / TotalExpense) * 100;
                                        }

                                        int i = Math.round(avg);
                                        finalList.add(new listModel(dataClass.getCatagory(), colorDict.get(dataClass.getCatagory()), String.valueOf(i), String.valueOf(catagory)));


                                        pieChart.addPieSlice(
                                                new PieModel(
                                                        dataClass.getCatagory(),
                                                        avg,
                                                        Color.parseColor((String) colorDict.get(dataClass.getCatagory()))));

                                        adapter.notifyDataSetChanged();
                                        infoadapter.notifyDataSetChanged();

                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                        }


                    }


                } else {
                    nothing.setVisibility(View.VISIBLE);
                    setTextData(0.00F, MonthIncome);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setTextData(float F, TextView textView) {
        float totalIncome = F;
        NumberFormat formate = NumberFormat.getCurrencyInstance();
        formate.setCurrency(Currency.getInstance("LKR"));
        String newExpense = formate.format(totalIncome);
        textView.setText(newExpense);
    }

}