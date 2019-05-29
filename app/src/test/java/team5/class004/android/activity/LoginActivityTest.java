package team5.class004.android.activity;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static team5.class004.android.activity.LoginActivity.isValidEmail;

public class LoginActivityTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIsValidEmail() {
        String input = "acmxkz@gmail.com";
        System.out.println("input: " + input);
        System.out.println(isValidEmail(input));
    }
}