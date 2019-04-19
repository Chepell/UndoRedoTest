import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;

public class Controller {
    @FXML
    private TextArea textArea;

    @FXML
    private Button undoBtn;

    @FXML
    private Button redoBtn;

    @FXML
    private Button clearBtn;

    private EditorStageKeeper manager;

    private Clipboard clipboard;

    private boolean spacePressed;
    private boolean backspacePressed;
    private boolean tabPressed;
    private boolean delPressed;

    @FXML
    void initialize() {
        clipboard = Clipboard.getSystemClipboard();
        manager = new EditorStageKeeper(new EditorStage(textArea.getText()));
//        manager.addStage(new EditorStage("1"));
//        manager.addStage(new EditorStage("2"));
//        manager.addStage(new EditorStage("3"));
//        manager.addStage(new EditorStage("4"));
//        manager.undo();
//        manager.undo();
//        manager.addStage(new EditorStage("5"));
//
//        textArea.setText(manager.getCurrent().value().toString());

//        textArea.textProperty().addListener(stringChangeListener());


        // при нажатии кнопки отмены
        undoBtn.setOnAction(actionEvent -> undo());

        // при нажатии кнопки повтора
        redoBtn.setOnAction(actionEvent -> redo());

        // отчистка истории изменений
        clearBtn.setOnAction(actionEvent -> manager.clear());

        // замена стандартных горячих клавиш
        textArea.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.Z && keyEvent.isControlDown()) {
                keyEvent.consume();
                if (keyEvent.isShiftDown()) {
                    redo();
                } else {
                    undo();
                }
                textArea.positionCaret(textArea.getLength());
            }
        });

        // обработка триггеров
        textArea.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            // SPACE
            if (keyEvent.getCode() == KeyCode.SPACE && !spacePressed) {
                spacePressed = true;
                manager.addStage(new EditorStage(textArea.getText()));
                // BACK_SPACE
            } else if (keyEvent.getCode() == KeyCode.BACK_SPACE && !backspacePressed) {
                backspacePressed = true;
                manager.addStage(new EditorStage(textArea.getText()));
                // TAB
            } else if (keyEvent.getCode() == KeyCode.TAB && !tabPressed) {
                tabPressed = true;
                manager.addStage(new EditorStage(textArea.getText()));
                // ENTER
            } else if (keyEvent.getCode() == KeyCode.ENTER) {
                manager.addStage(new EditorStage(textArea.getText()));
                // DEL
            } else if (keyEvent.getCode() == KeyCode.DELETE && !delPressed) {
                delPressed = true;
                manager.addStage(new EditorStage(textArea.getText()));
                // Ctrl+V
            } else if (keyEvent.getCode() == KeyCode.V && keyEvent.isControlDown()) {
                manager.addStage(new EditorStage(textArea.getText()));

//                keyEvent.consume();
//                // если буффер не пустой
//                if (clipboard.hasString()) {
//                    manager.addStage(new EditorStage(textArea.getText()));
//
//                    var text = textArea.getText();
//                    var caretPosition = textArea.getCaretPosition();
//                    var clipboardString = clipboard.getString();
//                    var newText = text.substring(0, caretPosition) + clipboardString + text.substring(caretPosition);
//                    var newCaretPosition = caretPosition + clipboardString.length();
//
//                    textArea.setText(newText);
//                    textArea.positionCaret(newCaretPosition);
//                }
                // Ctrl+X
            } else if (keyEvent.getCode() == KeyCode.X && keyEvent.isControlDown()) {
//                keyEvent.consume();
                manager.addStage(new EditorStage(textArea.getText()));

//                System.out.println("Украл Ctrl+X");
//                if (clipboard.hasString()) {
//                    manager.addStage(new EditorStage(textArea.getText()));
//
//                    var text = textArea.getText();
//                    var caretPosition = textArea.getCaretPosition();
//                    var clipboardString = clipboard.getString();
//                    var newText = text.substring(0, caretPosition) + clipboardString + text.substring(caretPosition);
//                    var newCaretPosition = caretPosition + clipboardString.length();
//
//                    textArea.setText(newText);
//                    textArea.positionCaret(newCaretPosition);
//                }
                //
            } else if (textArea.getCaretPosition() != textArea.getText().length() && keyEvent.getCode() == KeyCode.A) {
                System.out.println("Где-то в середине");
            }

//            else if (keyEvent.getCode() != KeyCode.SPACE && keyEvent.getCode() != KeyCode.BACK_SPACE && backspacePressed) {
//                manager.addStage(new EditorStage(textArea.getText()));
//            }

            if (keyEvent.getCode() != KeyCode.SPACE) {
                if (spacePressed) {
                    spacePressed = false;
                }
            }

            if (keyEvent.getCode() != KeyCode.TAB) {
                if (tabPressed) {
                    tabPressed = false;
                }
            }

            if (keyEvent.getCode() != KeyCode.BACK_SPACE) {
                if (backspacePressed) {
                    backspacePressed = false;
                    manager.addStage(new EditorStage(textArea.getText()));
                }
            }
        });
    }

    private void undo() {
//        textArea.textProperty().removeListener(stringChangeListener());
        // сохраняю текущее состояние в менеджер
        manager.addStage(new EditorStage(textArea.getText()));
        // перемещаю указатель по списку изменений влево на одну позицию
        manager.undo();
        // получаю значение указателя
        String value = manager.getCurrent().value().toString();
        // обновляю текст в поле
        textArea.setText(value);
//        showChangeList();

//        textArea.textProperty().addListener(stringChangeListener());
    }

    private void redo() {
        // перемещаю указатель по списку изменений вправо на одну позицию
        manager.redo();
        // получаю значение указателя
        String value = (String) manager.getCurrent().value();
        // обновляю текст в поле
        textArea.setText(value);
    }

    private ChangeListener<String> stringChangeListener() {
        return (observableValue, oldValue, newValue) -> {

            boolean stable00 = newValue.startsWith(oldValue) && newValue.length() > oldValue.length();

            // длина строки не поменялась, но какие-то символы внутри строки изменились
//            boolean changed00 = !newValue.equals(oldValue) && newValue.length() == oldValue.length();
//
//            // длина строки уменьшилась справа и длина строки больше чем длина текущего состояния в менеджере
//            boolean changed01 = oldValue.startsWith(newValue) && newValue.length() < oldValue.length()
//                    && oldValue.length() > ((String) manager.getCurrent().value()).length();
//
//            // длина строки увеличилась справа и длина строки меньше чем длина текущего состояния в менеджере
//            boolean changed02 = newValue.startsWith(oldValue) && newValue.length() > oldValue.length()
//                    && newValue.length() < ((String) manager.getCurrent().value()).length();
//
//            // длина строки уменьшилась на какое-то количество символов, но не справа, а в середине или в начале
//            boolean changed03 = !oldValue.startsWith(newValue) && newValue.length() < oldValue.length();
//
//            // длина строки увеличилась на какое-то количество символов, но не справа, а в середине или в начале
//            boolean changed04 = !newValue.startsWith(oldValue) && newValue.length() > oldValue.length();


//            if (stable00) {
//                manager.refreshChangeable(new EditorStage(newValue));
//            } else {
//                manager.addStage(new EditorStage(newValue));
//            }

//            if (changed00 || changed01 || changed02 || changed03 || changed04) {
//                manager.addStage(new EditorStage(newValue));
//            } else {
//                manager.refreshChangeable(new EditorStage(newValue));
//            }

            manager.addStage(new EditorStage(newValue));
//            showChangeList();
        };
    }

    private void showChangeList() {
        System.out.println("*Текущий*список*");
        List<Changeable<String>> changeableList = manager.getChangeableList();
        for (Changeable<String> stringChangeable : changeableList) {
            System.out.println(stringChangeable.value());
        }
        System.out.println("****************");
    }
}
