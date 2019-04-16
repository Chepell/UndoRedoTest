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
    void initialize() {
        ChangeManager manager = new ChangeManager();

        textArea.textProperty().addListener((observableValue, oldValue, newValue) -> {
            // длина строки не поменялась, но какие-то символы внутри строки изменились
            boolean changed00 = !newValue.equals(oldValue) && newValue.length() == oldValue.length();

            // длина строки уменьшилась справа и длина строки больше чем длина текущего состояния в менеджере
            boolean changed01 = oldValue.startsWith(newValue) && newValue.length() < oldValue.length()
                    && oldValue.length() > ((String) manager.getCurrentChangeable().value()).length();

            // длина строки увеличилась справа и длина строки меньше чем длина текущего состояния в менеджере
            boolean changed02 = newValue.startsWith(oldValue) && newValue.length() > oldValue.length()
                    && newValue.length() < ((String) manager.getCurrentChangeable().value()).length();

            // длина строки уменьшилась на какое-то количество символов, но не справа, а в середине или в начале
            boolean changed03 = !oldValue.startsWith(newValue) && newValue.length() < oldValue.length();

            // длина строки увеличилась на какое-то количество символов, но не справа, а в середине или в начале
            boolean changed04 = !newValue.startsWith(oldValue) && newValue.length() > oldValue.length();


            if (changed00 || changed01 || changed02 || changed03 || changed04) {
//                if (newValue.length() > oldValue.length()) {
//                    manager.addChangeable(new Change(newValue));
//                } else {
//                    manager.addChangeable(new Change(oldValue));
//                }
                manager.addChangeable(new Change(oldValue));
            }

            List<Changeable<String>> changeableList = manager.getChangeableList();
            for (Changeable<String> stringChangeable : changeableList) {
                System.out.println(stringChangeable.value());
            }
            System.out.println("*************");
        });

        undoBtn.setOnAction(actionEvent -> {
            manager.undo();
            String value = (String) manager.getCurrentChangeable().value();
            System.out.println("$$$$");
            System.out.println(value);
            System.out.println("$$$$");
        });
    }
}
