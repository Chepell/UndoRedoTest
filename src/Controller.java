import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.*;

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
    private boolean insideString;
    private boolean inputInside;

    @FXML
    void initialize() {
        clipboard = Clipboard.getSystemClipboard();
        manager = new EditorStageKeeper(new EditorStage(textArea.getText(), textArea.getCaretPosition()));

        // при нажатии кнопки отмены
        undoBtn.setOnAction(actionEvent -> undo());

        // при нажатии кнопки повтора
        redoBtn.setOnAction(actionEvent -> redo());

        // отчистка истории изменений
        clearBtn.setOnAction(actionEvent -> manager.clear(new EditorStage(textArea.getText(), textArea.getCaretPosition())));

        // замена стандартных горячих клавиш
        textArea.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.Z && keyEvent.isControlDown()) {
                keyEvent.consume();
                if (keyEvent.isShiftDown()) {
                    redo();
                } else {
                    undo();
                }
            }
        });

        // обработка триггеров с клавиатуры
        textArea.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            // SPACE
            if (keyEvent.getCode() == KeyCode.SPACE && !spacePressed) {
                spacePressed = true;
                manager.addStage(new EditorStage(textArea.getText(), textArea.getCaretPosition()));
                // BACK_SPACE
            } else if (keyEvent.getCode() == KeyCode.BACK_SPACE && !backspacePressed) {
                backspacePressed = true;
                manager.addStage(new EditorStage(textArea.getText(), textArea.getCaretPosition()));
                // TAB
            } else if (keyEvent.getCode() == KeyCode.TAB && !tabPressed) {
                tabPressed = true;
                manager.addStage(new EditorStage(textArea.getText(), textArea.getCaretPosition()));
                // ENTER
            } else if (keyEvent.getCode() == KeyCode.ENTER) {
                manager.addStage(new EditorStage(textArea.getText(), textArea.getCaretPosition()));
                // DEL
            } else if (keyEvent.getCode() == KeyCode.DELETE && !delPressed) {
                delPressed = true;
                manager.addStage(new EditorStage(textArea.getText(), textArea.getCaretPosition()));
                // Ctrl+V
            } else if (keyEvent.getCode() == KeyCode.V && keyEvent.isControlDown()) {
                manager.addStage(new EditorStage(textArea.getText(), textArea.getCaretPosition()));

//                keyEvent.consume();
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
                manager.addStage(new EditorStage(textArea.getText(), textArea.getCaretPosition()));

//                keyEvent.consume();
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

                // фиксирую вход внутрь строки
            } else if (textArea.getCaretPosition() != textArea.getText().length() && !insideString
                    && !keyEvent.isControlDown()) {
                insideString = true;
                // ввожу символы внутри строки
            } else if (insideString && !inputInside && keyEvent.getText().matches(".")
                    && !keyEvent.isControlDown()) {
                inputInside = true;
                manager.addStage(new EditorStage(textArea.getText(), textArea.getCaretPosition()));
            }


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
                    manager.addStage(new EditorStage(textArea.getText(), textArea.getCaretPosition()));
                }
            }
            refreshIfOutside();
        });

        // обработка триггеров с мыши
        textArea.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY
                    && textArea.getCaretPosition() != textArea.getText().length() && !insideString) {
                insideString = true;
            }
            refreshIfOutside();
        });
    }

    private void refreshIfOutside() {
        if (textArea.getCaretPosition() == textArea.getText().length()) {
            if (insideString) {
                insideString = false;
            }

            if (inputInside) {
                inputInside = false;
            }
        }
    }

    private void undo() {
        // сохраняю текущее состояние в менеджер
        manager.addStage(new EditorStage(textArea.getText(), textArea.getCaretPosition()));
        // перемещаю указатель по списку изменений влево на одну позицию
        manager.undo();
        refreshTextArea();
    }

    private void redo() {
        // перемещаю указатель по списку изменений вправо на одну позицию
        manager.redo();
        refreshTextArea();
    }

    private void refreshTextArea() {
        // получаю значение указателя
        String value = (String) manager.getCurrent().value();
        int caretPosition = manager.getCurrent().caretPosition();
        // обновляю текст в поле
        textArea.setText(value);
        textArea.positionCaret(caretPosition);
    }
}
