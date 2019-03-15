package uhh_lt.datenbank;

import java.text.DecimalFormat;

public class Statistikmethoden
{
    private SolrConnect solrConnect;

    public Statistikmethoden()
    {
        solrConnect = new SolrConnect();
    }

    /**
     * Es wird ein String erstellt, der aufsteigend nach Dauer sortiert eine Reihe von [Dauer, Preis] Substrings
     * enthält
     * @return ein String aus [Dauer, Preis] Substrings
     */
    public String dauerPreisComparer()
    {
        return solrConnect.comparer("t_time");
    }

    /**
     * Es wird ein String erstellt, der aufsteigend nach Dauer sortiert eine Reihe von [Fragelänge, Preis] Substrings
     * enthält
     * @return ein String aus [Fragelänge, Preis] Substrings
     */
    public String fragelängePreisComparer()
    {
        return solrConnect.comparer("t_length");
    }

    /**
     * Ermittelt, wie häufig Watson mit den Rechtsexperten mit jeweils true übereinstimmt
     * @return die true positives von Watson
     */
    public int getWatson11()
    {
        return solrConnect.getUebereinstimmung("Watson_istmieter", true, true);
    }

    /**
     * Ermittelt, wie häufig Watson mit den Rechtsexperten mit jeweils false übereinstimmt
     * @return die true negatives von Watson
     */
    public int getWatson22()
    {
        return solrConnect.getUebereinstimmung("Watson_istmieter", false, false);
    }

    /**
     * Ermittelt, wie häufig Watson mit den Rechtsexperten nicht übereinstimmt, da Watson true sagt und die Rechtsexperten
     * sagen false
     * @return
     */
    public int getWatson12()
    {
        return solrConnect.getUebereinstimmung("Watson_istmieter", true, false);
    }

    /**
     * Ermittelt, wie häufig Watson mit den Rechtsexperten nicht übereinstimmt, da Watson false sagt und die Rechtsexperten
     * sagen true
     * @return die false negatives von Watson
     */
    public int getWatson21()
    {
        return solrConnect.getUebereinstimmung("Watson_istmieter",false, true);
    }

    /**
     * Ermittelt, wie häufig die Listen mit den Rechtsexperten übereinstimmen mit jeweils true
     * @return die true positives des bereinigten Expertensystems
     */
    public int getListe11()
    {
        return solrConnect.getUebereinstimmung("Expertensystem_istmieter", true, true);
    }

    /**
     * Ermittelt, wie häufig die Listen mit den Rechtsexperten übereinstimmen mit jeweils false
     * @return die true negatives des bereinigten Expertensystems
     */
    public int getListe22()
    {
        return solrConnect.getUebereinstimmung("Expertensystem_istmieter", false, false);
    }

    /**
     * Ermittelt, wie häufig die Listen mit den Rechtsexperten nicht übereinstimmt, da die Listen true und die
     * Rechtsexperten false sagen
     * @return die false positives des bereinigten Expertensystems
     */
    public int getListe12()
    {
        return solrConnect.getUebereinstimmung("Expertensystem_istmieter",true, false);
    }

    /**
     * Ermittelt, wie häufig die bereinigten Listen mit den Rechtsexperten nicht übereinstimmt, da die Listen false und die
     * Rechtsexperten true sagen
     * @return die false negatives des bereinigten Expertensystems
     */
    public int getListe21()
    {
        return solrConnect.getUebereinstimmung("Expertensystem_istmieter",false, true)-solrConnect.getAnzahlProblemfaelle();
    }

    /**
     * Ermittelt, wie häufig die Listen ohne Bereinigung der Problemfälle mit den Rechtsexperten nicht übereinstimmt,
     * da die Listen false und die Rechtsexperten true sagen
     * @return die false negatives des unbereinigten Expertensystems
     */
    public int getListe21Alle()
    {
        return solrConnect.getUebereinstimmung("Expertensystem_istmieter", false, true);
    }


    /**
     * Gibt die Trefferquote (richtig positiv geteilt durch richtig positiv plus falsch negativ) der Listen aus
     * @return die Trefferquote oder -1 im Fehlerfall
     */
    public String getTrefferquoteListen()
    {
        int richtige = getListe11();
        int falneg = getListe21();
        DecimalFormat f = new DecimalFormat("0.00");
        if(solrConnect.getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / (richtige + falneg);
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     * Gibt die Trefferquote (richtig positiv geteilt durch richtig positiv plus falsch negativ) von Watson aus
     * @return die Trefferquote des Watsonsystems oder -1 im Fehlerfall
     */
    public String getTrefferquoteWatson()
    {
        int richtige = getWatson11();
        int falneg = getWatson21();
        DecimalFormat f = new DecimalFormat("0.00");
        if(solrConnect.getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / (richtige + falneg);
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     * Die Methode gibt die Genauigkeit (richtig positiv geteilt durch richtig positiv plus falsch positiv) der Listen zurück
     * @return die Genauigkeit oder -1 im Fehlerfall
     */
    public String getGenauigkeitListen()
    {
        int richtige = getListe11();
        int falpo = getListe12();
        DecimalFormat f = new DecimalFormat("0.00");
        if(solrConnect.getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / (richtige + falpo);
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     * Die Methode gibt die Genauigkeit (richtig positiv geteilt durch richtig positiv plus falsch positiv) von Watson zurück
     * @return die Genauigkeit des Watsonsystems oder -1 im Fehlerfall
     */
    public String getGenauigkeitWatson()
    {
        int richtige = getWatson11();
        int falpo = getWatson12();
        DecimalFormat f = new DecimalFormat("0.00");
        if(solrConnect.getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / (richtige + falpo);
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     * Ermittelt die Korrektklassifikationsrate des bereinigten Expertensystems
     * @return die Korrektklassifikationsrate des bereinigten Expertensystems
     */
    public String getKorrektklassifikationsrateListen()
    {
        int richtige = getListe11()+getListe22();
        int alle = solrConnect.getAnzahlRechtsexpertenfelder()-solrConnect.getAnzahlProblemfaelle();
        DecimalFormat f = new DecimalFormat("0.00");
        if(solrConnect.getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / alle;
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     * Ermittelt die Korrektklassifikationsrate des Watsonsystems
     * @return die Korrektklassifikationsrate des Watsonsystems
     */
    public String getKorrektklassifikationsrateWatson() {
        int richtige = getWatson11()+getWatson22();
        int alle = solrConnect.getAnzahlRechtsexpertenfelder();
        DecimalFormat f = new DecimalFormat("0.00");
        if (solrConnect.getAnzahlRechtsexpertenfelder() > 0) {
            float genauigkeit = (float) richtige / alle;
            return (f.format(genauigkeit * 100));
        }
        return "-1";
    }

    /**
     * Ermittelt die Falschklassifikationsrate des bereinigten Expertensystems
     * @return die Falschklassifikationsrate des bereinigten Expertensystems
     */
    public String getFalschklassifikationsrateListen()
    {
        int richtige = getListe12()+getListe21();
        int alle = solrConnect.getAnzahlRechtsexpertenfelder()-solrConnect.getAnzahlProblemfaelle();
        DecimalFormat f = new DecimalFormat("0.00");
        if(solrConnect.getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / alle;
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     * Ermittelt die Falschklassifikationsrate des Watsonsystems
     * @return die Falschklassifikationsrate des Watsonsystems
     */
    public String getFalschklassifikationsrateWatson()
    {
        int richtige = getWatson12()+getWatson21();
        int alle = solrConnect.getAnzahlRechtsexpertenfelder();
        DecimalFormat f = new DecimalFormat("0.00");
        if(solrConnect.getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / alle;
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     * Ermittelt die Falschklassifikationsrate des unbereinigten Expertensystems
     * @return die Falschklassifikationsrate des unbereinigten Expertensystems
     */
    public String getAlleFalschklassifikationsrateListen()
    {
        int richtige = getListe12()+getListe21Alle();
        int alle = solrConnect.getAnzahlRechtsexpertenfelder();
        DecimalFormat f = new DecimalFormat("0.00");
        if(solrConnect.getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / alle;
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     * Ermittelt die Korrektklassifikationsrate des unbereinigten Expertensystems
     * @return die Korrektklassifikationsrate des unbereinigten Expertensystems
     */
    public String getAlleKorrektklassifikationsrateListen()
    {
        int richtige = getListe11()+getListe22();
        int alle = solrConnect.getAnzahlRechtsexpertenfelder();
        DecimalFormat f = new DecimalFormat("0.00");
        if(solrConnect.getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / alle;
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     * Die Methode gibt die Genauigkeit (richtig positiv geteilt durch richtig positiv plus falsch positiv) der Listen
     * ohne Aussortieren der Problemfälle zurück
     * @return die Genauigkeit oder -1 im Fehlerfall
     */
    public String getAlleGenauigkeitListen()
    {
        int richtige = getListe11();
        int falpo = getListe12();
        DecimalFormat f = new DecimalFormat("0.00");
        if(solrConnect.getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / (richtige + falpo);
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     * Gibt die Trefferquote (richtig positiv geteilt durch richtig positiv plus falsch negativ) der Listen aus
     * @return die Trefferquote oder -1 im Fehlerfall
     */
    public String getAlleTrefferquoteListen()
    {
        int richtige = getListe11();
        int falneg = getListe21Alle();
        DecimalFormat f = new DecimalFormat("0.00");
        if(solrConnect.getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / (richtige + falneg);
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

}
