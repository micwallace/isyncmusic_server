/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ISyncMusicServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author michael
 */
public class Setup {

    private File musicdir;
    private JSONObject jsobject;
    private org.json.simple.JSONObject configarray;
    private Object configobject;

    public Setup() {
        musicdir = null;
    }
    // WILL EVENTUALLY BE DEPRECIATED
    public Setup(File _musicdir) {
        musicdir = _musicdir;
    }
    public void runindex() {
        if (musicdir == null){
            getDirConfig();
        }
        if (musicdir != null) {
            System.out.println("Indexing: "+musicdir);
            jsobject = new JSONObject();
            runindex(musicdir);
            // write the index to disc
            FileWriter file;
            try {
                file = new FileWriter(musicdir + File.separator + "iasindex.json");
                file.write(jsobject.toString());
                file.flush();
                file.close();
            } catch (IOException ex) {
                Logger.getLogger(Setup.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error");
            }
            // nullify global object to save mem
            jsobject = null;
            System.out.println("Index completed successfully!");
        } else {
            System.out.println("Error; null dir");
            return; //do nothing
        }

    }

    public void runindex(File dir) {
        Pattern pattern;
        Matcher matcher;
        if (dir != null) {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                if (children != null) {
                    for (int i = 0; i < children.length; i++) {
                        if (children[i] != null) {
                            File child = new File(dir, children[i]);
                            if (child != null) {
                                runindex(child);
                            }
                        }
                    }
                }
            } else {
                pattern = Pattern.compile("((.*\\.(?i)(mp3|m4a|avi|wav|m3u|aac|mp4))$)");
                matcher = pattern.matcher(dir.getName());
                if (matcher.matches()) {
                    // attempt to read ID3 object
                    AudioFile f = null;
                    String idartist = "";
                    String idalbum = "";
                    String idsong = "";
                    try {
                        f = AudioFileIO.read(dir.getAbsoluteFile());
                        Tag tag = f.getTag();
                        if (tag!=null){
                            idartist = tag.getFirst(FieldKey.ARTIST);
                            idalbum = tag.getFirst(FieldKey.ALBUM);
                            idsong = tag.getFirst(FieldKey.TITLE);
                        }
                    } catch (            CannotReadException | IOException | org.jaudiotagger.tag.TagException | ReadOnlyFileException | InvalidAudioFrameException ex) {
                        Logger.getLogger(Setup.class.getName()).log(Level.WARNING, null, ex);
                    }
                    // build values array
                    try {
                        JSONObject jsarray = new JSONObject();
                        jsarray.put("filename", dir.getName());
                        jsarray.put("artist", dir.getParentFile().getParentFile().getName());
                        jsarray.put("album", dir.getParentFile().getName());
                        jsarray.put("id3album", idalbum.toString());
                        jsarray.put("id3song", idsong.toString());
                        jsarray.put("filesize", dir.length());
                        // get relative filepath; REDUNDANT AND DEPRECIATED too much data used for this
                        //String relativepath = dir.getAbsolutePath().substring(musicdir.toString().length());
                        //jsarray.put("relpath", relativepath);
                        try {
                            jsobject.append(dir.getParentFile().getParentFile().getName(), jsarray.toString());
                        } catch (JSONException ex) {
                            Logger.getLogger(Setup.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(Setup.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public void saveconfig() {
        FileWriter file;
        try {
            file = new FileWriter("iasconfig.json");
            file.write(configarray.toString());
            file.flush();
            file.close();
        } catch (IOException ex) {
            Logger.getLogger(Setup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Object readconfig() {
        configarray = new org.json.simple.JSONObject();
        // return new configuration (new json array) if the saved one is lost or damaged
        File confpath = new File("iasconfig.json");
        if (!confpath.exists()){
            return configarray;
        }
        try {
            configobject = new Object();
            JSONParser parser = new JSONParser();
            configobject = parser.parse(new FileReader("iasconfig.json"));
            // create JSON array from oject; need to do single import to use simple.json object (as we are using its parser)
            configarray = (org.json.simple.JSONObject) (Object) configobject;
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Setup.class.getName()).log(Level.SEVERE, null, ex);
        }
        return configarray;
    }
    
    public String getDirConfig(){
        readconfig();
        if (musicdir!=null){
            return musicdir.toString();
        }
        // extract config values from json, return "0" if value does not exsist  
        if (configarray.containsKey("dir")){
           String dir = configarray.get("dir").toString();
           musicdir = new File(dir);
           return dir;
        } else {
           return "0";
        }
    }
    public void setDirConfig(String dir){
        readconfig();
        // set new musicdir value; used by indexer
        musicdir = new File(dir);
        // set new value for dir
        configarray.remove("dir");
        configarray.put("dir", dir);
    }
    public String[] getWSConfig(){
        readconfig();
        // get ws configuration if it exists.
        if (configarray.containsKey("wsactive")){
            String[] creds = {"1", configarray.get("wsuser").toString(), configarray.get("wspass").toString()};
            return creds;
        } else {
            String[] creds = {"0"};
            return creds;
        }
    }
    public void addWSconfig(String username, String hashpass){
        removeWSconfig();
            configarray.put("wsactive", "1");
            configarray.put("wsuser", username);
            configarray.put("wspass", hashpass);
    }
    public void removeWSconfig(){
        readconfig();
            // TBC
            configarray.remove("wsactive");
            configarray.remove("wsuser");
            configarray.remove("wspass");
    }
}
