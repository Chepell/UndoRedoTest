import java.util.Objects;

/**
 * Artem Voytenko
 * 16.04.2019
 * <p>
 * Реализация класса конкретных состояний, тут класс параметризирован строкой.
 * а нужно параметриховать каким-то объектом содержащим цитаты и текст в html
 */

public class Change implements Changeable<String> {
    private final String currentValue;

    public Change(String value) {
        this.currentValue = value;
    }

    public Change() {
        this.currentValue = "";
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
        Change change = (Change) o;
        return Objects.equals(currentValue, change.currentValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentValue);
    }
}
