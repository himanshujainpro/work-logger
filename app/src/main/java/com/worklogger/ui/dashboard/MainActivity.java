package com.worklogger.ui.dashboard;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.worklogger.R;
import com.worklogger.data.model.firebase.SingleLog;
import com.worklogger.ui.auth.LoginActivity;
import com.worklogger.utils.Network;
import com.worklogger.utils.UI;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
        , NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference userLogs = db.collection("users").document(mAuth.getUid())
            .collection("logs");

    final Calendar newCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    private DatePickerDialog mdatePickerDialog;

    private UI mUI;
    private Button mBtnSelectDate;
    private EditText mEtDes;
    private DrawerLayout drawerLayout;
    private TextView textViewName;
    private TextView textViewEmail;

    private Spinner mSpinnerSelectHours;
    private String[] mArrayHours;

    private Spinner mSpinnerSelectUnit;

    private String mSelectedCounter = "0";
    private String mSelectedDate = dateFormat.format(newCalendar.getTime());
    ;
    private String mSelectedUnit = "Minutes";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mBtnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdatePickerDialog.show();
            }
        });

        mSpinnerSelectHours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedCounter = mArrayHours[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpinnerSelectUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        mSelectedUnit = "Minutes";
                        break;
                    case 1:
                        mSelectedUnit = "Hours";
                        break;
                    case 2:
                        mSelectedUnit = "Days";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.btn_save_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLogToFireStore();
            }
        });
    }

    private void initViews() {
        mUI = new UI(this);
        textViewEmail = findViewById(R.id.tv_email);
        textViewName = findViewById(R.id.tv_name);
        textViewName.setText(mAuth.getCurrentUser().getDisplayName());
        textViewEmail.setText(mAuth.getCurrentUser().getEmail());
        mBtnSelectDate = findViewById(R.id.btn_select_date);
        mBtnSelectDate.setText(mSelectedDate);
        mSpinnerSelectHours = findViewById(R.id.spinner_hours_main);
        mEtDes = findViewById(R.id.edt_des);
        mSpinnerSelectUnit = findViewById(R.id.spinner_select_unit_main);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSelectUnit.setAdapter(adapter);

        mdatePickerDialog = new DatePickerDialog(this, MainActivity.this,
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));

        mArrayHours = new String[101];

        for (int i = 0; i < 101; i++) mArrayHours[i] = String.valueOf(i);

        ArrayAdapter hoursAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
                mArrayHours);

        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSelectHours.setAdapter(hoursAdapter);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(i, i1, i2);

        mBtnSelectDate.setText(dateFormat.format(newDate.getTime()));
        mSelectedDate = dateFormat.format(newDate.getTime());
    }


    // user data validation task
    private boolean validateData() {
        boolean isValid = true;

        if (mSelectedDate.isEmpty()) {
            mUI.showToast("Please Select Date");
            isValid = false;
        }
        if (TextUtils.isEmpty(mEtDes.getText().toString())) {
            mEtDes.setError("Description is missing!");
            isValid = false;
        }
        return isValid;
    }

    //Network task
    private void saveLogToFireStore() {
        if (Network.isInternetAvailable(this)) {
            if (validateData()) {
                mUI.showProgressDialog();
                SingleLog singleLog = new SingleLog();
                singleLog.setmCount(mSelectedCounter);
                singleLog.setmDate(mSelectedDate);
                singleLog.setmDescription(mEtDes.getText().toString());
                singleLog.setmUnit(mSelectedUnit);
                singleLog.setmUid(mAuth.getUid());
                userLogs.add(singleLog).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        mUI.hideProgressDialog();
                        mUI.showToast(getString(R.string.txt_data_saved));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mUI.hideProgressDialog();
                        mUI.showToast(e.getMessage());
                    }
                });
            }
        } else {
            mUI.showToast(getString(R.string.txt_no_network));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logs:
                startActivity(new Intent(MainActivity.this, MyLogsActivity.class));
                break;
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
