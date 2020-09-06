package com.example.firebasedemo1setup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.example.firebasedemo1setup.Adapter.NotesAdapterHorizontal;
import me.relex.circleindicator.CircleIndicator;
import static com.example.firebasedemo1setup.Data.NotesData.notesList;

public class NoteDisplay extends AppCompatActivity {

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private NotesAdapterHorizontal notesAdapterHorizontal;
    private ProgressDialog progressDialog;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_display);

        sharedPreferences = getSharedPreferences("FireNotesData", Context.MODE_PRIVATE);

        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Loading Memory...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        notesAdapterHorizontal = new NotesAdapterHorizontal(this, notesList,sharedPreferences.getString("UID",""),progressDialog);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(notesAdapterHorizontal);
        circleIndicator = findViewById(R.id.circleIndicator);
        circleIndicator.setViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        notesList.clear();
        startActivity(new Intent(NoteDisplay.this, ShowData.class));
        finish();
    }
}
