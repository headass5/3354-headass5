package com.aahlad.thismessagingservice;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class AddContactTest {

    @Rule
    public ActivityTestRule<AddContact> addContactActivityTestRule = new ActivityTestRule<AddContact>(AddContact.class);

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    private AddContact addContact = null;

    @Before
    public void setUp() {
        addContact = addContactActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View view = addContact.findViewById(R.id.create_contact);
        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        addContact = null;
    }
}