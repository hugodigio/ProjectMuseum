package polytech.projetrevamuseum.activities;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import polytech.projetrevamuseum.R;

public class Description extends AppCompatActivity {

    StringBuilder description;
    File fichierdescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        //récupérer la valeur envoyé de l'activité précédente
        //contient l'url de la description
        String descriptionPath = getIntent().getExtras().getString("cheminDescription","defaultKey");
        String directoryName = getIntent().getExtras().getString("directoryName","defaultKey");
        Log.v("Description", "chemin description: " + descriptionPath);
        description = new StringBuilder();
        fichierdescription = new File(Environment.getExternalStorageDirectory()+"/"+directoryName+"/"+descriptionPath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(fichierdescription));
            String line;

            while ((line = br.readLine()) != null) {
                description.append(line);
                description.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //afficher la description
        TextView descriptionTextView = findViewById(R.id.MuseumDescription);
        descriptionTextView.setText(Html.fromHtml(description.toString()));
    }
}
