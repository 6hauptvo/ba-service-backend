package uhh_lt.webserver;

import org.junit.Test;
import uhh_lt.classifier.MieterClassifier;

import static org.junit.Assert.assertTrue;

public class MieterClassifierTest {

    private MieterClassifier mieterClassifier;

    public MieterClassifierTest()
    {
        mieterClassifier = new MieterClassifier();
    }

    @Test
    public void classifySimple()
    {
        Double mC = mieterClassifier.classify("Ich bin Mieter");
        assertTrue("Die Mieterwahrscheinlichkeit muss 1 sein", 1.0 == mC);
        Double vC = mieterClassifier.classify("Ich bin Vermieter");
        assertTrue("Die Mieterwahrscheinlichkeit muss 0 sein", 0.0 == vC);
        Double nC = mieterClassifier.classify("Ich bin weder Mieter noch Vermieter");
        assertTrue("Die Mieterwahrscheinlichkeit muss 0.5 sein", 0.5 == nC);
    }

    @Test
    public void classifyMehrfachnennung()
    {
        Double mC = mieterClassifier.classify("Ich bin Mieter, bin Vermieter und ich miete eine Wohnung");
        System.out.println(mC);
        assertTrue("Die Mieterwahrscheinlichkeit muss 2/3 sein", ((float) 2/3) == mC);
        Double nC = mieterClassifier.classify("Ich bin Mieter  und bin Vermieter und ich vermiete und ich miete");
        assertTrue("Die Mieterwahrscheinlichkeit muss 0.5 sein", 0.5 == nC);
        Double aC = mieterClassifier.classify("Ich bin Mieter  und bin Mieter und ich vermiete und ich miete");
        assertTrue("Die Mieterwahrscheinlichkeit muss 0.75 sein", 0.75 == aC);
    }
}