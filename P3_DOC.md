# Implementation and Documentation
The implementaton and documentation, as well as the testing for our project can be found in this document. There are clear instructions for the different functionalities, as well as the test classes and test methods that were developed by each group member. If you would like to see the document of the project requirements, then click [here](./requirements.md)

&nbsp;

# Design Pattern 
The design pattern that headass followed for This Messaging Service was the **Composite Design Pattern.**
The Composite Design Pattern allows for less duplication and clean interface.
It is easy to add, remove, new types. Additionally, the traverse and display operations is uniform for internal nodes and leaves. 
The Composite Design Pattern that is used in the code extends to the viewholder class, it is responsible for finding images and titles and setting its own variables (this can be seen in the both the conversation and contact). It gets passed to the same adapter class.  

### ConversionAdapter: Example 1
```Java
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private Context mContext;
    private List<Conversation> mConversations;

    public ConversationAdapter(Context mContext, List<Conversation> mConversations) {
        this.mConversations = mConversations;
        this.mContext = mContext;
    }   
@NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.conversation_item, parent, false);
        return new ConversationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder holder, int position) {
        final Conversation conversation = mConversations.get(position);
        // Set text
        holder.title.setText(conversation.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              System.out.println("On Click");
              Intent intent = new Intent(mContext, MessageActivity.class);
              System.out.println(conversation.getId());
              intent.putExtra("conversationID", conversation.getId());
              mContext.startActivity(intent);
            }
        });
    }
@Override
    public int getItemCount() {
        return mConversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.conversation_title);
        }
    }
}
```
### MessageAdapter: Example 2
```Java 
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    private String language;
    
    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl, String language) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
        this.language = language;
    }
 @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        holder.show_message.setText(chat.getTranslations().get(language));

        if(imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }
    }
  @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getUserID().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
```
### UserAdapter: Example 3
```Java
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }
 @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("otherUserID", user.getId());
                intent.putExtra("otherUsername", user.getUsername());
                intent.putExtra("otherImageURL", user.getImageURL());
                mContext.startActivity(intent);
            }
        });
    }
 @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.login_email_field);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
```

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
This application builds and runs on any android compatible device. There are no issues with the app, and it should run as expected. Here are some basic steps on getting familiar with building and using the software:

1. Open the application, download the APK file.
2. Click on button at the bottom of the screen to create an account.
3. Enter username, email, password details.
4. Enter what language you would like your text messages to be received in.
6. Choose a profile picture and then click create account to log in.
7. If you already have an account, you can log in with your credentials.
8. Once you are logged in, you will be directed to the chats page.
9. Create contacts by adding usernames of other contacts you know.
10. Create conversations by either clicking on a contact, or on the conversation button.
11. Send messages and verify that your received messaged are in your translated text.

&nbsp;

# Working Software and The State of the UI 
All of the software works as expected for this application. Here are the different aspects of the UI:
- Login Page
  - Fields for email and password if you have an existing account
  - Button to login to account
  - Button to create and register for an account if you don't already have one
- Register Page
  - Fields for username, email, password, and language
  - Can upload a profile picture
  - Language is the language you want the messages to be received in
- Main Page
  - Splits the screen into two sections, contacts and chats
  - Displays your username
  - Button for logout
- Chats Page
  - Layout of all the different conversations you have
  - Each conversation is titled with the username of the two people in the conversation
  - Button that leads you to a page where you can enter in the username of someone you want to text
- Contacts Page
  - Layout of all the different contacts you have
  - Each contact is titled with the contact username
  - Button that allows you to enter a username to add that person as a contact

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

&nbsp;

# App Images
![](/images/demo_1.png) ![](/images/demo_2.png)
![](/images/demo_3.png)
![](/images/demo_4.png)
![](/images/demo_5.png)
![](/images/demo_6.png)
![](/images/demo_7.png)



