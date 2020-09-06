package com.example.firebasedemo1setup.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.firebasedemo1setup.Data.NoteFormat;
import com.example.firebasedemo1setup.Data.NoteInterface;
import com.example.firebasedemo1setup.Data.NotesData;
import com.example.firebasedemo1setup.NoteDisplay;
import com.example.firebasedemo1setup.R;
import com.example.firebasedemo1setup.ShowData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.firebasedemo1setup.Data.NotesData.notesList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder> {

    private String noteId="", noteDate="";
    private Context context;
    private ArrayList<NoteFormat> list;
    private NoteInterface updateNoteInterface;
    private String uid;

    private ProgressDialog progressDialog;

    private StorageReference storageReference;

    public NotesAdapter(Context context, ArrayList<NoteFormat> list, String uid, ProgressDialog progressDialog)
    {
        this.uid = uid;
        this.context = context;
        this.list = list;
        updateNoteInterface = (NoteInterface) context;
        storageReference = FirebaseStorage.getInstance().getReference();
        this.progressDialog = progressDialog;
    }
    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_note,parent,false);
        return new NotesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotesHolder holder, final int position) {
        final NoteFormat note = list.get(position);
        String title = note.getNoteTitle();
        String description = note.getNoteDescription();
        String date = note.getNoteDate();
        String location = uid+"/"+note.getImageUrl();

        storageReference.child(location).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.ic_error)
                        .error(R.drawable.ic_error);
                Glide.with(context).load(uri.toString()).apply(options).into(holder.ivNote);
            }
        });

        holder.tvTitle.setText(title);
        holder.tvDescription.setText(description);
        holder.tvDate.setText(date);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteFormat noteFormat= notesList.get(position);
                notesList.remove(noteFormat);
                notesList.add(0,noteFormat);
                Intent intent = new Intent(context,NoteDisplay.class);
                intent.putExtra("position",position);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteFormat notesDetail = list.get(position);
                updateDialog(notesDetail);
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_delete);
                dialog.show();

                TextView tvDelete = dialog.findViewById(R.id.tvYes);
                TextView tvCancel = dialog.findViewById(R.id.tvNo);

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        updateNoteInterface.deleteNote(note);
                        holder.cardView.setVisibility(View.GONE);

                    }
                });
            }
        });
        progressDialog.dismiss();
        Toast.makeText(context, "Tap note to Open", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    static class NotesHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvDescription, tvDate;
        private ImageView ivEdit, ivDelete, ivNote;
        private CardView cardView;

        NotesHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvRowTitle);
            tvDescription = itemView.findViewById(R.id.tvRowDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivNote = itemView.findViewById(R.id.ivNote);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

     private void updateDialog(final NoteFormat note)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_note);
        dialog.show();

        final EditText etTitle = dialog.findViewById(R.id.etTitleUpdate);
        final EditText etDescription = dialog.findViewById(R.id.etDescriptionUpdate);
        final DatePicker datePicker = dialog.findViewById(R.id.datePicker);

        Button buttonUpdate = dialog.findViewById(R.id.buttonUpdateNote);

        String[] dateArray = note.getNoteDate().split("/");
        datePicker.updateDate(Integer.parseInt(dateArray[2])
                ,Integer.parseInt(dateArray[1])-1,
                Integer.parseInt(dateArray[0]));

        etTitle.setText(note.getNoteTitle());
        etDescription.setText(note.getNoteDescription());
        noteId = note.getNoteID();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                String title = etTitle.getText().toString();
                String description = etDescription.getText().toString();
                noteDate = datePicker.getDayOfMonth()+"/"+ (datePicker.getMonth() + 1)+"/"+datePicker.getYear();

                NoteFormat noteUpdated = new NoteFormat(title,description,noteId,noteDate,note.getImageUrl());
                updateNoteInterface.updateNote(noteUpdated);
            }
        });
    }

}
