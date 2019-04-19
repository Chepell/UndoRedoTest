import java.util.Objects;

/**
 * Artem Voytenko
 * 16.04.2019
 * <p>
 * Реализация класса конкретных состояний, тут класс параметризирован строкой.
 * а нужно параметриховать каким-то объектом содержащим цитаты и текст в html
 */

public class EditorStage implements Changeable<String> {
    private final String currentValue;

    public EditorStage(String value) {
        this.currentValue = value;
    }

    @Override
    public void undo() {
        System.out.println("назад к значению: " + currentValue);
    }

    @Override
    public void redo() {
        System.out.println("вперед к значению: " + currentValue);
    }

    @Override
    public String value() {
        return currentValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EditorStage editorStage = (EditorStage) o;
        return Objects.equals(currentValue, editorStage.currentValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentValue);
    }
}
