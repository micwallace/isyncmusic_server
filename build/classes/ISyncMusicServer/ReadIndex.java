/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ISyncMusicServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import org.clapper.util.misc.FileHashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author michael TODO: Minimise on main window close TODO: Add server status
 * bar icons TODO: Method to count number of files within a directory TODO:
 */
public class ReadIndex {

    private JSONObject jsobject;
    public File musicdir;
    public String artist;
    public String album;
    public FileHashMap topdir;
    private JSONParser jsparser;
    private boolean indexread = false;
    private DefaultListModel artistlist = null;
    private DefaultListModel albumlist = null;
    private ArrayList songlist = null;

    public ReadIndex(File _musicdir) {
        // init var; get music dir
        musicdir = _musicdir;
        jsobject = new JSONObject();
        jsparser = new JSONParser();
        try {
            topdir = new FileHashMap("ismsdata");
        } catch (IOException ex) {
            Logger.getLogger(ReadIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isIndexRead() {
        return indexread;
    }

    public void readIndex() {
        try {
            // write into json object
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(musicdir + File.separator + "iasindex.json"));
            jsobject = (JSONObject) obj;
        } catch (IOException | org.json.simple.parser.ParseException ex) {
            Logger.getLogger(ReadIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        parsetohashmap();
        jsobject = null;
        artistlist = null;
        albumlist = null;
        songlist = null;
        indexread = true;
        System.gc();
    }

    public void parsetohashmap() {
        Iterator<Map.Entry> it = jsobject.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = it.next();
            topdir.put(me.getKey(), me.getValue());
        }
    }

    public DefaultListModel sortlistmodel(ArrayList unsortedlist) {
        DefaultListModel sortedlist = new DefaultListModel();
        Collections.sort(unsortedlist);
        Iterator it = unsortedlist.iterator();
        while (it.hasNext()) {
            sortedlist.addElement(it.next());
            it.remove();
        }
        return sortedlist;
    }

    public DefaultListModel sortarraylistmodel(ArrayList unsortedlist) {
        // sort list that contains array values; sort using index 0
        // SORTING NOT YET IMPLEMENTED; passthrough function
        DefaultListModel sortedlist = new DefaultListModel();
        Iterator it = unsortedlist.iterator();
        while (it.hasNext()) {
            sortedlist.addElement(it.next());
            it.remove();
        }
        return sortedlist;
    }

    public DefaultListModel getallartists() {
        if (artistlist == null) {
            artistlist = new DefaultListModel();
            // get the key strings of the top level json (artists); return as list object
            TreeSet<String> artisttm = new TreeSet<>(topdir.keySet());
            for (String key : artisttm) {
                String value = topdir.get(key).toString();
            }
            Iterator it = artisttm.iterator();
            while (it.hasNext()) {
                artistlist.addElement(it.next());
                //it.remove(); // avoids a ConcurrentModificationException
            }
        }
        return artistlist;
    }

    public DefaultListModel getallalbums() {
        if (albumlist == null) {
            ArrayList finalal = new ArrayList();
            // get the key strings of the top level json (artists); return as list object
            Iterator it = topdir.values().iterator();
            while (it.hasNext()) {
                ArrayList tempal;
                tempal = (ArrayList) it.next();
                Iterator it2 = tempal.iterator();
                while (it2.hasNext()) {
                    Object tempobj = new Object();
                    try {
                        tempobj = jsparser.parse(it2.next().toString());
                    } catch (ParseException ex) {
                        Logger.getLogger(ReadIndex.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    JSONObject tempjs;
                    tempjs = (JSONObject) (Object) tempobj;
                    //System.out.print(tempjs.get("album"));
                    if (!finalal.contains(tempjs.get("album"))) {
                        //alistmodel.addElement(tempjs.get("album"));
                        finalal.add(tempjs.get("album"));
                    }
                    //it2.remove(); // avoids a ConcurrentModificationException
                }
                //it.remove(); // avoids a ConcurrentModificationException
            }
            albumlist = sortlistmodel(finalal);
            albumlist.add(0, "All");
        }
        return albumlist;
    }

    public DefaultListModel getallsongs() {
        DefaultListModel slistmodel = new DefaultListModel();
        ArrayList finalal = genSongsArray();
        Iterator it = finalal.iterator();
        while (it.hasNext()) {
            slistmodel.addElement(it.next());
            //it.remove();
        }
        /*
         * slistmodel = new DefaultListModel(); ArrayList finalal =
         * genSongsArray(); slistmodel = sortlistmodel(finalal);
         */
        return slistmodel;
    }

    public ArrayList genSongsArray() {
        if (songlist == null) {
            songlist = new ArrayList();
            Iterator it = topdir.values().iterator();
            while (it.hasNext()) {
                ArrayList tempal;
                tempal = (ArrayList) it.next();
                Iterator it2 = tempal.iterator();
                while (it2.hasNext()) {
                    Object tempobj = new Object();

                    try {
                        tempobj = jsparser.parse(it2.next().toString());
                    } catch (ParseException ex) {
                        Logger.getLogger(ReadIndex.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    JSONObject tempjs;
                    tempjs = (JSONObject) (Object) tempobj;
                    //System.out.print(tempjs.get("filename"));
                    songlist.add(new SongListModel((tempjs.get("id3song").equals("")) ? (tempjs.get("filename").toString()) : (tempjs.get("id3song")).toString(), tempjs.get("artist").toString() + File.separator + tempjs.get("album") + File.separator + tempjs.get("filename").toString()));
                    //it2.remove(); // avoids a ConcurrentModificationException
                }
                //it.remove(); // avoids a ConcurrentModificationException
            }
        }
        return songlist;
    }

    public ArrayList genSongsSizeArray() {
        ArrayList arrayal = new ArrayList();
        Iterator it = topdir.values().iterator();
        while (it.hasNext()) {
            ArrayList tempal;
            tempal = (ArrayList) it.next();
            Iterator it2 = tempal.iterator();
            while (it2.hasNext()) {
                Object tempobj = new Object();
                try {
                    tempobj = jsparser.parse(it2.next().toString());
                } catch (ParseException ex) {
                    Logger.getLogger(ReadIndex.class.getName()).log(Level.SEVERE, null, ex);
                }
                JSONObject tempjs;
                tempjs = (JSONObject) (Object) tempobj;
                arrayal.add(tempjs.get("filesize"));
                //it2.remove(); // avoids a ConcurrentModificationException
            }
            //it.remove(); // avoids a ConcurrentModificationException
        }
        return arrayal;
    }

    public DefaultListModel getartistalbums(String _artist) {

        artist = _artist;
        DefaultListModel artistfilter;
        ArrayList finalal = new ArrayList();
        ArrayList tempal;
        tempal = (ArrayList) topdir.get(artist);
        Iterator it = tempal.iterator();
        while (it.hasNext()) {
            Object tempobj = new Object();
            try {
                tempobj = jsparser.parse(it.next().toString());
            } catch (ParseException ex) {
                Logger.getLogger(ReadIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
            JSONObject tempjs;
            tempjs = (JSONObject) (Object) tempobj;
            //System.out.print(tempjs.get("filename"));
            if (!finalal.contains(tempjs.get("album"))) {
                finalal.add(tempjs.get("album"));
            }
            //it.remove(); // avoids a ConcurrentModificationException
        }
        artistfilter = sortlistmodel(finalal);
        return artistfilter;
        // return albums from artists
    }

    public DefaultListModel getalbumsongs(String _artist, String _album) {
        album = _album;
        artist = _artist;
        DefaultListModel albumfilter;
        ArrayList finalal = new ArrayList();
        // get the key strings of the top level json (artists); return as list object
        Iterator it = topdir.values().iterator();
        while (it.hasNext()) {
            ArrayList tempal;
            tempal = (ArrayList) it.next();
            Iterator it2 = tempal.iterator();
            while (it2.hasNext()) {
                Object tempobj = new Object();
                try {
                    tempobj = jsparser.parse(it2.next().toString());
                } catch (ParseException ex) {
                    Logger.getLogger(ReadIndex.class.getName()).log(Level.SEVERE, null, ex);
                }
                JSONObject tempjs;
                tempjs = (JSONObject) (Object) tempobj;
                //System.out.print(tempjs.get("album"));
                if (tempjs.get("album").equals(album) && (tempjs.get("artist").equals(artist) || artist.equals("All"))) {
                    finalal.add(new SongListModel((tempjs.get("id3song").equals("")) ? (tempjs.get("filename").toString()) : (tempjs.get("id3song")).toString(), tempjs.get("artist").toString() + File.separator + tempjs.get("album").toString() + File.separator + tempjs.get("filename").toString()));
                }
                //it2.remove(); // avoids a ConcurrentModificationException
            }
            //it.remove(); // avoids a ConcurrentModificationException
        }
        albumfilter = sortarraylistmodel(finalal);
        return albumfilter;
    }

    public DefaultListModel getartistsongs(String _artist) {
        artist = _artist;
        DefaultListModel artistsongs;
        ArrayList finalal = new ArrayList();
        ArrayList tempal;
        tempal = (ArrayList) topdir.get(artist);
        Iterator it = tempal.iterator();
        while (it.hasNext()) {
            Object tempobj = new Object();
            try {
                tempobj = jsparser.parse(it.next().toString());
            } catch (ParseException ex) {
                Logger.getLogger(ReadIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
            JSONObject tempjs;
            tempjs = (JSONObject) (Object) tempobj;
            //System.out.print(tempjs.get("filename"));
            finalal.add(new SongListModel((tempjs.get("id3song").equals("")) ? (tempjs.get("filename").toString()) : (tempjs.get("id3song")).toString(), tempjs.get("artist").toString() + File.separator + tempjs.get("album") + File.separator + tempjs.get("filename").toString()));
            //it.remove(); // avoids a ConcurrentModificationException
        }
        artistsongs = sortarraylistmodel(finalal);
        return artistsongs;
        // return albums from artists
    }

    public int countFiles() {
        //parsetohashmap();
        ArrayList songs_array = genSongsSizeArray();
        if (songs_array != null) {
            return songs_array.size();
        } else {
            return -1;
        }
    }

    public String getTotalFileSize() {
        long total = 0;
        // parsetohashmap();
        ArrayList sizes_array = genSongsSizeArray();

        for (int i = 0; i < sizes_array.size(); i++) {
            String size = sizes_array.get(i).toString();
            //System.out.println(size);
            total += Integer.parseInt(size);
        }
        String formattedtotal = humanReadableByteCount(total, false);
        return formattedtotal;
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
