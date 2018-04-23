package polytech.projetrevamuseum.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import polytech.projetrevamuseum.Obj3DView;
import polytech.projetrevamuseum.R;

public class Main2Activity extends AppCompatActivity {

    private MediaPlayer mp;
    private String identity;
    private NfcAdapter nfcAdapter;
    private TextView title;

    public static final String EXTRA_MESSAGE = "msg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Intent intent = getIntent();
        //identity = intent.getStringExtra(Plan.EXTRA_MESSAGE);
        Toast.makeText(this, identity, Toast.LENGTH_SHORT).show();

        ImageButton imgBut = (ImageButton) findViewById(R.id.imageButton3);
        imgBut.setImageResource(getResources().getIdentifier("troisd", "drawable", "polytech.projetrevamuseum"));
        title = (TextView)findViewById(R.id.namePiece);
        title.setText(identity);

        // TEXTE
        TextView txt = (TextView) findViewById(R.id.textView2);
        AssetManager assetManager=getAssets();
        InputStream input;
        try {
            input=assetManager.open(identity+".txt");
            int size=input.available();
            byte[] buffer=new byte[size];
            input.read(buffer);
            input.close();
            String text=new String(buffer);
            txt.setText(text);
        } catch (Exception e){
            txt.setText("file not found");
        }

        //IMAGE
        ImageView img = (ImageView) findViewById(R.id.imageView2);
        int DrawResId = getResources().getIdentifier(identity, "drawable", "polytech.projetrevamuseum");
        if(DrawResId != 0){
            img.setImageResource(DrawResId);
        }

        //AUDIO
        final ImageButton button = (ImageButton) findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mp == null) {
                    int rawResId = getResources().getIdentifier(identity, "raw", "polytech.projetrevamuseum");
                    if(rawResId != 0){
                        Uri pathzik = Uri.parse("android.resource://polytech.projetrevamuseum/" + rawResId);
                        mp = MediaPlayer.create(Main2Activity.this, pathzik );
                    }
                }
                if(mp.isPlaying()){
                    mp.stop();
                    button.setImageResource(getResources().getIdentifier("play","drawable", "polytech.projetrevamuseum"));
                    try {
                        mp.prepare();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.seekTo(0);
                }
                else {
                    mp.start();
                    button.setImageResource(getResources().getIdentifier("pause","drawable", "polytech.projetrevamuseum"));
                }
            }
        });
    }

    public void switchActVideo(View v) {
        if(mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                final ImageButton button = (ImageButton) findViewById(R.id.imageButton);
                button.setImageResource(getResources().getIdentifier("play", "drawable", "polytech.projetrevamuseum"));
                try {
                    mp.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.seekTo(0);
            }
        }
        Intent intent = new Intent(this, Main3Activity.class);
        intent.putExtra(Main2Activity.EXTRA_MESSAGE, identity);
        startActivity(intent);
    }

    public void switchAct3D(View v){
        Intent intent = new Intent(this, Obj3DView.class);
        intent.putExtra(Main2Activity.EXTRA_MESSAGE, identity);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.seekTo(0);
            }
        }
    }

    // -------------------- NFC ---------------------
    @Override
    protected void onResume() { //permet a l'app de capturer les interruption nfc a la place du systeme
        super.onResume();
        Intent intent = new Intent(this, Plan.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onPause() {  //rend la main au systeme pour la puce nfc
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }
}
