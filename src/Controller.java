import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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

    private ChangeManager manager = new ChangeManager(5);

    @FXML
    void initialize() {
//        manager.addChangeable(new Change("1"));
//        manager.addChangeable(new Change("2"));
//        manager.addChangeable(new Change("3"));
//        manager.addChangeable(new Change("4"));
//        manager.undo();
//        manager.undo();
//        manager.addChangeable(new Change("5"));
//
//        textArea.setText(manager.getCurrent().value().toString());

        textArea.textProperty().addListener(stringChangeListener());

        // при нажатии кнопки отмены
        undoBtn.setOnAction(actionEvent -> undo());

        // при нажатии кнопки повтора
        redoBtn.setOnAction(actionEvent -> redo());

        // отчистка истории изменений
        clearBtn.setOnAction(actionEvent -> manager.clear());

        // замена стандартных горячих клавиш
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.Z && keyEvent.isControlDown()) {
                if (keyEvent.isShiftDown()) {
                    redo();
                } else {
                    undo();
                }
                textArea.positionCaret(textArea.getLength());
                keyEvent.consume();
            }
        });
    }

    private void undo() {
        textArea.textProperty().removeListener(stringChangeListener());

        // перемещаю указатель по списку изменений влево на одну позицию
        manager.undo();
        // получаю значение указателя
        String value = manager.getCurrent().value().toString();
        // обновляю текст в поле
        textArea.setText(value);
        showChangeList();

        textArea.textProperty().addListener(stringChangeListener());
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
//                manager.refreshChangeable(new Change(newValue));
//            } else {
//                manager.addChangeable(new Change(newValue));
//            }

//            if (changed00 || changed01 || changed02 || changed03 || changed04) {
//                manager.addChangeable(new Change(newValue));
//            } else {
//                manager.refreshChangeable(new Change(newValue));
//            }

            manager.addChangeable(new Change(newValue));
            showChangeList();
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
