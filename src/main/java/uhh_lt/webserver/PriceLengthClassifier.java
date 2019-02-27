package uhh_lt.webserver;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

//import org.json.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PriceLengthClassifier {
    static SolrClient client = new HttpSolrClient.Builder("http://ltdemos:8983/solr/fea-schema-less-2").build();
    SolrConnect connect = new SolrConnect();

    public static void main(String[] args) throws IOException {
        FileWriter fw = new FileWriter("resources/averages_length.txt");
        int sum = 0;
        SolrDocumentList results = getSolrResults("price:[20 TO 29]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_length")).get(0);
        }
        float average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Fragelänge bei Preisen zwischen 20 und 29 Euro ist " + average0 + " Wörter.");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[30 TO 39]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_length")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Fragelänge bei Preisen zwischen 30 und 39 Euro ist " + average0 + " Wörter.");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[40 TO 49]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_length")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Fragelänge bei Preisen zwischen 40 und 49 Euro ist " + average0 + " Wörter.");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[50 TO 59]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_length")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Fragelänge bei Preisen zwischen 50 und 59 Euro ist " + average0 + " Wörter.");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[60 TO 69]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_length")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Fragelänge bei Preisen zwischen 60 und 69 Euro ist " + average0 + " Wörter.");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[70 TO 79]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_length")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Fragelänge bei Preisen zwischen 70 und 79 Euro ist " + average0 + " Wörter.");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[80 TO 89]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_length")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Fragelänge bei Preisen zwischen 80 und 89 Euro ist " + average0 + " Wörter.");
        fw.write("\n");

        sum = 0;
        results = getSolrResults("price:[90 TO 100]");
        for (SolrDocument result:results) {
            sum += ((ArrayList<Long>)result.get("t_length")).get(0);
        }
        average0 = (float)sum/results.size();
        fw.write("Die durchschnittliche Fragelänge bei Preisen zwischen 90 und 100 Euro ist " + average0 + " Wörter.");

        fw.close();
    }

    public static SolrDocumentList getSolrResults(String s) {
        SolrQuery query = new SolrQuery();

        query.setQuery(s);
        query.setFields("t_length");
        query.setStart(0);
        query.setRows(10000);

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