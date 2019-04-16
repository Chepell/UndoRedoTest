/**
 * Artem Voytenko
 * 16.04.2019
 * <p>
 * Реализация класса конкретных состояний, тут класс параметризирован строкой.
 * а нужно параметриховать каким-то объектом содержащим цитаты и текст в html
 */

public class Change implements Changeable<String> {
    private final String currentValue;

    public Change(String v) {
        this.currentValue = v;
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
}
