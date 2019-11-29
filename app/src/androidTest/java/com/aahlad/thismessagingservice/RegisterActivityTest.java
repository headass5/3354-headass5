package com.aahlad.thismessagingservice;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class RegisterActivityTest {

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Rule
    public ActivityTestRule<RegisterActivity> registerActivityTestRule = new ActivityTestRule<RegisterActivity>(RegisterActivity.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private RegisterActivity registerActivity = null;

    // Before test, set up RegisterActivity activity
    @Before
    public void setUp() {
        registerActivity = registerActivityTestRule.getActivity();
    }

    // Test launch to make sure that the register activity loaded successfully
    @Test
    public void testLaunch() {
        View view = registerActivity.findViewById(R.id.appTitle2);
        assertNotNull(view);
    }

    /*
    Test to see if the user successfully registered. This test method calls the register function.
    Once the register function is called, then the current user should be created, so the expected
    result is to return true after the method asserts that the user was created after registration.
     */
    @Test
    public void testIfUserCreated() {
        registerActivity.register("username", "email@gmail.com", "password", "en");
        assertNotNull(auth.getCurrentUser());
    }

    /*
    Test to see if the user is not created. This test method calls the register function, but does not
    include an argument for the email. You need all four parameters to register, so if a parameter
    is missing, then the method should throw an exception and not create a user. Therefore, the
    expected value is that an exception should be thrown and the current user should be null.
     */
    @Test
    public void testIfUserNotCreated() {
        registerActivity.register("username", "", "password", "en");
        thrown.expect(IllegalArgumentException.class);
        assertNull(auth.getCurrentUser());
    }

    // After test, set registerActivity to null
    @After
    public void tearDown() {
        registerActivity = null;
    }
}