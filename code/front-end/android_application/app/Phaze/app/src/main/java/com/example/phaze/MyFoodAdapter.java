package com.example.phaze;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phaze.classes.Food;

import java.util.ArrayList;
import java.util.List;

public class MyFoodAdapter extends RecyclerView.Adapter<MyFoodAdapter.MyViewHolder>
{

    List<Food> data = new ArrayList<>();
    Context context;

    private RecyclerViewClickListener listener;

    // Custom adapter for the searched foods
    public MyFoodAdapter(Context ct, List<Food> food, RecyclerViewClickListener listener)
    {
        context = ct;
        data = food;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the layout from foodview_layout.xml
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.foodview_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Binds the view holder to the data being passed
        holder.meal.setText(data.get(position).getName());
        holder.calories.setText(data.get(position).getCaloriesInt() + "cal");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }

    // Holds all of the information for each food item
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView meal;
        TextView calories;

        // Constructor for the adapter view holder
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            meal = itemView.findViewById(R.id.mealText);
            calories = itemView.findViewById(R.id.mealCalories);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            listener.onClick(itemView, getAdapterPosition());
        }
    }
}
