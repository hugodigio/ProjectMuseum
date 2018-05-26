package polytech.projetrevamuseum.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import polytech.projetrevamuseum.Obj3DView;
import polytech.projetrevamuseum.R;

public class ArtWork extends AppCompatActivity {

    //Dossier de l'Oeuvre
    File artDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_work);

        //Demande de permission de lecture et ecriture
        // A supprimer pour la version finale
        if (Build.VERSION.SDK_INT >= 23) {
            //demande explicite de l'autorisation de lire dans la memoire de l'appareil (obligatoire pour android > 6.0)
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)        {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 2);
                }
            }
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)        {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 2);
                }
            }
        }

        // A remplacer par un get extra pour avoir le bon dossier
        final String artDirectoryString; //= getIntent().getExtras().getString("artDirectory");
        artDirectory=new File(Environment.getExternalStorageDirectory().getPath()+"/ProjectMuseum/floor 1/oeuvre 1");

        listeMedia();


    }

    public void listeMedia(){
        LinearLayout scmedia=findViewById(R.id.ListMediaLayout);
        String name;

        for(final File file : artDirectory.listFiles()){

            if(!file.isDirectory()){

                //Le fichier est une image
                if(file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")){
                    if (file.getName().endsWith(".jpeg")){
                        name=file.getName().substring(0,file.getName().length()-5);
                    }
                    else{
                        name=file.getName().substring(0,file.getName().length()-4);
                    }
                    Button imgBut = new Button(this);
                    Drawable top = getResources().getDrawable(R.drawable.image);
                    imgBut.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
                    imgBut.setText(name);
                    scmedia.addView(imgBut);
                    imgBut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideAll();
                            RelativeLayout layout_Image = findViewById(R.id.LayoutImage);
                            ImageView imageView_image= findViewById(R.id.ImageViewImage);
                            imageView_image.setImageURI(Uri.parse(file.getPath()));
                            layout_Image.setVisibility(View.VISIBLE);
                        }
                    });
                }
                //Le fichier est un texte
                if(file.getName().endsWith(".txt") || file.getName().endsWith(".html") || file.getName().endsWith(".xml")) {
                    if (file.getName().equals("+tag.txt")) {

                        if (file.getName().endsWith(".html")){
                            name=file.getName().substring(0,file.getName().length()-5);
                        }
                        else{
                            name=file.getName().substring(0,file.getName().length()-4);
                        }

                        Button textBut = new Button(this);
                        Drawable top = getResources().getDrawable(R.drawable.texte);
                        textBut.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
                        textBut.setText(name);
                        scmedia.addView(textBut);
                        textBut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideAll();
                                ScrollView layout_Texte = findViewById(R.id.LayoutTexte);
                                TextView TextViewText = findViewById(R.id.TextViewText);

                                StringBuilder description = new StringBuilder();
                                try {
                                    BufferedReader br = new BufferedReader(new FileReader(file));
                                    String line;

                                    while ((line = br.readLine()) != null) {
                                        description.append(line);
                                        description.append('\n');
                                    }
                                    br.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                TextViewText.setText(Html.fromHtml(description.toString()));
                                layout_Texte.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

                //Le fichier est une video
                if(file.getName().endsWith(".mp4") || file.getName().endsWith(".avi")) {
                    name=file.getName().substring(0,file.getName().length()-4);
                    Button vidBut = new Button(this);
                    Drawable top = getResources().getDrawable(R.drawable.video);
                    vidBut.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
                    vidBut.setText(name);
                    scmedia.addView(vidBut);
                    vidBut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideAll();
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(file), "video/*");
                            startActivity(intent);
                        }
                    });
                }

                //Le fichier est un audio
                if(file.getName().endsWith(".mp3") || file.getName().endsWith(".wav")) {
                    name=file.getName().substring(0,file.getName().length()-4);

                    Button audBut = new Button(this);
                    Drawable top = getResources().getDrawable(R.drawable.audio);
                    audBut.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
                    audBut.setText(name);
                    scmedia.addView(audBut);
                    audBut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideAll();
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(file), "audio/*");
                            startActivity(intent);
                        }
                    });
                }

                //Le fichier est un modele 3D
                if(file.getName().endsWith(".obj")){
                    name=file.getName().substring(0,file.getName().length()-4);
                    Button troisdBut = new Button(this);
                    Drawable top = getResources().getDrawable(R.drawable.troisd);
                    troisdBut.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
                    troisdBut.setText(name);
                    scmedia.addView(troisdBut);

                    troisdBut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideAll();

                            Intent intent = new Intent(getApplicationContext(), Obj3DView.class);
                            intent.putExtra(Main2Activity.EXTRA_MESSAGE, file.getPath());
                            startActivity(intent);
                        }
                    });
                }
            }
        }
    }

    public void hideAll(){

        //Hide Layout Image
        RelativeLayout layout_Image = findViewById(R.id.LayoutImage);
        layout_Image.setVisibility(View.GONE);

        //Hide Layout Texte
        ScrollView layout_Texte = findViewById(R.id.LayoutTexte);
        layout_Texte.setVisibility(View.GONE);

    }
}
