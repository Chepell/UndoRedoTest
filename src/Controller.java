import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

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
    // буффер для получения данных из текстового поля для дальнейшей обработки,
    // поиска изменений и помещения их в ChangeManager
    private StringProperty textFieldBuffer = new SimpleStringProperty();

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


        // биндю проперти к текствому полю
        textFieldBuffer.bind(textArea.textProperty());
        // устанавливаю слушатель изменений в проперти
        textFieldBuffer.addListener(stringChangeListener());


//        textArea.focusedProperty().addListener(observable -> {
//            if (textArea.isFocused()) {
//                // биндю проперти к текствому полю
//                textFieldBuffer.bind(textArea.textProperty());
//                // устанавливаю слушатель изменений в проперти
//                textFieldBuffer.addListener(stringChangeListener());
//            } else {
//                textFieldBuffer.removeListener(stringChangeListener());
//                textFieldBuffer.unbind();
//            }
//        });

        // при нажатии кнопки отмены
        undoBtn.setOnAction(actionEvent -> {
            textFieldBuffer.removeListener(stringChangeListener());
            textFieldBuffer.unbind();

            // перемещаю указатель по списку изменений влево на одну позицию
            manager.undo();
            // получаю значение указателя
            String value = manager.getCurrent().value().toString();
            // обновляю текст в поле
            textArea.setText(value);

            showChangeList();

            textFieldBuffer.bind(textArea.textProperty());
            textFieldBuffer.addListener(stringChangeListener());
        });

        // при нажатии кнопки повтора
        redoBtn.setOnAction(actionEvent -> {
            // перемещаю указатель по списку изменений вправо на одну позицию
            manager.redo();
            // получаю значение указателя
            String value = (String) manager.getCurrent().value();
            // обновляю текст в поле
            textArea.setText(value);
        });

        clearBtn.setOnAction(actionEvent -> {
            manager.clear();
        });

    }

    private ChangeListener<String> stringChangeListener() {
        return (observableValue, oldValue, newValue) -> {
//            // длина строки не поменялась, но какие-то символы внутри строки изменились
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
//
//            if (changed00 || changed01 || changed02 || changed03 || changed04) {
//                if (newValue.length() > oldValue.length()) {
//                    manager.addChangeable(new Change(newValue));
//                } else {
//                    manager.addChangeable(new Change(oldValue));
//                }
//                manager.addChangeable(new Change(oldValue));
//
//                showChangeList();
//            }

            boolean changed = true; //!newValue.equals(oldValue);


            if (changed && !newValue.equals(manager.getCurrent().value().toString())) {
                manager.addChangeable(new Change(newValue));
                showChangeList();
            }
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
