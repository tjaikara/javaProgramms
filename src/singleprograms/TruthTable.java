package singleprograms;

/**
 * Created by taikara on 2/20/17.
 */
public class TruthTable {

    public static void main(String[] args) {

        System.out.println("-----------------P and Q truth table with bit values -------------------------");
        System.out.println();
        System.out.print("P |\tQ |\tP AND Q|\tP OR Q |\tP XOR Q |\tNOT P |\n");
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int logicAND = i & j;
                int logicOR = i | j;
                int logicXOR = i ^ j;
                int logicNOT = "0".equals(String.valueOf(i)) ? 1 : 0;

                System.out.print(i + " |\t" + j + " | \t" + logicAND + "  |\t\t" + logicOR + "  | \t\t" + logicXOR + "   |\t " + logicNOT + "\t  |\n");
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

                System.out.print(("true".equals(String.valueOf(p)) ? p + "  |\t": p +" |\t") + ("true".equals(String.valueOf(q)) ? q + "  |\t": q +" |\t") +
                        ("true".equals(String.valueOf(String.valueOf(p&q))) ? String.valueOf(p&q) + "    |\t": String.valueOf(p&q) +"   |\t")
                        + ("true".equals(String.valueOf(String.valueOf(p|q))) ? String.valueOf(p|q) + "   |\t": String.valueOf(p|q) +"  |\t")
                        + ("true".equals(String.valueOf(String.valueOf(p^q))) ? String.valueOf(p^q) + "    |\t": String.valueOf(p^q) +"   |\t")
                        + ("true".equals(String.valueOf(!p)) ? String.valueOf(!p) + "  |\n": String.valueOf(!p) +" |\n"));
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
