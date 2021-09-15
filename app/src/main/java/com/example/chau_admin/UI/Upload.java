package com.example.chau_admin.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.chau_admin.R;
import com.example.chau_admin.Running_Service.Pusher;
import com.example.chau_admin.models.Vendor_uploads;
import com.example.chau_admin.utils.Find;
import com.example.chau_admin.utils.util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.chau_admin.utils.Constants.REQUEST_KEY;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.pushy.sdk.Pushy;

import static android.app.Activity.RESULT_OK;
import static com.example.chau_admin.utils.util.PICK_IMAGE;

public class Upload extends Fragment {


    private EditText category_name, broadcast_message, topic_token;
    private Button upload, select, broadcast_button,medit_post;
    private ImageView image_view;
    private TextView mprogress;
    private ArrayAdapter arrayAdapter;
    private Spinner spinner;
    private Uri imgUri;
    private FirebaseFirestore mfirebaseFirestore;


    private List<String> list_broadcast_section;
    private String p1, p2, p3, TAG = "Uploader", drop_down;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        mfirebaseFirestore = FirebaseFirestore.getInstance();

        upload = (Button) view.findViewById(R.id.upload);
        medit_post = (Button) view.findViewById(R.id.edit_post);
        select = (Button) view.findViewById(R.id.choose);
        image_view = (ImageView) view.findViewById(R.id.photo);
        category_name = (EditText) view.findViewById(R.id.Category_name);
        mprogress = (TextView) view.findViewById(R.id.progress);
        broadcast_message = (EditText) view.findViewById(R.id.notification_broadcasting);
        topic_token = (EditText) view.findViewById(R.id.topics);
        broadcast_button = (Button) view.findViewById(R.id.broadcast);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        Populate_spinner();

        select.setOnClickListener(this::file_picker);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drop_down = (spinner.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        upload.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getUid() != null) {
                if (imgUri != null) {
                    if (getFile_extension(imgUri).equalsIgnoreCase("png") | getFile_extension(imgUri).equalsIgnoreCase("jpg") | getFile_extension(imgUri).equalsIgnoreCase("jpeg")) {
                        if (!category_name.getText().toString().isEmpty())
                            send_data_to_firebase(category_name.getText().toString(), generate_name());
                        else
                            new util().message("EditText is Empty !", getContext());
                    } else
                        new util().message("Error Pls select only IMG files", getContext());

                } else
                    new util().message("Error Occurred while select an image file", getContext());

            } else
                new util().message("Pls sign in", getContext());
        });

        medit_post.setOnClickListener(j->{
            new util().open_Fragment(new Post(),null,requireActivity());
        });


        broadcast_button.setOnClickListener(m -> {
            if (!drop_down.trim().equals("Choose"))
                if (!broadcast_message.getText().toString().isEmpty())
                    if (!topic_token.getText().toString().isEmpty())
                        PUSH_NOTIFICATION((drop_down.equals("Topic")) ? "/topics/".concat(topic_token.getText().toString()) : topic_token.getText().toString());
                    else
                        new util().message("Pls select a Section", getContext());
        });


        return view;
    }

    private void Populate_spinner() {
        list_broadcast_section = new ArrayList<>();
        list_broadcast_section.add("Choose");
        list_broadcast_section.add("Topic");
        list_broadcast_section.add("Device");
        arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, list_broadcast_section);
        arrayAdapter.setDropDownViewResource(R.layout.text_pad);
        spinner.setAdapter(arrayAdapter);
        
    }

    private void PUSH_NOTIFICATION(String c) {
        List<String> device;
        device = ((topic_token.getText().toString().isEmpty()) ? members_call() : Collections.singletonList((c)));
        String[] to = device.toArray(new String[device.size()]);
        Map<String, Object> pay_load_data = new HashMap<>();
        pay_load_data.put("message", broadcast_message.getText().toString());
        Pusher.pushrequest push = new Pusher.pushrequest(pay_load_data, to);

        //Device id must match  list of app ID's
        REQUEST_KEY = "cb511ecb9256108ee4bbe770a6f6525ae800c5d473c3ee628779a285f5d882ee";

        try {
            Pusher.sendPush(push);
            new util().message("Sent to " + to, getContext());

        } catch (Exception ex) {
            new util().message(ex.toString(), getContext());
            System.out.println(ex);
        }

    }


    private List<String> members_call() {
        return null;
    }


    //Step1
    private void send_data_to_firebase(String category_name, String doc_id_and_item_uploader_id) {
        DocumentReference reference = mfirebaseFirestore.collection(getString(R.string.category_uplaod)).document(doc_id_and_item_uploader_id);
        Vendor_uploads uploads = new Vendor_uploads(doc_id_and_item_uploader_id.concat(".png"), category_name);
        reference.set(uploads).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    new util().message("Uploaded  successfully.. Pls wait image uploading..", getContext());
                    credentials(doc_id_and_item_uploader_id);
                } else
                    new util().message("Error " + task.getException(), getContext());
            }
        });

    }


    //Step2
    private void credentials(final String m) {

        DocumentReference user = mfirebaseFirestore.collection("east").document("lab");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    p1 = task.getResult().getString("p1");
                    p2 = task.getResult().getString("p2");
                    p3 = task.getResult().getString("p3");
                    //System.out.println(id + " " + p1 + "  " + p2 + "  " + p3);

                    try {
                        if (p1.length() > 0 && p2.length() > 0 && p3.length() > 0)
                            send_data_to_s3(imgUri, m.concat(".png"), p1, p2, p3);
                    } catch (URISyntaxException e) {
                        new util().message(e.toString(), getContext());
                        Log.d(TAG, e.toString());
                        mprogress.setVisibility(View.GONE);
                    }

                }
            }
        });
    }


    //Step3
    private void send_data_to_s3(Uri imgUri, String media_key, String p1, String p2, String p3) throws URISyntaxException {

        AWSCredentials credentials = new BasicAWSCredentials(p1, p2);
        AmazonS3 s3 = new AmazonS3Client(credentials);
        java.security.Security.setProperty("networkaddress.cache.ttl", "60");
        s3.setRegion(Region.getRegion(Regions.EU_WEST_3));
        //s3.setObjectAcl("", ".png", CannedAccessControlList.PublicRead);
        TransferUtility transferUtility = new TransferUtility(s3, getContext());
        String d = Find.get_file_selected_path(imgUri, getContext());
        TransferObserver trans = transferUtility.upload(p3, media_key, new File(d));
        trans.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDone = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDo = (int) percentDone;

                mprogress.setText("Uploading... " + percentDo);
                if (percentDo == 100) {
                    mprogress.setText("Uploaded");
                    hide_progress();
                }


            }

            @Override
            public void onError(int id, Exception ex) {
                new util().message(ex.getLocalizedMessage(), getContext());
                Log.d(TAG, ex.getLocalizedMessage());
                hide_progress();

            }

        });
    }

    private void hide_progress() {
        mprogress.setVisibility(View.GONE);
    }


    //Media selector  Custom ui
    public void file_picker(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setDataAndType(imgUri, "image/*");
        startActivityForResult(intent, PICK_IMAGE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            assert data != null;
            imgUri = data.getData();
            assert imgUri != null;
            if (imgUri.toString().contains("image")) {
                image_view.setImageURI(imgUri);
            } else
                new util().message("Pls Select an Image.", getContext());
        }
    }


    private String getFile_extension(Uri uri) {
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(getContext().getContentResolver().getType(uri));
    }


    public String generate_name() {
        long x = System.currentTimeMillis();
        long q = System.nanoTime();
        return String.valueOf(x).concat(String.valueOf(q));
    }

}