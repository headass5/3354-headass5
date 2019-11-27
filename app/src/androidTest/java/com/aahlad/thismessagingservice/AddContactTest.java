package com.aahlad.thismessagingservice;

import androidx.test.rule.AddContactRule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase. firestore.FirebaseFirestore;
import android.view.View;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class AddContactTest {

  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private FirebaseAuth auth = FirebaseAuth.getInstance();

  @Rule
  public AddContactRule<AddContact> addContactRule = new AddContactRule<AddContact>(AddContact.class);

  private AddContact addContact = null;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private AddContact addContact = null;

  /*
 @Before
  assertNotNull(view);
   */



  @Before
  public void setUp() throws Exception {
    addContact = addContactRule.getActivity();
  }

  @Test

  public void ifContactNotCreated() {
    addContact.add("username");
    thrown.expect(IllegalArgumentException.class);
    assertNull(auth.getCurrentUser());
  }

  /*
  @Test
  public void testLaunch(){
    View view = addContact.findViewById(R.id.new_contact_username);
    assertNotNull(view);

  }
  */

  @After
  public void tearDown() throws Exception {
    addContact = null;
  }

  private void add(final String username) {
    Thread addUserThread = new Thread(new Runnable() {
      @Override

      public void run() {

      }
      });
      addUserThread.start();

      }
    }

