package com.example.firebasedemo1setup.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.firebasedemo1setup.Data.NoteFormat;
import com.example.firebasedemo1setup.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class NotesAdapterHorizontal extends PagerAdapter {

    private Activity context;
    private ArrayList<NoteFormat> list;
    private String uid;
    private ProgressDialog progressDialog;

    private StorageReference storageReference;

    public NotesAdapterHorizontal(Activity context, ArrayList<NoteFormat> list, String uid, ProgressDialog progressDialog)
    {
        this.context = context;
        this.list = list;
        this.uid = uid;
        this.progressDialog = progressDialog;

        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        NoteFormat note = list.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.horizontal_note_view, container, false);
        TextView tvTitle, tvDescription, tvDate;
        CardView cardView;
        final ImageView ivNote;
        tvTitle = layout.findViewById(R.id.tvTitle);
        tvDescription = layout.findViewById(R.id.tvDescription);
        tvDate = layout.findViewById(R.id.tvDate);
        ivNote = layout.findViewById(R.id.ivNote);
        cardView = layout.findViewById(R.id.cardView);

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
                Glide.with(context).load(uri.toString()).apply(options).into(ivNote);
            }
        });
        tvTitle.setText(title);
        tvDescription.setText(description);
        tvDate.setText(date);
        progressDialog.dismiss();
        Toast.makeText(context, "Long Tap to share as Image!!!", Toast.LENGTH_SHORT).show();

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View screenView = view.getRootView();
                screenView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
                screenView.setDrawingCacheEnabled(false);
                final  String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
                File dir = new File(dirPath);
                if(!dir.exists())
                    dir.mkdirs();
                File file = new File(dirPath, "Memories.jpeg");
                try {
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.getPath()));
                context.startActivity(Intent.createChooser(share, "Share Image"));
                return true;
            }
        });

        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        notifyDataSetChanged();
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}
