import java.io.*;

public class FileDemo {
    public static void main(String[] args) {
        try {
            // Writing to file
            FileWriter fw = new FileWriter("sample.txt");
            fw.write("Hello, this is file handling in Java");
            fw.close();

            // Reading from file
            BufferedReader br = new BufferedReader(new FileReader("sample.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            br.close();

        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}
