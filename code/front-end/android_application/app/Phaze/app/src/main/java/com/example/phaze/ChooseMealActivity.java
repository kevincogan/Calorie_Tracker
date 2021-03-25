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
import com.example.phaze.classes.User;
import com.example.phaze.meals.Breakfast;
import com.example.phaze.meals.Dinner;
import com.example.phaze.meals.Lunch;
import com.example.phaze.meals.Snacks;

public class ChooseMealActivity extends AppCompatActivity {

    Food food;
    RecyclerView recyclerView;

    MyAdapter.RecyclerViewClickListener listener;

    String[] mealNames = {"Breakfast", "Lunch", "Dinner", "Snacks"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_meal);

        // Gets the food from the previous activity
        getFood();

        // Sets up the recycler view
        setOnClickListener();
        recyclerView = findViewById(R.id.recyclerView);
        MyAdapter myAdapter = new MyAdapter(this, mealNames, listener);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // This sets up the back button and creates a larger press detection area
        ImageView backButton = findViewById(R.id.backbutton);
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
    }

    // Gets the food from the previous activity
    public void getFood()
    {
        Intent intent = getIntent();
        food = (Food) intent.getSerializableExtra("Food");
    }

    // Sets the on click listener for each meal item in the recycler view
    public void setOnClickListener()
    {
        listener = new MyAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                switch (mealNames[position]){
                    case "Breakfast":
                        Breakfast.addMeal(food);
                        break;
                    case "Lunch":
                        Lunch.addMeal(food);
                        break;
                    case "Dinner":
                        Dinner.addMeal(food);
                        break;
                    case "Snacks":
                        Snacks.addMeal(food);
                        break;
                }

                // Updates the User's information
                User.updateStats(food.getCaloriesInt(), food.getProtein(), food.getCarbs(), food.getFat());
                User.putSaveLocal();

                // Updates the user's information on the database
                User.updateServer("overwrite");
                User.updateServerMeals("overwrite");

                // Goes back to the main menu
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        };
    }
}