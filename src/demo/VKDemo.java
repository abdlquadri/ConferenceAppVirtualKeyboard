/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import com.sun.javafx.tk.TKSceneListener;
import com.sun.javafx.tk.quantum.GlassScene;
import java.lang.reflect.Field;
import com.javafx.scheduleapp.control.VirtualKeyboard;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author abdlquadri
 */
public class VKDemo extends Application {

    private Animation keyboardSlideAnimation;
    private VirtualKeyboard keyboard;
    private Scene scene;
    private TextField txtField;
    private boolean IS_VK_DISABLED;

    @Override
    public void start(Stage primaryStage) {
        keyboard = new VirtualKeyboard();
      txtField = new TextField();
        txtField.setText("Hello Virtual Keyboard.");

        StackPane root = new StackPane() {
            @Override protected void layoutChildren() {
                final double w = getWidth();
                final double h = getHeight();
                super.layoutChildren();
                keyboard.resizeRelocate(0, h, w, w * (3.0/11.0));
//                VirtualKeyboardSkin skin = (VirtualKeyboardSkin) keyboard.getSkin();
//                skin.layoutChildren(0, h, w, w * (3.0/11.0));
                txtField.setMaxWidth(500);
                txtField.setLayoutX(300);
                txtField.setLayoutY(9);
            }
        };
        root.getChildren().addAll(txtField,keyboard);
        scene = new Scene(root, 900, 550);
           if (!IS_VK_DISABLED) {
            keyboard.setOnAction(new EventHandler<KeyEvent>() {
                private Object sceneListener;
                @Override public void handle(KeyEvent event) {
                    if (sceneListener == null) {
                        try {
                            GlassScene peer = (GlassScene) scene.impl_getPeer();
                            Field f = GlassScene.class.getDeclaredField("sceneListener");
                            f.setAccessible(true);
                            sceneListener = (TKSceneListener) f.get(peer);
                        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                        }
                    }

                    // TODO not sure how to implement
//                    sceneListener.keyEvent(
//                            (EventType<KeyEvent>)event.getEventType(),
//                            event.getCode().impl_getCode(),
//                            event.getCharacter().toCharArray(),
//                            event.isShiftDown(), false, false, false);
                }
            });
        scene.focusOwnerProperty().addListener(new ChangeListener<Node>() {
            @Override
            public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
                System.out.println("Focused!!!!!!");
                boolean isTextOwner = false;
                Parent parent = newValue instanceof Parent ? (Parent) newValue : null;
                while (parent != null) {
                    if (parent instanceof TextInputControl) {
                        isTextOwner = true;
                        break;
                    }
                    parent = parent.getParent();
                }

                if (isTextOwner && keyboard.getTranslateY() == 0) {
                    // The focus on a text input control and therefore we must show the keyboard
                System.out.println("Focused Text!!!!!!");
                    if (keyboardSlideAnimation != null) {
                        keyboardSlideAnimation.stop();
                    }
                    TranslateTransition tx = new TranslateTransition(Duration.seconds(.4), keyboard);
                    tx.setToY(-(scene.getWidth() * (3.0 / 11.0)));
                    keyboardSlideAnimation = tx;
                    keyboardSlideAnimation.play();
                } else if (!isTextOwner && keyboard.getTranslateY() != 0) {
                    if (keyboardSlideAnimation != null) {
                        keyboardSlideAnimation.stop();
                    }
                    TranslateTransition tx = new TranslateTransition(Duration.seconds(.4), keyboard);
                    tx.setToY(0);
                    keyboardSlideAnimation = tx;
                    keyboardSlideAnimation.play();
                }

                if (newValue != null) {
                    VirtualKeyboard.Type type = (VirtualKeyboard.Type) newValue.getProperties().get("vkType");
                    keyboard.setType(type == null ? VirtualKeyboard.Type.TEXT : type);
                }
            }
        });
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }}

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
