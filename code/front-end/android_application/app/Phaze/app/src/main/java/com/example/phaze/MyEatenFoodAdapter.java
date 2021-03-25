package com.example.phaze;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phaze.classes.Food;

import java.util.ArrayList;
import java.util.List;

public class MyEatenFoodAdapter extends RecyclerView.Adapter<MyEatenFoodAdapter.MyViewHolder>
{

//    #FF8426 - orange

    List<Food> data = new ArrayList<>();
    Context context;

    boolean isEnabled = false;
    List<Food> selected = new ArrayList<>();

    private RecyclerViewClickListener listener;

    // Custom adapter for the Meal User Information menu
    public MyEatenFoodAdapter(Context ct, List<Food> food, RecyclerViewClickListener listener)
    {
        context = ct;
        data = food;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the layout from eatenfoodview_layout.xml
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.eatenfoodview_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Binds the view holder to the data being passed
        holder.meal.setText(data.get(position).getName());
        holder.calories.setText(data.get(position).getCaloriesInt() + "cal");
        holder.protein.setText(spanText("Protein", data.get(position).getProtein()));
        holder.carbs.setText(spanText("Carbs", data.get(position).getCarbs()));
        holder.fat.setText(spanText("Fat", data.get(position).getFat()));

        // Sets the on click listener for the edit button
        MealUserInformation.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allows the user to delete eaten food
                if (MealUserInformation.edit.getText().equals("Edit")){
                    MealUserInformation.edit.setText("Cancel");
                    isEnabled = true;
                    MealUserInformation.editFood();
                    MealUserInformation.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // If the delete button is clicked, delete the food and notify the data set changed
                            if (selected.size() > 0) {
                                for (Food food : selected) {
                                    data.remove(food);
                                }
                                finishAndDelete();
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
                else{
                    finish();
                }
                notifyDataSetChanged();
            }
        });

        // If the User is able to delete
        if (isEnabled){
            holder.check.setVisibility(View.VISIBLE);
            holder.dropdown.setVisibility(View.GONE);
            holder.arrow.setBackgroundResource(R.drawable.arrow_down);
        }
        // If he is not reset everything
        else{
            holder.itemSelected = false;
            holder.check.setBackgroundResource(R.drawable.check_circle_unselected);
            holder.check.setVisibility(View.GONE);
        }
    }

    // Finish the delete process without deleting
    public void finish()
    {
        MealUserInformation.edit.setText("Edit");
        isEnabled = false;
        selected.clear();
        MealUserInformation.delete.setBackgroundColor(Color.parseColor("#B4B4B4"));
        MealUserInformation.editFood();
    }

    // Finish the delete process while deleting
    public void finishAndDelete()
    {
        MealUserInformation.edit.setText("Edit");
        isEnabled = false;
        selected.clear();
        MealUserInformation.delete.setBackgroundColor(Color.parseColor("#B4B4B4"));
        MealUserInformation.updatedFlag = true;
        MealUserInformation.editFood();
    }

    // If the item is clicked to be deleted
    private void ClickItem(MyViewHolder holder) {
        Food food = data.get(holder.getAdapterPosition());

        // If the item was not previously selected
        if (holder.itemSelected == false){

            // Add to delete list
            holder.itemSelected = true;
            holder.check.setBackgroundResource(R.drawable.check_circle);
            selected.add(food);
        }
        // If the item was previously selected
        else {

            // Deselect it and remove it from the delete queue
            holder.itemSelected = false;
            holder.check.setBackgroundResource(R.drawable.check_circle_unselected);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            selected.remove(food);
        }

        // Change the colour of the delete button based on the number of foods selected
        if (selected.size() > 0){
            MealUserInformation.delete.setBackgroundColor(Color.parseColor("#FF8426"));
        }
        else {
            MealUserInformation.delete.setBackgroundColor(Color.parseColor("#B4B4B4"));
        }
    }

    // Build a custom string for the macro legend of each food
    public CharSequence spanText(String macro, double value)
    {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString l = new SpannableString(macro + "\n");
        SpannableString v = new SpannableString(value + "g");

        l.setSpan(new ForegroundColorSpan(Color.GRAY), 0, macro.length(), 0);
        l.setSpan(new RelativeSizeSpan(0.8f), 0, macro.length(), 0);
        builder.append(l);

        v.setSpan(new ForegroundColorSpan(Color.BLACK), 0, String.valueOf(value).length() + 1, 0);
        v.setSpan(new RelativeSizeSpan(1.2f), 0, String.valueOf(value).length() + 1, 0);
        builder.append(v);

        return TextUtils.concat(builder);
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

        ImageView check;
        ImageView arrow;

        LinearLayout dropdown;

        TextView protein;
        TextView carbs;
        TextView fat;

        boolean itemSelected = false;

        // Constructor for the adapter view holder
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            meal = itemView.findViewById(R.id.mealText);
            calories = itemView.findViewById(R.id.mealCalories);
            check = itemView.findViewById(R.id.check);
            arrow = itemView.findViewById(R.id.arrow);
            dropdown = itemView.findViewById(R.id.dropdown);
            protein = itemView.findViewById(R.id.protein);
            carbs = itemView.findViewById(R.id.carbs);
            fat = itemView.findViewById(R.id.fat);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {

            // Hide and display extra information regarding the food (macros) on click
            listener.onClick(itemView, getAdapterPosition());
            if (dropdown.getVisibility() == View.GONE && !isEnabled){
                dropdown.setVisibility(View.VISIBLE);
                arrow.setBackgroundResource(R.drawable.arrow_up);
            }
            else{
                dropdown.setVisibility(View.GONE);
                arrow.setBackgroundResource(R.drawable.arrow_down);
            }
            if (isEnabled){
                ClickItem(this);
            }
        }
    }
}
