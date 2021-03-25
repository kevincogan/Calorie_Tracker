package com.example.phaze;

import android.accounts.NetworkErrorException;

import com.example.phaze.classes.Food;

import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class RequestFoodTest {

    @Test
    public void request() throws InterruptedException, NetworkErrorException, JSONException, IOException {

        String input1 = "ingr=apple";
        String input2 = "ingr=donut";
        String input3 = "ingr=pizza";

        String expected1 = "Apple";
        String expected2 = "Donut";
        String expected3 = "Hamburger";

        RequestFood rf = new RequestFood();
        List<Food> output;

        output = rf.Request(input1);
        assertEquals(expected1, output.get(0).getName());

        System.out.println("1");

        output = rf.Request(input2);
        assertEquals(expected2, output.get(0).getName());

        System.out.println("2");

        output = rf.Request(input3);
        assertNotEquals(expected3, output.get(0).getName());

        System.out.println("3");
    }

    @Test
    public void getFood_appid() {
        String output;
        String expected = "f635a9ab";

        RequestFood rf = new RequestFood();
        output = rf.getFood_appid();

        assertEquals(expected, output);
    }

    @Test
    public void getFood_appkey() {
        String output;
        String expected = "fee3c57a8a37a553efef8356f775f5c3";

        RequestFood rf = new RequestFood();
        output = rf.getFood_appkey();

        assertEquals(expected, output);
    }
}