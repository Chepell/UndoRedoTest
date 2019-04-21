/**
 * Artem Voytenko
 * 16.04.2019
 */

public interface Changeable<T> {
    /**
     * отмена
     */
    void undo();

    /**
     * возврат
     */
    void redo();

    /**
     * значение поля содержащего текущее состояние
     */
    T value();

    /**
     * позиция каретки в текущем состоянии
     */
    int caretPosition();
}
