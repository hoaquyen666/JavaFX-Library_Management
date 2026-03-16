package com.example.seniormanager.controller;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.action.Action;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable
{
    @FXML
    private StackPane mainContentArea;

    @FXML
    private VBox sidebar;

    private javafx.scene.control.Button currentActiveButton;


    // Chiều rộng khi thu gọn (chỉ hiện icon)
    private final double COLLAPSED_WIDTH = 70.0;
    // Chiều rộng khi mở rộng (hiện cả text)
    private final double EXPANDED_WIDTH = 250.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Đảm bảo sidebar bắt đầu ở trạng thái thu gọn
        if (sidebar != null) {
            sidebar.setPrefWidth(COLLAPSED_WIDTH);

            //Khi chuột di chuyển vào Sidebar
            sidebar.setOnMouseEntered(event -> openSidebar());

            //Khi chuột rời khỏi Sidebar
            sidebar.setOnMouseExited(event -> closeSidebar());
        }
    }

    private void openSidebar() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(0.2),
                        new javafx.animation.KeyValue(sidebar.prefWidthProperty(), EXPANDED_WIDTH))
        );
        timeline.play();
    }

    private void closeSidebar() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(0.2),
                        new javafx.animation.KeyValue(sidebar.prefWidthProperty(), COLLAPSED_WIDTH))
        );
        timeline.play();
    }


    @FXML
    void handleAccount(ActionEvent event) {
        setActive(event);
        loadGiaoDienCon("/com/example/seniormanager/staff-management/account-view/Account.fxml");
    }

    @FXML
    void handleStaff(ActionEvent event){
        setActive(event);
        loadGiaoDienCon("/com/example/seniormanager/staff-management/staff-view/staff-main-view.fxml");
    }

    @FXML
    void handleShift(ActionEvent event){
        setActive(event);
        loadGiaoDienCon("/com/example/seniormanager/staff-management/shift-view/shift.fxml");
    }

    @FXML
    void addAccount(ActionEvent event){

    }

    private void loadGiaoDienCon(String fileName){
        try{
            URL resourceUrl = getClass().getResource(fileName);
            if (resourceUrl == null) {
                System.err.println("Lỗi đường dẫn: KHÔNG TÌM THẤY FILE " + fileName);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent subView = loader.load();

            mainContentArea.getChildren().clear();
            mainContentArea.getChildren().add(subView);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), subView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            TranslateTransition slideUp = new TranslateTransition(Duration.millis(350), subView);
            slideUp.setFromY(30);
            slideUp.setToY(0);

            ParallelTransition transition = new ParallelTransition(fadeIn, slideUp);
            transition.play();
        }catch (IOException e){
            System.err.println("Lỗi khi đọc file FXML: " + e.getMessage());
            e.printStackTrace();
        }catch (NullPointerException e){
            System.err.println("Lỗi đường dẫn NullPointerException");
            e.printStackTrace();
        }
    }

    private void setActive(ActionEvent event) {
        if (event != null && event.getSource() instanceof javafx.scene.control.Button) {
            javafx.scene.control.Button clickedBtn = (javafx.scene.control.Button) event.getSource();
            if (currentActiveButton != null) {
                currentActiveButton.getStyleClass().remove("active-nav");
            }
            if (!clickedBtn.getStyleClass().contains("active-nav")) {
                clickedBtn.getStyleClass().add("active-nav");
            }
            currentActiveButton = clickedBtn;
        }
    }
}
