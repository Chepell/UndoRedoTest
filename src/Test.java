import java.util.List;

/**
 * Artem Voytenko
 * 16.04.2019
 */

public class Test {
    public static void main(String[] args) {
        // создание менеджера изменений
        ChangeManager manager = new ChangeManager();

        // добавление последовательных состояний строки в менеджер
        manager.addChangeable(new Change("1"));
        manager.addChangeable(new Change("12"));
        manager.addChangeable(new Change("123"));
        manager.addChangeable(new Change("1"));
        manager.addChangeable(new Change("1dscs"));
        manager.addChangeable(new Change("1dscs85651"));
        manager.addChangeable(new Change("1"));

        manager.undo();
        manager.redo();
        manager.undo();
        manager.undo();
        manager.undo();

//        manager.addChangeable(new Change("77"));
//        manager.undo();
//        manager.redo();

        List<Changeable<String>> changeableList = manager.getChangeableList();

        for (Changeable<String> changeable : changeableList) {
            System.out.println(changeable.value());
        }
    }
}

