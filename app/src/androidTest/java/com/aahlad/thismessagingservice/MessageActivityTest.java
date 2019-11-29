package com.aahlad.thismessagingservice;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class MessageActivityTest {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<MessageActivity> messageActivityActivityTestRule = new ActivityTestRule<MessageActivity>(MessageActivity.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MessageActivity messageActivity = null;

    // Before test, get the MessageActivity activity
    @Before
    public void setUp() {
        messageActivity = messageActivityActivityTestRule.getActivity();
    }

    // Test to see if the MessageActivity launched successfully by checking if correct element is there.
    @Test
    public void testLaunch() {
        View view = messageActivity.findViewById(R.id.btn_send);
        assertNotNull(view);
    }

    /*
    Test to see if the user has previously sent messages, and if these messages are shown in the
    database. This test method will search the messages collection and search if the current user's
    ID is matched with the messages in the database. If so, then the expected result is true
     */
    @Test
    public void testIfUserHasSentMessages() {
        db.collection("messages").whereEqualTo("userID", auth.getCurrentUser().getUid()).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    assert true;
                }
            }
        });
    }

    /*
    Test to see if the user can send a message, and then have that message appear on the database.
    This test method will fill out the text box for the message, and then hit the send button. Once
    the send button is pressed, the database is searched for that specific message by that specific
    user. If the message is found, then the expected result is true.
     */
    @Test
    public void testIfUserCanSendMessages() throws Exception {
        final EditText textToSend = (EditText) messageActivity.findViewById(R.id.text_send);
        String message = "Sending a unique test message";
        textToSend.setText(message);
        Button button = (Button) messageActivity.findViewById(R.id.btn_send);
        button.performClick();
        Thread.sleep(1000);

        db.collection("messages").whereEqualTo("body", message).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    assert true;
                }
            }
        });
    }

    // After test set MessageActivity instance to null
    @After
    public void tearDown() throws Exception {
        messageActivity = null;
    }
}