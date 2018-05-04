package polytech.projetrevamuseum;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TagManager {

    final static int ID_DELETE = 1;
    final static int ID_ADD = 2;
    final static int ID_MODIFY = 3;
    final static String TAG_FILENAME = "+tag.txt";

    private File tagsDirectory;

    public TagManager(String pathInSDcard){
        tagsDirectory = new File(Environment.getExternalStorageDirectory() + "/" + pathInSDcard);
    }

    public TagManager(File tagsDirectory){
        this.tagsDirectory = tagsDirectory;
    }

    /**
     * Cherche le chemin d'acces du TAG correspondant
     * @param idTAG code du tag que l'on cherche
     * @return renvoi l'emplacement du TAG
     */
    public File findTag(String idTAG){
        File tagDirectory = null;

        //on vérifie si le dossier de l'application est vide
        if(tagsDirectory.list().length != 0){
            //on parcours tout les fichiers du repertoire de l'application
            for(File roomsDirectory : tagsDirectory.listFiles()){
                //si c'est un repertoire, il s'agit d'un dossier qui contient des oeuvres
                if(roomsDirectory.isDirectory()){
                    //on regarde tout les fichiers contenu dans ce repertoire qui contient des oeuvres
                    for(File artsDirectory : roomsDirectory.listFiles()){
                        //si c'est un dossier, on est alors dans le repertoire d'une oeuvre
                        if(artsDirectory.isDirectory()){
                            //on parcours les fichiers du repertoire à la recherche d'un fichier de tag
                            for(File fileInArtsDirectory : artsDirectory.listFiles()){
                                if(fileInArtsDirectory.getName().equals(TAG_FILENAME)){
                                    //on recupere le tag contenu dans ce fichier
                                    Log.d("TagManager","(findTag)UN FICHIER DE TAG A ETE TROUVE: "+fileInArtsDirectory.getPath());
                                    try {
                                        BufferedReader br = new BufferedReader(new FileReader(fileInArtsDirectory));
                                        String line;

                                        while ((line = br.readLine()) != null) {
                                            Log.d("TagManager","(findTag)ligne du fichier: "+line);
                                            if (!line.contains("//")){
                                                if(line.equals(idTAG)){
                                                    tagDirectory = artsDirectory;
                                                    Log.d("TagManager","TAG TROUVé !!");
                                                }
                                            } else{
                                                Log.d("TagManager","(findTag) ligne commentée");
                                            }
                                        }
                                        br.close();
                                    }
                                    catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //DEBUG
        if(tagDirectory != null){
            Log.d("TagManager", "(findTag)RETURN: " + tagDirectory.getPath());
        } else {
            Log.d("TagManager", "(findTag)RETURN: null");
        }

        return tagDirectory;
    }

    /**
     * supprime le tag contenu dans le repertoire indiqué
     * @param tagDirectory Url du répertoire du Tag à supprimer
     */
    public void deleteTAG(File tagDirectory){
        File tag = new File(tagDirectory.getPath()+"/"+TAG_FILENAME);
        if(tag.exists()){
            tag.delete();
        }
    }

    /**
     * supprime le tag contenu dans le repertoire indiqué
     * @param tagID ID du TAG
     */
    public void deleteTAG(String tagID){
        File tagDirectory = findTag(tagID);
        deleteTAG(tagDirectory);
    }

    /**
     * ajouter un tag à un répertoire
     * @param tagDirectory repertoire du TAG
     * @param tagID ID du TAG
     */
    public void addTAG(File tagDirectory, String tagID){
        Log.d("TagManager","(addTAG)debut");

        File tag = new File(tagDirectory.getPath()+"/"+TAG_FILENAME);
        if(tag.exists()){
            Log.d("TagManager","(addTAG)fichier deja existant, suppression");
            deleteTAG(tagDirectory);
        }
        try {
            Log.d("TagManager","(addTAG)fichier cree: "+tag.createNewFile());
            BufferedWriter bw = new BufferedWriter(new FileWriter(tag));
            bw.write(tagID);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Modifier l'emplacement d'un tag deja existant
     * @param originalTagDirectory ancien repertoire du TAG
     * @param newTagDirectory nouveau repertoire du TAG
     */
    public void modifyTAG(File originalTagDirectory, File newTagDirectory){
        moveFile(originalTagDirectory,TAG_FILENAME,newTagDirectory);
    }

    /**
     * Modifier l'emplacement d'un tag deja existant
     * @param idTAG id du TAG a deplacer
     * @param newTagDirectory nouveau repertoire du TAG
     */
    public void modifyTAG(String idTAG, File newTagDirectory){
        modifyTAG(findTag(idTAG), newTagDirectory);
    }

    public ArrayList<File> getUntaggedArts(){

        ArrayList<File> files = new ArrayList<>();

        //on vérifie si le dossier de l'application est vide
        if(tagsDirectory.list().length != 0){
            //on parcours tout les fichiers du repertoire de l'application
            for(File roomsDirectory : tagsDirectory.listFiles()){
                //si c'est un repertoire, il s'agit d'un dossier qui contient des oeuvres
                if(roomsDirectory.isDirectory()){
                    //on regarde tout les fichiers contenu dans ce repertoire qui contient des oeuvres
                    for(File artsDirectory : roomsDirectory.listFiles()){
                        //si c'est un dossier, on est alors dans le repertoire d'une oeuvre
                        if(artsDirectory.isDirectory()){
                            Boolean found = false;
                            //on parcours les fichiers du repertoire à la recherche d'un fichier de tag
                            for(File fileInArtsDirectory : artsDirectory.listFiles()){
                                if(fileInArtsDirectory.getName().equals(TAG_FILENAME)){
                                    found = true;
                                }
                            }
                            if(!found){
                                files.add(artsDirectory);
                            }
                        }
                    }
                }
            }
        }

        return files;
    }

    public ArrayList<File> getAllArts(){

        ArrayList<File> files = new ArrayList<>();

        //on vérifie si le dossier de l'application est vide
        if(tagsDirectory.list().length != 0){
            //on parcours tout les fichiers du repertoire de l'application
            for(File roomsDirectory : tagsDirectory.listFiles()){
                //si c'est un repertoire, il s'agit d'un dossier qui contient des oeuvres
                if(roomsDirectory.isDirectory()){
                    //on regarde tout les fichiers contenu dans ce repertoire qui contient des oeuvres
                    for(File artsDirectory : roomsDirectory.listFiles()){
                        //si c'est un dossier, on est alors dans le repertoire d'une oeuvre
                        if(artsDirectory.isDirectory()){
                            files.add(artsDirectory);
                        }
                    }
                }
            }
        }

        return files;
    }

    //fonction PRIVATE
    private void moveFile(File inputPath, String inputFile, File outputPath) {

        FileInputStream in;
        FileOutputStream out;

        try {

            //create output directory if it doesn't exist
            if (!outputPath.exists())
            {
                outputPath.mkdirs();
            }


            in = new FileInputStream(inputPath.getPath() + "/" + inputFile);
            out = new FileOutputStream(outputPath.getPath() + "/"+ inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
