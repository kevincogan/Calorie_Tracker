package com.example.phaze;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phaze.classes.Food;
import com.example.phaze.classes.User;
import com.example.phaze.meals.Breakfast;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class UserTest extends AppCompatActivity {

    @Mock
    private Context context;

    @Test
    public void startClient() {
        assertNull(User.client);

        User.startClient();
        assertNotNull(User.client);
    }

    @Test
    public void testLocalSave() {

        MockitoAnnotations.initMocks(this);
        when(context.getApplicationContext()).thenReturn(context);

        int calories = 150;

        double protein = 20.0;
        double carbs = 26.9;
        double fat = 10.5;

        User.context = context;

        User.setCaloriesEaten(calories);

        User.setTotalProtein(protein);
        User.setTotalCarbs(carbs);
        User.setTotalFat(fat);

        User.putSaveLocal();

        User.resetUser();

        User.getSaveLocal();

        assertEquals(calories, User.getCaloriesEaten());
        assertEquals(protein, User.getTotalProtein(), 0.1);
        assertEquals(carbs, User.getTotalCarbs(), 0.1);
        assertNotEquals(12.6, User.getTotalFat(), 0.1);
    }

    @Test
    public void getServerInfo() {

        int calories = 150;

        double protein = 20.0;
        double carbs = 26.9;
        double fat = 10.5;

        User.username = "test";

        User.startClient();

        User.setCaloriesEaten(calories);

        User.setTotalProtein(protein);
        User.setTotalCarbs(carbs);
        User.setTotalFat(fat);

        User.updateServer("overwrite");

        User.resetUser();

        User.username = "test";

        User.getServerInfo();

        assertEquals(calories, User.getCaloriesEaten());
        assertEquals(protein, User.getTotalProtein(), 0.1);
        assertEquals(carbs, User.getTotalCarbs(), 0.1);
        assertNotEquals(12.6, User.getTotalFat(), 0.1);
    }

    @Test
    public void decodeString() {
        String input = "donut%default 100g%150%20.5%20.5%20.5|curry%default 100g%150%20.5%20.5%20.5";
        String[] output;
        String[] expected = {"donut%default 100g%150%20.5%20.5%20.5", "curry%default 100g%150%20.5%20.5%20.5"};

        output = User.decodeString(input);

        assertEquals(expected, output);
    }

    @Test
    public void updateServerMeals() {

        User.updateServerMeals("overwrite");
        User.getServerInfo();

        assertEquals(0, Breakfast.getMeals().size());

        Food food = new Food("donut", "Default 100g", 150.0, 20.5, 10.7, 35.8);
        Breakfast.addMeal(food);

        User.updateServerMeals("overwrite");

        assertEquals("donut", Breakfast.getMeals().get(0).getName());
    }
}