package controller;

import com.sun.webkit.dom.NamedNodeMapImpl;
import com.sun.xml.internal.bind.v2.runtime.property.PropertyFactory;
import dao.ContactDAO;
import dao.GroupDAO;
import entity.Contact;
import entity.Group;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;
import java.util.Optional;


public class ContactController {
    @FXML
    private ComboBox<Group> cbGroup;
    @FXML
    private TextField iptSearch;
    @FXML
    private TableView tbContact;
    @FXML
    private TableColumn<String, Contact> clFirstName, clLastName, clPhone, clGroupName, clEmail, clBirthDate;

    private List<Contact> contacts;
    private final String CONTACT_PATH = "data/contact.txt";
    private final String GROUP_PATH = "data/group.txt";
    public ContactDAO contactDAO;

    @FXML
    void initialize() throws Exception {
        contactDAO = new ContactDAO();
        contacts = contactDAO.loadContact(CONTACT_PATH);
        initTable();
        showGroup(new GroupDAO().loadGroup(GROUP_PATH));
        showContact(contacts);
        tbContact.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public List<Contact> getContacts(){
        return this.contacts;
    }

    void initTable(){
        clFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        clLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        clPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        clEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        clBirthDate.setCellValueFactory(new PropertyValueFactory<>("dob"));
        clGroupName.setCellValueFactory(new PropertyValueFactory<>("group"));
    }

    //output all contact to tblContact
    public  void showContact(List<Contact> c) {
        tbContact.getItems().clear();
        String group = cbGroup.getSelectionModel().getSelectedItem().getName();
        if (group.equals("All")){
            for (Contact contact : c) tbContact.getItems().add(contact);
        }else{
            for (Contact contact : c){
                if (contact.getGroup().equalsIgnoreCase(group)) tbContact.getItems().add(contact);
            }
        }
    }

    //output all groups to dropdownlist
    public  void showGroup(List<Group> g){
        cbGroup.getItems().clear();
        for (Group group : g) cbGroup.getItems().add(group);
        cbGroup.getSelectionModel().select(0);
    }

    public void onGroupChange(ActionEvent e){
        showContact(contacts);
    }

    //do corresponding actions for search, delete, update and add contact
    public void onSearchContact(ActionEvent evt){
        String group = cbGroup.getSelectionModel().getSelectedItem().getName();
        List<Contact> c = contactDAO.search(contacts, group, iptSearch.getText());
        showContact(c);
    }

    public void onAddContact(ActionEvent evt) throws Exception{
        FXMLLoader loader = loadUI("addContact", "Add a new Contact");
        AddContactController addController = loader.getController();
        addController.setContacts(contacts);
        addController.setContactController(this);
    }

    public void onDeleteContact(ActionEvent evt) throws Exception{
        int i = tbContact.getSelectionModel().getSelectedIndex();
        if (i >= tbContact.getItems().size() || i <0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("Select a Contact to Delete");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Do you want to delete selected contact?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                contacts.remove(i);
                showContact(contacts);
                contactDAO.saveToFile(contacts, CONTACT_PATH);
            }
        }
    }

    public void onUpdateContact(ActionEvent evt) throws Exception{
        int i = tbContact.getSelectionModel().getSelectedIndex();
        if (i >= tbContact.getItems().size() || i <0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("Select a Contact to Update");
            alert.showAndWait();
        }else{
            FXMLLoader loader = loadUI("updateContact", "Update a Contact");
            UpdateContactController updateController = loader.getController();
            updateController.setContacts(contacts);
            updateController.setContactController(this);
            updateController.setUpdatedContact((Contact) tbContact.getItems().get(i));
        }
    }

    //manage the groups
    public void onManageGroup(ActionEvent evt) throws Exception{
        FXMLLoader loader = loadUI("group", "Group a Manager");
        GroupController groupController = loader.getController();
        groupController.setContactController(this);
    }

    public FXMLLoader loadUI(String fxmlName, String title) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../ui/" + fxmlName + ".fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
        return loader;
    }
}
