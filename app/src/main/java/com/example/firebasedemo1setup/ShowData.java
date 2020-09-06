package com.example.firebasedemo1setup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.firebasedemo1setup.Adapter.NotesAdapter;
import com.example.firebasedemo1setup.Data.NoteFormat;
import com.example.firebasedemo1setup.Data.NoteInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import static com.example.firebasedemo1setup.Data.NotesData.notesList;

public class ShowData extends AppCompatActivity implements View.OnClickListener, NoteInterface {

    private TextView tvNoData;
    private FloatingActionButton fabAddNote;
    private ImageView ivLogOut;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerAllNotes;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        if(!isNetworkAvailable())
        {
            Toast.makeText(this, "Please Turn on Net and Restart the app Again", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Extracting Memories.....");
        progressDialog.setCancelable(false);

        sharedPreferences= getSharedPreferences("FireNotesData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String uid = sharedPreferences.getString("UID","");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference("UserNotes").child(uid);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerAllNotes = findViewById(R.id.recyclerAllNotes);
        recyclerAllNotes.setLayoutManager(linearLayoutManager);

        notesList.clear();
        readAllNotes();

        ids();
    }

    private void ids()
    {
        tvNoData = findViewById(R.id.tvNoData);
        fabAddNote = findViewById(R.id.fabAddNote);
        ivLogOut = findViewById(R.id.ivLogOut);
        ivLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                editor.putString("UID","");
                editor.putBoolean("LoginStatus",false);
                editor.commit();
                startActivity(new Intent(ShowData.this, LoginActivity.class));
                finish();
            }
        });
        fabAddNote.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        startActivity(new Intent(ShowData.this,AddNoteActivity.class));
        finish();
    }

    public void readAllNotes()
    {
        progressDialog.show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot i:dataSnapshot.getChildren())
                {
                    NoteFormat note = i.getValue(NoteFormat.class);
                    notesList.add(note);
                }
                if(notesList.size()!=0)
                    recyclerAllNotes.setAdapter(new NotesAdapter(ShowData.this, notesList, sharedPreferences.getString("UID",""),progressDialog));
                else
                {
                    progressDialog.dismiss();
                    tvNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void updateNote(NoteFormat note) {
        databaseReference.child(note.getNoteID()).setValue(note)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            notesList.clear();
                           readAllNotes();
                        }
                    }
                });
    }

    @Override
    public void deleteNote(final NoteFormat note) {
        databaseReference.child(note.getNoteID()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            String location = sharedPreferences.getString("UID","")+"/"+note.getImageUrl();
                            storageReference.child(location).delete();
                            notesList.remove(note);
                            readAllNotes();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
