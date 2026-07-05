import java.util.*;

public class CollectionDemo {
    public static void main(String[] args) {

        ArrayList<String> list = new ArrayList<>();

        list.add("Apple");
        list.add("Banana");
        list.add("Mango");

        System.out.println("Elements: " + list);

        list.remove("Banana");

        System.out.println("After removal: " + list);

        for (String item : list) {
            System.out.println(item);
        }
    }
}