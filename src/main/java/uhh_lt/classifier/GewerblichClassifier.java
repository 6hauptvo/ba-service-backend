package uhh_lt.classifier;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashMap;

/**
 * Der gewerblichClassifier stellt auf Grundlage von eingelesenen Wortlisten fest, ob ein eingegebenener Text
 * von einem privaten oder gewerblichen Mietverhältnis die Rede ist. Hierfür wird eine Wahrscheinlichkeit ausgegeben.
 */

public class GewerblichClassifier implements ClassifierInterface
{
    private HashMap<String, Integer> privatTerms = new HashMap<>();
    private HashMap<String, Integer> gewerblichTerms = new HashMap<>();
    private int privatScore;
    private int gewerblichScore;
    private double gewerblichWahrscheinlichkeit;


    public GewerblichClassifier()
    {
        dateiEinleser("gewerblich", gewerblichTerms);
        dateiEinleser("privat", privatTerms);
        privatScore = 0;
        gewerblichScore = 0;
    }

    /**
     * Der Dateieinleser liest Textdateien aus dem resources folder ein
     *
     * @param Filename Den Filenamen als String
     * @param Dictionary Eine HashMap
     */

    private void dateiEinleser(String Filename, HashMap<String, Integer> Dictionary) {
        //System.out.println("loading: " +Filename);

        InputStream input = getClass().getClassLoader().getResourceAsStream(Filename);

        BufferedReader TSVFile = null;
        try {
            TSVFile = new BufferedReader(
                    new InputStreamReader(input));
            String dataRow = null; // Read first line

            dataRow = TSVFile.readLine();
            while (dataRow != null) {
                //String[] dataArray = dataRow.split("\t");
                String data = dataRow.trim();
                //for (String item : data)
                //{
                if (!data.isEmpty()) {
                    Dictionary.put(data.toLowerCase(), 1);
                }

                //}

                dataRow = TSVFile.readLine(); // Read next line of data.
            }
            //System.out.println(Dictionary);
            TSVFile.close();

        } catch (FileNotFoundException e) {
            System.err.println("Die Datei konnte nicht geöffnet werden");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("...done");
    }

    public Double classify(String text)
    {
        //System.out.println(text);

        text = text.toLowerCase();
        privatScore = 0;
        gewerblichScore = 0;
        gewerblichWahrscheinlichkeit = 0;

        for(String key : gewerblichTerms.keySet() ) {
            int count = StringUtils.countMatches(text, key);
            //System.out.println("gewerblich_key " +key  +"\t" + text.contains(key));
            if (text.contains(key)) {
                gewerblichScore += gewerblichTerms.get(key) * count;
            }
        }

        for(String key : privatTerms.keySet() ) {
            int count = StringUtils.countMatches(text, key);
            //System.out.println("privat_key " +key  +"\t" + text.contains(key));
            if (text.contains(key)) {
                privatScore += privatTerms.get(key) * count;
            }
        }

        if (gewerblichScore + privatScore == 0) {
            return 0.5;
        }
        gewerblichWahrscheinlichkeit = (double)gewerblichScore / (gewerblichScore + privatScore);
        return gewerblichWahrscheinlichkeit;
    }

    @Override
    public boolean istHauptklasse() {
        if (gewerblichWahrscheinlichkeit > 0.5) {
            return true;
        } else if (gewerblichWahrscheinlichkeit < 0.5) {
            return false;
        }
        return false;
    }

    /**
     * Gibt eine Zahl zurück, die die Wahrscheinlichkeit dafür, dass es sich um einen privat handelt
     * ausgibt
     * @return float Die privatwahrscheinlichkeit
     */

    public Double getprivatwahrscheinlichkeit()
    {
        return 1-gewerblichWahrscheinlichkeit;
    }

    /**
     * Gibt eine Zahl zurück, die die Wahrscheinlichkeit dafür, dass es sich um einen gewerblich handelt
     * ausgibt
     * @return float die gewerblichwahrscheinlichkeit
     */

    public Double getgewerblichwahrscheinlichkeit()
    {
        return gewerblichWahrscheinlichkeit;
    }

    /**
     * Gibt die gewerblichwahrscheinlichkeit in einem kurzen Text eingebettet zurück.
     * @return Einen String
     */

    public String getgewerblichwahrscheinlichkeitAsString()
    {
        if (gewerblichWahrscheinlichkeit > 0.5)
        {
            return "Analyse: gewerblich mit einer Wahrscheinlichkeit von " + gewerblichWahrscheinlichkeit;
        }

        else if (gewerblichWahrscheinlichkeit < 0.5)
        {
            return "Analyse: privat mit einer Wahrscheinlichkeit von " + (1-gewerblichWahrscheinlichkeit);
        }

        else
        {
            return "Es konnte anhand der Frage nicht ermittelt werden, ob Sie ein gewerblich oder ein privat sind.";
        }
    }

    /**
     * Gibt den privatscore zurück
     * @return privatscore als int
     */

    public int getprivatScore()
    {
        return privatScore;
    }

    /**
     * Gibt den gewerblichscore zurück
     * @return gewerblichscore als int
     */

    public int getgewerblichScore()
    {
        return gewerblichScore;
    }


    @Override
    public Object istHauptklasse(String text) {
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