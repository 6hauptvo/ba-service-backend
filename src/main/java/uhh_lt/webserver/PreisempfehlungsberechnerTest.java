package uhh_lt.webserver;

public class PreisempfehlungsberechnerTest {

    public static void main(String args[]) {
        try {
            Preisempfehlungsberechner.getPrice("Ich bin Mieter in Hamburg.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}