package uhh_lt.webserver;

import org.apache.http.util.Args;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GetRecommendedPrice {
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
        writer.write(" ");
        for (String id:ids) {
            String frage = connect.getFrage(id);
            int wortAnzahl = GetComplexity.countWord(frage);
            int nounCount = GetComplexity.complexNounCount(frage);
            String price = connect.getPreis(id);


            sb = new StringBuilder();

            sb.append(wortAnzahl + ", " + nounCount + ", " + price);

            writer.write(sb.toString());
            writer.write("\n");
        }
        writer.close();
    }

    public static double getPrice(String frage) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("resources/price_training_2.csv");
        Instances dataset = source.getDataSet();
        dataset.setClassIndex(dataset.numAttributes()-1);
        LinearRegression lr = new LinearRegression();
        lr.buildClassifier(dataset);

        return 0;
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