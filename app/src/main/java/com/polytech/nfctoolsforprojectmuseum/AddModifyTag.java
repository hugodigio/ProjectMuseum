package com.polytech.nfctoolsforprojectmuseum;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class AddModifyTag extends AppCompatActivity {

    TextView instructions;
    TextView listType;
    LinearLayout buttonLayout;
    Button buttonAllArts;
    Button buttonUntaggedArts;
    TagManager tagManager;



    String tagID;
    File artDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modify_tag);

        instructions = findViewById(R.id.instructions);
        listType = findViewById(R.id.ListType);
        buttonLayout = findViewById(R.id.ButtonLayout);
        buttonAllArts = findViewById(R.id.AllArts);
        buttonUntaggedArts = findViewById(R.id.UntaggedArt);

        tagManager = new TagManager(getIntent().getExtras().getString("DirectoryName","defaultKey"));

        tagID = getIntent().getExtras().getString("tagID","defaultKey");
        final String artDirectoryString = getIntent().getExtras().getString("artDirectory");

        if(artDirectoryString != null){
            Log.d("AddModifyTag","Il sagit d'une modification: "+artDirectoryString);
            artDirectory = new File(artDirectoryString);
            instructions.setText(getString(R.string.EditTagInstructions));
        } else {
            instructions.setText(getString(R.string.AddTagInstructions));
        }
        showAllArts();


        buttonAllArts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButtons();
                showAllArts();
            }
        });

        buttonUntaggedArts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButtons();
                showUntaggedArts();
            }
        });
    }

    private void deleteButtons() {
        buttonLayout.removeAllViews();
    }

    private void showAllArts() {
        listType.setText(R.string.AllArtsType);
        for(final File oeuvre : tagManager.getAllArts()){
            Button oeuvreBtn = new Button(this);
            oeuvreBtn.setText(oeuvre.getName());
            oeuvreBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            oeuvreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(artDirectory != null){
                        //si nous modifions un tag existant
                        tagManager.deleteTAG(artDirectory);
                    }
                    tagManager.addTAG(oeuvre,tagID);


                    finish();
                }
            });
            buttonLayout.addView(oeuvreBtn);
        }
    }

    private void showUntaggedArts() {
        listType.setText(R.string.UnTaggedArtsType);
        for(final File oeuvre : tagManager.getUntaggedArts()){
            Button oeuvreBtn = new Button(this);
            oeuvreBtn.setText(oeuvre.getName());
            oeuvreBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            oeuvreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(artDirectory != null){
                        //si nous modifions un tag existant
                        tagManager.deleteTAG(artDirectory);
                    }
                    tagManager.addTAG(oeuvre,tagID);


                    finish();
                }
            });
            buttonLayout.addView(oeuvreBtn);
        }
    }
}
