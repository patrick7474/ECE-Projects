import java.util.Scanner;

public class Bubble {
   public Bubble() {
   }

   public static void main(String[] var0) {
      Scanner var1 = new Scanner(System.in);
      System.out.print("Enter number of elements: ");
      int var2 = var1.nextInt();
      int[] var3 = new int[var2];
      System.out.println("Enter elements:");

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4] = var1.nextInt();
      }

      for(int var7 = 0; var7 < var2 - 1; ++var7) {
         for(int var5 = 0; var5 < var2 - var7 - 1; ++var5) {
            if (var3[var5] > var3[var5 + 1]) {
               int var6 = var3[var5];
               var3[var5] = var3[var5 + 1];
               var3[var5 + 1] = var6;
            }
         }
      }

      System.out.println("Sorted Array:");

      for(int var8 = 0; var8 < var2; ++var8) {
         System.out.print(var3[var8] + " ");
      }

      var1.close();
   }
}
