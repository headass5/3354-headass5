# Software Requirements for This Messaging Service 

## Definitions, Acronyms, and Abbreviations
SMS: Short Message Service \
MMS: Multimedia Messaging Service


## Functional Requirements
1. The System will allow users to send messages in a 1:1 conversation
   * 1.1. The user will be able to translate received messages into their desired language.
   * 1.2. The users meesages will be stored in the database locally
   * 1.3. Messages will be stored in the database so they will be persisted when logged out
   
2. The user should be able to login/logout account
   * 2.1. Existing users will be able to log into their account.
   * 2.2. The user will be able to log out of the account when they wish to.
   
3. The System will allow users to view all their conversations
   * 3.1. The user will be able to view a list of all their conversations and click on certain ones.
   * 3.2. The user will be able to add contacts and have conversations with those contacts
   * 3.3. The user will be able to create a new conversation.
   
4. A translation service in order to power on demand translations
   * 4.1. The translation selection will only affect the messages sent after the selection has been made.
   * 4.2. Translation is made on demand.  
   
5. The System will allow users to create account
	* 5.1. The system will authorize users credentials
	* 5.2. The system will be able to manage the user’s information
	* 5.3. The system will be able to delete the user's information
    * 5.4. The system will ask the user what language they want their messages to be translated to
    * 5.5. The user will be able to load a profile picture
    * 5.6. The user will be able to create a username


## Non-functional Requirements
1. Performance
   * 1.1. The app will be responsive and attempt to avoid slow loads
   
2. Aesthetics
   * 2.1. The app's UI will be clean and clear, user friendly
   * 2.2. A good color scheme will be presented to make the app appealing to potential users.

## Constraints
1. Process
   * 1.1. The `master` branch will be protected and changes will require at least 1 approving review to be able to merge

## Use Case Diagram
![The Use Case Diagram](/images/use-case.png)
