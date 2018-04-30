package com.polytech.nfctoolsforprojectmuseum;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ScanMenu extends AppCompatActivity {

    //est une classe qui permet de faire l'interface avec la puce NFC
    NfcAdapter AdapterNFC;
    TagManager tagManager;
    TextView idTagTextView;
    Button buttonAdd;
    Boolean nfcReady = false;

    ////CONSTANTES
    //repertoire de l'application
    final static String PROJETCTMUSEUM_DIRECTORY = "ProjectMuseum";
    //Pour la fonction qui cree les boites de dialogue
    final static int NFC_MISSING  = 1;
    final static int NFC_DISABLED = 2;
    final static int ID_FOUND = 3;
    //valeurs
    final static int DELETE_TAG = 1;
    final static int MODIFY_TAG = 2;

    /**
     * Fonction qui est appellee lors de la creation de l'activite (= menu)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_menu);
        idTagTextView = findViewById(R.id.IDnfcTag);
        buttonAdd = findViewById(R.id.boutonAdd);
        tagManager = new TagManager(PROJETCTMUSEUM_DIRECTORY);


        askForPermission();
        checkAppDirectory();
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

        if(nfcReady){
            Intent intent = new Intent(this, ScanMenu.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            IntentFilter[] intentFilters = new IntentFilter[]{};
            AdapterNFC.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfcReady){
            //permet de redoonner au systeme la main sur le NFC (ne plus capturer les evenements du NFC)
            AdapterNFC.disableForegroundDispatch(this);
        }
    }

    /**
     * evenement pour le bouton d'ajout de TAG
     */
    private class ButtonAddListener implements View.OnClickListener{
        String tagID;

        public ButtonAddListener(String tagID){
            this.tagID = tagID;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), AddModifyTag.class);
            intent.putExtra("tagID",tagID);
            intent.putExtra("DirectoryName",PROJETCTMUSEUM_DIRECTORY);
            startActivity(intent);
            buttonAdd.setVisibility(View.GONE);

        }
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
            nfcReady = false;
            //NFC absent sur l'appareil
            Toast.makeText(getApplicationContext(),R.string.NoNFCDetected, Toast.LENGTH_SHORT).show();
            NFCDialog(NFC_MISSING);
        } else if(!AdapterNFC.isEnabled()){
            nfcReady = false;
            //NFC present mais desactivé. Ouvre les parametres de l'appareil
            NFCDialog(NFC_DISABLED);
        } else {
            nfcReady = true;
        }
    }

    private void NFCDialog(final int error){
        NFCDialog(error, null, null);
    }
    /**
     * Classe qui affiche une boite de dialogue a l'utilisateur en fonction du contexte rencontrée
     * @param error erreur rencontrée
     * @return retourne un identifiant pouvant etre interpreté
     */
    private void NFCDialog(final int error, final File artDirectory, final String tagID){
        String title = "";
        String message = "";
        String positiveButton = "";
        String negativeButton = "";
        String neutralButton  = "";
        AlertDialog.Builder Dialog;


        //on cree le message de l'erreur
        switch (error){
            case NFC_MISSING:
                title = getString(R.string.NoNFCDetected);
                message = getString(R.string.NoNFCDetectedMessage);
                positiveButton = getString(R.string.CloseApp);
                break;
            case NFC_DISABLED:
                title = getString(R.string.NFCDisabled);
                message = getString(R.string.NFCDisabledMessage);
                positiveButton = getString(R.string.GoNFCSettings);
                break;
            case ID_FOUND:
                title = getString(R.string.IDFound);
                message = getString(R.string.IDFoundMessage)+"\n\n\""+artDirectory.getName()+"\"\n\n"+getString(R.string.AskWhatToDo);
                positiveButton = getString(R.string.IDReplace);
                negativeButton = getString(R.string.Delete);
                neutralButton = getString(R.string.DoNothing);
        }

        //creation et affichage de la boite de dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        switch(error){
            case NFC_MISSING:
                builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.setCancelable(false);
                break;
            case NFC_DISABLED:
                builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent GoSettings = new Intent(Settings.ACTION_NFC_SETTINGS);
                        startActivity(GoSettings);
                    }
                });
                builder.setCancelable(false);
                break;
            case ID_FOUND:
                builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), AddModifyTag.class);
                        intent.putExtra("tagID",tagID);
                        intent.putExtra("DirectoryName",PROJETCTMUSEUM_DIRECTORY);
                        intent.putExtra("artDirectory",artDirectory.getPath());
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //supprimer le fichier tag.txt associé à ce tag
                        tagManager.deleteTAG(artDirectory);
                        Toast.makeText(getApplicationContext(),R.string.TAGdeleted,Toast.LENGTH_SHORT).show();
                        buttonAdd.setVisibility(View.VISIBLE);
                    }
                });
                builder.setNeutralButton(neutralButton,null);
                builder.setCancelable(true);
        }
        builder.show();
    }

    /**
     * recupère l'intent recu (généré dans notre cas par le NFC)
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String tagID = NFCreadingIntent(intent);
        idTagTextView.setText(tagID);
        File artDitectory = tagManager.findTag(tagID);

        if (artDitectory == null){

            buttonAdd.setVisibility(View.VISIBLE);
            buttonAdd.setOnClickListener(new ButtonAddListener(tagID));
        } else {
            buttonAdd.setVisibility(View.INVISIBLE);
            NFCDialog(ID_FOUND,artDitectory, tagID);
        }
    }

    /**
     * permet de lire le contenu du Tag NFC scanné
     * @param intent contient les données du NFC dans un Intent (classe qui permet de passer d'une activité à une autre)
     * @return retourne l'ID du tag scanné
     */
    private String NFCreadingIntent(Intent intent) {
        String action = intent.getAction();
        Log.d("(ScanMenu)","intent capturé, action:"+action);
        StringBuilder idTag = new StringBuilder();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Log.d("(ScanMenu)","Action du TAG capturée");
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(tag == null){
                idTagTextView.setText("tag == null");
            }else{

                byte[] tagId = tag.getId();
                for(int i=0; i<tagId.length; i++){
                    idTag.append(Integer.toHexString(tagId[i] & 0xFF)).append(" ");
                }
                //supprimer l'espace en trop
                idTag.delete(idTag.length()-1,idTag.length());

                /* ------------ DEBUG TAG ------------*/
                        StringBuilder tagDebug = new StringBuilder();
                        tagDebug.append(tag.toString() + "\n");

                        tagDebug.append("\nTag Id: \n");
                        tagDebug.append(idTag);
                        tagDebug.append("\n");

                        String[] techList = tag.getTechList();
                        tagDebug.append("\nTech List\n");
                        tagDebug.append("length = " + techList.length +"\n");
                        for(int i=0; i<techList.length; i++){
                            tagDebug.append(techList[i] + "\n ");
                        }

                        Log.d("(ScanMenu)","information de Tag"+"\n"+tagDebug.toString());
                /* ---------- FIN DEBUG TAG -----------*/


            }
        }
        return idTag.toString();
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
    private boolean checkAppDirectory() {
        boolean DirectoryExist = false;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            // Le périphérique de stockage externe existe (carte SD/cleUSB)
            File dossier = new File(Environment.getExternalStorageDirectory().getPath()+"/"+PROJETCTMUSEUM_DIRECTORY);
            if(dossier.exists() && dossier.isDirectory()) DirectoryExist = true;
        }else{
            // Le périphérique n'existe pas ou on ne peut ecrire dessus
            Toast.makeText(getApplicationContext(),"Erreur: Aucune memoire externe detectée",Toast.LENGTH_LONG).show();
        }

        if(!DirectoryExist){
            String messageErreur = "";

            messageErreur += getString(R.string.RepertoireInexistant)+"\n\n";
            messageErreur += getString(R.string.ExplicationFermeture);

            //creation et affichage de la boite de dialogue
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(messageErreur)
                    .setTitle(getString(R.string.checkDirectoryTitle))
                    .setPositiveButton(R.string.closeApp, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(!new File(Environment.getExternalStorageDirectory().getPath()+"/"+PROJETCTMUSEUM_DIRECTORY).mkdirs()) Log.d("checkAppDirectory","pas cree");
                            finish();
                            System.exit(0);
                        }
                    });
            builder.setCancelable(false);
            builder.show();
        }


        return DirectoryExist;
    }
}
