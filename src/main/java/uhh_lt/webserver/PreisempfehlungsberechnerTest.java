package uhh_lt.webserver;

public class PreisempfehlungsberechnerTest {
    private static Preisempfehlungsberechner grc = new Preisempfehlungsberechner();

    /**
     * Testet die Preisempfehlungsberechner Methoden
     */
                public static void main (String[] args) throws Exception{
        try {
            grc.getPrice("Guten Tag, ich habe vor einem Jahr zusammen mit meiner Frau eine Mietwohnung in Hamburg angemietet. Heute habe ich meine neue Nebenkostenabrechnung bekommen und die ist deutlich teurer als zuvor.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            grc.getPrice("Schönen guten Tag,\n" +
                        "\n" +
                        "ich bin Eigentümer eines großzügigen Einfamilienhauses auf dem Land, das aus zwei Wohneinheiten und einem ca. 1650 qm großen Grundstück besteht. Ursprünglich wurde der Hauptteil (ca. 260 qm) von meiner Familie (meinen Eltern, mir + 2 Katzen) bewohnt. Zusätzlich haben wir stets eine ca. 60 qm große Einliegerwohnung vermietet. Nach dem Tod meines Vaters im Jahr 2010, bin zunächst ich dort aus beruflichen Gründen ausgezogen, da ich ca. 30 km entfernt in der Stadt arbeite und plante dort mit meiner Lebensgefährtin einen gemeinsamen Lebensmittelpunkt zu schaffen. Ende 2012 ist dann auch meine Mutter dort ausgezogen und wir haben uns dazu entschieden, das gesamte Objekt, bis auf einen ca. 50 qm großen Hobby-Werkstattbereich zu vermieten, da ich mich als neuer Eigentümer nicht durch einen Verkauf von der Immobilie trennen wollte.\n" +
                        "\n" +
                        "Die neuen Mieter (eine 6-köpfige Familie) haben dann im Januar 2013 sowohl den Hauptteil des Objekts als auch die Einliegerwohnung bezogen. Meine Lebensgefährtin und ich haben parallel ebenfalls in der Stadt ein deutlich kleineres Objekt angemietet, dass wir seitdem gemeinsam mit den beiden Katzen bewohnen.\n" +
                        "\n" +
                        "Nachdem nun gut 1 1/2 Jahre unseres Zusammenlebens hinter uns liegen, kann man sagen, dass dieses Projekt definitiv gescheitert ist und so haben wir beschlossen, die gemeinsame Wohnung in der Stadt aufzukündigen.\n" +
                        "\n" +
                        "Parallel haben die Mieter meines Einfamilienhauses nach der ersten Jahresabrechnung festgestellt, dass Ihnen das Bewohnen der insgesamt 320 qm dann doch zu teuer ist und haben zum 30.06.2014 die 60 qm Einliegerwohnung gekündigt.\n" +
                        "\n" +
                        "Nun klingt das ja erstmal nahezu ideal, in Anbetracht der geschilderten Situation und so habe ich für mich die Perspektive entwickelt, erstmal wieder die Einliegerwohnung auf dem Land zu beziehen.\n" +
                        "\n" +
                        "Nun folgt aber mein Problem:\n" +
                        "\n" +
                        "Grundsätzlich hätten die Mieter kein Problem damit, mich als neuen Nachbar in meinem Haus zu begrüßen, aber mit den beiden Katzen (Freigänger) können sie sich definitiv nicht arrangieren, da eines ihrer Kinder unter einer Katzenallergie leidet und sich als Therapiemaßnahme einer Hypersensibilisierung unterziehen muss. Dabei wird in regelmäßigen Abständen das Immunsystem sozusagen heruntergefahren und in dieser Zeit ist das Kind natürlich immer sehr anfällig gegenüber äußeren Einflüssen (Mückenstiche, Kontakt mit Katzen, etc.).\n" +
                        "\n" +
                        "Nun bin ich aber auch nicht gewillt, die beiden Katzen einfach wegzugeben (was mit Sicherheit das Einfachste wäre), da ich mit diesen beiden Tieren bereits eine unglaubliche Odysse hinter mir habe und aufgrund von Akzeptanz-Problemen mit meiner Lebensgefährtin auch schon viel Geld (1600 Euro + x) investiert habe, um sie z.B. vorrübergehend in Katzenpensionen unterzubringen, etc. Es wäre aus meiner Sicht einfach völlig absurd, die Tiere jetzt für die Mieter wegzugeben. Dann hätte ich das definitiv wohl auch schon eher für meine Lebensgefährtin tun müssen und wir hätten uns dadurch mit Sicherheit viele Konflikte erspart.\n" +
                        "\n" +
                        "Ich habe den Mietern bereits angeboten, dass ich bereit wäre, alle im Erdgeschoß ihres Wohnbereichs liegenden Fenster als Schutz vor den Katzen mit Fliegengittern zu versehen. Aber das reicht ihnen nicht aus. Aussage vom vergangenen Freitag: Entweder ich gebe die Katzen weg oder sie werden sich eine andere Bleibe suchen.\n" +
                        "\n" +
                        "Damit könnte ich vermutlich auch leben, sind es eh nicht die besten Mieter, die quasi rein gar nichts an Haus und Hof machen und das gesamte Objekt in gut 1 1/2 Jahren schon ziemlich runtergewirtschaftet haben.\n" +
                        "\n" +
                        "Nun meine Fragen:\n" +
                        "\n" +
                        "Gibt es rechtliche Schritte, die die Mieter einleiten könnten, sollte ich auf meinem Standpunkt beharren und mich für den Verbleib meiner Katzen und somit für den Auszug der Mieter entscheiden?\n" +
                        "\n" +
                        "Bisher haben sie auch gesagt, dass sie dann von selbst ausziehen würden, ob das dann auch so unkompliziert erfolgt, kann ich allerdings noch nicht einschätzen. Wäre es grundsätzlich auch rechtlich möglich, dass sie einen Auszug verweigern?\n" +
                        "\n" +
                        "PS: Die ganze Sache drängt etwas, da ich bereits aus der gemeinsamen Wohnung ausgezogen bin und somit eine schnelle Lösung mit den Mietern erzielt werden muss.\n" +
                        "\n" +
                        "Ich freue mich über eine Antwort und verbleibe \n" +
                        "mit freundlichem Gruß,\n" +
                        "Katzenfreund");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
