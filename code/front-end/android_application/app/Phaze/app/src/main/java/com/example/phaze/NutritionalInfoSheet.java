package com.example.phaze;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phaze.classes.Food;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NutritionalInfoSheet extends BottomSheetDialogFragment {

    String food_name;

    View view;

    RecyclerView recyclerView;
    MyScannedFoodAdapter.RecyclerViewClickListener listener;

    ImageView addFood;
    Food food;
    List<Food> foodList = new ArrayList<>();

    public void setName(String name)
    {
        food_name = name;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sheet_nutritional_info, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

//        setTime();

        addFood = view.findViewById(R.id.addFood);

        // Queries the food database
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    // Request the food from the database
                    RequestFood rf = new RequestFood();

                    // Special cases for certain foods
                    if (food_name.equals("ingr=hot%20dog")){
                        rf.n = 20;
                    }

                    List<Food> tmpList = rf.Request(food_name);
                    // Get the food information
                    if (tmpList.size() > 0) {
                        // Special cases for certain foods
                        if (food_name.equals("ingr=steak")) {
                            food = tmpList.get(1);
                        }
                        else if (food_name.equals("ingr=hot%20dog")){
                            food = tmpList.get(18);
                        }
                        else{
                            food = tmpList.get(0);
                        }
                        foodList.add(food);

                        // Display the nutritional information
                        setNutritionalInfo();
                    } else {
                        // If no food was found
                        TextView notFound = view.findViewById(R.id.notFound);
                        notFound.setVisibility(View.VISIBLE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NetworkErrorException e) {
                    // ADD "Please connect to internet activity"
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Set the add food button on click listener
        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Start the Choose meal activity
                Intent intent = new Intent(getActivity(), ChooseMealActivity.class);
                intent.putExtra("Food", food);
                System.out.println(food.getCalories());
                startActivity(intent);
                return;
            }
        });
        return view;
    }

    // Set the nutritional information
    public void setNutritionalInfo()
    {
        // Add the food list to the recycler view
        setOnClickListener();
        MyScannedFoodAdapter myScannedFoodAdapter = new MyScannedFoodAdapter(getActivity(), foodList, listener);
        recyclerView.setAdapter(myScannedFoodAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Set on click listener for the recycler view items
    public void setOnClickListener()
    {
        listener = new MyScannedFoodAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
            }
        };
    }

    // Sets the time (not used anymore but plans to use in the future)
    public void setTime()
    {
        TextView time = view.findViewById(R.id.time);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm a");

        date.setTimeZone(TimeZone.getTimeZone("GMT"));

        String localTime = date.format(currentLocalTime);
        time.setText(localTime);
    }
}
