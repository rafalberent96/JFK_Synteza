import static java.lang.System.out;

public class Class {

    public static void main(String[] args) {
        Class m = new Class();
        try {
            m.cos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String upper(String s) {
        String aux = s.toUpperCase();
        return aux;
    }
    private static void log(String str) {
        out.print((char) 27 + "[32m");
        out.println(str);
        out.print((char) 27 + "[0m");
    }
    public static int count(String s) {
        return 0;
    }

    public int cos() throws Exception{
        System.out.println("");
        throw new Exception("cos");

    }

}



