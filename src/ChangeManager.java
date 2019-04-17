import java.util.ArrayList;
import java.util.List;

/**
 * Artem Voytenko
 * 16.04.2019
 * <p>
 * Менеджер изменений
 */

public class ChangeManager {
    // пустая головная нода
    private Node headNode = new Node();
    // нода на которой находится указатель
    private Node currentIndex;
    // максимальное количество изменений хранимое в списке
    private final int maxHistoryLength;
    // счетчик добавленния в список изменений
    private int changeCounter = 0;

    /**
     * Создание менеджера изменений
     * установка указателя на головную ноду
     */
    public ChangeManager(int maxHistoryLength) {
        this.maxHistoryLength = maxHistoryLength;
        currentIndex = headNode;
    }

    /**
     * Отчистка списка изменений выставлением ссылки на
     * головную ноду и сброс счетчика добавленных изменений
     */
    public void clear() {
        // Текущее значение сохраняю в головную ноду
        headNode.changeable = currentIndex.changeable;
        // Обнуляю ссылку справа, весь спискок теперь не имеет прямой ссылки и будет отчищен GC
        headNode.right = null;
        // Установка указателя в головную ноду
        currentIndex = headNode;
        // Сброс счетчика добавленных изменений
        changeCounter = 0;
    }

    /**
     * Добавление объекта изменений в менеджер
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

        // Если не достигнуто ограничение на длину хранимых изменений
        if (changeCounter < maxHistoryLength) {
            // обновляю счетчик
            changeCounter++;
        } else {
            // Головной узел сдвигаю вправо
            headNode = headNode.right;
            // Обнуляю у новой головы левый узел
            headNode.left = null;
        }
    }

    /**
     * Отмена
     */
    public void undo() {
        if (canUndo()) {
            // Сдвиг указателя на левый узел
            moveLeft();
            changeCounter--;
            // Вызов метода интерфейса
            currentIndex.changeable.undo();
        }
    }

    /**
     * Возврат
     */
    public void redo() {
        if (canRedo()) {
            // Сдвиг указателя на правый узел
            moveRight();
            changeCounter++;
            // Вызов метода интерфейса для возврата
            currentIndex.changeable.redo();
        }
    }

    /**
     * Сдвиг указателя влево
     */
    private void moveLeft() {
        currentIndex = currentIndex.left;
    }

    /**
     * Сдвиг указателя вправо
     */
    private void moveRight() {
        currentIndex = currentIndex.right;
    }

    /**
     * Проверка что указатель находится не на крайнем левом головном узле
     */
    public boolean canUndo() {
        return currentIndex != headNode;
    }

    /**
     * Проверка что справа от указателя еще есть узел
     */
    public boolean canRedo() {
        return currentIndex.right != null;
    }

    /**
     * Возвращает содержание узла на котором находится указатель
     * в виде объекта реализующего интерфейс Changeable
     */
    public <T> Changeable<T> getCurrent() {
        return currentIndex.changeable;
    }

    /**
     * Возвращает все изменения сохраненные в менеджере в виде ArrayList
     */
    public <T> List<Changeable<T>> getChangeableList() {
        // создаю пустой список для наполнения его изменениями
        List<Changeable<T>> result = new ArrayList<>();

        // перемещаю указатель на первый элемент справа от родительского
        Node rightNode = headNode.right;

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
        private Changeable changeable;

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
