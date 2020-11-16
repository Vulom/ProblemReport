package com.example.campusproblemreport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class NewProblem extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int TAKE_IMAGE_REQUEST = 0;
    private static final int PICK_IMAGE_REQUEST = 1;
    StorageReference ref;
    Button imagebt;
    EditText message;
    String spinnertxt;
    Button send;
    ImageView imageView;
    DatabaseReference reff;
    StorageReference sreff;
    EditText location;
    EditText note;

    private Uri Imageuri;

    Report report;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_problem);
        imageView = (ImageView) findViewById(R.id.imageView);
        message=(EditText)findViewById(R.id.message);
        location=(EditText)findViewById(R.id.location);
        send=(Button)findViewById(R.id.send);
        imagebt=(Button)findViewById(R.id.imgup);
        note=(EditText)findViewById(R.id.imagenote);
        report=new Report();

        reff = FirebaseDatabase.getInstance().getReference().child("Reports");
        sreff = FirebaseStorage.getInstance().getReference().child("Reports");


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();

            }
        });

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        imagebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(NewProblem.this);


            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
        spinnertxt =  parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
switch(requestCode) {

    case 0:
        if (resultCode == RESULT_OK && data != null) { //Take photo result
            Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(selectedImage);

        }
        break;
    case 1:
        if (resultCode == RESULT_OK && data != null && data.getData() != null) { //Choose from  gallery result
            Imageuri = data.getData();
            imageView.setImageURI(Imageuri);
        }
   break;
}
}

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Please upload an image according to your problem");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, TAKE_IMAGE_REQUEST);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , PICK_IMAGE_REQUEST);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void uploadImage() {
        if (Imageuri != null  ) {


                   ref = sreff.child("images/" + UUID.randomUUID().toString());
                    ref.putFile(Imageuri)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    Log.d("Ne", "onSuccess: Ne" + taskSnapshot.getTask().getResult());
                                    Toast
                                            .makeText(NewProblem.this,
                                                    "Image Uploaded",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    Log.d("TAGs", "onSuccess: Test");

                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d("TAGs2", "onSuccess: Uri önce test ");
                                            Uri downloadUrl = uri;
                                            String ImageURL = downloadUrl.toString();
                                            Log.d("TAGs", "onSuccess: ImageURL: " + ImageURL);
                                            //Database Insert
                                            report.setImageurl(ImageURL);
                                            report.setDescription(message.getText().toString().trim());
                                            report.setType(spinnertxt.trim());
                                            report.setLocation(location.getText().toString().trim());
                                            report.setNote(note.getText().toString().trim());
                                            reff.push().setValue(report);
                                            startActivity(new Intent(NewProblem.this, MainActivity.class));
                                        }
                                    });
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            Toast
                                    .makeText(NewProblem.this,
                                            "Failed" + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }
        else {
            //Database Insert
            report.setDescription(message.getText().toString().trim());
            report.setType(spinnertxt.trim());
            report.setLocation(location.getText().toString().trim());
            report.setNote(note.getText().toString().trim());
            reff.push().setValue(report);
            startActivity(new Intent(NewProblem.this, MainActivity.class));
        }
    }
        }
