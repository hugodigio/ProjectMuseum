package polytech.projetrevamuseum.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayDeque;

import polytech.projetrevamuseum.R;
import polytech.projetrevamuseum.TagManager;
import polytech.projetrevamuseum.activities.ContenuOeuvre;

public class HistoriqueTAGs extends AppCompatActivity {

    TagManager tagManager;
    LinearLayout historyLayout;
    Button clearHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique_tags);

        String DirectoryName = getIntent().getExtras().getString("directoryName");
        tagManager = new TagManager(DirectoryName);
        historyLayout = findViewById(R.id.HistoriqueLayout);
        clearHistoryButton = findViewById(R.id.clearHistoryButton);

        UpdateButtons();

        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagManager.clearHistory();
                UpdateButtons();
            }
        });

    }

    public void UpdateButtons(){

        historyLayout.removeAllViews();

        ArrayDeque<File> historique = tagManager.getHistorique();

        for(final File file:historique){

            Button button = new Button(this);
            button.setText(file.getName());
            button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ContenuOeuvre.class);
                    Intent contenuIntent = new Intent(getApplicationContext(),ContenuOeuvre.class);
                    contenuIntent.putExtra("artDirectory", file.getPath());
                    startActivity(contenuIntent);
                }
            });
            historyLayout.addView(button);
        }
    }
}
