package uhh_lt.classifier;

public class WatsonMieterClassifierTester
{
    /**
     * Testet, ob die Watson Anbindung funktioniert und ob das System die richtigen Werte liefert
     */
    public static void main(String[] args){
        WatsonMieterClassifier MC = new WatsonMieterClassifier();
        System.out.println(MC.classify("Ich bin Mieter."));
    }
}
