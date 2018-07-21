import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Solution {

    // Complete the rotLeft function below.
    static int[] rotLeft(int[] a, int d) {
        if (d == a.length) {
            return a;
        }
        int[] ret = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int newIndex = (i + (a.length - d)) % a.length;
            ret[newIndex] = a[i];
        }
        return ret;
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        System.out.println((0 + 5 - 2) % 5);
        System.out.println((1 + 5 - 2) % 5);
        System.out.println((2 + 5 - 2) % 5);
        System.out.println((3 + 5 - 2) % 5);
        System.out.println((4 + 5 - 2) % 5);
    }
}
