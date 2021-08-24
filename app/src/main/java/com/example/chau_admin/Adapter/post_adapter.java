package com.example.chau_admin.Adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chau_admin.R;
import com.example.chau_admin.models.Category;
import com.example.chau_admin.utils.util;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class post_adapter extends FirestoreRecyclerAdapter<Category, post_adapter.MyHolder> {
    public post_adapter(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull Category model) {

        holder.user_tag.setText(model.getCategory());
        new util().IMG(holder.poster_value,model.getImg_url(),holder.progressBar);
        holder.poster_value.setOnClickListener(view -> {
            Bundle b = new Bundle();

        });


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content, parent, false);
        return new MyHolder(view);
    }


    class MyHolder extends RecyclerView.ViewHolder {
        private CircleImageView poster_value;
        private TextView user_tag;
        private ProgressBar progressBar;

        public MyHolder(View view) {
            super(view);
            poster_value = (CircleImageView) view.findViewById(R.id.cat_img);
            user_tag = (TextView) view.findViewById(R.id.cat_name);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        }
    }
}
