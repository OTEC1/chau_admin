package com.example.chau_admin.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.example.chau_admin.models.Vendor_uploads;
import com.example.chau_admin.utils.Find;
import com.example.chau_admin.utils.util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.net.URISyntaxException;

import  com.example.chau_admin.utils.util.*;

import static com.example.chau_admin.utils.util.PICK_IMAGE;

public class Uploader extends AppCompatActivity {


    private BottomNavigationView navigationView;
    private ImageView image_view;
    private TextView mprogress;
    private EditText category_name;
    private Button upload,select;
    private  Uri imgUri;

    private FirebaseFirestore mfirebaseFirestore;

    private String p1,p2,p3,TAG="Uploader";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploader);
        mfirebaseFirestore=FirebaseFirestore.getInstance();

        upload =(Button) findViewById(R.id.upload);
        select =(Button) findViewById(R.id.choose);
        image_view =(ImageView) findViewById(R.id.photo);
        category_name =(EditText) findViewById(R.id.Category_name);
        mprogress = (TextView) findViewById(R.id.progress);
        navigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        new util().bot_nav(navigationView,this);


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file_picker(v);
            }
        });



        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getUid() != null){
                    if (imgUri != null) {
                        if (getFile_extension(imgUri).equalsIgnoreCase("png") | getFile_extension(imgUri).equalsIgnoreCase("jpg") | getFile_extension(imgUri).equalsIgnoreCase("jpeg")) {
                            if (!category_name.getText().toString().isEmpty())
                                send_data_to_firebase(category_name.getText().toString(), generate_name());
                            else
                                new util().message("EditText is Empty !", getApplicationContext());
                        } else
                            new util().message("Error Pls select only IMG files", getApplicationContext());

                    } else
                        new util().message("Error Occurred while select an image file", getApplicationContext());

                    }
                else
                    new util().message("Pls sign in",getApplicationContext());
            }
        });





    }





    //Step1
    private void send_data_to_firebase(String category_name,String doc_id_and_item_uploader_id) {
        DocumentReference reference = mfirebaseFirestore.collection(getString(R.string.category_uplaod)).document(doc_id_and_item_uploader_id);
        Vendor_uploads uploads = new Vendor_uploads(doc_id_and_item_uploader_id.concat(".png"),category_name);
        reference.set(uploads).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    new util().message("Uploaded  successfully.. Pls wait image uploading..", getApplicationContext());
                    credentials(doc_id_and_item_uploader_id);
                }
                else
                    new util().message("Error " + task.getException(),getApplicationContext());
            }
        });

    }


    //Step2
    private void credentials(final String m) {

        DocumentReference user =mfirebaseFirestore.collection("east").document("lab");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    p1 = task.getResult().getString("p1");
                    p2 = task.getResult().getString("p2");
                    p3 = task.getResult().getString("p3");
                    //System.out.println(id + " " + p1 + "  " + p2 + "  " + p3);

                    try {
                        if(p1.length()>0 && p2.length()>0 && p3.length()>0)
                            send_data_to_s3(imgUri,m.concat(".png"),p1,p2,p3);
                    } catch (URISyntaxException e) {
                        new util().message(e.toString(),getApplicationContext());
                        Log.d(TAG,e.toString());
                        hide_progress();
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
        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
        String d = Find.get_file_selected_path(imgUri, getApplicationContext());
        TransferObserver trans = transferUtility.upload(p3, media_key, new File(d));
        trans.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDone = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDo = (int) percentDone;

                mprogress.setText("Uploading... "+percentDo);
                if(percentDo == 100) {
                    mprogress.setText("Uploaded");
                    hide_progress();
                }




            }

            @Override
            public void onError(int id, Exception ex) {
                new util().message(ex.getLocalizedMessage(),getApplicationContext());
                Log.d(TAG,ex.getLocalizedMessage());
                hide_progress();

            }

        });
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
               new util().message("Pls Select an Image.",getApplicationContext());
        }
    }


    private String getFile_extension(Uri uri) {
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(getApplicationContext().getContentResolver().getType(uri));
    }


    public String generate_name() {
        long x = System.currentTimeMillis();
        long q = System.nanoTime();
        return String.valueOf(x).concat(String.valueOf(q));
    }


    private void hide_progress() {
    }

}