package com.example.firebasedemo1setup;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.firebasedemo1setup.Data.NoteFormat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;

import static com.example.firebasedemo1setup.Data.NotesData.notesList;

public class AddNoteActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText etTitle, etDescription;
    private Button btnSaveNote, buttonSelectFile;
    private DatePicker datePicker;

    private int FileRequestCode=101;

    private ProgressDialog progressDialog;

    private SharedPreferences sharedPreferences;

    private StorageReference storageReference;
    private DatabaseReference databaseReferenceNotes;
    private Uri filePath = null;
    private static String URL = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);
        sharedPreferences= getSharedPreferences("FireNotesData", Context.MODE_PRIVATE);
        String uid = sharedPreferences.getString("UID","");

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReferenceNotes = FirebaseDatabase.getInstance().getReference("UserNotes").child(uid);

        ids();
    }

    private void ids()
    {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        buttonSelectFile = findViewById(R.id.buttonSelectFile);
        datePicker = findViewById(R.id.datePicker);

        btnSaveNote.setOnClickListener(this);
        buttonSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,
                        "Choose Image to Upload.."),FileRequestCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==FileRequestCode && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            filePath = data.getData();
            buttonSelectFile.setText(filePath.getPath());
        }
    }

    @Override
    public void onClick(View view)
    {
        String date = datePicker.getDayOfMonth()+"/"+ (datePicker.getMonth() + 1)+"/"+datePicker.getYear();
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();

        if(TextUtils.isEmpty(title))
            etTitle.setError("Required");
        else if (TextUtils.isEmpty(description))
            etDescription.setError("Required");
        else if(filePath==null)
            Toast.makeText(this, "Choose an image to Upload", Toast.LENGTH_SHORT).show();
        else
        {
            progressDialog.show();
            uploadFile();
            progressDialog.setMessage("Saving Memory....");
            String key = databaseReferenceNotes.push().getKey();
            NoteFormat note = new NoteFormat(title,description,key,date,URL);
            assert key != null;
            databaseReferenceNotes.child(key).setValue(note)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                notesList.clear();
                                startActivity(new Intent(AddNoteActivity.this,ShowData.class));
                                finish();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void uploadFile()
    {
        if(filePath!=null && filePath.getPath()!=null)
        {
            URL = (new File(filePath.getPath())).getName()+"."+ getFileExtension();
            StorageReference sRef = storageReference.child(sharedPreferences.getString("UID","")).child(URL);
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        double progress
                                = (100.0
                                * taskSnapshot.getBytesTransferred()
                                / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage(
                                "Uploaded "
                                        + (int)progress + "%");
                    }
                });
        }
    }

    private String getFileExtension() {
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(filePath));
    }

    @Override
    public void onBackPressed() {
        notesList.clear();
        startActivity(new Intent(AddNoteActivity.this, ShowData.class));
        finish();
    }
}
