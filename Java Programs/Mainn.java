abstract class Shape {
    int a, b;

    abstract void printArea();
}

class Rectangle extends Shape {

    void printArea() {
        System.out.println("Area of Rectangle = " + (a * b));
    }
}

class Triangle extends Shape {

    void printArea() {
        System.out.println("Area of Triangle = " + (0.5 * a * b));
    }
}


class Circle extends Shape {

    void printArea() {
        System.out.println("Area of Circle = " + (3.14 * a * a));
    }
}

public class Mainn {
    public static void main(String[] args) {

        Rectangle r = new Rectangle();
        r.a = 10;
        r.b = 5;
        r.printArea();

        Triangle t = new Triangle();
        t.a = 10;
        t.b = 5;
        t.printArea();

        Circle c = new Circle();
        c.a = 7;  // radius
        c.printArea();
    }
}