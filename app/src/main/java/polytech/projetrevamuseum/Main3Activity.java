package polytech.projetrevamuseum;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Main3Activity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = getIntent();
        String identity = intent.getStringExtra(Main2Activity.EXTRA_MESSAGE);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main3);

        VideoView videov = (VideoView) findViewById(R.id.myVideo) ;
        identity = identity + "_v";

        Toast.makeText(this, identity, Toast.LENGTH_SHORT).show();

        int rawResId = getResources().getIdentifier(identity, "raw", "gouinquang.projetrevamuseum");
        Uri pathvid = Uri.parse("android.resource://gouinquang.projetrevamuseum/" + rawResId);
        videov.setVideoURI(pathvid);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videov);
        videov.setMediaController(mediaController);

        //detection fin de video
        videov.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                Main3Activity.super.onBackPressed();
            }
        });

        videov.start();
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
