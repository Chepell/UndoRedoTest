import java.util.Objects;

/**
 * Artem Voytenko
 * 16.04.2019
 * <p>
 * Класс для хранения состояний редактора
 */

public class EditorStage {
    private final String currentValue;
    private final int caretPosition;

    public EditorStage() {
        this.currentValue = "";
        this.caretPosition = 0;
    }

    public EditorStage(String value, int caretPosition) {
        this.currentValue = value;
        this.caretPosition = caretPosition;
    }

    public String value() {
        return currentValue;
    }

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