package polytech.projetrevamuseum.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

import polytech.projetrevamuseum.R;
import polytech.projetrevamuseum.activities.Main2Activity;

public class Plan extends AppCompatActivity {

    LinearLayout ButtonLayout;
    ImageView image;
    Boolean emptyImage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButtonLayout = findViewById(R.id.LayoutBoutons);
        image = findViewById(R.id.MapImage);

        String DirectoryName = getIntent().getExtras().getString("directoryName","defaultKey");

        boolean found = false;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            // Le périphérique de stockage externe existe (carte SD/cleUSB)
            File dossier = new File(Environment.getExternalStorageDirectory().getPath()+"/"+DirectoryName);
            File fichier = null;
            //parcours du dossier de l'application
            for(File file : dossier.listFiles()){
                if(file.isDirectory()){
                    //parcours du dossier d'une salle
                    for(File file2 : file.listFiles()){
                        if(!file2.isDirectory() && (file2.getName().endsWith(".png") || file2.getName().endsWith(".jpg") || file2.getName().endsWith(".jpeg"))){
                            addBouton(file.getName(),file2);
                        }
                    }
                }
            }
        }else{
            // Le périphérique n'existe pas ou on ne peut ecrire dessus
            Toast.makeText(getApplicationContext(),"Erreur: Aucune memoire externe detectée",Toast.LENGTH_LONG).show();
        }
    }

    public void addBouton(String ButtonName, final File ImageSrc){
        Button button = new Button(this);
        //button.setLayoutParams(new LinearLayout.LayoutParams());
        button.setText(ButtonName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.setImageURI(Uri.parse(ImageSrc.getAbsolutePath()));
            }
        });
        if(emptyImage){
            image.setImageURI(Uri.parse(ImageSrc.getAbsolutePath()));
        }
        ButtonLayout.addView(button);
    }


    //changement d'activity
    public void switchAct(String texte) {

        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }
}
