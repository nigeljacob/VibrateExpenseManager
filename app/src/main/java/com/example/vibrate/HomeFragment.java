package com.example.vibrate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class HomeFragment extends Fragment {

    ViewGroup cont;

    TextView greeting, userName, balance, incomeCurrency, expenseCurrency, incomeremaining, expenseRemaining, income, expense;
    ConstraintLayout IncomeBG, ExpenseBG;
    private ImageView checkMark, errorMark, incomeIcon, expenseIcon;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser ;

    RecyclerView recyclerView;
    List<DataClass> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;

    MyAdapter adapter;

    SwipeRefreshLayout layout;

    private Button addBill, addExpenseButton;
    public final static String BILL_TYPE = "com.example.vibrate.message_key";

    public static HomeFragment fa;
    void onCreate()
    {
        fa = this;
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


//        Window window = getActivity().getWindow();
//
//// clear FLAG_TRANSLUCENT_STATUS flag:
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//// finally change the color
//        window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.darkBlur));

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = v.findViewById(R.id.recycle_view);
        recyclerView.setNestedScrollingEnabled(false);

        layout = v.findViewById(R.id.swipeToRefresh);

        greeting = v.findViewById(R.id.maingreeting);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        balance = v.findViewById(R.id.currency);
        checkMark = v.findViewById(R.id.check);
        errorMark = v.findViewById(R.id.error);
        IncomeBG = v.findViewById(R.id.incomeBG);
        ExpenseBG = v.findViewById(R.id.ExpenseBG);
        incomeIcon = v.findViewById(R.id.incomeIcon);
        expenseIcon = v.findViewById(R.id.ExpenceIcon);
        income = v.findViewById(R.id.IncomeText);
        expense = v.findViewById(R.id.ExpenseText);
        incomeCurrency = v.findViewById(R.id.incomecurrency);
        expenseCurrency = v.findViewById(R.id.Expensecurrency);
        incomeremaining = v.findViewById(R.id.incomeRemaining);
        expenseRemaining = v.findViewById(R.id.ExpenseRemaining);

        addBill = v.findViewById(R.id.addMoney);
        addExpenseButton = v.findViewById(R.id.addExpense);

        cont = v.findViewById(R.id.Home);

        checkMark.setVisibility(View.INVISIBLE);
        errorMark.setVisibility(View.INVISIBLE);


        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        int date = c.get(Calendar.DATE);



        if(timeOfDay >= 0 && timeOfDay < 12){
            greeting.setText("Good Morning!");
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            greeting.setText("Good Afternoon!");
        }else if(timeOfDay >= 16 && timeOfDay < 24) {
            greeting.setText("Good Evening!");
        }

        String email = currentUser.getEmail();
        String Path = email.replaceAll("[-+.^:,]","");

        IncomeBG.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(cont);
                visible = !visible;
                incomeCurrency.setVisibility(visible? View.VISIBLE: View.GONE);
                incomeremaining.setVisibility(visible? View.VISIBLE: View.GONE);
                incomeIcon.setVisibility(visible? View.INVISIBLE: View.VISIBLE);
                income.setVisibility(visible? View.INVISIBLE: View.VISIBLE);
            }
        });

        ExpenseBG.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(cont);
                visible = !visible;
                expenseCurrency.setVisibility(visible? View.VISIBLE: View.GONE);
                expenseRemaining.setVisibility(visible? View.VISIBLE: View.GONE);
                expenseIcon.setVisibility(visible? View.INVISIBLE: View.VISIBLE);
                expense.setVisibility(visible? View.INVISIBLE: View.VISIBLE);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.WrapContentDialog);
        builder.setCancelable(false);
        builder.setView(R.layout.alert_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new MyAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference(Path);
        Query query = databaseReference.orderByChild("timestamp");
        dialog.show();

        fetchData(dialog);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(getActivity(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                String Key = dataList.get(position).getUniqueID();
                String bill = dataList.get(position).getBill();

                float NowAmount = Float.parseFloat(dataList.get(position).getAmount());
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Path);
                Query query = ref.orderByChild("uniqueID").equalTo(Key);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item: snapshot.getChildren()) {
                            item.getRef().removeValue();

                            FirebaseDatabase.getInstance().getReference(Path).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(bill)) {
                                        String PreviousAmountString = snapshot.child(bill).getValue(String.class);
                                        float PreviousAmount = Float.parseFloat(PreviousAmountString);
                                        float newAmount = PreviousAmount - NowAmount;
                                        String newAmountString = String.valueOf(newAmount);
                                        FirebaseDatabase.getInstance().getReference(Path).child(bill).setValue(newAmountString);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getActivity(), "Successfully Deleted", Toast.LENGTH_SHORT).show();

                dataList.remove(position);
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,float dX, float dY,int actionState, boolean isCurrentlyActive){

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.ErrorRed))
                        .addCornerRadius(1, 10)
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                        .addSwipeLeftLabel("Delete")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(getActivity(), R.color.white))
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback2 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT ) {


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(getActivity(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_update);

                EditText title = bottomSheetDialog.findViewById(R.id.EditTitle);
                EditText desc = bottomSheetDialog.findViewById(R.id.descedittext);
                AutoCompleteTextView autoCompleteTextView = bottomSheetDialog.findViewById(R.id.autoCompleteListLayout);
                EditText amount = bottomSheetDialog.findViewById(R.id.amountEditTextField);
                Button update = bottomSheetDialog.findViewById(R.id.EditButton);
                Button cancel = bottomSheetDialog.findViewById(R.id.CancelButton);
                ProgressBar progressBar = bottomSheetDialog.findViewById(R.id.progress);

                progressBar.setVisibility(View.INVISIBLE);

                int position = viewHolder.getAdapterPosition();
                title.setText(dataList.get(position).getTitle());
                desc.setText(dataList.get(position).getDescription());
                autoCompleteTextView.setText(dataList.get(position).getCatagory());
                amount.setText(dataList.get(position).getAmount());
                String thisAmount = dataList.get(position).getAmount();

                String uniqueKey = dataList.get(position).getUniqueID();

                String Billtype = dataList.get(position).getBill();

                if (Billtype.equals("Expense")) {
                    String[] list = {"Restaurant", "Family", "Education", "Entertainment", "Personal", "travelling", "Functions", "Household", "Other"};
                    ArrayAdapter<String> adapterItems = new ArrayAdapter<String>(getActivity(), R.layout.list_item, list);
                    autoCompleteTextView.setAdapter(adapterItems);

                } else if (Billtype.equals("Income")) {
                    String[] list = {"Monthly Salary", "Extra Income", "Personal", "Sales Income", "Other"};
                    ArrayAdapter<String> adapterItems = new ArrayAdapter<String>(getActivity(), R.layout.list_item, list);
                    autoCompleteTextView.setAdapter(adapterItems);
                }

                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String itemSelector = adapterView.getItemAtPosition(i).toString();
                        Toast.makeText(getContext(), "Item: " + itemSelector, Toast.LENGTH_SHORT).show();
                    }
                });


                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        update.setVisibility(View.INVISIBLE);

                        String newTitle = title.getText().toString();
                        String newDesc = desc.getText().toString();
                        String cat = autoCompleteTextView.getText().toString();
                        String newAmount = amount.getText().toString();


                        FirebaseDatabase.getInstance().getReference(Path).orderByChild("uniqueID").equalTo(uniqueKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().child(uniqueKey).child("title").setValue(newTitle);
                                snapshot.getRef().child(uniqueKey).child("description").setValue(newDesc);
                                snapshot.getRef().child(uniqueKey).child("catagory").setValue(cat);
                                snapshot.getRef().child(uniqueKey).child("amount").setValue(newAmount);


                                FirebaseDatabase.getInstance().getReference(Path).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String oldamountString = snapshot.child(Billtype).getValue(String.class);
                                        float oldAmount = Float.parseFloat(oldamountString);
                                        float thisAmountFloat = Float.parseFloat(thisAmount);
                                        float newAmountFloat = Float.parseFloat(newAmount);
                                        float previousAmount = oldAmount - thisAmountFloat;
                                        float newtotal = previousAmount + newAmountFloat;
                                        String newtotalString = String.valueOf(newtotal);

                                        snapshot.getRef().child(Billtype).setValue(newtotalString);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        update.setVisibility(View.VISIBLE);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                update.setVisibility(View.VISIBLE);
                            }

                        });

                        bottomSheetDialog.dismiss();
                        fetchData(dialog);
                        adapter.notifyDataSetChanged();

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                        adapter.notifyDataSetChanged();
                        fetchData(dialog);
                    }
                });


                bottomSheetDialog.show();



            }
            @Override
            public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,float dX, float dY,int actionState, boolean isCurrentlyActive){

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.blue))
                        .addSwipeRightActionIcon(R.drawable.baseline_edit_24)
                        .addSwipeRightLabel("Edit")
                        .setSwipeRightLabelColor(ContextCompat.getColor(getActivity(), R.color.white))
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }



        };

        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(simpleItemTouchCallback2);
        itemTouchHelper2.attachToRecyclerView(recyclerView);

        addBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddIncomeExpense.class);
                String type = "Income";
                String[] item = {"Monthly Salary", "Extra Income", "Personal", "Sales Income", "Other"};
                intent.putExtra("Arraylist", item);
                intent.putExtra(BILL_TYPE, type);
                startActivityForResult(intent, 1234);

            }

            public void onBackPressed(View view) {

            }

        });

        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData(dialog);
                ReplaceFragment(new HomeFragment());
                layout.setRefreshing(false);
            }
        });


        return v;
    }

    private void fetchData(AlertDialog dialog) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    if (!itemSnapshot.getValue().getClass().getSimpleName().equals("String")) {
                        DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                        dataList.add(0, dataClass);
                        if (snapshot.hasChild("Income")) {
                            String TotalIncomeString = snapshot.child("Income").getValue(String.class);
                            float TotalIncome = Float.parseFloat(TotalIncomeString);
                            NumberFormat formate = NumberFormat.getCurrencyInstance();
                            formate.setCurrency(Currency.getInstance("LKR"));
                            String newIncome = formate.format(TotalIncome);
                            incomeCurrency.setText(newIncome);
                        }
                        if (snapshot.hasChild("Expense")) {
                            String TotalExpenseString = snapshot.child("Expense").getValue(String.class);
                            float TotalExpense = Float.parseFloat(TotalExpenseString);
                            NumberFormat formate = NumberFormat.getCurrencyInstance();
                            formate.setCurrency(Currency.getInstance("LKR"));
                            String newExpense = formate.format(TotalExpense);
                            expenseCurrency.setText(newExpense);

                        }
                        if (snapshot.hasChild("Income") && (snapshot.hasChild("Expense"))) {

                            String Income = snapshot.child("Income").getValue(String.class);
                            String expense = snapshot.child("Expense").getValue(String.class);
                            float income = Float.parseFloat(Income);
                            float Expense = Float.parseFloat(expense);

                            float remainingBalance = income - Expense;

                            NumberFormat formatter = new DecimalFormat("#,###.00");
                            String formattedNumber = formatter.format(remainingBalance);

                            balance.setText(formattedNumber);

                            if (remainingBalance <= 0.00) {
                                checkMark.setVisibility(View.INVISIBLE);
                                errorMark.setVisibility(View.VISIBLE);
                            } else {
                                checkMark.setVisibility(View.VISIBLE);
                                errorMark.setVisibility(View.INVISIBLE);
                            }
                        } else if (snapshot.hasChild("Income") && (!snapshot.hasChild("Expense"))) {
                            String Income = snapshot.child("Income").getValue(String.class);
                            float Balance = Float.parseFloat(Income);

                            NumberFormat formatter = new DecimalFormat("#,###.00");
                            String formattedNumber = formatter.format(Balance);

                            checkMark.setVisibility(View.VISIBLE);

                            balance.setText(formattedNumber);
                        } else if (!snapshot.hasChild("Income") && (snapshot.hasChild("Expense"))) {
                            String Expense = snapshot.child("Expense").getValue(String.class);
                            float Balance = Float.parseFloat(Expense);

                            NumberFormat formatter = new DecimalFormat("#,###.00");
                            String formattedNumber = formatter.format(Balance);

                            balance.setText("-" + formattedNumber);

                            errorMark.setVisibility(View.VISIBLE);
                        }

                    }

                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_fragmentholder, fragment);
        fragmentTransaction.commit();
    }



    }

