/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ISyncMusicServer;

/**
 *
 * @author michael
 */
public class SongListModel {
    public String name;
    public String relpath;
    public SongListModel(String _name, String _path) {
        this.name = _name;
        this.relpath = _path; 
    }
    @Override
    public String toString() {
        return this.name;
    }
    public String getRelPath() {
        return this.relpath;
    }
}
