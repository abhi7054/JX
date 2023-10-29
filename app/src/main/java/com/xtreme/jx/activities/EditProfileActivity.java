package com.xtreme.jx.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xtreme.jx.R;
import com.xtreme.jx.model.User;
import com.xtreme.jx.utils.AppPref;
import com.xtreme.jx.utils.Constant;
import com.xtreme.jx.utils.Util;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends BaseActivity {

    @BindView(R.id.iv_user_image)
    ImageView userImageView;

    @BindView(R.id.tv_user_name)
    EditText userName;

    User user;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    public static final int PICK_IMAGE = 1;
    public static final int CROP_IMAGE = 2;
    static int PERMISSION = 101;

    Uri outputPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            outputPath = Uri.fromFile(new File(Util.getCacheStoragePath(this) + "/" + Util.getCurrentTimeStamp() + ".png"));
            Crop.of(Uri.fromFile(new File(picturePath)), outputPath).asSquare().start(this);
        }

        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            Glide.with(this).load(outputPath.getPath()).placeholder(R.drawable.user_placeholder).into(userImageView);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION) {
            if (checkImagePermission()) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, getString(R.string.profile_update)), PICK_IMAGE);
            }
        }
    }

    void init() {
        user = AppPref.getUser(this);
        if (user == null) {
            return;
        }
        userName.setText(user.getUsername());
        Glide.with(this).load(user.getImage()).placeholder(R.drawable.user_placeholder).into(userImageView);

        // get the Firebase  storage reference
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    boolean checkImagePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
        return false;
    }

    void uploadImage() {
        setProgressBar(true);
        StorageReference fileReference = storageReference.child("userProfile").child(System.currentTimeMillis() + ".png");
        fileReference.putFile(outputPath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String downloadUri = task.getResult().toString();
                    updateImageUri(downloadUri);
                } else {
                    Log.e("uploadImage", "false");
                }
            }
        });
    }

    void updateImageUri(String imageUri) {
        DocumentReference washingtonRef = mDatabase.collection(Constant.USER_COLLECTION).document(user.getDocId());
        washingtonRef.update("image", imageUri).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot successfully updated!");
                user.setImage(imageUri);
                AppPref.setUserData(EditProfileActivity.this, user);
                setProgressBar(false);
                onClickBack();
                Toast.makeText(EditProfileActivity.this, getString(R.string.profile_update), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error updating document", e);
                setProgressBar(false);
            }
        });
    }

    void updateUserName() {
        setProgressBar(true);
        DocumentReference washingtonRef = mDatabase.collection(Constant.USER_COLLECTION).document(user.getDocId());
        washingtonRef.update("username", userName.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot successfully updated!");
                user.setUsername(userName.getText().toString().trim());
                AppPref.setUserData(EditProfileActivity.this, user);
                if (outputPath != null && !outputPath.getPath().isEmpty()) {
                    uploadImage();
                } else {
                    setProgressBar(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error updating document", e);
                setProgressBar(false);
            }
        });
    }

    @OnClick(R.id.iv_edit)
    void onClickEditImage() {
        if (checkImagePermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE);
        }
    }

    @OnClick(R.id.iv_back)
    void onClickBack() {
        Util.hideKeyboard(this);
        finish();
    }

    @OnClick(R.id.rl_save)
    void onClickSave() {
        Util.hideKeyboard(this);
        if (userName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "User name should be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        updateUserName();
    }
}
