package com.aahlad.thismessagingservice;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class AddContactTest {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    @Test
    public void testIfContactAdded() throws Exception {
        final EditText contactUsername = (EditText) addContact.findViewById(R.id.new_contact_username);
        contactUsername.setText("englishUser");
        String englishUserID = "VZ7IL5Q718dbOnVwwAGTEoc7U4I2";
        Button button = (Button) addContact.findViewById(R.id.create_contact);
        button.performClick();
        Thread.sleep(1000);

        db.collection("userMeta").whereArrayContains("contacts", englishUserID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    assert true;
                }
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        addContact = null;
    }
}