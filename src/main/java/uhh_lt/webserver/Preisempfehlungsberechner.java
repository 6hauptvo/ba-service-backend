package uhh_lt.webserver;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import uhh_lt.datenbank.SolrConnect;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Preisempfehlungsberechner {
    static SolrClient client = new HttpSolrClient.Builder("http://ltdemos:8983/solr/fea-schema-less-2").build();

    public static void main(String[] args) {
        SolrConnect connect = new SolrConnect();
        List<String> ids = readIdFile("resources/outputID.txt");
        StringBuilder sb = new StringBuilder();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("resources/price_training_2.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.write("Anzahl der Wörter, komplexe Nomen, Preis");
        writer.write("\n");
        for (String id:ids) {
            String frage = connect.getFrage(id);
            int wortAnzahl = Komplexitätsberechner.countWord(frage);

            int nounCount = Komplexitätsberechner.complexNounCount(frage);
            String price = connect.getPreis(id);


            sb = new StringBuilder();

            sb.append(wortAnzahl + ", " + nounCount + ", " + price);

            writer.write(sb.toString());
            writer.write("\n");

        }
        writer.close();
    }

    public double getPrice(String frage) throws Exception {

        Classifier lrLoaded = (Classifier) weka.core.SerializationHelper.read(getClass().getClassLoader().getResourceAsStream("price.model"));
        int wortAnzahl = Komplexitätsberechner.countWord(frage);
        int nounCount = Komplexitätsberechner.complexNounCount(frage);
        double[] testCase = new double[]{wortAnzahl, nounCount};

        DenseInstance inst = new DenseInstance(1.0, testCase);
        double prediction = lrLoaded.classifyInstance(inst);

        System.out.println(prediction);
        return prediction;
    }

    public void trainModel() throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("resources/price_training_2.csv");
        Instances dataset = source.getDataSet();
        dataset.setClassIndex(dataset.numAttributes() - 1);
        LinearRegression lr = new LinearRegression();
        lr.buildClassifier(dataset);
        weka.core.SerializationHelper.write("resources/price.model", lr);

    }

    private static List<String> readIdFile(String filename) {

        Scanner s = null;
        try {
            s = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String> out = new ArrayList<>();
        while (s.hasNextLine()){
            out.add(s.nextLine());
        }
        s.close();

        return out;
    }
}