package uhh_lt.webserver;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.json.simple.JSONObject;
import uhh_lt.classifier.MieterClassifier;
import uhh_lt.classifier.WatsonMieterClassifier;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import static java.lang.Math.toIntExact;
import static junit.framework.Assert.assertEquals;

public class SolrConnect
{
    private static SolrClient client;
    private JsonImport jsonImport;

    public SolrConnect()
    { // für ssh  : localhost , sonst ltdemos:8983/solr/fea-schema-less-2
         client = new HttpSolrClient.Builder("http://ltdemos:8983/solr/fea-schema-less-2").build();
         jsonImport = new JsonImport();
    }

    public void store(JSONObject object, boolean commit) {
        MieterClassifier mc = new MieterClassifier();
        WatsonMieterClassifier wmc = new WatsonMieterClassifier();
        SolrInputDocument inputDocument = new SolrInputDocument();
        inputDocument.addField("id", object.get("Topic_id"));
        String tDate = (String) object.get("T_Date");
        inputDocument.addField("t_date", tDate);
        inputDocument.addField("t_subject", object.get("T_Subject"));
        inputDocument.addField("price", object.get("T_Price"));
        inputDocument.addField("t_message", object.get("T_Message"));
        inputDocument.addField("t_summary", object.get("T_Summary"));
        inputDocument.addField("a_date", object.get("R_posted"));
        inputDocument.addField("a_message", object.get("R_Message"));
        inputDocument.addField("t_time", Datendifferenzberechner.Differenz((String)object.get("T_Date"),(String)object.get("R_posted")));
        inputDocument.addField("Expertensystem_istmieter", mc.istHauptklasse((String)object.get("T_Message")));
        inputDocument.addField("Expertensystem_wert", mc.getMieterwahrscheinlichkeit());
        inputDocument.addField("Watson", wmc.classify((String)object.get("T_Message")));
        inputDocument.addField( "Watson istmieter", wmc.istHauptklasse());
        inputDocument.addField("t_length", Komplexitätsberechner.countWord((String)object.get("T_Message")));

        try {
            client.add(inputDocument);
            if (commit) {
                client.commit();
            }
        }
        catch (SolrServerException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void commit() {
        try {
            client.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String search(String searchTerm) {

        SolrQuery query = new SolrQuery();
        query.setQuery(searchTerm);
        query.setFields("id");
        query.setStart(0);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder out = new StringBuilder();
        SolrDocumentList results = response.getResults();
        for (int i = 0; i < results.size(); ++i) {
            out.append(results.get(i)).append("\n");
        }
        return out.toString();
    }

    public boolean isFullyAnnotatedMieter(String id){
        SolrQuery query = new SolrQuery();
        query.setQuery("id:" + id + "AND Rechtsexperten_istmieter2:*");
        QueryResponse response = null;

        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.getResults().size()>0) {
            return false;
        }
        return true;
    }

    public boolean isFullyAnnotatedGewerblich(String id){
        SolrQuery query = new SolrQuery();
        query.setQuery("id:" + id + "AND Rechtsexperten_istgewerblich2:*");
        QueryResponse response = null;

        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.getResults().size()>0) {
            return false;
        }
        return true;
    }

    public String getFrage(String id) {
    SolrQuery query = new SolrQuery();
    query.setQuery("id:" + id).setFields("t_message").setStart(0).setRows(10000);
    QueryResponse response = null;

        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SolrDocumentList results = response.getResults();
        return String.valueOf(results.get(0).get("t_message"));
    }

    public String getPreis(String id) {
        SolrQuery query = new SolrQuery();
        query.setQuery("id:" + id).setFields("price").setStart(0).setRows(10000);

        QueryResponse response = null;

        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SolrDocumentList results = response.getResults();
        return String.valueOf(results.get(0).get("price"));
    }

    public void printIdInDoc() throws IOException
    {
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*").setFields("id").setStart(0).setRows(10000);
        QueryResponse response = null;

        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SolrDocumentList results = response.getResults();
        FileWriter fw = new FileWriter("resources/outputID.txt");
        for (int i = 0; i < results.size(); ++i) {
            System.out.println(results.get(i));
            fw.write(String.valueOf(results.get(i).get("id")));
            fw.write("\n");
        }
        fw.close();
    }

    /**
     * Wenn der Mieter- oder Vermieterbutton gedrückt wurde, wird entweder ein neues Feld "Rechtsexperten_istmieter" oder
     * "Rechtsexperten_istmieter2" angelegt und mit dem entsprechenden Wert gefüllt oder es wird nichts getan
     * @param docID  Die ID, den Primärschlüssel, als String
     * @param istMieter  Wenn es sich um einen Mieter handelt true, sonst false
     */
    public void mieterButtonsPushed(String docID, Object istMieter)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+ docID);
        QueryResponse response = null;
        try
        {
            response = client.query(query);
        }
        catch (SolrServerException | IOException e)
        {
            e.printStackTrace();
        }

        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);

        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docID);
        }

        SolrDocument oldDoc = response.getResults().get(0);

        Collection<String> feldnamensliste = oldDoc.getFieldNames();
        ArrayList<String> list = new ArrayList<String>();

        for (String str:feldnamensliste)
        {
            list.add(str);
        }

        String feld = "Rechtsexperten_istmieter";
        String feld2 = "Rechtsexperten_istmieter2";

        if(!list.contains(feld))
        {
            addRechtsexpertenfeldMieter(docID, istMieter);
        }

        else if(list.contains(feld) && !list.contains(feld2))
            {
                addRechtsexpertenfeldMieter2(docID, istMieter);
            }
    }

    /**
     * Fügt in Solr das neue Feld "Problemfall" hinzu und setzt dieses auf den eingegebenen Wert
     * @param docID Die DokumentenID, der Primärschlüssel
     */
    public void mieterProblemfallButtonPushed(String docID)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+ docID);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docID);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        Collection<String> feldnamensliste = oldDoc.getFieldNames();
        ArrayList<String> list = new ArrayList<String>();
        for (String str:feldnamensliste)
        {
            list.add(str);
        }

        String feld = "Problemfall";
        if(!list.contains(feld))
        {
            addField(docID, "Problemfall", true);
        }
    }

    public void gewerblichButtonsPushed(String docID, boolean istGewerblich)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+ docID);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docID);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        Collection<String> feldnamensliste = oldDoc.getFieldNames();
        ArrayList<String> list = new ArrayList<String>();
        for (String str:feldnamensliste)
        {
            list.add(str);
        }

        String feld = "Rechtsexperten_istgewerblich";
        String feld2 = "Rechtsexperten_istgewerblich2";
        if(!list.contains(feld))
        {
            addRechtsexpertenfeldGewerblich(docID, istGewerblich);
        }

        else if(list.contains(feld) && !list.contains(feld2))
        {

                addRechtsexpertenfeldGewerblich2(docID, istGewerblich);
        }
    }

    public void gewerblichProblemfallButtonPushed(String docID)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+ docID);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docID);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        Collection<String> feldnamensliste = oldDoc.getFieldNames();
        ArrayList<String> list = new ArrayList<String>();
        for (String str:feldnamensliste)
        {
            list.add(str);
        }

        String feld = "Problemfall_Gewerblich";
        if(!list.contains(feld))
        {
            addField(docID, "Problemfall_Gewerblich", true);
        }
    }

    /**
     * Der SolrUpdater fügt der Datenbank eine neues Feld hinzu und füllt dieses mit den eingegebenen Daten
     * @param  docID Die ID, den Primärschlüssel, als String
     * @param  istMieter Wenn es sich um einen Mieter handelt true, sonst false
     */
    public void addRechtsexpertenfeldMieter(String docID, Object istMieter)
    {
        addField(docID, "Rechtsexperten_istmieter", istMieter);
    }

    /**
     * Der SolrUpdater fügt der Datenbank eine neues Feld hinzu und füllt dieses mit den eingegebenen Daten
     * @param  docID Die ID, den Primärschlüssel, als String
     * @param  istMieter Wenn es sich um einen Mieter handelt true, sonst false
     */
    public void addRechtsexpertenfeldMieter2(String docID, Object istMieter)
    {
        addField(docID, "Rechtsexperten_istmieter2", istMieter);
    }

    public void addRechtsexpertenfeldGewerblich(String docID, boolean istGewerblich)
    {
        addField(docID, "Rechtsexperten_istgewerblich", istGewerblich);
    }

    public void addRechtsexpertenfeldGewerblich2(String docID, boolean istGewerblich)
    {
        addField(docID, "Rechtsexperten_istgewerblich2", istGewerblich);
    }

    /**
     *Es wird ein neues Feld in Solr erzeugt und mit einem eingegebenen Wert gefüllt
     * @param docID Die DokumentenID, der Primärschlüssel
     * @param fieldName Der Name des Feldes als String
     * @param object Der Wert, der dem Feld hinzugefügt werden soll
     */
    public void addField(String docID, String fieldName, Object object)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+ docID);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docID);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        SolrInputDocument inputDocument = new SolrInputDocument();
        Collection<String> feldnamensliste = oldDoc.getFieldNames();
        ArrayList<String> list = new ArrayList<String>();
        for (String str:feldnamensliste)
        {
            list.add(str);
        }

        for (int i=0; i<list.size();i++)
        {
            inputDocument.addField(list.get(i), oldDoc.getFieldValue(list.get(i)));
        }

        inputDocument.addField(fieldName, object);
        try {
            client.add(inputDocument);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            client.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Es können gezielt Felder per ID in der Datenbank aufgerufen und verändert werden
     * @param fieldName Ein Feldnamen
     * @param docID Eine ID als String
     * @param object Eine zu setzende Änderunng
     **/

    public void changeValueByField(String docID, String fieldName, Object object)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+docID);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docID);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        SolrInputDocument inputDocument = new SolrInputDocument();
        Collection<String> feldnamensliste = oldDoc.getFieldNames();
        ArrayList<String> list = new ArrayList<String>();
        for (String str:feldnamensliste)
        {
            list.add(str);
        }

        for (int i=0; i<list.size();i++)
        {
            inputDocument.addField(list.get(i), oldDoc.getFieldValue(list.get(i)));
        }

        inputDocument.getField(fieldName).setValue(object, 1.0f);
        try {
            client.add(inputDocument);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            client.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Die Methode liest die Textdatei "outputID.txt" ein und gibt eine Arrayliste zurück
     * @return
     */
    public ArrayList idEinleser()
    {
        ArrayList arrayList = new ArrayList();

        InputStream input = getClass().getClassLoader().getResourceAsStream("outputID.txt");

        BufferedReader TSVFile = null;
        try {
            TSVFile = new BufferedReader(
                    new InputStreamReader(input));
            String dataRow = null; // Read first line

            try {
                dataRow = TSVFile.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (dataRow != null)
            {
                String data = dataRow.trim();
                if (!data.isEmpty())
                {
                    arrayList.add(data);
                }
                dataRow = TSVFile.readLine(); // Read next line of data.
            }
            TSVFile.close();
        }catch (FileNotFoundException e) {
            System.err.println("Die Datei konnte nicht geöffnet werden");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /**
     * Die Methode gibt die entsprechende Frage zu einer gegebenen ID zurück, um sie für Klassifikationen einlesen
     * zu können
     * @param docId
     * @return
     */
    public Object fragenAusgeber(String docId)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+docId);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docId);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        Object question = oldDoc.getFieldValue("t_message");
        return question;
    }

    /**
     * Die Methode aktualisiert in Solr alle Felder "Expertensystem_istmieter" und "Expertensystem_wert", indem neue
     * Klassifizierungen durchgeführt und die alten damit überschrieben werden
     */
    public void changeExpertensystemFields()
    {
        ArrayList arrayList = new ArrayList();
        arrayList = idEinleser();

        for(int i = 0; i<arrayList.size(); i++)
        {
            Object question = fragenAusgeber(arrayList.get(i).toString());
            MieterClassifier mieterClassifier = new MieterClassifier();
            Object value = mieterClassifier.istHauptklasse(question.toString());
            Object value2 = mieterClassifier.classify(question.toString());
            changeValueByField(arrayList.get(i).toString(), "Expertensystem_istmieter", value);
            changeValueByField(arrayList.get(i).toString(), "Expertensystem_wert", value2);
        }
    }

    /**
     * Die Methode aktualisiert in Solr alle Felder "Watson_istmieter" und "Watson", indem neue Klassifizierungen durchgeführt
     * und die alten damit überschrieben werden
     */
    public void changeWatsonFields()
    {
        ArrayList arrayList = new ArrayList();
        arrayList = idEinleser();

        for(int i = 0; i<arrayList.size(); i++)
        {
            Object question = fragenAusgeber(arrayList.get(i).toString());
            WatsonMieterClassifier watsonmieterClassifier = new WatsonMieterClassifier();
            Object value = watsonmieterClassifier.classify(question.toString());
            Object value2 = watsonmieterClassifier.istHauptklasse(question.toString());
            changeValueByField(arrayList.get(i).toString(), "Watson_istmieter", value2);
            changeValueByField(arrayList.get(i).toString(), "Watson", value);
        }
    }

    /**
     * Anhand einer ID wird das JSON-Objekt aus Solr gelöscht
     * @param docID Eine ID als String
     */
    public void solrDeleteByID(String docID)
    {
        try {
            client.deleteById(docID);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            client.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Die Methode nimmt einen Feldnamen entgegen und vergleicht ihn mit dem Feld "Preis"
     * @param fieldName Nimmt einen Feldnamen entgegen, um den Wert des Feldes dem Feld "Preis" zu vergleichen
     * @return
     */
    public String comparer(String fieldName) {
        StringBuilder sb = new StringBuilder();
        SolrQuery query = new SolrQuery();
        query.set("q", "*:*");
        query.setRows(10001);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SolrDocumentList results = response.getResults();
        Object doc = "";
        Object doc1 = "";
        ArrayList<Object> array1 = new ArrayList<Object>();
        ArrayList<Object> array2 = new ArrayList<Object>();
        for (SolrDocument document : results) {
            doc = ((List)document.getFieldValue(fieldName)).get(0);
            array1.add(doc);
            doc1 = ((List)document.getFieldValue("price")).get(0);
            array2.add(doc1);
        }

        HashMap<Integer, String> hmap = new HashMap<Integer, String>();
        for(int i=0;i<array1.size(); i++)
        {
            int key = Integer.valueOf((array1.get(i)).toString());
            hmap.put(key, array2.get(i).toString());
        }

        hmap = removeXValuesFromHashMap(20, hmap);

        Map<Integer, String> map = new TreeMap<Integer, String>(hmap);
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        while(iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry)iterator2.next();
            sb.append("[" + me2.getKey() + "," + me2.getValue()+ "],");
        }
        return sb.toString().substring(0,sb.length()-1);
    }

    /**
     *
     * @param anzahl
     * @param hmap
     * @return
     */
    HashMap removeXValuesFromHashMap(int anzahl, HashMap hmap)
    {
        Iterator<Integer> iterator = hmap.keySet().iterator();

        for(int i= 0; i<=anzahl;i++)
        {
            iterator =hmap.keySet().iterator();
            int value = 0;
            int tmp;
            while (iterator.hasNext())
            {
                tmp = iterator.next();
                if (tmp > value)
                {
                    value = tmp;
                }
            }
            hmap.remove(value);
        }
        return hmap;
    }

    /**
     * Es wird ein String erstellt, der aufsteigend nach Dauer sortiert eine Reihe von [Dauer, Preis] Substrings
     * enthält
     * @return
     */
    public String dauerPreisComparer()
    {
        return comparer("t_time");
    }

    /**
     * Es wird ein String erstellt, der aufsteigend nach Dauer sortiert eine Reihe von [Fragelänge, Preis] Substrings
     * enthält
     * @return
     */
    public String fragelängePreisComparer()
    {
        return comparer("t_length");
    }

    /**
     * Eine allgemeine Methode um Übereinstimmungen zwischen den Listen oder  Watson mit den Rechtsexperten übereinstimmt
     * @param fieldname1
     * @param param1
     * @param param2
     * @return
     */
    public int getÜbereinstimmung(String fieldname1, Object param1, Object param2)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", ""+fieldname1+":"+param1+" AND "+"Rechtsexperten_istmieter"+":"+param2);
        query.setRows(10001);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SolrDocumentList results = response.getResults();
        long key = results.getNumFound();
        int keyInt = toIntExact(key);
        return keyInt;
    }

    /**
     * Ermittelt, wie häufig Watson mit den Rechtsexperten mit jeweils true übereinstimmt
     * @return
     */
    public int getWatson11()
    {
        return getÜbereinstimmung("Watson_istmieter", true, true);
    }

    /**
     * Ermittelt, wie häufig Watson mit den Rechtsexperten mit jeweils false übereinstimmt
     * @return
     */
    public int getWatson22()
    {
        return getÜbereinstimmung("Watson_istmieter", false, false);
    }

    /**
     * Ermittelt, wie häufig Watson mit den Rechtsexperten nicht übereinstimmt, da Watson true sagt und die Rechtsexperten
     * sagen false
     * @return
     */
    public int getWatson12()
    {
        return getÜbereinstimmung("Watson_istmieter", true, false);
    }

    /**
     * Ermittelt, wie häufig Watson mit den Rechtsexperten nicht übereinstimmt, da Watson false sagt und die Rechtsexperten
     * sagen true
     * @return
     */
    public int getWatson21()
    {
        return getÜbereinstimmung("Watson_istmieter",false, true);
    }

    /**
     * Ermittelt, wie häufig die Listen mit den Rechtsexperten übereinstimmen mit jeweils true
     * @return
     */
    public int getListe11()
    {
        return getÜbereinstimmung("Expertensystem_istmieter", true, true);
    }

    /**
     * Ermittelt, wie häufig die Listen mit den Rechtsexperten übereinstimmen mit jeweils false
     * @return
     */
    public int getListe22()
    {
        return getÜbereinstimmung("Expertensystem_istmieter", false, false);
    }

    /**
     * Ermittelt, wie häufig die Listen mit den Rechtsexperten nicht übereinstimmt, da die Listen true und die
     * Rechtsexperten false sagen
     * @return
     */
    public int getListe12()
    {
        return getÜbereinstimmung("Expertensystem_istmieter",true, false);
    }

    /**
     * Ermittelt, wie häufig die Listen mit den Rechtsexperten nicht übereinstimmt, da die Listen false und die
     * Rechtsexperten true sagen
     * @return
     */
    public int getListe21()
    {
        return getÜbereinstimmung("Expertensystem_istmieter",false, true)-getAnzahlProblemfälle();
    }

    /**
     * Ermittelt, wie häufig die Listen ohne Bereinigung der Problemfälle mit den Rechtsexperten nicht übereinstimmt,
     * da die Listen false und die Rechtsexperten true sagen
     * @return
     */
    public int getListe21Alle()
    {
        return getÜbereinstimmung("Expertensystem_istmieter", false, true);
    }

    /**
     * Gibt die Gesamtzahl der Felder "Rechtsexperten_istmieter" zurück
     * @return
     */
    public int getAnzahlRechtsexpertenfelder()
    {
        SolrConnect solrconnect = new SolrConnect();
        SolrQuery query = new SolrQuery();
        query.set("q", "Rechtsexperten_istmieter:*");
        query.setRows(10001);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SolrDocumentList results = response.getResults();
        long key = results.getNumFound();
        int keyInt = toIntExact(key);
        return keyInt;
    }

    /**
     * Mithilfe der Methode lässt sich prüfen, ob es sich bei der gegebenen ID um einen Problemfall handelt
     * @param
     * @return
     */
    public boolean istProblemfall(String docId)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+docId);
        query.setRows(10001);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);

        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docId);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        String fieldValue = oldDoc.getFieldValue("Expertensystem_wert").toString();
        if(fieldValue.compareTo("[0.5]") == 0)
        {
            return true;
        }
        return false;
    }

    /**
     * Gibt die Anzahl an Problemfällen, bei denen im Expertensystem der Wert 0.5 beträgt, zurück
     * @return
     */
    public int getAnzahlProblemfälle()
    {
        SolrConnect solrconnect = new SolrConnect();
        SolrQuery query = new SolrQuery();
        query.set("q", "Expertensystem_wert:"+0.5+" AND "+"Rechtsexperten_istmieter:"+true);
        query.setRows(10001);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SolrDocumentList results = response.getResults();
        long key = results.getNumFound();
        int keyInt = toIntExact(key);
        return keyInt;
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
        if(getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / (richtige + falneg);
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     * Gibt die Trefferquote (richtig positiv geteilt durch richtig positiv plus falsch negativ) von Watson aus
     * @return
     */
    public String getTrefferquoteWatson()
    {
        int richtige = getWatson11();
        int falneg = getWatson21();
        DecimalFormat f = new DecimalFormat("0.00");
        if(getAnzahlRechtsexpertenfelder()>0)
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
        if(getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / (richtige + falpo);
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     * Die Methode gibt die Genauigkeit (richtig positiv geteilt durch richtig positiv plus falsch positiv) von Watson zurück
     * @return
     */
    public String getGenauigkeitWatson()
    {
        int richtige = getWatson11();
        int falpo = getWatson12();
        DecimalFormat f = new DecimalFormat("0.00");
        if(getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / (richtige + falpo);
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     *
     * @return
     */
    public String getKorrektklassifikationsrateListen()
    {
        int richtige = getListe11()+getListe22();
        int alle = getAnzahlRechtsexpertenfelder()-getAnzahlProblemfälle();
        DecimalFormat f = new DecimalFormat("0.00");
        if(getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / alle;
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     *
     * @return
     */
    public String getKorrektklassifikationsrateWatson() {
        int richtige = getWatson11()+getWatson22();
        int alle = getAnzahlRechtsexpertenfelder();
        DecimalFormat f = new DecimalFormat("0.00");
        if (getAnzahlRechtsexpertenfelder() > 0) {
            float genauigkeit = (float) richtige / alle;
            return (f.format(genauigkeit * 100));
        }
        return "-1";
    }

    /**
     *
     * @return
     */
    public String getFalschklassifikationsrateListen()
    {
        int richtige = getListe12()+getListe21();
        int alle = getAnzahlRechtsexpertenfelder()-getAnzahlProblemfälle();
        DecimalFormat f = new DecimalFormat("0.00");
        if(getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / alle;
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     *
     * @return
     */
    public String getFalschklassifikationsrateWatson()
    {
        int richtige = getWatson12()+getWatson21();
        int alle = getAnzahlRechtsexpertenfelder();
        DecimalFormat f = new DecimalFormat("0.00");
        if(getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / alle;
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     *
     * @return
     */
    public String getAlleFalschklassifikationsrateListen()
    {
        int richtige = getListe12()+getListe21Alle();
        int alle = getAnzahlRechtsexpertenfelder();
        DecimalFormat f = new DecimalFormat("0.00");
        if(getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / alle;
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     *
     * @return
     */
    public String getAlleKorrektklassifikationsrateListen()
    {
        int richtige = getListe11()+getListe22();
        int alle = getAnzahlRechtsexpertenfelder();
        DecimalFormat f = new DecimalFormat("0.00");
        if(getAnzahlRechtsexpertenfelder()>0)
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
        if(getAnzahlRechtsexpertenfelder()>0)
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
        if(getAnzahlRechtsexpertenfelder()>0)
        {
            float genauigkeit = (float) richtige / (richtige + falneg);
            return (f.format(genauigkeit*100));
        }
        return "-1";
    }

    /**
     *
     * @param text
     * @return
     */
    public boolean istProblemfallMieter(String text)
    {
        MieterClassifier mieterClassifier = new MieterClassifier();
        double wert = mieterClassifier.classify(text);
        if(wert == 0.5)
        {
            return true;
        }
        return false;
    }

    /**
     *
     */
    public void istProblemfallGewerblich(String text)  //Boolean!!!
    {
        //GewerblichClassifier gewerblichClassifier = new GewerblichClassifier;
        //double wert = gewerblichClassifier.classify(text);
        //if(wert == 0.5)
        //{
        //    return true
        //}
        //return false;
    }

    /**
     *
     * @param text
     * @return
     */
    public String absendeButtonPushed(String text)
    {
        Preisempfehlungsberechner preisempfehlungsberechner = new Preisempfehlungsberechner();
        double param1 = 0.00;
        try
        {
            param1 = preisempfehlungsberechner.getPrice(text);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String param2 = "";
        if(istProblemfallMieter(text)) //&& !istProblemfallGewerblich())
        {
            param2 = "Möchten Sie vielleicht angeben ob sie Mieter/in oder Vermieter/in sind, falls Sie das noch " +
                    "nicht getan haben?";
        }

        else if(!istProblemfallMieter(text)) //&& istProblemfallGewerblich())
        {
            param2 = "Möchten Sie vielleicht angeben ob die Nutzung gewerblich oder privat ist, falls Sie das noch " +
                    "nicht getan haben und für relevant halten?";
        }

        else if(istProblemfallMieter(text)) //&& istProblemfallGewerblich())
        {
            param2 = "Möchten Sie vielleicht angeben ob sie Mieter/in oder Vermieter/in sind, falls Sie das noch " +
                    "nicht getan haben?" +
                    "Möchten Sie vielleicht zusätzlich angeben ob die Nutzung gewerblich oder privat ist, falls Sie das noch " +
                    "nicht getan haben und für relevant halten?";
        }

        return param1 + param2;
    }
}