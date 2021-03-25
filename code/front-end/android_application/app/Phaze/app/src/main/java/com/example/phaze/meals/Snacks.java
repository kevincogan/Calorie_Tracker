package com.example.phaze.meals;

import com.example.phaze.classes.Food;

import java.util.ArrayList;
import java.util.List;

public class Snacks {

    private static List<Food> meals = new ArrayList<>();
    private static int totolCalories = 0;

    // Adds a meal and calculates the total calories
    public static void addMeal(Food food)
    {
        Snacks.meals.add(food);
        Snacks.totolCalories += food.getCaloriesInt();
    }

    // Returns the meals
    public static List<Food> getMeals()
    {
        return Snacks.meals;
    }

    // Returns the total calories
    public static int getCalories()
    {
        return Snacks.totolCalories;
    }

    public static void removeMeal(int position)
    {
        Snacks.totolCalories -= meals.get(position).getCalories();
        meals.remove(position);
    }

    // Updates the meals with a new list of foods and overwrites the old one
    public static void updateMeals(List<Food> foods)
    {
        Snacks.totolCalories = 0;
        Snacks.meals = new ArrayList<>();

        for (Food food : foods){
            Snacks.addMeal(food);
        }
    }

    // Resets the class
    public static void reset()
    {
        Snacks.totolCalories = 0;
        Snacks.meals.clear();
    }
}
