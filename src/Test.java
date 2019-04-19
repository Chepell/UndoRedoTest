import java.util.List;

/**
 * Artem Voytenko
 * 16.04.2019
 */

public class Test {
    public static void main(String[] args) {
        // создание менеджера изменений
        EditorStageKeeper manager = new EditorStageKeeper(new EditorStage(""));

        // добавление последовательных состояний строки в менеджер
        manager.addStage(new EditorStage("1"));
        manager.addStage(new EditorStage("12"));
        manager.addStage(new EditorStage("123"));
        manager.addStage(new EditorStage("1"));
        manager.addStage(new EditorStage("1dscs"));
        manager.addStage(new EditorStage("1dscs85651"));
        manager.addStage(new EditorStage("1"));

        manager.undo();
        manager.redo();
        manager.undo();
        manager.undo();
        manager.undo();

//        manager.addStage(new EditorStage("77"));
//        manager.undo();
//        manager.redo();

        List<Changeable<String>> changeableList = manager.getChangeableList();

        for (Changeable<String> changeable : changeableList) {
            System.out.println(changeable.value());
        }
    }
}

