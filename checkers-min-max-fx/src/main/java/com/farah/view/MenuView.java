package com.farah.view;

import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.util.Arrays;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * MenuView class extends the MenuBar layout to add the following user menus items
 * Difficulty - Set the game difficulty which changes the maxDepth used in the minimax algorithm
 * Help - Toggle whether to show available user moves
 * About - Game rules, and additional implementation information
 */
public class MenuView extends MenuBar {

    private final CheckMenuItem hintMenuItem;
    private final RadioMenuItem easyMode;
    private final RadioMenuItem mediumMode;
    private final RadioMenuItem hardMode;

    public MenuView() {
        this.hintMenuItem = new CheckMenuItem("Show Hints");

        this.easyMode = new RadioMenuItem("Easy");
        this.mediumMode = new RadioMenuItem("Medium");
        this.hardMode = new RadioMenuItem("Hard");

        getMenus().addAll(createMenus());
    }

    public CheckMenuItem getHintMenuItem() {
        return hintMenuItem;
    }

    public RadioMenuItem getEasyMode() {
        return easyMode;
    }

    public RadioMenuItem getHardMode() {
        return hardMode;
    }

    public RadioMenuItem getMediumMode() {
        return mediumMode;
    }

    public void showCheckerRules(ActionEvent actionEvent){
        var aboutAlert = new Alert(INFORMATION);

        aboutAlert.setHeaderText(null);
        aboutAlert.setTitle("Checker Rules");
        aboutAlert.setContentText(
                "Normal Checker Moves\n\n" +
                "Normal checker pieces can only move/capture diagonally in a forward direction.\n\n" +
                "Kings Checker Moves\n\n" +
                "King checker pieces can move/capture diagonally in both forward/backward directions.\n\n" +
                "Forced Captures\n\n" +
                "Players must capture pieces when they can. If multiple captures are possible the user decides.\n\n" +
                "End Game\n\n" +
                "The game ends/quits if a player cannot make a move/has run out of pieces!\n\nA confirmation message will display before termination.");
        aboutAlert.showAndWait();
    }

    private List<Menu> createMenus(){
        return List.of(
                createDifficultyMenu(getEasyMode(), getMediumMode(), getHardMode()),
                createHelpMenu(getHintMenuItem()),
                createAboutMenu());
    }

    private Menu createAboutMenu(){
        var aboutMenu = new Menu("About");
        var checkerRules = new MenuItem("Checker Rules");

        aboutMenu.getItems().add(checkerRules);

        checkerRules.setOnAction(this::showCheckerRules);

        return aboutMenu;
    }

    private Menu createHelpMenu(MenuItem helpMenuItem){
        var helpMenu = new Menu("Help");

        helpMenu.getItems().add(helpMenuItem);

        return helpMenu;
    }

    private Menu createDifficultyMenu(RadioMenuItem... radioMenuItems){
        var difficultyMenu = new Menu("Difficulty");
        var toggleGroup = new ToggleGroup();

        Arrays.stream(radioMenuItems).forEach(radioMenuItem -> radioMenuItem.setToggleGroup(toggleGroup));

        getEasyMode().setSelected(true);

        difficultyMenu.getItems().addAll(radioMenuItems);
        return difficultyMenu;
    }
}
