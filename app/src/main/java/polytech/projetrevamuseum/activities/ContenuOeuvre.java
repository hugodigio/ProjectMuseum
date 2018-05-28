package polytech.projetrevamuseum.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import polytech.projetrevamuseum.Obj3DView;
import polytech.projetrevamuseum.R;
public class ContenuOeuvre extends AppCompatActivity {

    //Dossier de l'Oeuvre
    File artDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenu_oeuvre);

        //evite un plantage sur les versions récentes d'android
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        String artDirectorystr = getIntent().getExtras().getString("artDirectory", "Erreur");
        if (!artDirectorystr.equals("Erreur")) {
            artDirectory = new File(artDirectorystr);
            listeMedia();
        }


    }

    //
    public void listeMedia() {
        LinearLayout scmedia = findViewById(R.id.ListMediaLayout);
        String name;
        ArrayList<String> exclusion = new ArrayList<String>();
        exclusion.add("+tag.txt");

        //Exclusion des image de texture des modeles 3D
        for (final File file : artDirectory.listFiles()) {
            if (!file.isDirectory()) {
                //Recherche des modeles 3D (.obj)
                if (file.getName().endsWith("_obj")) {
                    try {
                        BufferedReader brObj = new BufferedReader(new FileReader(file));
                        String lineObj;

                        //Extraction du nom du fichier de texture (.mtl)
                        while ((lineObj = brObj.readLine()) != null) {
                            if (lineObj.contains(".mtl")) {
                                String nameMtl = lineObj.substring(lineObj.lastIndexOf(" ") + 1, lineObj.length());
                                nameMtl = nameMtl.substring(0, nameMtl.length() - 4) + "_mtl";

                                File fileMtl = new File(artDirectory + "/" + nameMtl);
                                BufferedReader brMtl = new BufferedReader(new FileReader(fileMtl));
                                String lineMtl;

                                //Extraction du nom de l'image de texture
                                while ((lineMtl = brMtl.readLine()) != null) {
                                    if (lineMtl.contains(".png") || lineMtl.contains(".jpg") || lineMtl.contains(".jpeg")) {
                                        String namePic = lineMtl.substring(lineMtl.lastIndexOf(" ") + 1, lineMtl.length());
                                        exclusion.add(namePic);
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        for (final File file : artDirectory.listFiles()) {

            if (!file.isDirectory() && !exclusion.contains(file.getName())) {

                //Le fichier est une image
                if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
                    //Suppression de l'extension
                    if (file.getName().endsWith(".jpeg")) {
                        name = file.getName().substring(0, file.getName().length() - 5);
                    } else {
                        name = file.getName().substring(0, file.getName().length() - 4);
                    }
                    //Verification de la taille de l'image
                    if (file.length() < 2362482) {
                        //Creation du bouton
                        Button imgBut = new Button(this);
                        Drawable top = getResources().getDrawable(R.drawable.image);
                        imgBut.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                        imgBut.setText(name);
                        scmedia.addView(imgBut);
                        imgBut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideAll();
                                RelativeLayout layout_Image = findViewById(R.id.LayoutImage);
                                ImageView imageView_image = findViewById(R.id.ImageViewImage);
                                imageView_image.setImageURI(Uri.parse(file.getPath()));
                                layout_Image.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

                //Le fichier est un texte
                if (file.getName().endsWith(".txt") || file.getName().endsWith(".html") || file.getName().endsWith(".xml")) {
                    //Suppression de l'extension
                    if (file.getName().endsWith(".html")) {
                        name = file.getName().substring(0, file.getName().length() - 5);
                    } else {
                        name = file.getName().substring(0, file.getName().length() - 4);
                    }

                    //Creation du bouton
                    Button textBut = new Button(this);
                    Drawable top = getResources().getDrawable(R.drawable.texte);
                    textBut.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                    textBut.setText(name);
                    scmedia.addView(textBut);
                    textBut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideAll();
                            ScrollView layout_Texte = findViewById(R.id.LayoutTexte);
                            TextView TextViewText = findViewById(R.id.TextViewText);

                            //Interpretation et transformation du fichier texte
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

                //Le fichier est une video
                if (file.getName().endsWith(".mp4") || file.getName().endsWith(".avi")) {

                    //Suppression de l'extension
                    name = file.getName().substring(0, file.getName().length() - 4);

                    //Creation du bouton
                    Button vidBut = new Button(this);
                    Drawable top = getResources().getDrawable(R.drawable.video);
                    vidBut.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
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
                if (file.getName().endsWith(".mp3") || file.getName().endsWith(".wav")) {

                    //Suppression de l'extension
                    name = file.getName().substring(0, file.getName().length() - 4);

                    //Creation du bouton
                    Button audBut = new Button(this);
                    Drawable top = getResources().getDrawable(R.drawable.audio);
                    audBut.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
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
                if (file.getName().endsWith("_obj")) {

                    //Suppression de l'extension
                    name = file.getName().substring(0, file.getName().length() - 4);

                    //Creation du bouton
                    Button troisdBut = new Button(this);
                    Drawable top = getResources().getDrawable(R.drawable.troisd);
                    troisdBut.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
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

    public void hideAll() {

        //Hide Layout Image
        RelativeLayout layout_Image = findViewById(R.id.LayoutImage);
        layout_Image.setVisibility(View.GONE);

        //Hide Layout Texte
        ScrollView layout_Texte = findViewById(R.id.LayoutTexte);
        layout_Texte.setVisibility(View.GONE);
    }
}