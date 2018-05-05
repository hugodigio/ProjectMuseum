package polytech.projetrevamuseum.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;

import polytech.projetrevamuseum.R;
public class ContenuOeuvre extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenu_oeuvre);

        TextView title = findViewById(R.id.titleContenu);
        title.setText(getIntent().getExtras().getString("artDirectory", "Erreur"));

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuPrincipal.class);
        startActivity(intent);
        finish();
    }
}
