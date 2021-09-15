package com.example.chau_admin.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ProgressBar;

import com.example.chau_admin.Adapter.post_adapter;
import com.example.chau_admin.R;
import com.example.chau_admin.models.Category;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;


public class Post extends Fragment {


    private FirebaseFirestore mfirebaseFirestore;
    private CollectionReference collectionReference;
    private com.example.chau_admin.Adapter.post_adapter post_adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;


    @Override
    public void onStart() {
        super.onStart();
        post_adapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(post_adapter!=null)
            post_adapter.stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mfirebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = mfirebaseFirestore.collection(getString(R.string.Category_uplaods));
        Query query = collectionReference.whereNotIn("category", Arrays.asList("Choose category","All dishes")).orderBy("category", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>().setQuery(query, Category.class).build();
        setLayout(options);

        return view;
    }

    private void setLayout(FirestoreRecyclerOptions<Category> query) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        post_adapter = new post_adapter(query);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(post_adapter);
        progressBar.setVisibility(View.GONE);
    }
}