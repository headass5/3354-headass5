package com.aahlad.thismessagingservice;

import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class FirebaseQueryTest {

    @Rule
    public ActivityTestRule<FirebaseQuery> firebaseQueryActivityTestRule = new ActivityTestRule<FirebaseQuery>(FirebaseQuery.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private FirebaseQuery firebaseQuery = null;

    // Before test, get activity for FirebaseQuery
    @Before
    public void setUp() {
        firebaseQuery = firebaseQueryActivityTestRule.getActivity();
    }

    /*
    Test to see if the message translated successfully. In this test method, the translateMessage()
    method is called with the parameters for the text and the language for translation. In this
    test method, we are translating hello in French (fr), which should return Bonjour.
    Expected result: test method just assert true when the message is translated successfully.
     */
    @Test
    public void testIfMessageTranslated() {
        String result = firebaseQuery.translateMessage("hello", "fr").toString();
        assertEquals("Bonjour", result);
    }
}