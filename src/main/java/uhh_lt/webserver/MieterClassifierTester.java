package uhh_lt.webserver;

public class MieterClassifierTester
{
    public static void main(String[] args)
    {
        MieterClassifier MC = new MieterClassifier();
        System.out.println(MC.classify("Ich bin Vermieter."));
    }
}
