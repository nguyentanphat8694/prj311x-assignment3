package controller;

import dao.GroupDAO;
import entity.Contact;
import entity.Group;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateContactController {
    @FXML
    TextField iptFirstName, iptLastName, iptPhone, iptEmail;
    @FXML
    DatePicker dpDOB;
    @FXML
    ComboBox<Group> cbGroup;
    @FXML
    Button btnSave, btnClose;

    private ContactController contactController;
    private List<Contact> contacts;
    private final String CONTACT_PATH = "data/contact.txt";
    private final String GROUP_PATH = "data/group.txt";
    private Contact updatedContact;

    public void  setContactController(ContactController contactController) {
        this.contactController = contactController;
    }

    public  void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public  void setUpdatedContact(Contact updatedContact) throws  Exception{
        this.updatedContact = updatedContact;
        iptFirstName.setText(updatedContact.getFirstName());
        iptLastName.setText(updatedContact.getLastName());
        iptPhone.setText(updatedContact.getPhone());
        iptEmail.setText(updatedContact.getEmail());
        Date date = new SimpleDateFormat("MM-dd-yyyy").parse(updatedContact.getDob());
        dpDOB.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        cbGroup.getItems().clear();
        for (Group g : new GroupDAO().loadGroup(GROUP_PATH)) cbGroup.getItems().add(g);
        cbGroup.getSelectionModel().select(new Group(updatedContact.getGroup()));
    }


    @FXML
    void initialize()throws  Exception {
        iptFirstName.setText("");
        iptLastName.setText("");
        iptPhone.setText("");
        iptEmail.setText("");
        dpDOB.setValue(LocalDate.now());
    }

    public  void onSaveContact(ActionEvent evt) throws  Exception {
        String firstName = iptFirstName.getText().trim();
        if (firstName.isEmpty()){
            iptFirstName.setText("First name cannot be empty");
            return;
        }
        iptFirstName.setText("");
        String lastName = iptLastName.getText().trim();
        if (lastName.isEmpty()){
            iptLastName.setText("Last name cannot be empty");
            return;
        }
        iptLastName.setText("");
        String phone = iptPhone.getText().trim();
        if (phone.isEmpty() || !phone.matches("\\d+")){
            iptFirstName.setText("Phone contact digits only");
            return;
        }
        iptPhone.setText("");
        String email = iptEmail.getText().trim();
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mtch = emailPattern.matcher(email);
        if (!mtch.matches()){
            iptEmail.setText("Email is invalid");
            return;
        }
        iptEmail.setText("");
        String birthDate = dpDOB.getValue().toString();
        String group = cbGroup.getSelectionModel().getSelectedItem().getName();
        Contact c = new Contact(firstName, lastName, phone, email, birthDate, group);
        int i = contactController.contactDAO.indexOf(contacts, updatedContact);
        int j = contactController.contactDAO.indexOf(contacts, c);
        if (j == -1 || j == i){
            contactController.contactDAO.updateContact(contacts, c, i);
            contactController.showContact(contacts);
            contactController.contactDAO.saveToFile(contacts, CONTACT_PATH);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Contact has been updated");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("Information of contact is existed");
            alert.showAndWait();
        }
    }

    public  void onClose(ActionEvent evt) throws  Exception {
        final Node source = (Node) evt.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
