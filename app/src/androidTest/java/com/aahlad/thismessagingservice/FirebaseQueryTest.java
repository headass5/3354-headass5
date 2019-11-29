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

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<FirebaseQuery> firebaseQueryActivityTestRule = new ActivityTestRule<FirebaseQuery>(FirebaseQuery.class);

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    private FirebaseQuery firebaseQuery = null;

    @Before
    public void setUp() {
        firebaseQuery = firebaseQueryActivityTestRule.getActivity();
    }

    @Test
    public void testIfMessageTranslated() {
        
    }
}