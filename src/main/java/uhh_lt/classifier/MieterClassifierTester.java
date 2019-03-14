package uhh_lt.classifier;

public class MieterClassifierTester
{
    /**
     * Testet, ob das Expertensystem die richtigen Werte liefert
     */
    public static void main(String[] args)
    {
        MieterClassifier MC = new MieterClassifier();
        System.out.println(MC.classify("Ich bin Vermieter."));
    }

}