/**
 * Artem Voytenko
 * 16.04.2019
 */

public class TestStringChanges {
    public static void main(String[] args) {

        // симуляция как бы слушателя для TextField
        String oldValue = "4654нг312";
        String newValue = "465456312";

        // состояние менеджера имзменений
        String changesStage = "";

        // строка просто прирастает справа символами, изменения не фиксируется
//        boolean stable00 = newValue.startsWith(oldValue) && newValue.length() > oldValue.length();

        // длина строки не поменялась, но какие-то символы внутри строки изменились
        boolean changed00 = !newValue.equals(oldValue) && newValue.length() == oldValue.length();

        // длина строки уменьшилась справа и длина строки больше чем длина текущего состояния в менеджере
        boolean changed01 = oldValue.startsWith(newValue) && newValue.length() < oldValue.length()
                && newValue.length() > changesStage.length();

        // длина строки увеличилась справа и длина строки меньше чем длина текущего состояния в менеджере
        boolean changed02 = newValue.startsWith(oldValue) && newValue.length() > oldValue.length()
                && newValue.length() < changesStage.length();

        // длина строки уменьшилась на какое-то количество символов, но не справа, а в середине или в начале
        boolean changed03 = !oldValue.startsWith(newValue) && newValue.length() < oldValue.length();

        // длина строки увеличилась на какое-то количество символов, но не справа, а в середине или в начале
        boolean changed04 = !newValue.startsWith(oldValue) && newValue.length() > oldValue.length();


        if (changed00 || changed01 || changed02 || changed03 || changed04) {
            if (newValue.length() > oldValue.length()) {
                changesStage = newValue; // это типа manager.addChangeable(oldValue)
            } else {
                changesStage = oldValue;
            }
        }

        System.out.println("changed00: " + changed00);
        System.out.println("changed01: " + changed01);
        System.out.println("changed02: " + changed02);
        System.out.println("changed03: " + changed03);
        System.out.println("changed04: " + changed04);
        System.out.println("changesStage: " + changesStage);
    }
}
