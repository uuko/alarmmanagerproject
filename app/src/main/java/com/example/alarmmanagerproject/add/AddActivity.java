package com.example.alarmmanagerproject.add;

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
import com.example.alarmmanagerproject.MainActivity;
import com.example.alarmmanagerproject.R;
import com.example.alarmmanagerproject.SharedPreUtils;
import com.example.alarmmanagerproject.Time.TimePickerFragment;
import com.example.alarmmanagerproject.databinding.ActivityAddBinding;
import com.example.alarmmanagerproject.home.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Random;

public class AddActivity extends AppCompatActivity implements AddConstract.View, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private DatabaseReference databaseReference;
    private Integer mPriority;
    private Calendar store;
    private String key;
    ActivityAddBinding activityAddBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_add);
        init();

    }

    public void init() {
        activityAddBinding = DataBindingUtil.setContentView(this, R.layout.activity_add);
        activityAddBinding.setView(this);
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
                        activityAddBinding.datePicker.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onSubmit() {
        if (activityAddBinding.setTimercheck.isChecked()) {
            Log.d("test", "onSubmit: " + TextUtils.isEmpty(activityAddBinding.timePicker.getText()));
            if (!TextUtils.isEmpty(activityAddBinding.timePicker.getText())) {
                startAlarm(store);
            }
        } else {
            cancelAlarm();
        }
        setDatabaseReference();
    }

    private  void  setDatabaseReference(){
        Integer todoNum = new Random().nextInt();
        key = Integer.toString(todoNum);
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("DoesApp").child("Todo" + todoNum);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("priority").setValue(mPriority);
                dataSnapshot.getRef().child("clock").setValue(activityAddBinding.timePicker.getText().toString());
                dataSnapshot.getRef().child("key").setValue(key);
                dataSnapshot.getRef().child("title").setValue(activityAddBinding.title.getText().toString());
                dataSnapshot.getRef().child("desc").setValue(activityAddBinding.discription.getText().toString());
                dataSnapshot.getRef().child("date").setValue(activityAddBinding.datePicker.getText().toString());
                Intent intent = new Intent(AddActivity.this, HomeActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onPrioritySelected() {
        if ((activityAddBinding.radButton1).isChecked()) {
            mPriority = 1;
        } else if ((activityAddBinding.radButton2).isChecked()) {
            mPriority = 2;
        } else if ((activityAddBinding.radButton3).isChecked()) {
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
    private void updateTimeText(Calendar c) {
        String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        activityAddBinding.timePicker.setText(timeText);
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
