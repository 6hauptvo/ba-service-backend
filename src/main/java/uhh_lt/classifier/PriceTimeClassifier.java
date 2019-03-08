package uhh_lt.classifier;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import uhh_lt.datenbank.SolrConnect;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
//import org.json.*;

public class PriceTimeClassifier
{
    private static SolrClient client = new HttpSolrClient.Builder("http://ltdemos:8983/solr/fea-schema-less-2").build();

    public static void main(String[] args) throws IOException {
        FileWriter fw = new FileWriter("resources/averages_time.txt");
        int sum = 0;
        SolrDocumentList results = getSolrResults("price:[20 TO 29]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_time")).get(0);
        }
        float average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Wartezeit bei Preisen zwischen 20 und 29 Euro ist " + average0 + " Minuten. Es gibt " + results.size() + " Ergebnisse");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[30 TO 39]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_time")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Wartezeit bei Preisen zwischen 30 und 39 Euro ist " + average0 + " Minuten. Es gibt " + results.size() + " Ergebnisse");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[40 TO 49]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_time")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Wartezeit bei Preisen zwischen 40 und 49 Euro ist " + average0 + " Minuten. Es gibt " + results.size() + " Ergebnisse");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[50 TO 59]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_time")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Wartezeit bei Preisen zwischen 50 und 59 Euro ist " + average0 + " Minuten. Es gibt " + results.size() + " Ergebnisse");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[60 TO 69]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_time")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Wartezeit bei Preisen zwischen 60 und 69 Euro ist " + average0 + " Minuten. Es gibt " + results.size() + " Ergebnisse");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[70 TO 79]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_time")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Wartezeit bei Preisen zwischen 70 und 79 Euro ist " + average0 + " Minuten. Es gibt " + results.size() + " Ergebnisse");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[80 TO 89]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_time")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Wartezeit bei Preisen zwischen 80 und 89 Euro ist " + average0 + " Minuten. Es gibt " + results.size() + " Ergebnisse");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[90 TO 100]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_time")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Wartezeit bei Preisen zwischen 90 und 100 Euro ist " + average0 + " Minuten. Es gibt " + results.size() + " Ergebnisse");

        fw.close();
    }

    public static SolrDocumentList getSolrResults(String s) {
        SolrQuery query = new SolrQuery();
        query.setQuery(s).setFields("t_time").setStart(0).setRows(10000);

        QueryResponse response = null;

        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SolrDocumentList results = response.getResults();
        return results;
    }
}