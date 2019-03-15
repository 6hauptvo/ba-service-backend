package uhh_lt.datenbank;

public class SolrConnectTester {

    /**
     * Mit dieser Mainmethode lassen sich in SolrConnect implementierte Methoden testen
     */
    public static void main(String[] args)
    {
        SolrConnect connect = new SolrConnect();
        connect.getAnzahlProblemfaelleOhneRechtsexpertenfeldMieter();
   }
}
