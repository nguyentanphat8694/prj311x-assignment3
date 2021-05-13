package entity;

import dao.ContactDAO;

import java.util.List;
import java.util.Vector;

public class Group {

    private String name;

    public  Group(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }

    @Override
    public String toString() {
        return String.format("%-20s\n",name);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || name == null) return false;
        Group g = (Group)obj;
        return name.equals(((Group) obj).getName());
    }

    //return list of contact by group name
    public List<Contact> contacts() throws  Exception {
        List<Contact> contacts = new ContactDAO().loadContact("data/contact.txt");
        List<Contact> rs = new Vector<>();
        if (this.name.equals("All")){
            for (Contact contact : contacts) rs.add(contact);
        }else{
            for (Contact contact : contacts){
                if (contact.getGroup().equalsIgnoreCase(this.name)) rs.add(contact);
            }
        }
        return rs;
    }
}
