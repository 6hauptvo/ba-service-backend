package uhh_lt.classifier;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashMap;

/**
 * Der MieterClassifier stellt auf Grundlage von eingelesenen Wortlisten fest, ob ein eingegebenener Text
 * aus Sicht eines Vermieters oder Mieters geschrieben wurde. Hierfür wird eine Wahrscheinlichkeit ausgegeben.
 */

public class MieterClassifier implements ClassifierInterface
{
    private HashMap<String, Integer> vermieterTerms = new HashMap<>();
    private HashMap<String, Integer> mieterTerms = new HashMap<>();
    private int vermieterScore;
    private int mieterScore;
    private double mieterWahrscheinlichkeit;

    public MieterClassifier()
    {
        dateiEinleser("Mieter", mieterTerms);
        dateiEinleser("Vermieter", vermieterTerms);
        vermieterScore = 0;
        mieterScore = 0;
    }

    /**
     * Der Dateieinleser liest Textdateien aus dem resources folder ein
     * @param Filename Den Filenamen als String
     * @param Dictionary Eine HashMap
     */
    private void dateiEinleser(String Filename, HashMap<String, Integer> Dictionary)
    {
        System.out.println("loading: " +Filename);
        InputStream input = getClass().getClassLoader().getResourceAsStream(Filename);
        BufferedReader TSVFile;
        try
        {
            assert input != null;
            TSVFile = new BufferedReader(
                    new InputStreamReader(input));
            String dataRow; // Read first line

                dataRow = TSVFile.readLine();
                while (dataRow != null)
                {
                    String data = dataRow.trim();
                    if (!data.isEmpty())
                    {
                        Dictionary.put(data.toLowerCase(), 1);
                    }
                    dataRow = TSVFile.readLine(); // Read next line of data
                }
                TSVFile.close();
            }
        catch (FileNotFoundException e)
        {
            System.err.println("Die Datei konnte nicht geöffnet werden");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("...done");
    }

    /**
     * Bestimmt mithilfe des Expertensystems aus Textdateien mit Keywords, ob der Fragesteller Mieter oder Vermieter ist.
     */
    public Double classify(String text)
    {
        //System.out.println(text);
        text = text.toLowerCase();
        vermieterScore = 0;
        mieterScore = 0;
        mieterWahrscheinlichkeit = 0;

        for(String key : mieterTerms.keySet() ) {
            int count = StringUtils.countMatches(text, key);
            //System.out.println("mieter_key " +key  +"\t" + text.contains(key));
            if (text.contains(key)) {
                mieterScore += mieterTerms.get(key) * count;
            }
        }

        for(String key : vermieterTerms.keySet() ) {
            int count = StringUtils.countMatches(text, key);
            //System.out.println("vermieter_key " +key  +"\t" + text.contains(key));
            if (text.contains(key)) {
                vermieterScore += vermieterTerms.get(key) * count;
            }
        }

        if (mieterScore + vermieterScore == 0) {
            return 0.5;
        }
        mieterWahrscheinlichkeit = (double)mieterScore / (mieterScore + vermieterScore);
        return mieterWahrscheinlichkeit;
    }

    @Override
    public boolean istHauptklasse()
    {
        if (mieterWahrscheinlichkeit > 0.5)
        {
            return true;
        }
        else if (mieterWahrscheinlichkeit < 0.5)
        {
            return false;
        }
        return false;
    }

    /**
     * Gibt eine Zahl zurück, die die Wahrscheinlichkeit dafür, dass es sich um einen Vermieter handelt
     * ausgibt
     * @return float Die Vermieterwahrscheinlichkeit
     */
    public Double getVermieterwahrscheinlichkeit()
    {
        return 1-mieterWahrscheinlichkeit;
    }

    /**
     * Gibt eine Zahl zurück, die die Wahrscheinlichkeit dafür, dass es sich um einen Mieter handelt
     * ausgibt
     * @return float die Mieterwahrscheinlichkeit
     */
    public Double getMieterwahrscheinlichkeit()
    {
        return mieterWahrscheinlichkeit;
    }

    /**
     * Gibt die Mieterwahrscheinlichkeit in einem kurzen Text eingebettet zurück.
     * @return Einen String
     */
    public String getMieterwahrscheinlichkeitAsString()
    {
        if (mieterWahrscheinlichkeit > 0.5)
        {
            return "Analyse: Mieter mit einer Wahrscheinlichkeit von " + mieterWahrscheinlichkeit;
        }

        else if (mieterWahrscheinlichkeit < 0.5)
        {
            return "Analyse: Vermieter mit einer Wahrscheinlichkeit von " + (1-mieterWahrscheinlichkeit);
        }

        else
        {
            return "Es konnte anhand der Frage nicht ermittelt werden, ob Sie ein Mieter oder ein Vermieter sind.";
        }
    }

    /**
     * Gibt den Vermieterscore zurück
     * @return Vermieterscore als int
     */
    public int getVermieterScore()
    {
        return vermieterScore;
    }

    /**
     * Gibt den Mieterscore zurück
     * @return Mieterscore als int
     */
    public int getMieterScore()
    {
        return mieterScore;
    }

    @Override
    public Object istHauptklasse(String text)
    {
        double p = classify(text);

        if (p > 0.5) {
            return true;
        } else if (p < 0.5) {
            return false;
        } else {
            return "unknown";
        }
    }
}