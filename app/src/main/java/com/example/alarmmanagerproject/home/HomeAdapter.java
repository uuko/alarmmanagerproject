package com.example.alarmmanagerproject.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmmanagerproject.ItemTouchHelperAdapter;
import com.example.alarmmanagerproject.MainActivity;
import com.example.alarmmanagerproject.R;
import com.example.alarmmanagerproject.SimpleItemTouchHelperCallback;
import com.example.alarmmanagerproject.databinding.HomeItemBinding;
import com.example.alarmmanagerproject.edit.EditActivity;
import com.example.alarmmanagerproject.model.Todo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder<HomeItemBinding>> implements ItemTouchHelperAdapter {
    private Todo todoItem;
    private HomeItemBinding homeItemBinding;
    private ArrayList<Todo> todoList;
    private String key;
    private Context c;
    private HomeContract.View view;

    public HomeAdapter(ArrayList<Todo> todoList,HomeContract.View view) {
        this.todoList = todoList;
        this.view=view;
    }
    @NonNull
    @Override
    public ViewHolder<HomeItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("test", "onCreateViewHolder: " + todoList.get(0).getTitle());
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        c = parent.getContext();
        homeItemBinding = homeItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder<>(homeItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<HomeItemBinding> holder, int position) {
        Log.d("test", "onCreateViewHolder: " + todoList.get(0).getTitle());

        todoItem = todoList.get(position);
        final String getTitle = todoList.get(position).getTitle();
        final Integer getPiority = todoList.get(position).getPriority();
        final String getdate = todoList.get(position).getDate();
        final String getdces = todoList.get(position).getDesc();
        final String getkey = todoList.get(position).getKey();
        final String getclock = todoList.get(position).getClock();
        key = todoList.get(position).getKey();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aa = new Intent(c, EditActivity.class);
                aa.putExtra("piority", getPiority);
                aa.putExtra("title", getTitle);
                aa.putExtra("date", getdate);
                aa.putExtra("decs", getdces);
                aa.putExtra("key", getkey);
                aa.putExtra("clock", getclock);
                c.startActivity(aa);
                Log.d("kkk", "onClick: "+key);
            }
        });
        if (todoList.get(position).getPriority()==1)
        {
//            myViewHolder.numberdoes.setTextColor(Color.parseColor("#FFFFFF"));
            holder.itemView.setBackgroundColor(Color.RED);
        }
        if (todoList.get(position).getPriority()==2){
            holder.itemView.setBackgroundColor(Color.YELLOW);
        }
        if (todoList.get(position).getPriority()==3){
            holder.itemView.setBackgroundColor(Color.BLUE);
        }
        holder.bind(todoItem);
    }


    @Override
    public int getItemCount() {
        return todoList == null ? 0 : todoList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(todoList,fromPosition,toPosition);
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemDissmiss(int position) {
        todoList.remove(position);
        notifyItemRemoved(position);
        view.onDelClick(key);
    }




    public class ViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {

        private final B mViewDataBinding;

        public ViewHolder(B binding) {
            super(binding.getRoot());
            mViewDataBinding = binding;
        }

        public void bind(final Object object) {
            mViewDataBinding.setVariable(com.example.alarmmanagerproject.BR.data, object);
            mViewDataBinding.executePendingBindings();
        }

    }


}
