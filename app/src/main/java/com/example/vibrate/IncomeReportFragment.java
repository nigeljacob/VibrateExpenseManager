package com.example.vibrate;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Random;

public class IncomeReportFragment extends Fragment {

    private FirebaseAuth mAuth;

    List<DataClass> dataList;


    List<String> amount;

    PieChart pieChart;

    TextView currency, salaryPerc, incomePerc, personalPerc, salesPerc, otherPerc, currency2, nothing, monthincomeText, monthIncome;
    CardView cardView;

    Spinner spinner;
    LinearLayout linearLayout;

    Query query;

    ConstraintLayout constraintLayout;

    ArrayAdapter<String> adapterItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_income_report, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        spinner = v.findViewById(R.id.month_spinner);

        pieChart = v.findViewById(R.id.piechart);
        currency = v.findViewById(R.id.currencyTextIncome02);
        currency2 = v.findViewById(R.id.currencyTextIncome);
        nothing = v.findViewById(R.id.nothingText);

        linearLayout = v.findViewById(R.id.Layout);

        constraintLayout = v.findViewById(R.id.l01);
        monthIncome = v.findViewById(R.id.currencyTextIncome01);
        monthincomeText = v.findViewById(R.id.IncomeText01);
        
        salaryPerc = v.findViewById(R.id.percentageSalary);
        incomePerc = v.findViewById(R.id.percentageIncome);
        personalPerc = v.findViewById(R.id.percentagePersonal);
        salesPerc = v.findViewById(R.id.percentageSales);
        otherPerc = v.findViewById(R.id.percentageOther);

        cardView = v.findViewById(R.id.numberCard);
        String email = currentUser.getEmail();
        String Path = email.replaceAll("[-+.^:,]","");

        dataList = new ArrayList<>();

        amount = new ArrayList<>();
        amount.add(0,"0");
        amount.add(1,"0");
        amount.add(2,"0");
        amount.add(3,"0");
        amount.add(4,"0");

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        System.out.println(month);
        int year = c.get(Calendar.YEAR);

        spinner.setSelection(month, true);

        monthincomeText.setText((CharSequence) spinner.getSelectedItem() + " INCOME");


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                monthincomeText.setText((CharSequence) spinner.getSelectedItem() + " INCOME");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Path);


                if(spinner.getSelectedItem().equals("All")) {
                    query = databaseReference.orderByChild("bill").equalTo("Income");
                    monthincomeText.setText((CharSequence) spinner.getSelectedItem());
                    constraintLayout.setVisibility(View.GONE);
                } else {
                    query = databaseReference.orderByChild("month").equalTo(String.valueOf(spinner.getSelectedItemPosition() + 1) + String.valueOf(year));
                    constraintLayout.setVisibility(View.VISIBLE);
                }




                ArrayList<String> item = new ArrayList<>();
                ArrayList<String> MonthlySalary = new ArrayList<>();
                ArrayList<String> ExtraIncome = new ArrayList<>();
                ArrayList<String> Personal = new ArrayList<>();
                ArrayList<String> SalesIncome= new ArrayList<>();
                ArrayList<String> other= new ArrayList<>();

                item.clear();
                MonthlySalary.clear();
                ExtraIncome.clear();
                Personal.clear();
                SalesIncome.clear();
                other.clear();

                amount = new ArrayList<>();
                amount.add(0,"0");
                amount.add(1,"0");
                amount.add(2,"0");
                amount.add(3,"0");
                amount.add(4,"0");


                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dataList.clear();

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                dataList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (snapshot.exists()) {
                                        DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                                        if (dataClass.getCatagory().equals("Monthly Salary")) {
                                            MonthlySalary.add(dataClass.getAmount());
                                        } else if (dataClass.getCatagory().equals("Extra Income")) {
                                            ExtraIncome.add(dataClass.getAmount());
                                        } else if (dataClass.getCatagory().equals("Personal")) {
                                            Personal.add(dataClass.getAmount());
                                        } else if (dataClass.getCatagory().equals("Sales Income")) {
                                            SalesIncome.add(dataClass.getAmount());
                                        } else if (dataClass.getCatagory().equals("Other")) {
                                            other.add(dataClass.getAmount());
                                        }

                                    } else {
                                        pieChart.setVisibility(View.INVISIBLE);
                                        linearLayout.setVisibility(View.INVISIBLE);
                                        nothing.setVisibility(View.VISIBLE);
                                        setTextData(0, monthIncome);
                                    }

                                }
                                if (snapshot.exists()) {
                                    pieChart.setVisibility(View.VISIBLE);
                                    linearLayout.setVisibility(View.VISIBLE);
                                    nothing.setVisibility(View.INVISIBLE);
                                    float MonthlySalaryTotal = 0;
                                    for (String amountAVG : MonthlySalary) {
                                        float ammount = Float.parseFloat(amountAVG);
                                        MonthlySalaryTotal = MonthlySalaryTotal + ammount;
                                        float totalIncome = Float.parseFloat(currency.getText().toString());
                                        float averge = (MonthlySalaryTotal / totalIncome) * 100;
                                        int i = Math.round(averge);
                                        amount.set(0, String.valueOf(i));
                                    }

                                    float ExtraIncomeTotal = 0;
                                    for (String amountAVG : ExtraIncome) {
                                        float ammount = Float.parseFloat(amountAVG);
                                        ExtraIncomeTotal = ExtraIncomeTotal + ammount;
                                        float totalIncome = Float.parseFloat(currency.getText().toString());
                                        float averge = (ExtraIncomeTotal / totalIncome) * 100;
                                        int i = Math.round(averge);
                                        amount.set(1, String.valueOf(i));
                                    }

                                    float PersonalTotal = 0;
                                    for (String amountAVG : Personal) {
                                        float ammount = Float.parseFloat(amountAVG);
                                        PersonalTotal = PersonalTotal + ammount;
                                        float totalIncome = Float.parseFloat(currency.getText().toString());
                                        float averge = (PersonalTotal / totalIncome) * 100;
                                        int i = Math.round(averge);
                                        amount.set(2, String.valueOf(i));
                                    }

                                    float SalesIncomeTotal = 0;
                                    for (String amountAVG : SalesIncome) {
                                        float ammount = Float.parseFloat(amountAVG);
                                        SalesIncomeTotal = SalesIncomeTotal + ammount;
                                        float totalIncome = Float.parseFloat(currency.getText().toString());
                                        float averge = (SalesIncomeTotal / totalIncome) * 100;
                                        int i = Math.round(averge);
                                        amount.set(3, String.valueOf(i));
                                    }

                                    float OtherTotal = 0;
                                    for (String amountAVG : other) {
                                        float ammount = Float.parseFloat(amountAVG);
                                        OtherTotal = OtherTotal + ammount;
                                        float totalIncome = Float.parseFloat(currency.getText().toString());
                                        float averge = (OtherTotal / totalIncome) * 100;
                                        int i = Math.round(averge);
                                        amount.set(4, String.valueOf(i));
                                    }

                                    float TotalMonthIncome = MonthlySalaryTotal + ExtraIncomeTotal + PersonalTotal + SalesIncomeTotal + OtherTotal;
                                    setTextData(TotalMonthIncome, monthIncome);

                                    pieChart.clearChart();

                                    pieChart.addPieSlice(
                                            new PieModel(
                                                    "Monthly Salary",
                                                    Integer.parseInt(amount.get(0)),
                                                    Color.parseColor("#E53935")));

                                    pieChart.addPieSlice(
                                            new PieModel(
                                                    "Extra Income",
                                                    Integer.parseInt(amount.get(1)),
                                                    Color.parseColor("#5D45B1")));


                                    pieChart.addPieSlice(
                                            new PieModel(
                                                    "Personal",
                                                    Integer.parseInt(amount.get(2)),
                                                    Color.parseColor("#FFC200")));

                                    pieChart.addPieSlice(
                                            new PieModel(
                                                    "Sales Income",
                                                    Integer.parseInt(amount.get(3)),
                                                    Color.parseColor("#1E88E5")));

                                    pieChart.addPieSlice(
                                            new PieModel(
                                                    "Other",
                                                    Integer.parseInt(amount.get(4)),
                                                    Color.parseColor("#43A047")));

                                    salaryPerc.setText(amount.get(0) + "%");
                                    incomePerc.setText(amount.get(1) + "%");
                                    personalPerc.setText(amount.get(2) + "%");
                                    salesPerc.setText(amount.get(3) + "%");
                                    otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4))) + "%");

                                    float finalMonthlySalaryTotal = MonthlySalaryTotal;
                                    float finalExtraIncomeTotal = ExtraIncomeTotal;
                                    float finalPersonalTotal = PersonalTotal;
                                    float finalSalesIncomeTotal = SalesIncomeTotal;
                                    float finalOtherTotal = OtherTotal;
                                    cardView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (salaryPerc.getText().toString().equals(amount.get(0) + "%")) {
                                                setTextData(finalMonthlySalaryTotal, salaryPerc);
                                                setTextData(finalExtraIncomeTotal, incomePerc);
                                                setTextData(finalPersonalTotal, personalPerc);
                                                setTextData(finalSalesIncomeTotal, salesPerc);
                                                setTextData(finalOtherTotal, otherPerc);
                                            } else {
                                                salaryPerc.setText(amount.get(0) + "%");
                                                incomePerc.setText(amount.get(1) + "%");
                                                personalPerc.setText(amount.get(2) + "%");
                                                salesPerc.setText(amount.get(3) + "%");
                                                if (!amount.get(4).equals("0")) {
                                                    otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4)) - 1) + "%");
                                                } else {
                                                    otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4))) + "%");
                                                }

                                            }
                                        }
                                    });

                                    pieChart.startAnimation();
                                } else {
                                    pieChart.setVisibility(View.INVISIBLE);
                                    linearLayout.setVisibility(View.INVISIBLE);
                                    nothing.setVisibility(View.VISIBLE);
                                    setTextData(0, monthIncome);
                                    salaryPerc.setText(amount.get(0) + "%");
                                    incomePerc.setText(amount.get(1) + "%");
                                    personalPerc.setText(amount.get(2) + "%");
                                    salesPerc.setText(amount.get(3) + "%");
                                    if (!amount.get(4).equals("0")) {
                                        otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4)) - 1) + "%");
                                    } else {
                                        otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4))) + "%");
                                    }
                                    cardView.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            if (salaryPerc.getText().toString().equals(amount.get(0) + "%")) {
                                                setTextData(0, salaryPerc);
                                                setTextData(0, incomePerc);
                                                setTextData(0, personalPerc);
                                                setTextData(0, salesPerc);
                                                setTextData(0, otherPerc);
                                            } else {
                                                salaryPerc.setText(amount.get(0) + "%");
                                                incomePerc.setText(amount.get(1) + "%");
                                                personalPerc.setText(amount.get(2) + "%");
                                                salesPerc.setText(amount.get(3) + "%");
                                                if (!amount.get(4).equals("0")) {
                                                    otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4)) - 1) + "%");
                                                } else {
                                                    otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4))) + "%");
                                                }

                                            }
                                        }
                                    });
                                }
                            }




                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (!dataSnapshot.getValue().getClass().getSimpleName().equals("String")) {
                                DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                                if (item.contains(dataSnapshot.child("catagory").getValue(String.class))) {
                                    continue;
                                } else {
                                    item.add(dataSnapshot.child("catagory").getValue(String.class));
                                }
                            }



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Path);

        Query query = databaseReference.orderByChild("month").equalTo(String.valueOf(spinner.getSelectedItemPosition() + 1) + String.valueOf(year));

        ArrayList<String> item = new ArrayList<>();
        ArrayList<String> MonthlySalary = new ArrayList<>();
        ArrayList<String> ExtraIncome = new ArrayList<>();
        ArrayList<String> Personal = new ArrayList<>();
        ArrayList<String> SalesIncome= new ArrayList<>();
        ArrayList<String> other= new ArrayList<>();


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("Income")) {
                            String totalIncomeString = snapshot.child("Income").getValue(String.class);
                            float totalIncome = Float.parseFloat(totalIncomeString);
                            currency.setText(totalIncomeString);
                            NumberFormat formate = NumberFormat.getCurrencyInstance();
                            formate.setCurrency(Currency.getInstance("LKR"));
                            String newExpense = formate.format(totalIncome);
                            currency2.setText(newExpense);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                databaseReference.orderByChild("month").equalTo(String.valueOf(spinner.getSelectedItemPosition() + 1) + String.valueOf(year)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dataList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (snapshot.exists()) {
                                DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                                if (dataClass.getCatagory().equals("Monthly Salary")) {
                                    MonthlySalary.add(dataClass.getAmount());
                                } else if (dataClass.getCatagory().equals("Extra Income")) {
                                    ExtraIncome.add(dataClass.getAmount());
                                } else if (dataClass.getCatagory().equals("Personal")) {
                                    Personal.add(dataClass.getAmount());
                                } else if (dataClass.getCatagory().equals("Sales Income")) {
                                    SalesIncome.add(dataClass.getAmount());
                                } else if (dataClass.getCatagory().equals("Other")) {
                                    other.add(dataClass.getAmount());
                                }

                            } else {
                                pieChart.setVisibility(View.INVISIBLE);
                                linearLayout.setVisibility(View.INVISIBLE);
                                nothing.setVisibility(View.VISIBLE);
                                setTextData(0, monthIncome);
                            }

                        }
                        if (snapshot.exists()) {

                            pieChart.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.VISIBLE);
                            nothing.setVisibility(View.INVISIBLE);
                            float MonthlySalaryTotal = 0;
                            for (String amountAVG : MonthlySalary) {
                                float ammount = Float.parseFloat(amountAVG);
                                MonthlySalaryTotal = MonthlySalaryTotal + ammount;
                                float totalIncome = Float.parseFloat(currency.getText().toString());
                                float averge = (MonthlySalaryTotal / totalIncome) * 100;
                                int i = Math.round(averge);
                                amount.set(0, String.valueOf(i));
                            }

                            float ExtraIncomeTotal = 0;
                            for (String amountAVG : ExtraIncome) {
                                float ammount = Float.parseFloat(amountAVG);
                                ExtraIncomeTotal = ExtraIncomeTotal + ammount;
                                float totalIncome = Float.parseFloat(currency.getText().toString());
                                float averge = (ExtraIncomeTotal / totalIncome) * 100;
                                int i = Math.round(averge);
                                amount.set(1, String.valueOf(i));
                            }

                            float PersonalTotal = 0;
                            for (String amountAVG : Personal) {
                                float ammount = Float.parseFloat(amountAVG);
                                PersonalTotal = PersonalTotal + ammount;
                                float totalIncome = Float.parseFloat(currency.getText().toString());
                                float averge = (PersonalTotal / totalIncome) * 100;
                                int i = Math.round(averge);
                                amount.set(2, String.valueOf(i));
                            }

                            float SalesIncomeTotal = 0;
                            for (String amountAVG : SalesIncome) {
                                float ammount = Float.parseFloat(amountAVG);
                                SalesIncomeTotal = SalesIncomeTotal + ammount;
                                float totalIncome = Float.parseFloat(currency.getText().toString());
                                float averge = (SalesIncomeTotal / totalIncome) * 100;
                                int i = Math.round(averge);
                                amount.set(3, String.valueOf(i));
                            }

                            float OtherTotal = 0;
                            for (String amountAVG : other) {
                                float ammount = Float.parseFloat(amountAVG);
                                OtherTotal = OtherTotal + ammount;
                                float totalIncome = Float.parseFloat(currency.getText().toString());
                                float averge = (OtherTotal / totalIncome) * 100;
                                int i = Math.round(averge);
                                amount.set(4, String.valueOf(i));
                            }

                            float TotalMonthIncome = MonthlySalaryTotal + ExtraIncomeTotal + PersonalTotal + SalesIncomeTotal + OtherTotal;
                            setTextData(TotalMonthIncome, monthIncome);

                            pieChart.clearChart();

                            pieChart.addPieSlice(
                                    new PieModel(
                                            "Monthly Salary",
                                            Integer.parseInt(amount.get(0)),
                                            Color.parseColor("#E53935")));

                            pieChart.addPieSlice(
                                    new PieModel(
                                            "Extra Income",
                                            Integer.parseInt(amount.get(1)),
                                            Color.parseColor("#5D45B1")));


                            pieChart.addPieSlice(
                                    new PieModel(
                                            "Personal",
                                            Integer.parseInt(amount.get(2)),
                                            Color.parseColor("#FFC200")));

                            pieChart.addPieSlice(
                                    new PieModel(
                                            "Sales Income",
                                            Integer.parseInt(amount.get(3)),
                                            Color.parseColor("#1E88E5")));

                            pieChart.addPieSlice(
                                    new PieModel(
                                            "Other",
                                            Integer.parseInt(amount.get(4)),
                                            Color.parseColor("#43A047")));

                            salaryPerc.setText(amount.get(0) + "%");
                            incomePerc.setText(amount.get(1) + "%");
                            personalPerc.setText(amount.get(2) + "%");
                            salesPerc.setText(amount.get(3) + "%");
                            otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4))) + "%");

                            float finalMonthlySalaryTotal = MonthlySalaryTotal;
                            float finalExtraIncomeTotal = ExtraIncomeTotal;
                            float finalPersonalTotal = PersonalTotal;
                            float finalSalesIncomeTotal = SalesIncomeTotal;
                            float finalOtherTotal = OtherTotal;
                            cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (salaryPerc.getText().toString().equals(amount.get(0) + "%")) {
                                        setTextData(finalMonthlySalaryTotal, salaryPerc);
                                        setTextData(finalExtraIncomeTotal, incomePerc);
                                        setTextData(finalPersonalTotal, personalPerc);
                                        setTextData(finalSalesIncomeTotal, salesPerc);
                                        setTextData(finalOtherTotal, otherPerc);
                                    } else {
                                        salaryPerc.setText(amount.get(0) + "%");
                                        incomePerc.setText(amount.get(1) + "%");
                                        personalPerc.setText(amount.get(2) + "%");
                                        salesPerc.setText(amount.get(3) + "%");
                                        if (!amount.get(4).equals("0")) {
                                            otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4)) - 1) + "%");
                                        } else {
                                            otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4))) + "%");
                                        }

                                    }
                                }
                            });

                            pieChart.startAnimation();
                        } else {
                            pieChart.setVisibility(View.INVISIBLE);
                            linearLayout.setVisibility(View.INVISIBLE);
                            nothing.setVisibility(View.VISIBLE);
                            setTextData(0, monthIncome);
                            salaryPerc.setText(amount.get(0) + "%");
                            incomePerc.setText(amount.get(1) + "%");
                            personalPerc.setText(amount.get(2) + "%");
                            salesPerc.setText(amount.get(3) + "%");
                            if (!amount.get(4).equals("0")) {
                                otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4)) - 1) + "%");
                            } else {
                                otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4))) + "%");
                            }
                            cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (salaryPerc.getText().toString().equals(amount.get(0) + "%")) {
                                        setTextData(0, salaryPerc);
                                        setTextData(0, incomePerc);
                                        setTextData(0, personalPerc);
                                        setTextData(0, salesPerc);
                                        setTextData(0, otherPerc);
                                    } else {
                                        salaryPerc.setText(amount.get(0) + "%");
                                        incomePerc.setText(amount.get(1) + "%");
                                        personalPerc.setText(amount.get(2) + "%");
                                        salesPerc.setText(amount.get(3) + "%");
                                        if (!amount.get(4).equals("0")) {
                                            otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4)) - 1) + "%");
                                        } else {
                                            otherPerc.setText(String.valueOf(Integer.parseInt(amount.get(4))) + "%");
                                        }

                                    }
                                }
                            });
                        }
                    }




                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (!dataSnapshot.getValue().getClass().getSimpleName().equals("String")) {
                                DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                                if (item.contains(dataSnapshot.child("catagory").getValue(String.class))) {
                                    continue;
                                } else {
                                    item.add(dataSnapshot.child("catagory").getValue(String.class));
                                }
                            }



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        return v;
    }

    private void setTextData(float F, TextView textView) {
        float totalIncome = F;
        NumberFormat formate = NumberFormat.getCurrencyInstance();
        formate.setCurrency(Currency.getInstance("LKR"));
        String newExpense = formate.format(totalIncome);
        textView.setText(newExpense);
    }
}