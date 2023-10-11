package com.simplecounter;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class counterController implements Initializable {

    private counterTask counter;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label resultTextDisplay;
    @FXML
    private TextField inputField;
    @FXML
    private Button yellowBtn;
    @FXML
    private Tooltip tooltipMinimize;
    @FXML
    private Button redBtn;
    @FXML
    private Tooltip tooltipClose;
    @FXML
    private Button countBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private Rectangle btnRing;
    @FXML
    private HBox inputBox;

    private Timeline timeline;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setting the tooltip's position relative to the UI
        tooltipClose.setOnShowing(event -> positionTooltip(tooltipClose, 220, 16));
        // With default effects enabled: X = 210, Y = 10
        tooltipMinimize.setOnShowing(event -> positionTooltip(tooltipMinimize, 200, 16));
        // With default effects enabled: X = 190, Y = 10
        
        clipProgressBar(15.2, 3);
        initInputFilter();
        initKeyCombinationFilters();
        // initProgressBarAnimation();

        System.out.println();
        System.out.println("\033[0;90m[LOG] [" + getCurrentLocalTime() + "] :\033[95m Application started.\033[0m");
    }

    private void positionTooltip(Tooltip tooltip, double X, double Y) {
        Window window = redBtn.getScene().getWindow();
        double tooltipX = window.getX() + X;
        double tooltipY = window.getY() + Y;
        tooltip.setX(tooltipX);
        tooltip.setY(tooltipY);
    }

    @FXML
    public void handleRedButtonClick() {
        System.out.println("\033[0;90m[LOG] [" + getCurrentLocalTime() + "] :\033[95m Application terminated.\033[0m");
        System.out.println();
        Platform.exit();
    }

    @FXML
    public void handleYellowButtonClick() {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.setIconified(true);
    }
    
    @FXML
    private void handleMousePressedOnTitlebar(MouseEvent event) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.setUserData(new double[] { event.getScreenX(), event.getScreenY() });
    }
    
    @FXML
    private void handleWindowDrag(MouseEvent event) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        double data[] = (double[]) stage.getUserData();
        
        double deltaX = event.getScreenX() - data[0];
        double deltaY = event.getScreenY() - data[1];
        
        stage.setX(stage.getX() + deltaX);
        stage.setY(stage.getY() + deltaY);
        
        stage.setUserData(new double[] { event.getScreenX(), event.getScreenY() });
    }

    @FXML
    private void handleEnterKeyRelease (KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            countButtonClickAction(null);
        }
    }
    
    @FXML
    private void countButtonClickAction(ActionEvent actionEvent) {
        String inputString = inputField.getText();
        if (inputString != null && !inputString.isEmpty()) {
            inputString = inputString.replaceAll(",", "");
            long input = Long.parseLong(inputString);
            invokeCounterTask(input);
        } else {
            feedbackOnError(1);
            System.err.println("\033[0;90m[LOG] [" + getCurrentLocalTime() + "] :\033[0;91m [ERROR] Input is empty. \033[0m");
        }
    }

    private void invokeCounterTask(long input) {
        if (counter != null && counter.getState() == Worker.State.RUNNING) {
            feedbackOnError(1);
            System.err.println("\033[0;90m[LOG] [" + getCurrentLocalTime() + "] :\033[0;91m [ERROR] Task is already running. \033[0m");
            return;
        }

        disableInputField(true);
        
        countBtn.setDisable(true);
        countBtn.setVisible(false);

        cancelBtn.setDisable(false);
        cancelBtn.setVisible(true);
        
        counter = new counterTask(input);
        
        // Display the current counted value
        counter.valueProperty().addListener((ChangeListener<Long>) (observable, oldValue, newValue) -> resultTextDisplay.setText(String.valueOf(newValue)));

        // Updates progress bar
        progressBar.progressProperty().bind(counter.progressProperty());
        // progressBar.progressProperty().addListener((obs, old, val) -> {
        //     if (old.doubleValue() <= 0 && val.doubleValue() > 0) {
        //         timeline.playFromStart();
        //     }
        // });

        // Updates progress percentage
        progressLabel.textProperty().bind(counter.messageProperty());
        
        counter.setOnSucceeded(event -> {
            System.out.println("\033[0;90m[LOG] [" + getCurrentLocalTime() + "] :\033[0;92m Task was completed successfully.\033[0;90m " + 
            "-\033[0m INPUT: \033[0;93m" + input + " \033[0;90m>>\033[0m RESULT: \033[0;94m" + counter.getValue() + "\033[0m");
            resetProperties();
        });
        
        counter.setOnCancelled(event -> {
            System.out.println("\033[0;90m[LOG] [" + getCurrentLocalTime() + "] :\033[0m Task was cancelled by the user.");
            resetProperties();
        });
        
        Thread thread = new Thread(counter);
        thread.setDaemon(true);
        thread.start();
    }

    private void resetProperties() {
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);

        progressLabel.textProperty().unbind();
        progressLabel.setText("0");

        countBtn.setDisable(false);
        countBtn.setVisible(true);

        cancelBtn.setDisable(true);
        cancelBtn.setVisible(false);

        disableInputField(false);
    }
    
    @FXML
    private void cancelButtonClickAction() {
        counter.cancel();
    }

    @FXML
    private void countButtonMouseEntered() {
        if (!countBtn.isPressed() && countBtn.isHover()) {
            btnRing.setStroke(Color.valueOf("fff"));
        }
    }

    @FXML
    private void countButtonMouseExited() {
        if (!countBtn.isPressed()) {
            btnRing.setStroke(Color.valueOf("f5f5f560"));
        }
    }

    @FXML
    private void countButtonMousePressed() {
        if (countBtn.isPressed()) {
            btnRing.setStroke(Color.valueOf("15eb66"));
        }
    }

    @FXML
    private void countButtonMouseReleased() {
        if (countBtn.isHover()) {
            btnRing.setStroke(Color.valueOf("fff"));
        } else if (!countBtn.isPressed()) {
            btnRing.setStroke(Color.valueOf("f5f5f560"));
        }
    }

    @FXML
    private void cancelButtonMouseEntered() {
        if (!cancelBtn.isPressed() && cancelBtn.isHover()) {
            btnRing.setStroke(Color.valueOf("fff"));
        }
    }

    @FXML
    private void cancelButtonMouseExited() {
        if (!cancelBtn.isPressed()) {
            btnRing.setStroke(Color.valueOf("f5f5f560"));
        }
    }

    @FXML
    private void cancelButtonMousePressed() {
        if (cancelBtn.isPressed()) {
            btnRing.setStroke(Color.valueOf("db1616"));
        }
    }

    @FXML
    private void cancelButtonMouseReleased() {
        if (cancelBtn.isHover()) {
            btnRing.setStroke(Color.valueOf("fff"));
        } else if (!cancelBtn.isPressed()) {
            btnRing.setStroke(Color.valueOf("f5f5f560"));
        }
    }

    @FXML
    private void clearInputField() {
        inputField.clear();
    }
    
    private void disableInputField(boolean state) {
        if (state) {
            inputField.setOpacity(0.5);
            inputField.setBlendMode(BlendMode.HARD_LIGHT);
        } else {
            inputField.setOpacity(1);
            inputField.setBlendMode(BlendMode.SRC_OVER);
        }
    }

    private String getCurrentLocalTime() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return currentTime.format(formatter);
    }

    private void clipProgressBar(double radius, double insets) {
        Rectangle clipRect = new Rectangle();
        
        clipRect.widthProperty().bind(progressBar.widthProperty().subtract(insets * 2));
        clipRect.heightProperty().bind(progressBar.heightProperty().subtract(insets * 2));

        clipRect.setArcWidth(radius * 2);
        clipRect.setArcHeight(radius * 2);

        clipRect.setTranslateX(insets);
        clipRect.setTranslateY(insets);

        progressBar.setClip(clipRect);
    }

    private void initInputFilter() {
        // Event filter to allow only numerical input
        inputField.addEventFilter(KeyEvent.ANY, event -> {
            if (counter != null && counter.getState() == Worker.State.RUNNING) {
                if ((event.getCode() != KeyCode.ESCAPE) && (event.getCode() != KeyCode.ENTER)) {
                    event.consume();
                }
            } else if (event.getCharacter().matches("[\\s&&[^\\n\\r]]|[a-zA-Z\\p{Punct}]")) {
                event.consume(); // Suppress the input event
                feedbackOnError(2);
            }
        });
    }

    private void initKeyCombinationFilters() {
        anchorPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isShortcutDown() && (event.getCode() == KeyCode.V || event.getCode() == KeyCode.X)) {
                event.consume(); // Prevent Paste (Ctrl+V) & Cut (Ctrl+X) actions
                feedbackOnError(1);
            }
            else if (event.isAltDown() && event.getCode() == KeyCode.F4) {
                event.consume();
                handleRedButtonClick(); // Application's Exit (Alt+F4) action
            }
        });
    }

    private void feedbackOnError(int color) {
        // It's the default border color assigned for "#inputBox" in the CSS file.
        // I can't find an easy way to fetch this value dynamically yet, so doing it manually :(
        final String defaultColor = "linear-gradient(from 0% 0% to 100% 100%, #ffffff50, transparent)";
        String borderColor;
        double duration;

        switch (color) {
            case 1: // RED
                borderColor = "#db1616";
                duration = 0.8;
                break;
                
            case 2: // YELLOW
                borderColor = "#dbbd16";
                duration = 0.5;
                break;
        
            default:
                return;
        }

        inputBox.setStyle("-fx-border-color: " + borderColor + ";");

        PauseTransition delay = new PauseTransition(Duration.seconds(duration));
        delay.setOnFinished(event -> {
            inputBox.setStyle("-fx-border-color: " + defaultColor + ";");
        });
        delay.play(); // Revert back after the delay
    }



    // private void initProgressBarAnimation() {
    //     ObjectProperty<Node> node = new SimpleObjectProperty<>();
    //     ProgressBar bar = new ProgressBar(0) {
    //         @Override
    //         protected void layoutChildren() {
    //             super.layoutChildren();
    //             if (node.get() == null) {
    //                 Node n = lookup(".bar");
    //                 node.set(n);
    //                 int stripWidth = 15;
    //                 IntegerProperty x = new SimpleIntegerProperty(0);
    //                 IntegerProperty y = new SimpleIntegerProperty(stripWidth);
    //                 timeline = new Timeline(new KeyFrame(Duration.millis(35), (e) -> {
    //                     x.set(x.get() + 1);
    //                     y.set(y.get() + 1);                        
    //                     String style = "-fx-background-color: linear-gradient(from " + x.get() + "px " + x.get() + "px to " + y.get() + "px " + y.get() + "px, repeat, -fx-accent 50%, derive(-fx-accent, 30%) 50%);";
    //                     n.setStyle(style);
    //                     if (x.get() >= stripWidth * 2) {
    //                         x.set(0);
    //                         y.set(stripWidth);
    //                     }
    //                 }));
    //                 timeline.setCycleCount(Animation.INDEFINITE);
    //             }
    //         }
    //     };

    //     // progress bar color property
    //     String barColor = ("#558001");
    //     bar.setStyle("-fx-accent: " + barColor + ";");
    // }
}
