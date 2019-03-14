package uhh_lt.datenbank;

//import org.json.*;

public class SolrConnectTester {

    /**
     * Überprüft, ob der Import von JSON zu Solr richtig funktioniert
     */
    public static void main(String[] args)
    {
        SolrConnect connect = new SolrConnect();
        connect.getAnzahlProblemfaelleOhneRechtsexpertenfeldMieter();

        /**
        SolrConnect connect = new SolrConnect();
        JSONObject obj = new JSONObject();//"{\"T_Date\": \"12039230921039213\", \"Topic_id\": \"123\", \"T_Subject\": \"hallo du\", \"T_Price\": \"654341\", \"T_Message\": \"gfhshkz\", \"T_Summary\": \"gfhfghjghjfhtg\", \"R_posted\": \"fgdhgfhgdfhfg\", \"R_Message\": \"jdfghkjfgd\"}");
        obj.put("Topic_id", "1234");
        obj.put("T_Date", "2017-11-20 11:34:35");
        obj.put("R_posted", "2017-11-20 12:37:46");
        obj.put("price", 676);
        obj.put("T_Message", "Blablabla");
        obj.put("t_length", 234);
        obj.put("t_time", 50);

        connect.store(obj);
        */
   }
}
