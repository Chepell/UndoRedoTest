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
            stageKeeper.clear(new EditorStage());
            refreshTextArea();
            textArea.requestFocus();
        });

        // обработка триггеров с клавиатуры
        textArea.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            // если не было нажатия Shift, Ctrl, Alt. Ловлю обычные нажатия кнопок
            if (!keyEvent.isControlDown() && !keyEvent.isShiftDown() && !keyEvent.isAltDown()) {
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
                    // DEL
                } else if (keyEvent.getCode().equals(KeyCode.DELETE) && !delPressed) {
                    delPressed = true;
                    addCurrentStageToKeeper();
                    // ENTER
                } else if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    addCurrentStageToKeeper();
                }

                // ввожу первый символ внутри строки
                if (insideString && !inputInside && keyEvent.getText().matches(".")) {
                    System.out.println("Ввожу символ внутри строки");
                    inputInside = true;
                    addCurrentStageToKeeper();
                }

                // отмена фиксации первого нажатия кнопки undo после ввода любого символа
                if (keyEvent.getText().matches(".")) {
                    undoPressed = false;
                }

                // отмена фиксации нажатия пробела
                if (keyEvent.getCode().equals(KeyCode.SPACE) && spacePressed) {
                    spacePressed = false;
                }

                // отмена фиксации нажатия таба
                if (keyEvent.getCode().equals(KeyCode.TAB) && tabPressed) {
                    tabPressed = false;
                }

                // отмена фиксации нажатия бекспейса и так же необходимо запомнить
                // состояние при выходе из серии последовательных нажатий бекспейсов
                if (keyEvent.getCode().equals(KeyCode.BACK_SPACE) && backspacePressed) {
                    backspacePressed = false;
                    addCurrentStageToKeeper();
                }
            } else { // тут ловлю сочетания клавиш
                // перекрываю стандартные обработчики
                keyEvent.consume();
                // замена стандартных горячих клавиш
                if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.Z)) {
                    if (keyEvent.isShiftDown()) {
                        redo();
                    } else {
                        undo();
                    }
                    textArea.requestFocus();
                } // Ctrl+V / Вставить
                else if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.V)) {
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
                }
            }

            // фиксирую вход внутрь строки
            if (isCaretInside() && !insideString) {
                System.out.println("$$$ Фиксирую вход внутрь строки");
                insideString = true;
            }

            checkCaretInTheEnd();

            // Для отладки, вывести текущий список
            if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.NUMPAD9)) {
                System.out.println("#*****************#");
                for (EditorStage editorStage : stageKeeper.getEditorStageList()) {
                    if (editorStage.equals(stageKeeper.getCurrent())) {
                        System.out.println("* " + editorStage.value());
                    } else {
                        System.out.println(editorStage.value());
                    }
                    System.out.println("__________");
                }
                System.out.println("#*****************#");
            }
        });

        // обработка триггеров с мыши
        textArea.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && isCaretInside() && !insideString) {
                System.out.println("$$$Mouse Фиксирую вход внутрь строки");
                insideString = true;
            }
            checkCaretInTheEnd();
        });
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
     * метод сохранения состояния в список
     */
    private void addCurrentStageToKeeper() {
        stageKeeper.addStage(new EditorStage(textArea.getText(), textArea.getCaretPosition()));
    }


    /**
     * метод обновляет состояние в поле беря значения из списка на котором стоит указатель
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
    private void checkCaretInTheEnd() {
        // если каретка не внутри строки
        if (!isCaretInside()) {
            if (insideString) {
                insideString = false;
            }

            if (inputInside) {
                inputInside = false;
            }
        }
    }

    // todo 1. получаю текущее значение каретки в currentCaret.
    //  2. вызываю метод который ставит каретку в конец, получаю это значение в endCaret.
    //  3. если currentCaret != endCaret, то фиксирую вход внутрь строки. И возвращаю каретку в положение currentCaret
    //  4. Если же значения кареток равны, то возвращать картеку нет смысла, она не двигалась и она изначально
    //  была в конце строки. Оформить все это надо в виде методе isCaretInside, который возвращает true если
    //  внутри строки
    private boolean isCaretInside() {
        var currentCaret = textArea.getCaretPosition(); // getCaretPosition
        textArea.positionCaret(textArea.getLength()); // setCaretToEnd
        var endCaret = textArea.getText().length(); // getCaretPosition

        if (currentCaret != endCaret) {
            textArea.positionCaret(currentCaret); // setCartPosition
            return true;
        } else {
            return false;
        }
    }
}