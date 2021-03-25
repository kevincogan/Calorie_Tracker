package com.example.phaze.meals;

import com.example.phaze.classes.Food;

import java.util.ArrayList;
import java.util.List;

public class Breakfast {

    private static List<Food> meals = new ArrayList<>();
    private static int totolCalories = 0;

    // Adds a meal and calculates the total calories
    public static void addMeal(Food food)
    {
        Breakfast.meals.add(food);
        Breakfast.totolCalories += food.getCaloriesInt();
    }

    // Returns the meals
    public static List<Food> getMeals()
    {
        return Breakfast.meals;
    }

    // Returns the total calories
    public static int getCalories()
    {
        return Breakfast.totolCalories;
    }

    public static void removeMeal(int position)
    {
        Breakfast.totolCalories -= meals.get(position).getCaloriesInt();
        meals.remove(position);
    }

    // Updates the meals with a new list of foods and overwrites the old one
    public static void updateMeals(List<Food> foods)
    {
        Breakfast.totolCalories = 0;
        Breakfast.meals = new ArrayList<>();

        for (Food food : foods){
            Breakfast.addMeal(food);
        }
    }

    // Resets the class
    public static void reset()
    {
        Breakfast.totolCalories = 0;
        Breakfast.meals.clear();
    }

}
