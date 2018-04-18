package polytech.projetrevamuseum;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class MenuPrincipal extends AppCompatActivity {
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(MenuPrincipal.this, "nfcAdaptater = null, no NFC adaptater exists", Toast.LENGTH_LONG).show();
        }

        //Boutons du menu principal
        Button ButtonPlan = findViewById(R.id.MainMenuSeePlan);
        Button ButtonDescription = findViewById(R.id.MainMenuSeeDescription);
        Button ButtonHistory = findViewById(R.id.MainMenuHistory);

        //Listener des boutons
        ButtonPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Plan.class);
                startActivity(intent);
            }
        });
        ButtonDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),R.string.ComingSoon,Toast.LENGTH_SHORT).show();
            }
        });
        ButtonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),R.string.ComingSoon,Toast.LENGTH_SHORT).show();
            }
        });

    }

    // ------------------------------------ BEGIN NFC ZONE ---------------------------------------------

    @Override
    protected void onResume() { //permet a l'app de capturer les interruption nfc a la place du systeme
        super.onResume();
        Intent intent = new Intent(this, MenuPrincipal.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onPause() {  //rend la main au systeme pour la puce nfc
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            String texte = getTextFromNdefRecord(intent);
            if(texte == null){
                Toast.makeText(getApplicationContext(),R.string.ErreurTAG,Toast.LENGTH_LONG).show();
            }else{
                //switchAct(texte);
            }

        }

    }

    public String getTextFromNdefRecord(Intent intent){ //Renvoi le contenu texte de la puce nfc
        String tagContent = null;
        Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(parcelables == null){
            Log.e("getTextFromNdefRecord", "Erreur lors de la lecture du tag NFC");
        }else{
            NdefMessage ndefMessage = (NdefMessage) parcelables[0] ;
            NdefRecord[] ndefRecords = ndefMessage.getRecords();
            NdefRecord ndefRecord = ndefRecords[0];

            try {
                byte[] payload = ndefRecord.getPayload();
                String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                int languageSize = payload[0] & 0063;
                tagContent = new String(payload, languageSize + 1,
                        payload.length - languageSize - 1, textEncoding);
            } catch (UnsupportedEncodingException e) {
                Log.e("getTextFromNdefRecord", e.getMessage(), e);
            }
        }

        return tagContent;
    }

    // ------------------------------------- END NFC ZONE ----------------------------------------------
}
