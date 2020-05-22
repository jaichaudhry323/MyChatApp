package org.o7planning.mychatapp.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.o7planning.mychatapp.Model.User;
import org.o7planning.mychatapp.R;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    ImageView mProfileImage;
    TextView mUsername;
    TextView mEmail;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    StorageTask<UploadTask.TaskSnapshot> uploadTask;    // tasksnapshot encapsulates the state of the UploadTask of StorageTask

    Uri imageUri;
    final int REQUEST_CODE = 1;

    String msg = " \n n \n n \n";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragment = inflater.inflate(R.layout.fragment_profile, container, false);

        mUsername = fragment.findViewById(R.id.username);
        mProfileImage = fragment.findViewById(R.id.profile_image);
        mEmail = fragment.findViewById(R.id.email);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String userId = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // GET USER INFO IN USER CLASS'S OBJECT
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    if (user.getUsername() != null)
                        mUsername.setText(user.getUsername());

                    if (firebaseUser.getEmail() != null)
                        mEmail.setText(firebaseUser.getEmail());

                    if (user.getImageURL() == null | user.getImageURL().equals("default")) {
                        mProfileImage.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        // set the image that's on cloud
                        // Glide is jst like Picasso but its better bcoz of its caching and OutOfMemoryError handling technique
                        Glide.with(getContext()).load(user.getImageURL()).into(mProfileImage);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProfileImage.setOnClickListener(v -> {
            openImage();
        });

        return fragment;
    }


    // NOW WRITING THE CODE TO UPLOAD IMAGE FROM PHONE

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT); // PENDING
        startActivityForResult(intent, REQUEST_CODE);    // activity is started such that there is / will be declared within
        // this activity a callback listener after this activity is executed
        // request code is used to signify whether everything went alright or
        // not plus it is used to identify a single activity amongst different on ActivityResult functions
        Log.i("Profile Fragment", msg + "Starting Image Search " + msg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
                                                                                                                                                        Log.i("Profile Fragment", msg + "ImageUri : " + imageUri + msg);
                                                                                                                                                        Log.i("Profile Fragment", msg + "RESULT_OK : " + RESULT_OK + msg);
            // CHECK IF WHILE LOOP HERE GIVES ANY ERROR
            if (uploadTask != null && uploadTask.isInProgress()) {
                                                                                                            //                Log.i("Profile Fragment", msg + "Image Load in Progress : " + msg);
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                                                                                                    //                Log.i("Profile Fragment", msg + "Image extension : " + getFileExtension(ImageUri) + msg);
                uploadImage();
            }
                                                                                                                //            Log.i("Profile Fragment", msg + "Image extension : " + getFileExtension(ImageUri) + msg);
        }
    }

    public String getFileExtension(Uri url) {

        assert getContext() != null;
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
//       String type = null;
//       String extension = MimeTypeMap.getFileExtensionFromUrl(url);
//       if (extension != null) {
//           type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//       }
//       return type;
    }

    private void uploadImage() {

        ProgressDialog pd = new ProgressDialog(getContext());         // NOTE THIS
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri !=null)
        {

            StorageReference fileReference=storageReference.child("image"+firebaseUser.getUid()+"."+getFileExtension(imageUri));

            uploadTask =fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri cloudUrl=task.getResult();
                        String imageurl= cloudUrl.toString();

                        DatabaseReference UserDB=FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

                        // Now we update the image url
                        HashMap<String,Object>hashMap= new HashMap<>();
                        hashMap.put("imageurl",imageurl);
                        UserDB.updateChildren(hashMap);

                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });

        }
        else{
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();

        }

    }

}