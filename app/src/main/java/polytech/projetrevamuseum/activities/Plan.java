package polytech.projetrevamuseum.activities;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import polytech.projetrevamuseum.R;
import polytech.projetrevamuseum.activities.Main2Activity;

public class Plan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout ButtonLayout = findViewById(R.id.LayoutBoutons);
    }

    //changement d'activity
    public void switchAct(String texte) {

        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }
}
