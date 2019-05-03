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

    private EditorStageKeeper stageKeeper;

    private Clipboard clipboard;

    private boolean spacePressed;
    private boolean backspacePressed;
    private boolean tabPressed;
    private boolean delPressed;
    private boolean insideString;
    private boolean inputInside;
    private boolean undoPressed = true;

    @FXML
    void initialize() {
        clipboard = Clipboard.getSystemClipboard();
        stageKeeper = new EditorStageKeeper(new EditorStage(textArea.getText(), textArea.getCaretPosition()));

        // при нажатии кнопки undo
        undoBtn.setOnAction(actionEvent -> {
            undo();
            textArea.requestFocus();
        });

        // при нажатии кнопки redo
        redoBtn.setOnAction(actionEvent -> {
            redo();
            textArea.requestFocus();
        });

        // при нажатии кнопки clear
        clearBtn.setOnAction(actionEvent -> {
            textArea.setText("");
            textArea.positionCaret(0);
            stageKeeper.clear(new EditorStage("", 0));
            textArea.requestFocus();
        });

        // замена стандартных горячих клавиш
        textArea.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.Z)) {
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
            if (keyEvent.getCode().equals(KeyCode.SPACE) && !spacePressed) {
                spacePressed = true;
                addCurrentStageToKeeper();
                // BACK_SPACE
            } else if (keyEvent.getCode().equals(KeyCode.BACK_SPACE) && !backspacePressed) {
                backspacePressed = true;
                addCurrentStageToKeeper();
                // TAB
            } else if (keyEvent.getCode().equals(KeyCode.TAB) && !tabPressed) {
                tabPressed = true;
                addCurrentStageToKeeper();
                // ENTER
            } else if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                addCurrentStageToKeeper();
                // DEL
            } else if (keyEvent.getCode().equals(KeyCode.DELETE) && !delPressed) {
                delPressed = true;
                addCurrentStageToKeeper();
                // Ctrl+V / Вставить
            } else if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.V)) {
                keyEvent.consume();
                // Только если в буфере есть строка
                if (clipboard.hasString()) {
                    addCurrentStageToKeeper();

                    var text = textArea.getText();
                    var caretPosition = textArea.getCaretPosition();

                    var clipboardString = clipboard.getString();
                    var newText = text.substring(0, caretPosition) + clipboardString + text.substring(caretPosition);
                    var newCaretPosition = caretPosition + clipboardString.length();

                    textArea.setText(newText);
                    textArea.positionCaret(newCaretPosition);
                }
                // Ctrl+X / Вырезать
            } else if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.X)) {
                keyEvent.consume();
                var selectedText = textArea.getSelectedText();
                // Если есть выделенный текст
                if (selectedText != null && !selectedText.isEmpty()) {
                    addCurrentStageToKeeper();

                    var text = textArea.getText();
                    var caretPosition = textArea.getCaretPosition();

                    ClipboardContent content = new ClipboardContent();
                    content.putString(selectedText);
                    clipboard.setContent(content);

                    // если выделение происходило слева на право, то корректирую позицию курсора
                    if (caretPosition != text.indexOf(selectedText)) {
                        caretPosition -= selectedText.length();
                    }

                    var newText = text.replace(selectedText, "");

                    textArea.setText(newText);
                    textArea.positionCaret(caretPosition);
                }
                // фиксирую вход внутрь строки
            } else if (textArea.getCaretPosition() != textArea.getText().length() && !insideString
                    && !keyEvent.isControlDown()) {
                insideString = true;
                // ввожу символы внутри строки
            } else if (insideString && !inputInside && keyEvent.getText().matches(".")
                    && !keyEvent.isControlDown() && !keyEvent.isShiftDown() && !keyEvent.isAltDown()) {
                inputInside = true;
//                undoPressed = false;
                addCurrentStageToKeeper();
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
                    addCurrentStageToKeeper();
                }
            }

            // отмена фиксации первого нажатия кнопки undo после ввода какого-либо символа
            if (keyEvent.getText().matches(".") && !keyEvent.isControlDown() && !keyEvent.isShiftDown()
                    && !keyEvent.isAltDown()) {
                undoPressed = false;
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

    private void addCurrentStageToKeeper() {
        stageKeeper.addStage(new EditorStage(textArea.getText(), textArea.getCaretPosition()));
    }

    private void undo() {
        // сохраняю состояние только если еще не было нажатия кнопки undo
        if (!undoPressed) {
            // сохраняю нажатие кнопки undo
            undoPressed = true;
            // сохраняю текущее состояние в менеджер
            addCurrentStageToKeeper();
        }
        // перемещаю указатель по списку изменений влево на одну позицию
        stageKeeper.undo();
        refreshTextArea();
    }

    private void redo() {
        // перемещаю указатель по списку изменений вправо на одну позицию
        stageKeeper.redo();
        refreshTextArea();
    }

    /**
     * метод обновляет состояние в поле беря знаяения из списка сохранений
     */
    private void refreshTextArea() {
        // получаю значение указателя
        var value = (String) stageKeeper.getCurrent().value();
        var caretPosition = stageKeeper.getCurrent().caretPosition();
        // обновляю текст и каретку в поле
        textArea.setText(value);
        textArea.positionCaret(caretPosition);
    }

    /**
     * проверка расположения каретки и обновление флага
     */
    private void refreshIfOutside() {
        // если каретка вконце строки
        if (textArea.getCaretPosition() == textArea.getText().length()) {
            if (insideString) {
                insideString = false;
            }

            if (inputInside) {
                inputInside = false;
            }
        }
    }
}