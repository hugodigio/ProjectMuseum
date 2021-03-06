package polytech.projetrevamuseum.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import polytech.projetrevamuseum.R;
import polytech.projetrevamuseum.TagManager;

public class MenuPrincipal extends AppCompatActivity {
    private NfcAdapter adapterNFC;
    TagManager tagManager;

    boolean DirectoryExist = false;
    boolean DirectoryEmpty = true;
    final String DIRECTORYNAME = "ProjectMuseum";

    Boolean nfcReady = false;

    //Pour la fonction qui cree les boites de dialogue
    final static int NFC_MISSING  = 1;
    final static int NFC_DISABLED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        NFCinit();

        //Boutons du menu principal
        Button ButtonPlan = findViewById(R.id.MainMenuSeePlan);
        Button ButtonDescription = findViewById(R.id.MainMenuSeeDescription);
        Button ButtonHistory = findViewById(R.id.MainMenuHistory);

        //Verifications
        askForPermission();
        checkAppDirectory();

        //on utilise la classe TagManager que l'on a cree, elle prend en paramètre le dossier de l'application
        tagManager = new TagManager(DIRECTORYNAME);

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
            intent.putExtra("directoryName", DIRECTORYNAME);
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
                    File dossier = new File(Environment.getExternalStorageDirectory().getPath()+"/"+DIRECTORYNAME);
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
                        intent.putExtra("directoryName", DIRECTORYNAME);
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
            Intent intent = new Intent(getApplicationContext(), HistoriqueTAGs.class);
            intent.putExtra("directoryName",DIRECTORYNAME);
            startActivity(intent);
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
            File dossier = new File(Environment.getExternalStorageDirectory().getPath()+"/"+DIRECTORYNAME);
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
                                if(!new File(Environment.getExternalStorageDirectory().getPath()+"/"+DIRECTORYNAME).mkdirs()) Log.d("checkAppDirectory","pas cree");
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
    protected void onResume() { //executé a chaque fois que l'application est lancée (par l'utilisateur ou par le lecteur NFC)
        super.onResume();
        checkAppDirectory();
        NFCinit();

        /*
        onResume() est appelé quand on lance / relance l'appli
        la fonction est donc aussi appelée quand on scan un NFC puisque, lorsque l'on scan un NFC, l'appli s'ouvre.

        la condition ci-dessous permet de traiter le TAG NFC, seulement si on viens d'ouvrir l'appli via le scan d'un tag NFC

        l'action qui a engendré l'ouverture de l'appli est stocké dans la variable "action"
        */

        Intent intent = getIntent();
        String action = intent.getAction();

        if(action != null && (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) || action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED))){
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String tagID = getIDTag(tag);
            File artDirectory = tagManager.findTag(tagID);
            if (artDirectory == null){
                Toast.makeText(getApplicationContext(),R.string.UnknowTAG,Toast.LENGTH_SHORT).show();
            } else {
                Intent contenuIntent = new Intent(getApplicationContext(),ContenuOeuvre.class);
                contenuIntent.putExtra("artDirectory",artDirectory.getPath());
                startActivity(contenuIntent);
                tagManager.addHistory(artDirectory);
            }
        }

    }

    /**
     * Initialise la classe qui fait la relation avec la puce NFC, et vérifie si le NFC est présent
     * sur cet appareil ou si il est acivé.
     */

    private void NFCinit(){
        //récupere les information de la puce NFC
        adapterNFC = NfcAdapter.getDefaultAdapter(this);

        //test la presence du NFC ou son activation
        if(adapterNFC == null){
            //NFC absent sur l'appareil
            nfcReady = false;
            Toast.makeText(getApplicationContext(),R.string.NoNFC, Toast.LENGTH_SHORT).show();
            NFCDialog(NFC_MISSING);
        } else if(!adapterNFC.isEnabled()){
            //NFC present mais desactivé. Ouvre les parametres de l'appareil
            nfcReady = false;
            NFCDialog(NFC_DISABLED);
        } else {
            nfcReady = true;
        }
    }


    /**
     * Classe qui affiche une boite de dialogue a l'utilisateur en fonction du contexte rencontrée
     * @param error erreur rencontrée
     */
    private void NFCDialog(final int error){
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
        }
        builder.show();
    }

    /**
     * permet de lire le contenu du Tag NFC scanné
     * @param tag tag NFC dont on veut récupérer l'id
     * @return retourne l'ID du tag scanné
     */
    private String getIDTag(Tag tag) {
        StringBuilder idTag = new StringBuilder();
        if(tag != null){
            byte[] tagId = tag.getId();
            for(int i=0; i<tagId.length; i++){
                idTag.append(Integer.toHexString(tagId[i] & 0xFF));
                if(i+1 < tagId.length){
                    idTag.append(" ");
                }
            }
        }
        Log.d("(MenuPrincipal)","getIDTag: tag: |"+idTag.toString()+"|");
        return idTag.toString();
    }


    // ------------------------------------- END NFC ZONE ----------------------------------------------

}

