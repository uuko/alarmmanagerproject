package com.example.alarmmanagerproject.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.alarmmanagerproject.MainActivity;
import com.example.alarmmanagerproject.R;
import com.example.alarmmanagerproject.add.AddActivity;
import com.example.alarmmanagerproject.databinding.ActivityHomeBinding;
import com.example.alarmmanagerproject.model.Todo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements  HomeContract.View {
    private DatabaseReference mDatabase;
    ActivityHomeBinding activityHomeBinding;
    private HomeAdapter homeAdapter;
    ArrayList<Todo> todos=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("DoesApp");
        /*
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Integer doesNum = new Random().nextInt(); //id
        String keydoes = Integer.toString(doesNum);
        Todo user = new Todo("111", "111","111","111","111",0);
        Todo user1 = new Todo("222", "222","222","222","222",0);
        mDatabase.child("DoesApp"+keydoes).setValue(user);
        mDatabase.child("DoesApp"+keydoes).setValue(user1);*/
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                todos.clear();
            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                Log.d("111", "onDataChange: " + dataSnapshot1);
                Todo p = dataSnapshot1.getValue(Todo.class);
                Log.d("test", "onDataChange: "+p.getPriority());
                todos.add(p);
            }
                homeAdapter=new HomeAdapter(todos);
                activityHomeBinding.recyclerview.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                activityHomeBinding.recyclerview.setAdapter(homeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        mDatabase.addValueEventListener(postListener);

       // setContentView(R.layout.activity_home);
    }

    public  void  init(){
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        activityHomeBinding.setView(this);
        //activityHomeBinding.recyclerview.setAdapter();
    }
    @Override
    public void onAddClick() {
        Intent intent=new Intent(HomeActivity.this, AddActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSearchClick() {
        final String input = activityHomeBinding.editText.getText().toString();
        Log.d("777", "onClick: " + input);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todos.clear();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String title = (String) singleSnapshot.child("title").getValue();
                    Log.d("777", "input: "+input);
                    Log.d("777", "title: "+title);
                    if (title.equals(input)) {
                      Todo  p = singleSnapshot.getValue(Todo.class);
                        if (p.getTitle().equals(input)) {
                            todos.add(p);
                        }
                        Log.d("777", "onD: " + todos.get(0).getTitle());
                    }
                    Log.d("777", "onDataChange: " + title);
                    //do your logic
                }
                homeAdapter = new HomeAdapter(todos);
                activityHomeBinding.recyclerview.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                activityHomeBinding.recyclerview.setAdapter(homeAdapter);
                homeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
