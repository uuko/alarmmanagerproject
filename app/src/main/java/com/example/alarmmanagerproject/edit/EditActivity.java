package com.example.alarmmanagerproject.edit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.alarmmanagerproject.AlertReceiver;
import com.example.alarmmanagerproject.R;
import com.example.alarmmanagerproject.SharedPreUtils;
import com.example.alarmmanagerproject.Time.TimePickerFragment;
import com.example.alarmmanagerproject.add.AddActivity;
import com.example.alarmmanagerproject.databinding.ActivityEditBinding;
import com.example.alarmmanagerproject.home.HomeActivity;
import com.example.alarmmanagerproject.model.Todo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Random;

public class EditActivity extends AppCompatActivity implements EditContract.View,TimePickerDialog.OnTimeSetListener {
    private DatabaseReference databaseReference;
    private Integer mPriority;
    private Calendar store;
    private String key;
    ActivityEditBinding activityEditBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        Log.d("testd", "onCreate: "+  getIntent().getStringExtra("decs"));

    }
    public void init() {
        activityEditBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit);
        Todo todoData=new Todo(getIntent().getStringExtra("title"),getIntent().getStringExtra("date"),getIntent().getStringExtra("decs"),getIntent().getStringExtra("key"),getIntent().getStringExtra("clock"),0);
        activityEditBinding.setData(todoData);
        activityEditBinding.setView(this);
    }

    @Override
    public void onTimeClick() {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    @Override
    public void onDateClick() {
        final Calendar cm = Calendar.getInstance();
        int mYear = cm.get(Calendar.YEAR);
        int mMonth = cm.get(Calendar.MONTH);
        int mDay = cm.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        activityEditBinding.datePicker.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void onSubmit() {
        if (activityEditBinding.setTimercheck.isChecked()) {
            Log.d("test", "onSubmit: " + TextUtils.isEmpty(activityEditBinding.timePicker.getText()));
            if (!TextUtils.isEmpty(activityEditBinding.timePicker.getText())) {
                startAlarm(store);
            }
        } else {
            cancelAlarm();
        }
        setDatabaseReference();
    }

    private  void  setDatabaseReference(){

        key = getIntent().getStringExtra("key");
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("DoesApp").child("Todo" + key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("priority").setValue(mPriority);
                dataSnapshot.getRef().child("clock").setValue(activityEditBinding.timePicker.getText().toString());
                dataSnapshot.getRef().child("keydoes").setValue(key);
                dataSnapshot.getRef().child("title").setValue(activityEditBinding.title.getText().toString());
                dataSnapshot.getRef().child("desc").setValue(activityEditBinding.discription.getText().toString());
                dataSnapshot.getRef().child("date").setValue(activityEditBinding.datePicker.getText().toString());
                Intent intent = new Intent(EditActivity.this, HomeActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onPrioritySelected() {
        if ((activityEditBinding.radButton1).isChecked()) {
            mPriority = 1;
        } else if ((activityEditBinding.radButton2).isChecked()) {
            mPriority = 2;
        } else if ((activityEditBinding.radButton3).isChecked()) {
            mPriority = 3;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        store = Calendar.getInstance();
        store = c;
        updateTimeText(c);

    }
    private void updateTimeText(Calendar c) {
        String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        activityEditBinding.timePicker.setText(timeText);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        int alarmId = SharedPreUtils.getInt(this, "alarm_id", 0);
        SharedPreUtils.setInt(this, "alarm_id", ++alarmId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "您已設置定時提醒", Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "您尚未設置定時提醒", Toast.LENGTH_SHORT).show();
    }
}
