import java.util.ArrayList;
import java.util.List;

/**
 * Artem Voytenko
 * 16.04.2019
 * <p>
 * Менеджер изменений
 */

public class EditorStageKeeper {
    // пустая головная нода
    private Node headNode;
    // нода на которой находится указатель
    private Node currentIndex;
    // максимальное количество изменений хранимое в списке
    private final int MAX_HISTORY_LENGTH = 50;
    // счетчик добавленния в список изменений
    private int changeCounter = 0;

    /**
     * Создание менеджера изменений
     * установка указателя на головную ноду
     */
    public EditorStageKeeper(EditorStage editorStage) {
        headNode = new Node(editorStage);
        currentIndex = headNode;
    }

    /**
     * Отчистка списка изменений выставлением ссылки на
     * головную ноду и сброс счетчика добавленных изменений
     */
    public void clear(EditorStage editorStage) {
        // Текущее значение сохраняю в головную ноду
        headNode.editorStage = editorStage;
        // Обнуляю ссылку справа/слева, весь спискок теперь не имеет прямой ссылки и будет отчищен GC
        headNode.left = null;
        headNode.right = null;
        // Установка указателя в головную ноду
        currentIndex = headNode;
        // Сброс счетчика добавленных изменений
        changeCounter = 0;
    }

    /**
     * Добавление объекта изменений в менеджер
     */
    public void addStage(EditorStage editorStage) {

        // если добавляемое изменение уже есть в списке, то ничего не делать дальше
        if (editorStage.equals(currentIndex.editorStage)) {
            System.out.println("$$$ Такое состояние уже есть в списке: " + editorStage.value());
            return;
        }

        System.out.println("$$$ Добавляю в список состояние:\n" + editorStage.value());
        // Создаю новый узел с объектом изменений
        Node newNode = new Node(editorStage);
        // Относительно текущего узла, на котором находится указатель, новый узел должен быть справа
        currentIndex.right = newNode;
        // И для нового узла текущий узел находится слева
        newNode.left = currentIndex;
        // Перемещаю указатель на новый узел
        currentIndex = newNode;

        // Если не достигнуто ограничение на длину хранимых изменений
        if (changeCounter < MAX_HISTORY_LENGTH) {
            // обновляю счетчик
            changeCounter++;
        } else { // Если достигнуто орграничение на длину хранимых изменений, то отбраываю самый старый элемент слева
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
        }
    }

    /**
     * Проверка что указатель находится не на крайнем левом головном узле
     */
    private boolean canUndo() {
        return currentIndex != headNode;
    }

    /**
     * Проверка что справа от указателя еще есть узел
     */
    private boolean canRedo() {
        return currentIndex.right != null;
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
     * Возвращает содержание узла на котором находится указатель
     * в виде объекта реализующего интерфейс Changeable
     */
    public EditorStage getCurrent() {
        return currentIndex.editorStage;
    }

    /**
     * Возвращает все изменения сохраненные в менеджере в виде ArrayList
     */
    public List<EditorStage> getEditorStageList() {
        // создаю пустой список для наполнения его изменениями
        List<EditorStage> editorStageList = new ArrayList<>();

        // перемещаю указатель на первый элемент справа от родительского
        Node iterateNode = headNode;

        // если справа что-то есть
        while (iterateNode != null) {
            // сохраняю содержимое в список
            editorStageList.add(iterateNode.editorStage);
            // сдвигаю указатель вправо
            iterateNode = iterateNode.right;
        }
        return editorStageList;
    }

    /**
     * Реализация узла для двухстороннего связанного списка в виде внутреннего класса
     */
    private class Node {
        // Ссылки на связанные узлы списка
        private Node left = null;
        private Node right = null;
        // Содержание узла в виде объекта реализующего интерфейс Changeable
        private EditorStage editorStage;

        // Конструктор для создания узлов и изменениями и добавления их в связанный список
        Node(EditorStage editorStage) {
            this.editorStage = editorStage;
        }
    }
}
