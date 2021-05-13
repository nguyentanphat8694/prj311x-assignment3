package controller;


import dao.GroupDAO;
import entity.Group;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sun.security.acl.GroupImpl;

import java.util.List;
import java.util.Optional;


public class GroupController {
    @FXML
    TextField iptGroupSearch, iptGroupName;
    @FXML
    ListView<Group> lstGroup;

    private ContactController contactController;
    private final String CONTACT_PATH = "data/contact.txt";
    private final String GROUP_PATH = "data/group.txt";
    private List<Group> groups;
    private GroupDAO groupDAO;

    public void setContactController(ContactController contactController) {
        this.contactController = contactController;
    }

    @FXML
    void initialize() throws Exception {
        groupDAO = new GroupDAO();
        groups = groupDAO.loadGroup(GROUP_PATH);
        showGroup(groups);
        lstGroup.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onGroupSelect());
    }

    void showGroup(List<Group> g){
        lstGroup.getItems().clear();
        for (Group group : g ) lstGroup.getItems().add(group);
    }

    void onGroupSelect(){
        if (lstGroup.getSelectionModel().getSelectedItem() != null){
            iptGroupName.setText(lstGroup.getSelectionModel().getSelectedItem().getName());
        }
    }

    public  void onSearch(ActionEvent e){
        List<Group> g = groupDAO.search(groups, iptGroupSearch.getText());
        showGroup(g);
    }

    public  void onSave(ActionEvent e) throws Exception{
        String name = iptGroupName.getText().trim();
        if (name.isEmpty() || name.equals("")){
            showDialog("ERROR", "Group name cannot be empty", Alert.AlertType.ERROR);
        }else{
            Group g = new Group(name);
            int i = groupDAO.indexOf(groups, g);
            if (i != -1){
                showDialog("ERROR", "Group name exists already, choose another name", Alert.AlertType.ERROR);
            }else{
                groupDAO.saveGroupToList(groups, g);
                groupDAO.saveGroupToFile(groups, GROUP_PATH);
                showGroup(groups);
                contactController.showGroup(groups);
                showDialog("Information", "A new group has been added", Alert.AlertType.INFORMATION);
            }
        }
    }

    public  void onUpdate(ActionEvent e) throws Exception{
        int i = lstGroup.getSelectionModel().getSelectedIndex();
        if (i >= lstGroup.getItems().size() || i < 0){
            showDialog("Information", "Select a Group to update", Alert.AlertType.ERROR);
        }else{
            String oldGroup = lstGroup.getItems().get(i).getName();
            String newGroup = iptGroupName.getText().trim();
            if (groupDAO.updateGroup(groups, i, oldGroup, newGroup, contactController) == false){
                showDialog("Information", "Please select another name for group", Alert.AlertType.ERROR);
            }else{
                contactController.showGroup(groups);
                showGroup(groups);
                groupDAO.saveGroupToFile(groups, GROUP_PATH);
                showDialog("Information", "A Group has been updated", Alert.AlertType.INFORMATION);
            }
        }
    }

    public  void onDelete(ActionEvent e) throws Exception{
        int i = lstGroup.getSelectionModel().getSelectedIndex();
        if (i >= lstGroup.getItems().size() || i < 0){
            showDialog("Information", "Select a Group to delete", Alert.AlertType.ERROR);
        }
        int size = ((Group) lstGroup.getItems().get(i)).contacts().size();
        if (size > 0) {
            showDialog("ERROR", "Group has some contacts. cannot delete this one", Alert.AlertType.ERROR);
        }else {
            if (showDialog("Confirmation", "Do you want to delete selected group?", Alert.AlertType.CONFIRMATION).get() == ButtonType.OK){
                groups.remove(i);
                showGroup(groups);
                groupDAO.saveGroupToFile(groups, GROUP_PATH);
                contactController.showGroup(groups);
            }
        }
    }

    public  void onClose(ActionEvent e){
        final Node source = (Node) e.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    Optional<ButtonType> showDialog(String title, String content, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}
