package com.example.vibrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.StringValue;

import java.util.Calendar;
import java.util.Date;

public class AddIncomeExpense extends AppCompatActivity {

    private EditText editAmount, editTitle, editDescription;
    private TextView BillType;

    private Button save;

    private ProgressBar PB;
    public final static String BILL_TYPE = "com.example.vibrate.message_key";

    AutoCompleteTextView autoCompleteTextView;
    MainActivity mainActivity;

    ArrayAdapter<String> adapterItems;
    FirebaseAuth mAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income_expense);


        ImageView Button = (ImageView) findViewById(R.id.BackButton);
        autoCompleteTextView = findViewById(R.id.autoCompleteList);
        Intent intent = getIntent();
        String[] list = intent.getStringArrayExtra("Arraylist");
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, list);

        autoCompleteTextView.setAdapter(adapterItems);

        editTitle = findViewById(R.id.TitleEdit);
        editDescription = findViewById(R.id.DescriptionEdit);
        editAmount = findViewById(R.id.AmountEdit);
        BillType = findViewById(R.id.BillTypeText);

        String message = intent.getStringExtra(BILL_TYPE);
        BillType.setText(message);

        save = findViewById(R.id.SaveButton);
        PB = findViewById(R.id.ProgressBar);

        PB.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemSelector = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(AddIncomeExpense.this, "Item: " + itemSelector, Toast.LENGTH_SHORT).show();
            }
        });


        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.Grey));


        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity(1234);
                SaveData();
            }
        });


    }

    public void functionName(View v) {
        finish();
    }

    public void SaveData() {

        save.setVisibility(View.INVISIBLE);
        PB.setVisibility(View.VISIBLE);

        String title = editTitle.getText().toString();
        String Cat = autoCompleteTextView.getText().toString();
        String Description = editDescription.getText().toString();
        String Amount = editAmount.getText().toString();
        String billType = BillType.getText().toString();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String path = currentUser.getEmail();
        String email = path.replaceAll("[-+.^:,]","");

        Date date = new Date();

        Calendar c = Calendar.getInstance();
        int dates = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        String Date = String.valueOf(dates) + "/" +  String.valueOf(month) + "/" +String.valueOf(year);


        if (title.isEmpty()) {
            Toast.makeText(AddIncomeExpense.this, "title Cannot be Empty", Toast.LENGTH_SHORT).show();
            save.setVisibility(View.VISIBLE);
            PB.setVisibility(View.INVISIBLE);

        }  else if (Cat.isEmpty()) {
            Toast.makeText(AddIncomeExpense.this, "Category Cannot be Empty", Toast.LENGTH_SHORT).show();
            save.setVisibility(View.VISIBLE);
            PB.setVisibility(View.INVISIBLE);

        } else if (Amount.isEmpty()) {
            Toast.makeText(AddIncomeExpense.this, "Amount Cannot be Empty", Toast.LENGTH_SHORT).show();
            save.setVisibility(View.VISIBLE);
            PB.setVisibility(View.INVISIBLE);

        } else {


            if (billType.equals("Income")) {
                FirebaseDatabase.getInstance().getReference(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("Income")) {
                            String PreviousAmountString = snapshot.child("Income").getValue(String.class);
                            float PreviousAmount = Float.parseFloat(PreviousAmountString);
                            float CurrentAmount = Float.parseFloat(Amount);
                            float NewAmount = PreviousAmount + CurrentAmount;
                            String NewTotal = String.valueOf(NewAmount);
                            FirebaseDatabase.getInstance().getReference(email).child("Income").setValue(NewTotal);

                        } else {
                            FirebaseDatabase.getInstance().getReference(email).child("Income").setValue(Amount);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddIncomeExpense.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        save.setVisibility(View.VISIBLE);
                        PB.setVisibility(View.INVISIBLE);
                    }
                });

            } else if (billType.equals("Expense")) {
                FirebaseDatabase.getInstance().getReference(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("Expense")) {
                            String PreviousAmountString = snapshot.child("Expense").getValue(String.class);
                            float PreviousAmount = Float.parseFloat(PreviousAmountString);
                            float CurrentAmount = Float.parseFloat(Amount);
                            float NewAmount = PreviousAmount + CurrentAmount;
                            String NewTotal = String.valueOf(NewAmount);
                            FirebaseDatabase.getInstance().getReference(email).child("Expense").setValue(NewTotal);

                        } else {
                            FirebaseDatabase.getInstance().getReference(email).child("Expense").setValue(Amount);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            String key = FirebaseDatabase.getInstance().getReference(email).push().getKey();

            DataClass dataClass = new DataClass(title, Cat, Description, Amount, path, billType, Date, key, String.valueOf(month) + String.valueOf(year), String.valueOf(year));

            FirebaseDatabase.getInstance().getReference(email).child(key).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddIncomeExpense.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddIncomeExpense.this, MainActivity.class);
                        intent.putExtra("NewClicked", true);
                        if (BillType.getText().toString().equals("Expense")) {
                            intent.putExtra("addExpense", "Expense");
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();


                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddIncomeExpense.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    save.setVisibility(View.VISIBLE);
                    PB.setVisibility(View.INVISIBLE);
                }
            });
        }

    }

    @Override
    public void onResume(){
        super.onResume();

    }
}

