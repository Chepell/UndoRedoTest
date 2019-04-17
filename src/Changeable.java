/**
 * Artem Voytenko
 * 16.04.2019
 */

public interface Changeable<T> {
    /**
     *
     */
    void undo();

    /**
     * возврат
     */
    void redo();

    /**
     * возвращает значение поля содержащего текущее состояние
     */
    T value();


}
