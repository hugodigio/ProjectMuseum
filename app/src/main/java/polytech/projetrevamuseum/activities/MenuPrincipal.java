package polytech.projetrevamuseum.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import polytech.projetrevamuseum.R;

import static android.os.Environment.getExternalStorageDirectory;

public class MenuPrincipal extends AppCompatActivity {
    private NfcAdapter nfcAdapter;

    boolean DirectoryExist = false;
    boolean DirectoryEmpty = true;
    String  DirectoryName = "ProjectMuseum";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(MenuPrincipal.this, R.string.NoNFC, Toast.LENGTH_LONG).show();
        }

        //Boutons du menu principal
        Button ButtonPlan = findViewById(R.id.MainMenuSeePlan);
        Button ButtonDescription = findViewById(R.id.MainMenuSeeDescription);
        Button ButtonHistory = findViewById(R.id.MainMenuHistory);

        //Verifications
        askForPermission();
        checkAppDirectory();

        //Listener des boutons
        ButtonPlan.setOnClickListener(new listenerBoutonPlan());
        ButtonDescription.setOnClickListener(new listenerBoutonDescription());
        ButtonHistory.setOnClickListener(new listenerBoutonHistorique());


    }


    /* --------------------- LISTENER DES BOUTONS DU MENU ----------------------------------------*/

    /**
     * Classe pour gerer l'evenement lors du clic sur le bouton "voir plan du musée"
     */
    private class listenerBoutonPlan implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), Plan.class);
            //envoyer donnée a l'activité description
            intent.putExtra("directoryName", DirectoryName);
            startActivity(intent);
        }
    }

    /**
     * Classe pour gerer l'evenement lors du clic sur le bouton "historique tags NFC"
     */
    private class listenerBoutonDescription implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            checkAppDirectory();
            boolean found = false;
            if(!DirectoryEmpty){
                if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                    // Le périphérique de stockage externe existe (carte SD/cleUSB)
                    File dossier = new File(Environment.getExternalStorageDirectory().getPath()+"/"+DirectoryName);
                    File fichier = null;
                    for(File File : dossier.listFiles()){
                        if(!found && (File.getName().equals("Description.html") || File.getName().equals("description.html"))){
                            found = true;
                            fichier = new File(dossier.getPath()+File);
                        }
                    }
                    if(found){
                        Intent intent = new Intent(getApplicationContext(), Description.class);
                        //envoyer donnée a l'activité description
                        intent.putExtra("cheminDescription", fichier.getName());
                        intent.putExtra("directoryName", DirectoryName);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),getString(R.string.AucuneDescription),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    // Le périphérique n'existe pas ou on ne peut ecrire dessus
                    Toast.makeText(getApplicationContext(),"Erreur: Aucune memoire externe detectée",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Classe pour gerer l'evenement lors du clic sur le bouton "historique tags NFC"
     */
    private class listenerBoutonHistorique implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),R.string.ComingSoon,Toast.LENGTH_SHORT).show();
        }
    }


    /* -------------------- VERIFICATIONS -----------------------------*/

    /** Fonction qui demande les permissions à l'utilisateur pour les permissions risquees, qui
     *  demande l'autorisation explicite de l'utilisateur
     */
    private void askForPermission() {
        //condition pour tester si l'appareil a une version d'android > 6.0 (SDK version 23)
        if (Build.VERSION.SDK_INT >= 23) {
            //demande explicite de l'autorisation de lire dans la memoire de l'appareil (obligatoire pour android > 6.0)
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)        {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 2);
                }
            }
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)        {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 2);
                }
            }
        }
    }

    /**
     * Verifie si le dossier de l'application existe, sinon si il est vide.
     * Il affiche une boite de dialogue informant que l'application n'est pas prête,
     * puis ferme l'application.
     *
     */
    private void checkAppDirectory() {

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            // Le périphérique de stockage externe existe (carte SD/cleUSB)
            File dossier = new File(Environment.getExternalStorageDirectory().getPath()+"/"+DirectoryName);
            if(dossier.exists() && dossier.isDirectory()) DirectoryExist = true;
                if(dossier.listFiles() != null) if(dossier.listFiles().length != 0) DirectoryEmpty = false;
            Log.d("checkAppDirectory", "exist:"+DirectoryExist+" empty: "+DirectoryEmpty);
        }else{
            // Le périphérique n'existe pas ou on ne peut ecrire dessus
            Toast.makeText(getApplicationContext(),"Erreur: Aucune memoire externe detectée",Toast.LENGTH_LONG).show();
        }

        if(!DirectoryExist || DirectoryEmpty){
            String messageErreur = "";

            if(!DirectoryExist) messageErreur += getString(R.string.RepertoireInexistant)+"\n\n";
            else messageErreur += getString(R.string.RepertoireVide)+"\n\n";
            messageErreur += getString(R.string.ExplicationFermeture);

            //creation et affichage de la boite de dialogue
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(messageErreur)
                    .setTitle(getString(R.string.checkDirectoryTitle))
                    .setPositiveButton(R.string.closeApp, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(!DirectoryExist){
                                if(!new File(Environment.getExternalStorageDirectory().getPath()+"/"+DirectoryName).mkdirs()) Log.d("checkAppDirectory","pas cree");
                            }
                            finish();
                            System.exit(0);
                        }
                    });
            builder.setCancelable(false);
            builder.show();
        }



    }

    // ------------------------------------ BEGIN NFC ZONE ---------------------------------------------

    @Override
    protected void onResume() { //permet a l'app de capturer les interruption nfc a la place du systeme
        checkAppDirectory();
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

        Log.v("NFCTAG", "ID NFC:"+tagContent);

        return tagContent;
    }

    // ------------------------------------- END NFC ZONE ----------------------------------------------

}

