package com.example.phaze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.ImageView;

import com.example.phaze.classes.Food;

import java.util.ArrayList;
import java.util.List;

public class FoodInformationActivity extends AppCompatActivity {

    private Food food;
    ImageView backButton;

    RecyclerView recyclerView;
    MyScannedFoodAdapter.RecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_information);

        // This sets up the back button and creates a larger press detection area
        backButton = findViewById(R.id.backbutton);
        View parent = findViewById(R.id.top);
        parent.post(new Runnable() {
            public void run() {
                // Creates a delegate around the backbutton
                Rect delegateArea = new Rect();
                ImageView delegate = backButton;
                delegate.getHitRect(delegateArea);
                delegateArea.top -= 50;
                delegateArea.bottom += 50;
                delegateArea.left -= 50;
                delegateArea.right += 50;
                TouchDelegate expandedArea = new TouchDelegate(delegateArea, delegate);

                if (View.class.isInstance(delegate.getParent())) {
                    ((View) delegate.getParent()).setTouchDelegate(expandedArea);
                }
            }
        });

        // Sets up back button click listener
        // Returns to previous page if pressed
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }});

        // Retrieves the food selected on the previous activity
        getFoodContents();

        // Adds that food to a List
        List<Food> foodList = new ArrayList<>();
        foodList.add(food);


        // Adds that to the recycler view
        recyclerView = findViewById(R.id.recyclerView);

        setOnClickListener();
        MyScannedFoodAdapter myScannedFoodAdapter = new MyScannedFoodAdapter(this, foodList, listener);
        recyclerView.setAdapter(myScannedFoodAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Sets up the add food button click listener
        ImageView addFood = findViewById(R.id.addFood);
        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens the Choose Meal Activity
                Intent intent = new Intent(FoodInformationActivity.this, ChooseMealActivity.class);
                intent.putExtra("Food", food);
                startActivity(intent);
            }
        });
    }

    // Retrieves the food selected on the previous activity
    public void getFoodContents()
    {
        Intent intent = getIntent();
        food = (Food) intent.getSerializableExtra("Food");
    }

    // Sets the on click listener for each recycler view item
    public void setOnClickListener()
    {
        listener = new MyScannedFoodAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
            }
        };
    }

}