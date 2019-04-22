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
    private final int caretPosition;

    public EditorStage(String value, int caretPosition) {
        this.currentValue = value;
        this.caretPosition = caretPosition;
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
    public int caretPosition() {
        return caretPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EditorStage that = (EditorStage) o;

        if (caretPosition != that.caretPosition) return false;
        return Objects.equals(currentValue, that.currentValue);
    }

    @Override
    public int hashCode() {
        int result = currentValue != null ? currentValue.hashCode() : 0;
        result = 31 * result + caretPosition;
        return result;
    }
}