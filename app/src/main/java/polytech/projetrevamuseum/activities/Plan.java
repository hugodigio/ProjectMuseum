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
import android.widget.ListView;

import polytech.projetrevamuseum.R;
import polytech.projetrevamuseum.activities.Main2Activity;

public class Plan extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private ImageView map;
    private Button butRdj;
    private Button butRdc;
    private Button but1e;
    private Button but2e;
    private Button but3e;
    private String[] drawerItemsList;
    private ListView myDrawer;

    public static final String EXTRA_MESSAGE = "msg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //-- Drawer --
        drawerItemsList = getResources().getStringArray(R.array.items);
        myDrawer = (ListView) findViewById(R.id.nav_view);

        myDrawer.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, drawerItemsList));
        myDrawer.setOnItemClickListener(new DrawerItemClickListener());
        //-- ------ --

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /*
        //setup Description
        TextView desc = (TextView) findViewById(R.id.textView4);
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.description);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            desc.setText(new String(b));
        } catch (Exception e) {
            desc.setText("Error: can't show description.");
        }
        */

        //setup boutons & map
        map = (ImageView) findViewById(R.id.imageView2);
        butRdj = (Button) findViewById(R.id.butRdj);
        butRdc = (Button) findViewById(R.id.butRdc);
        but1e  = (Button) findViewById(R.id.but1e);
        but2e = (Button) findViewById(R.id.but2e);
        but3e = (Button) findViewById(R.id.but3e);

    }

    // ---------------- Changement map - boutons -----------------------------
    public void mapRdj(View v){
        map.setImageResource(getResources().getIdentifier("rdj", "drawable", "polytech.projetrevamuseum"));
        butRdj.setTextColor(Color.argb(255,255,255,255));
        butRdc.setTextColor(Color.argb(255,0,0,0));
        but1e.setTextColor(Color.argb(255,0,0,0));
        but2e.setTextColor(Color.argb(255,0,0,0));
        but3e.setTextColor(Color.argb(255,0,0,0));
    }
    public void mapRdc(View v){
        map.setImageResource(getResources().getIdentifier("rdc", "drawable", "polytech.projetrevamuseum"));
        butRdj.setTextColor(Color.argb(255,0,0,0));
        butRdc.setTextColor(Color.argb(255,255,255,255));
        but1e.setTextColor(Color.argb(255,0,0,0));
        but2e.setTextColor(Color.argb(255,0,0,0));
        but3e.setTextColor(Color.argb(255,0,0,0));
    }
    public void map1e(View v){
        map.setImageResource(getResources().getIdentifier("etage1", "drawable", "polytech.projetrevamuseum"));
        butRdj.setTextColor(Color.argb(255,0,0,0));
        butRdc.setTextColor(Color.argb(255,0,0,0));
        but1e.setTextColor(Color.argb(255,255,255,255));
        but2e.setTextColor(Color.argb(255,0,0,0));
        but3e.setTextColor(Color.argb(255,0,0,0));
    }
    public void map2e(View v){
        map.setImageResource(getResources().getIdentifier("etage2", "drawable", "polytech.projetrevamuseum"));
        butRdj.setTextColor(Color.argb(255,0,0,0));
        butRdc.setTextColor(Color.argb(255,0,0,0));
        but1e.setTextColor(Color.argb(255,0,0,0));
        but2e.setTextColor(Color.argb(255,255,255,255));
        but3e.setTextColor(Color.argb(255,0,0,0));
    }
    public void map3e(View v){
        map.setImageResource(getResources().getIdentifier("etage3", "drawable", "polytech.projetrevamuseum"));
        butRdj.setTextColor(Color.argb(255,0,0,0));
        butRdc.setTextColor(Color.argb(255,0,0,0));
        but1e.setTextColor(Color.argb(255,0,0,0));
        but2e.setTextColor(Color.argb(255,0,0,0));
        but3e.setTextColor(Color.argb(255,255,255,255));
    }
    // -----------------------------------------------------------------------

    //changement d'activity
    public void switchAct(String texte) {
        // ---- Drawer -------

        //--------------------

        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra(EXTRA_MESSAGE, texte);
        startActivity(intent);
        //message : nom de la piece
        //Toast.makeText(this, texte, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String clicked = drawerItemsList[position];
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            switchAct(clicked);
        }
    }
}
