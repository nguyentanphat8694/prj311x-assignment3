package dao;


import controller.ContactController;
import entity.Contact;
import entity.Group;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Locale;
import java.util.Vector;


public class GroupDAO {

    //load all groups from the file group in to a list
    public List<Group> loadGroup(String fname) throws Exception {
        List<Group> group = new Vector<>();
        LineNumberReader lnr = new LineNumberReader(new FileReader(fname));
        String line = "";
        while ((line = lnr.readLine()) != null){
            line = line.trim();
            if (!line.isEmpty()) group.add(new Group(line));
        }
        lnr.close();
        return group;
    }

    //save all groups from a given list to a text file
    public  void saveGroupToFile(List<Group> g, String fname) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fname));
        for (Group group : g) bw.write(group.toString());
        bw.close();
    }

    //return the first position of a given contact group in the list
    //otherwise return -1
    public int indexOf(List<Group> list, Group g) {
        for (int i = 0; i < list.size(); i++){
            Group group = list.get(i);
            if (group.getName().equalsIgnoreCase(g.getName())) return i;
        }
        return -1;
    }
    //save a group to a current list
    public  void saveGroupToList(List<Group> list, Group g) {
        list.add(g);
    }

    //return a list of Contact who information matched given search word
    public  List<Group> search(List<Group> c, String search) {
        List<Group> rs = new Vector<>();
        for (Group group : c){
            String s = group.toString().toLowerCase();
            if (s.contains(search.toLowerCase())) rs.add(group);
        }
        return rs;
    }

    //update a group in groups by a newGroup
    public  boolean updateGroup(List<Group> groups, int i, String oldGroup, String newGroup, ContactController contactController) {
        groups.get(i).setName(newGroup);
        int c = 0;
        for (Group g : groups){
            if (g.getName().equalsIgnoreCase(newGroup)) c++;
        }
        if (c >= 2){
            groups.get(i).setName(oldGroup);
            return false;
        }
        for (Contact contact : contactController.getContacts()){
            if (contact.getGroup().equalsIgnoreCase(oldGroup)) contact.setGroup(newGroup);
        }
        contactController.showContact(contactController.getContacts());
        return true;
    }
}
