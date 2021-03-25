package com.example.phaze.meals;

import com.example.phaze.classes.Food;

import java.util.ArrayList;
import java.util.List;

public class Lunch {

    private static List<Food> meals = new ArrayList<>();
    private static int totolCalories = 0;

    // Adds a meal and calculates the total calories
    public static void addMeal(Food food)
    {
        Lunch.meals.add(food);
        Lunch.totolCalories += food.getCaloriesInt();
    }

    // Returns the meals
    public static List<Food> getMeals()
    {
        return Lunch.meals;
    }

    // Returns the total calories
    public static int getCalories()
    {
        return Lunch.totolCalories;
    }

    public static void removeMeal(int position)
    {
        Lunch.totolCalories -= meals.get(position).getCalories();
        meals.remove(position);
    }

    // Updates the meals with a new list of foods and overwrites the old one
    public static void updateMeals(List<Food> foods)
    {
        Lunch.totolCalories = 0;
        Lunch.meals = new ArrayList<>();

        for (Food food : foods){
            Lunch.addMeal(food);
        }
    }

    // Resets the class
    public static void reset()
    {
        Lunch.totolCalories = 0;
        Lunch.meals.clear();
    }
}
