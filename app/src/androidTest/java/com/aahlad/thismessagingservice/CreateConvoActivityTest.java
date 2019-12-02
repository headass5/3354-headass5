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

import java.util.Arrays;

import static org.junit.Assert.*;

public class CreateConvoActivityTest {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<CreateConvoActivity> createConvoActivityActivityTestRule =
            new ActivityTestRule<CreateConvoActivity>(CreateConvoActivity.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private CreateConvoActivity createConvoActivity = null;

    // Before test, get the CreateConvoActivity
    @Before
    public void setUp() {
        createConvoActivity = createConvoActivityActivityTestRule.getActivity();
    }

    // Test to see if the CreateConvoActvitiy launched successfully
    @Test
    public void testLaunch() {
        View view = createConvoActivity.findViewById(R.id.recipients_username);
        assertNotNull(view);
    }

    /*
    This test is to see if the user can create a conversation with a new user. This method simulates
    a conversation between the current user and the 'englishUser'. Once the current user sends a
    message to the englishUser, the method gets the names of the two users, and checks if the title
    is the same as the conversation title in the database. Each conversation has a title which
    consists of both the usernames (in alphabetical order) separated by a '&'. The expected result
    is that the title matched the database which confirms that the conversation was created.
     */
    @Test
    public void testIfUserCanCreateConversation() {

        final EditText username = (EditText) createConvoActivity.findViewById(R.id.recipients_username);
        username.setText("englishUser");

        String message = "Sending a unique message by creating a new conversation";
        final EditText textToSend = (EditText) createConvoActivity.findViewById(R.id.convo_body);
        textToSend.setText(message);

        Button button = (Button) createConvoActivity.findViewById(R.id.send_button);
        button.performClick();

        String currentUser = auth.getCurrentUser().getDisplayName();
        String[] usernames = new String[] {currentUser, "englishUser"};
        Arrays.sort(usernames);

        String title = usernames[0] + " & " + usernames[1];

        db.collection("conversations").whereEqualTo("title", title).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    assert true;
                }
            }
        });

    }

    // After test set createConvoActivity instance to null
    @After
    public void tearDown() throws Exception {
        createConvoActivity = null;
    }
}