import java.util.ArrayList;
import java.util.List;

/**
 * Artem Voytenko
 * 16.04.2019
 * <p>
 * Менеджер изменений
 */

public class ChangeManager {
    // пустая родительская нода не содержащаа каки-либо изменений
    private Node parentNode = new Node();
    // нода на которой находится указатель
    private Node currentIndex;

    /**
     * Создание менеджера изменений
     * установка указателя на родительскую ноду
     */
    public ChangeManager() {
        currentIndex = parentNode;
    }

    /**
     * Создание менеджера на основе другого менеджера
     */
    public ChangeManager(ChangeManager manager) {
        currentIndex = manager.currentIndex;
    }

    /**
     * Отчистка списка изменений выставлением ссылки на родительскую ноду
     */
    public void clear() {
        currentIndex = parentNode;
    }

    /**
     * Добавление объекта измений в менеджер
     */
    public void addChangeable(Changeable changeable) {
        // Создаю новый узел с объектом изменений
        Node newNode = new Node(changeable);
        // Относительно текущего узла, на котором находится указатель, новый узел должен быть справа
        currentIndex.right = newNode;
        // И для нового узла текущий узел находится слева
        newNode.left = currentIndex;
        // Перемещаю указатель на новый узел
        currentIndex = newNode;
    }

    /**
     * Проверка что указатель находится не на крайнем левом родительском узле
     */
    public boolean canUndo() {
        return currentIndex != parentNode;
    }

    /**
     * Проверка что справа от указателя есть узел
     */
    public boolean canRedo() {
        return currentIndex.right != null;
    }

    /**
     * Отмена
     */
    public void undo() {
        // Исключение, если отмена невозможна
        if (!canUndo()) {
            throw new IllegalStateException("Cannot undo. Index is out of range.");
        }
        // Сдвиг указателя на левый узел
        moveLeft();
        // Вызов метода интерфейса
        currentIndex.changeable.undo();
    }

    /**
     * Возврат
     *
     * @throws IllegalStateException if canRedo returns false.
     */
    public void redo() {
        // Исключение, если возврат не возможен
        if (!canRedo()) {
            throw new IllegalStateException("Cannot redo. Index is out of range.");
        }
        // Сдвиг указателя на правый узел
        moveRight();
        // Вызов метода интерфейса для возврата
        currentIndex.changeable.redo();
    }

    /**
     * сдвиг указателя влево
     *
     * @throws IllegalStateException If the left index is null.
     */
    private void moveLeft() {
        // Исключение, если слева узел пустой
        if (currentIndex.left == null) {
            throw new IllegalStateException("Internal index set to null.");
        }
        // Указатель перемещаю на левый узел
        currentIndex = currentIndex.left;
    }

    /**
     * Сдвиг указателя вправо
     *
     * @throws IllegalStateException If the right index is null.
     */
    private void moveRight() {
        // Исключение, если справа узел пустой
        if (currentIndex.right == null) {
            throw new IllegalStateException("Internal index set to null.");
        }
        // Указатель перемещаю на правый узел
        currentIndex = currentIndex.right;
    }

    /**
     * Возвращает содержание узла на котором находится указатель
     * в виде объекта реализующего интерфейс Changeable
     */
    public Changeable getCurrentChangeable() {
        return currentIndex.changeable;
    }

    /**
     * Возвращает все изменения сохраненные в менеджере в виде ArrayList
     */
    public <T> List<Changeable<T>> getChangeableList() {
        // создаю пустой список для наполнения его изменениями
        List<Changeable<T>> result = new ArrayList<>();

        // перемещаю указатель на первый элемент справа от родительского
        Node rightNode = parentNode.right;

        // если справа что-то есть
        while (rightNode != null) {
            // сохраняю содержимое в список
            result.add(rightNode.changeable);
            // сдвигаю указатель вправо
            rightNode = rightNode.right;
        }

        return result;
    }

    /**
     * Реализация узла для двухстороннего связанного списка в виде внутреннего класса
     */
    private class Node {
        // Ссылки на связанные узлы списка
        private Node left = null;
        private Node right = null;
        // Содержание узла в виде объекта реализующего интерфейс Changeable
        private final Changeable changeable;

        // Конструктор для создания узлов и изменениями и добавления их в связанный список
        Node(Changeable changeable) {
            this.changeable = changeable;
        }

        // Конструктор для создания родительского узла списка
        Node() {
            changeable = new Change();
        }
    }
}
