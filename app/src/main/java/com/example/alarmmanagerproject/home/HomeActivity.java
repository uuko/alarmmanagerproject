package com.example.alarmmanagerproject.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.alarmmanagerproject.MainActivity;
import com.example.alarmmanagerproject.R;
import com.example.alarmmanagerproject.SimpleItemTouchHelperCallback;
import com.example.alarmmanagerproject.add.AddActivity;
import com.example.alarmmanagerproject.databinding.ActivityHomeBinding;
import com.example.alarmmanagerproject.model.Todo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private HomeContract.View view;
    private ArrayList<Todo> todos=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Todo");

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
                homeAdapter=new HomeAdapter(todos,view);
                ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(homeAdapter);
                //用Callback构造ItemtouchHelper
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                //调用ItemTouchHelper的attachToRecyclerView方法建立联系
                touchHelper.attachToRecyclerView(activityHomeBinding.recyclerview);
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
        view=this;
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
                    Log.d("777", "onDataChange: "
                            + title);
                    //do your logic
                }
                homeAdapter = new HomeAdapter(todos,view);
                ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(homeAdapter);
                //用Callback构造ItemtouchHelper
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                //调用ItemTouchHelper的attachToRecyclerView方法建立联系
                touchHelper.attachToRecyclerView(activityHomeBinding.recyclerview);
                activityHomeBinding.recyclerview.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                activityHomeBinding.recyclerview.setAdapter(homeAdapter);
                homeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDelClick(String key) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Todo").child("Todo" + key);
        Log.d("666", "onClick: "+mDatabase);
        mDatabase.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                } else {
                    Toast.makeText(HomeActivity.this, "失敗" +
                            "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
