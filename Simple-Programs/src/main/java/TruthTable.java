

/**
 * Created by taikara on 2/20/17.
 */
public class TruthTable {

    public static void main(String[] args) {

        System.out.println("-----------------P and Q truth table with bit values -------------------------");
        System.out.println();
        System.out.print("P \t| \tQ |\tP AND Q |\tP OR Q  |\tP XOR Q |\tNOT P |\n");
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {

                System.out.print(i + " \t| \t" + j + " | \t"
                                + (i & j) + " \t| \t\t"
                                + (i | j) + " \t| \t\t"
                                + (i ^ j) + " \t| \t"
                                + ("0".equals(String.valueOf(i)) ? 1 : 0) + "\t  |\n");
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("-----------------P and Q truth table with true and false----------------------");
        System.out.println();
        System.out.print("P\t  |\tQ\t  |\tP AND Q |\tP OR Q |\tP XOR Q |\tNOT P |\n");
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                boolean p = getBooleanValue(i);
                boolean q = getBooleanValue(j);

                System.out.print((p ? "true " : "false") +" |\t"+ (q ? "true " : "false") +" |\t"
                        + (p&q ? "true " : "false") +"   |\t"
                        + (p|q ? "true " : "false") +"  |\t"
                        + (p^q ? "true " : "false") +"   |\t"
                        + (!p ? "true " : "false") +" |\n");
            }
        }
    }

    private static boolean getBooleanValue(int i) {

        boolean p = false;

        switch (i) {
            case 0:
                p = false;
                break;
            case 1:
                p = true;
                break;
            default:
                System.out.println("Not Found case for " + i + "so returning false");
        }
        return p;
    }


}
