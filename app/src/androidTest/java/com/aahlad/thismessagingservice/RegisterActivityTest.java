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
    public ExpectedException thrown= ExpectedException.none();

    private RegisterActivity registerActivity = null;

    @Before
    public void setUp() {
        registerActivity = registerActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View view = registerActivity.findViewById(R.id.appTitle2);
        assertNotNull(view);
    }

    // If user enters correct credentials, then assert that a user is created
    @Test
    public void testIfUserCreated() {
        registerActivity.register("username", "email@gmail.com", "password", "en");
        assertNotNull(auth.getCurrentUser());
    }

    // If user enters incorrect credentials, then expect an IllegalArgumentException and
    // for there to be a null user
    @Test
    public void testIfUserNotCreated() {
        registerActivity.register("username", "", "password", "en");
        thrown.expect(IllegalArgumentException.class);
        assertNull(auth.getCurrentUser());
    }

    @After
    public void tearDown() {
        registerActivity = null;
    }
}