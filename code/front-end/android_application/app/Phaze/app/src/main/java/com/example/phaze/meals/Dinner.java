package com.example.phaze.meals;

import com.example.phaze.classes.Food;

import java.util.ArrayList;
import java.util.List;

public class Dinner {

    private static List<Food> meals = new ArrayList<>();
    private static int totolCalories = 0;

    // Adds a meal and calculates the total calories
    public static void addMeal(Food food)
    {
        Dinner.meals.add(food);
        Dinner.totolCalories += food.getCaloriesInt();
    }

    // Returns the meals
    public static List<Food> getMeals()
    {
        return Dinner.meals;
    }

    // Returns the total calories
    public static int getCalories()
    {
        return Dinner.totolCalories;
    }

    public static void removeMeal(int position)
    {
        Dinner.totolCalories -= meals.get(position).getCalories();
        meals.remove(position);
    }

    // Updates the meals with a new list of foods and overwrites the old one
    public static void updateMeals(List<Food> foods)
    {
        Dinner.totolCalories = 0;
        Dinner.meals = new ArrayList<>();

        for (Food food : foods){
            Dinner.addMeal(food);
        }
    }

    // Resets the class
    public static void reset()
    {
        Dinner.totolCalories = 0;
        Dinner.meals.clear();
    }
}
