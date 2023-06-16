package com.example.vibrate;

import android.annotation.SuppressLint;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class UploadFragment extends Fragment {

    ViewGroup Cont;
    private ProgressBar progressBar;
    private TextView Percentage, ExpenseCurrency, BillType;
    ConstraintLayout Error;

    private Button addExpense;

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;
    List<DataClass> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;

    MyAdapter adapter;

    public final static String BILL_TYPE = "com.example.vibrate.message_key";

    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_upload, container, false);

        Cont = v.findViewById(R.id.Expense);

        swipeRefreshLayout = v.findViewById(R.id.SwipetoRefreshLayout);
        recyclerView = v.findViewById(R.id.recycle_view);

        BillType = v.findViewById(R.id.BillTypeTextList);

        progressBar = v.findViewById(R.id.progress_bar);
        Percentage = v.findViewById(R.id.percentage);
        ExpenseCurrency = v.findViewById(R.id.currency);
        addExpense = v.findViewById(R.id.addExpense);

        Error = v.findViewById(R.id.errorMessage);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        progressBar.setProgress(0);
        Percentage.setText("0%");

        String path = currentUser.getEmail();
        String email = path.replaceAll("[-+.^:,]","");


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);


        dataList = new ArrayList<>();

        adapter = new MyAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference(email);
        Query query = databaseReference.orderByChild("bill").equalTo("Expense");

        fetchData(email, query);


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
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(email);
                Query query = ref.orderByChild("uniqueID").equalTo(Key);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item: snapshot.getChildren()) {
                            item.getRef().removeValue();

                            FirebaseDatabase.getInstance().getReference(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(bill)) {
                                        String PreviousAmountString = snapshot.child(bill).getValue(String.class);
                                        float PreviousAmount = Float.parseFloat(PreviousAmountString);
                                        float newAmount = PreviousAmount - NowAmount;
                                        String newAmountString = String.valueOf(newAmount);
                                        FirebaseDatabase.getInstance().getReference(email).child(bill).setValue(newAmountString);
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
                fetchData(email, query);

            }
            @Override
            public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

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


                        FirebaseDatabase.getInstance().getReference(email).orderByChild("uniqueID").equalTo(uniqueKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().child(uniqueKey).child("title").setValue(newTitle);
                                snapshot.getRef().child(uniqueKey).child("description").setValue(newDesc);
                                snapshot.getRef().child(uniqueKey).child("catagory").setValue(cat);
                                snapshot.getRef().child(uniqueKey).child("amount").setValue(newAmount);


                                FirebaseDatabase.getInstance().getReference(email).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        fetchData(email, query);
                        bottomSheetDialog.dismiss();
                        adapter.notifyDataSetChanged();

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchData(email, query);
                        bottomSheetDialog.dismiss();
                        adapter.notifyDataSetChanged();
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

        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddIncomeExpense.class);
                String type = "Expense";
                String[] item = {"Restaurant", "Family", "Education", "Entertainment", "Personal", "travelling", "Functions", "Household", "Other"};
                intent.putExtra("Arraylist", item);
                intent.putExtra(BILL_TYPE, type);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData(email, query);
                adapter.notifyDataSetChanged();
                ReplaceFragment(new UploadFragment());
                swipeRefreshLayout.setRefreshing(false);
            }
        });





        // Inflate the layout for this fragment
        return v;
    }

    private void refreshFragment(){
        getFragmentManager()
                .beginTransaction()
                .detach(this)
                .attach(this)
                .addToBackStack(null)
                .commit();
    }

    private void fetchData(String email, Query query) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    if (!itemSnapshot.getValue().getClass().getSimpleName().equals("String")) {
                        DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                        dataList.add(0, dataClass);

                        FirebaseDatabase.getInstance().getReference(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            boolean visiblle;
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild("Expense") && (snapshot.hasChild("Income"))) {
                                    String IncomeString = snapshot.child("Income").getValue(String.class);
                                    String ExpenseString = snapshot.child("Expense").getValue(String.class);
                                    float income = Float.parseFloat(IncomeString);
                                    float expense = Float.parseFloat(ExpenseString);
                                    float percentage = (expense/income) * 100;
                                    progressBar.setProgress((int) percentage);
                                    Integer progress = (int) percentage;
                                    String proressString = String.valueOf(progress);
                                    Percentage.setText(proressString + "%");

                                    NumberFormat formate = NumberFormat.getCurrencyInstance();
                                    formate.setCurrency(Currency.getInstance("LKR"));
                                    String newExpense = formate.format(expense);

                                    ExpenseCurrency.setText(newExpense);

                                    if (income < expense) {
                                        TransitionManager.beginDelayedTransition(Cont);
                                        visiblle = !visiblle;
                                        Error.setVisibility(visiblle? View.VISIBLE: View.GONE);
                                    }

                                } else if (snapshot.hasChild("Expense") && (!snapshot.hasChild("Income"))) {
                                    progressBar.setProgress(100);
                                    Percentage.setText("100%");
                                    String ExpenseString = snapshot.child("Expense").getValue(String.class);
                                    float expense = Float.parseFloat(ExpenseString);

                                    NumberFormat formate = NumberFormat.getCurrencyInstance();
                                    formate.setCurrency(Currency.getInstance("LKR"));
                                    String newExpense = formate.format(expense);

                                    ExpenseCurrency.setText(newExpense);

                                    TransitionManager.beginDelayedTransition(Cont);
                                    visiblle = !visiblle;
                                    Error.setVisibility(visiblle? View.VISIBLE: View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getActivity(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }

                adapter.notifyDataSetChanged();
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