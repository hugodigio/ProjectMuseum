package com.polytech.nfctoolsforprojectmuseum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ScanMenu extends AppCompatActivity {

    //est une classe qui permet de faire l'interface avec la puce NFC
    NfcAdapter AdapterNFC;
    TextView idTagTextView;

    //constantes
    final static int NFC_MISSING  = 1;
    final static int NFC_DISABLED = 2;

    /**
     * Fonction qui est appellee lors de la creation de l'activite (= menu)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_menu);
        idTagTextView = findViewById(R.id.IDnfcTag);

        NFCinit();

    }

    /**
     * Fonction qui est appellee lorsque l'on re-ouvre l'appllication
     */
    @Override
    protected void onResume() {
        super.onResume();

        //on verifie si le NFC est toujours disponible
        NFCinit();

        NFCreadingIntent();

    }


    /* ----------------------- NFC  ------------------------ */

    /**
     * Initialise la classe qui fait la relation avec la puce NFC, et vérifie si le NFC est présent
     * sur cet appareil ou si il est acivé.
     */
    private void NFCinit(){
        //récupere les information de la puce NFC
        AdapterNFC = NfcAdapter.getDefaultAdapter(this);

        //test la presence du NFC ou son activation
        if(AdapterNFC == null){
            //NFC absent sur l'appareil
            Toast.makeText(getApplicationContext(),R.string.NoNFCDetected, Toast.LENGTH_SHORT).show();
            NFCErrorDialog(NFC_MISSING);
        } else if(!AdapterNFC.isEnabled()){
            //NFC present mais desactivé. Ouvre les parametres de l'appareil
            NFCErrorDialog(NFC_DISABLED);
        }
    }

    /**
     * Classe qui affiche une boite de dialogue a l'utilisateur en fonction de l'erreur rencontrée
     * @param error erreur rencontrée
     */
    private void NFCErrorDialog(final int error){
        String title = "";
        String message = "";
        String button = "";
        AlertDialog.Builder Dialog;


        //on cree le message de l'erreur
        switch (error){
            case NFC_MISSING:
                title = getString(R.string.NoNFCDetected);
                message = getString(R.string.NoNFCDetectedMessage);
                button = getString(R.string.CloseApp);
                break;
            case NFC_DISABLED:
                title = getString(R.string.NFCDisabled);
                message = getString(R.string.NFCDisabledMessage);
                button = getString(R.string.GoNFCSettings);
                break;
        }

        //creation et affichage de la boite de dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (error){
                            case NFC_MISSING:
                                finish();
                                System.exit(0);
                                break;
                            case NFC_DISABLED:
                                Intent GoSettings = new Intent(Settings.ACTION_NFC_SETTINGS);
                                startActivity(GoSettings);
                                break;
                        }

                    }
                });
        builder.setCancelable(false); //la boite de dialogue de peut pas etre fermé en appuyant sur le bouton retour ou en cliquant en dehors de la fenetre
        builder.show();
    }

    private void NFCreadingIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(tag == null){
                idTagTextView.setText("tag == null");
            }else{

                StringBuilder tagInfo = new StringBuilder();
                byte[] tagId = tag.getId();
                for(int i=0; i<tagId.length; i++){
                    tagInfo.append(Integer.toHexString(tagId[i] & 0xFF)).append(" ");
                }

                idTagTextView.setText(tagInfo.toString());
            }
        }

    }
}
