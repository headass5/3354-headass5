package com.aahlad.thismessagingservice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {
  EditText username, email, password, confirmPassword;
  Button btn_register;
  Button btn_choose_profile;
  ImageView profile_img_view;
  Spinner languageDropdown;
  FirebaseAuth auth;
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  private boolean pictureSelected = false;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    
    username = findViewById(R.id.username);
    email = findViewById(R.id.register_email_field);
    password = findViewById(R.id.register_password_field);
    confirmPassword = findViewById(R.id.register_confirm_password_field);
    btn_register = findViewById(R.id.create_account_button);
    btn_choose_profile = findViewById(R.id.choose_profile_picture_button);
    languageDropdown = findViewById(R.id.language_dropdown);
    profile_img_view = findViewById(R.id.profile_img_view);
    profile_img_view.setDrawingCacheEnabled(true);
    
    auth = FirebaseAuth.getInstance();
    
    final Activity self = this;
    
    btn_choose_profile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ImagePicker.Companion.with(self)
          .crop()	    			//Crop image(Optional), Check Customization for more option
          .compress(1024)			//Final image size will be less than 1 MB(Optional)
          .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
          .start();
      }
    });
    
    btn_register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
      String txt_username = username.getText().toString();
      String txt_email = email.getText().toString();
      String txt_password = password.getText().toString();
      String confirmTxtPassword = confirmPassword.getText().toString();
      String selectedLanguage = languageDropdown.getSelectedItem().toString();
      String selectedLanguageCode;
      
      switch (selectedLanguage) {
        case "español":
          selectedLanguageCode = "es";
          break;
        case "Deutsche":
          selectedLanguageCode = "de";
          break;
        case "Français":
          selectedLanguageCode = "fr";
          break;
        default:
          selectedLanguageCode = "en";
          break;
      }
      
      if (!pictureSelected || TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(confirmTxtPassword)) {
        Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
      } else if (!txt_password.equals(confirmTxtPassword)) {
        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
      } else {
        register(txt_username, txt_email, txt_password, selectedLanguageCode);
      }
      }
    });
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      File file = ImagePicker.Companion.getFile(data);
      Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
  
      System.out.println("Setting imageView");
      profile_img_view.setImageBitmap(myBitmap);
      pictureSelected = true;
    } else if (resultCode == ImagePicker.RESULT_ERROR) {
      Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
    }
  }
  
  public void register(final String username, final String email, final String password, final String languageCode) {
    Thread addUserThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          AuthResult result = Tasks.await(auth.createUserWithEmailAndPassword(email, password));
          FirebaseUser firebaseUser = result.getUser();
          assert firebaseUser != null;
          String userid = firebaseUser.getUid();
          
          profile_img_view.buildDrawingCache();
          Bitmap bitmap = ((BitmapDrawable) profile_img_view.getDrawable()).getBitmap();
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
          byte[] data = baos.toByteArray();
  
          FirebaseStorage storage = FirebaseStorage.getInstance();
          
          StorageReference ref = storage.getReference(userid + "/profile.jpg");
          Tasks.await(ref.putBytes(data));
          
          Uri uri = Tasks.await(ref.getDownloadUrl());
          
          HashMap<String, Object> newUserMeta = new HashMap<>();
          newUserMeta.put("id", userid);
          newUserMeta.put("username", username);
          newUserMeta.put("imageURL", "default");
          newUserMeta.put("language", languageCode);
          
          if (uri != null) {
            newUserMeta.put("imageURL", uri.toString());
          }
          
          Tasks.await(db.collection(Constants.USER_META_PATH).document(userid).set(newUserMeta));
          
          UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
              .setDisplayName(username).setPhotoUri(uri).build();
          Tasks.await(firebaseUser.updateProfile(profileUpdates));
          Intent sendToLogin = new Intent(RegisterActivity.this, MainActivity.class);
          startActivity(sendToLogin);
          finish();
  
          
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
      }
    });
    addUserThread.start();
  }
}
