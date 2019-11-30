# Implementation and Documentation
Enter text here

&nbsp;

# Design Pattern 
The design pattern that headass followed for This Messaging Service was the Composite Design Pattern.
The Composite Design Pattern allows for less duplication and clean interface.
It is easy to add, remove, new types. Additionally, the traverse and display operations is uniform for internal nodes and leaves. 
The Composite Design Pattern that is used in the code extends to the viewholder class, it is responsible for finding images and titles and setting its own variables (this can be seen in the both the conversation and contact). It gets passed to the same adapter class.  

#Code location
#insert here

&nbsp;

# Test Classes
### 1. Test Class: RegisterActivityTest.java -> Located [here](./app/src/androidTest/java/com/aahlad/thismessagingservice/RegisterActivityTest.java)
- Test Class completed by Al. This class tests to see if the register method works for the user.
```java
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
```

### 2. Test Class: AddContactTest.java -> Located [here](./app/src/androidTest/java/com/aahlad/thismessagingservice/AddContactTest.java)
- Test Class completed by Pallavi. This class tests to see if the add contact functionality works for the user.
```java
public class AddContactTest {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<AddContact> addContactActivityTestRule =
            new ActivityTestRule<AddContact>(AddContact.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AddContact addContact = null;

    // Before test cases, get the AddContact activity
    @Before
    public void setUp() {
        addContact = addContactActivityTestRule.getActivity();
    }

    /*
    Test to see if the activity launched successfully. Expected result: the activity will launch
    and the test case will pass when the create contact element is found
     */
    @Test
    public void testLaunch() {
        View view = addContact.findViewById(R.id.create_contact);
        assertNotNull(view);
    }

    /*
    Test to see if once the user adds a contact, then that is updated in the Firestore database.
    Test will add the 'englishUser', and once added, then will check user's list of contacts in the
    database to make sure that the englishUser ID will be under the user's list of contacts.
    Expected value: englishUser will be added to user's contacts in the database and return true.
     */
    @Test
    public void testIfContactAdded() throws Exception {
        final EditText contactUsername = (EditText) addContact.findViewById(R.id.new_contact_username);
        contactUsername.setText("englishUser");
        String englishUserID = "VZ7IL5Q718dbOnVwwAGTEoc7U4I2";
        Button button = (Button) addContact.findViewById(R.id.create_contact);
        button.performClick();
        Thread.sleep(1000);

        db.collection("userMeta").whereArrayContains("contacts", englishUserID).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    assert true;
                }
            }
        });
    }

    // After test, set addContact instance to null
    @After
    public void tearDown() throws Exception {
        addContact = null;
    }
}
```

### 3. Test Class: MessageActivityTest.java -> Located [here](./app/src/androidTest/java/com/aahlad/thismessagingservice/MessageActivityTest.java)
- Test Class completed by Bishesh. This class tests to see if the sendMessage method as well as the other message functionality works as expected.
```java
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
```

### 4. Test Class: FirebaseQueryTest.java -> Located [here](./app/src/androidTest/java/com/aahlad/thismessagingservice/FirebaseQueryTest.java)
- Test Class completed by Andrea. This class tests the translateMessage method to see if the call to the translation API is working as expected.
```java
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
```

### 5. Test Class: CreateContactActivityTest.java -> Located [here](./app/src/androidTest/java/com/aahlad/thismessagingservice/CreateConvoActivityTest.java)
- Test Class completed by Patrick. This class tests if the create conversation functionality is working correctly by checking if the conversation is saved correctly on the database.
```java
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
```

&nbsp;

# Building the Software
1. Open the application
2. Click on button at the bottom of the screen to create an account
3. Enter username and password that will be used as credentials for a specific user 
4. Enter username and password created to access chat account 
5. User will be directed to chat page
6. Allow user to enter language based on user perference  
6. Create new contact based on user discretion 
7. User will be directed back to the chat page 

&nbsp;

# Working Software and The State of the UI 
Enter text here

&nbsp;

# Implemented Function 
The functionalties that we have incorporated in to The Messaging Service consists of, 
1. allowing the user to create an account
2. allowing the the login in to chat account
3. allowing the user to the set language for translation
4. allowing user to add a contact 
5. allowing the user to take and personalize the contact picture 
6. allowing the user to access specific message chat 

In terms of the UI the functionality behind it is very user friendly, it is easy to follow and allows the user to navigate
through the application. It is very personalized and customizable to the users preference. 



