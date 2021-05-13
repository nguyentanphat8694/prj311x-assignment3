package dao;

import entity.Contact;
import entity.Group;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Locale;
import java.util.Vector;


public class ContactDAO {

    //load all Contacts from the file Contact in to a list
    public List<Contact> loadContact(String fname) throws Exception {
        List<Contact> c = new Vector<>();
        LineNumberReader lnr = new LineNumberReader(new FileReader(fname));
        String line = "";
        while ((line = lnr.readLine()) != null){
            line = line.trim();
            if (!line.isEmpty()){
                String[] s = line.split(":");
                c.add(new Contact(s[0].trim(), s[1].trim(), s[2].trim(), s[3].trim(), s[4].trim(), s[5].trim()));
            }
        }
        lnr.close();
        return c;
    }

    //save all Contacts from a given list to a text file
    public  void saveToFile(List<Contact> g, String fname) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fname));
        for (Contact c : g) bw.write(c.toString());
        bw.close();
    }

    //return the first position of a given contact g in the list
    //otherwise return -1
    public int indexOf(List<Contact> list, Contact g) {
        for (int i = 0; i < list.size(); i++){
            Contact c = list.get(i);
            if (c.getFirstName().equalsIgnoreCase(g.getFirstName()) && c.getLastName().equalsIgnoreCase(g.getLastName())) return i;
        }
        return -1;
    }
    //save a Contact to a current list
    public  void saveToList(List<Contact> list, Contact g) {
        list.add(g);
    }
    //update information of a contact c at position i in the list
    public  void updateContact(List<Contact> list, Contact c, int i) {
        Contact contact = list.get(i);
        contact.setDob(c.getDob());
        contact.setEmail(c.getEmail());
        contact.setFirstName(c.getFirstName());
        contact.setLastName(c.getLastName());
        contact.setGroup(c.getGroup());
        contact.setPhone(c.getPhone());
    }
    //return a list of Contact who information matched given search word
    public  List<Contact> search(List<Contact> c, String group, String search) {
        if (group.equalsIgnoreCase("All")) group = "";
        List<Contact> rs = new Vector<>();
        for (Contact contact : c){
            String s = contact.toString().toLowerCase();
            if (s.contains(search.toLowerCase()) && contact.getGroup().contains(group)) rs.add(contact);
        }
        return rs;
    }
    //return a list of Contact who is in a given group
    public  List<Contact> contactByGroup(List<Contact> c, String group) {
        if (group.equalsIgnoreCase("All")) group = "";
        List<Contact> rs = new Vector<>();
        for (Contact contact : c){
            String s = contact.getGroup().toLowerCase();
            if (s.contains(group.toLowerCase())) rs.add(contact);
        }
        return rs;
    }
}
