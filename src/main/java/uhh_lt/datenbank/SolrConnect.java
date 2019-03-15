package uhh_lt.datenbank;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.json.simple.JSONObject;
import uhh_lt.classifier.GewerblichClassifier;
import uhh_lt.classifier.MieterClassifier;
import uhh_lt.classifier.WatsonMieterClassifier;
import uhh_lt.webserver.Datendifferenzberechner;
import uhh_lt.webserver.JsonImport;
import uhh_lt.webserver.Komplexitätsberechner;

import java.io.*;
import java.util.*;

import static java.lang.Math.toIntExact;
import static java.lang.String.valueOf;
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

    /**
     * Liest Daten in die Solr Datenbank ein
     * @param object Ein JSON Object
     * @param commit Wenn true wird committed
     */
    public void store(JSONObject object, boolean commit)
    {
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
        inputDocument.addField("t_time", Datendifferenzberechner.differenz((String)object.get("T_Date"),(String)object.get("R_posted")));
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
        catch (SolrServerException | IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Committed Daten in die Solr Datenbank
     */
    public void commit() {
        try {
            client.commit();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Führt eine Datenbankanfrage durch
     * @param searchTerm Ein Suchbegriff
     * @return die Datenbankeinträge zu einem gegebenen Suchbegriff werden ausgegeben
     */
    public String search(String searchTerm)
    {
        SolrQuery query = new SolrQuery();
        query.setQuery(searchTerm);
        query.setFields("id");
        query.setStart(0);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        StringBuilder out = new StringBuilder();
        assert response != null;
        SolrDocumentList results = response.getResults();
        for (SolrDocument result : results) {
            out.append(result).append("\n");
        }
        return out.toString();
    }

    /**
     * Gibt an ob schon zwei Datenbankeinträge zum Mieter bestehen
     * @param id die ID eines Datenbankeintags
     * @return true, wenn bereits zwei Felder bestehen und somit vollständig annotiert ist
     */
    public boolean isFullyAnnotatedMieter(String id)
    {
        SolrQuery query = new SolrQuery();
        query.setQuery("id:" + id + "AND Rechtsexperten_istmieter2:*");
        QueryResponse response = null;

        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        return response.getResults().size() <= 0;
    }

    /**
     * Gibt an ob schon zwei Datenbankeinträge zu Gewerblich bestehen
     * @param id die ID eines Datenbankeintags
     * @return true, wenn bereits zwei Felder bestehen und somit vollständig annotiert ist
     */
    public boolean isFullyAnnotatedGewerblich(String id){
        SolrQuery query = new SolrQuery();
        query.setQuery("id:" + id + "AND Rechtsexperten_istgewerblich2:*");
        QueryResponse response = null;

        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        return response.getResults().size() <= 0;
    }

    /**
     * Holt die Kundenfrage zu einer gegebenen ID aus der Datenbank
     * @param id Die ID eines Datenbankeintags
     * @return die Kundenanfrage zu der gegebenen ID
     */
    public String getFrage(String id) {
    SolrQuery query = new SolrQuery();
    query.setQuery("id:" + id).setFields("t_message").setStart(0).setRows(10000);
    QueryResponse response = null;

        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        SolrDocumentList results = response.getResults();
        return valueOf(results.get(0).get("t_message"));
    }

    /**
     * Gibt den Preis einer Frage anhand ihrer ID aus
     * @param id die ID von der Frage
     */
    public String getPreis(String id) {
        SolrQuery query = new SolrQuery();
        query.setQuery("id:" + id).setFields("price").setStart(0).setRows(10);

        QueryResponse response = null;

        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        SolrDocumentList results = response.getResults();
        Object doc;
        ArrayList<Object> array1 = new ArrayList<>();
        for (SolrDocument document : results) {
            doc = ((List)document.getFieldValue("price")).get(0);
            array1.add(doc);
        }
        return valueOf(array1).replace("[", "").replace("]", "");

    }

    /**
     * Erstellt ein Textfile mit allen IDs
     */
    public void printIdInDoc() throws IOException
    {
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*").setFields("id").setStart(0).setRows(10000);
        QueryResponse response = null;

        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        SolrDocumentList results = response.getResults();
        FileWriter fw = new FileWriter("resources/outputID.txt");
        for (SolrDocument result : results) {
            System.out.println(result);
            fw.write(valueOf(result.get("id")));
            fw.write("\n");
        }
        fw.close();
    }

    /**
     * Wenn der Mieter- oder Vermieterbutton gedrückt wurde, wird entweder ein neues Feld "Rechtsexperten_istmieter" oder
     * "Rechtsexperten_istmieter2" angelegt und mit dem entsprechenden Wert gefüllt oder es wird nichts getan
     * @param docID  die ID, den Primärschlüssel, als String
     * @param istMieter  wenn es sich um einen Mieter handelt true, sonst false
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

        assert response != null;
        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);

        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docID);
        }

        SolrDocument oldDoc = response.getResults().get(0);

        Collection<String> feldnamensliste = oldDoc.getFieldNames();
        ArrayList<String> list = new ArrayList<>();

        list.addAll(feldnamensliste);

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
     * @param docID die DokumentenID, der Primärschlüssel
     */
    public void mieterProblemfallButtonPushed(String docID)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+ docID);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docID);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        Collection<String> feldnamensliste = oldDoc.getFieldNames();
        ArrayList<String> list = new ArrayList<>();
        list.addAll(feldnamensliste);

        String feld = "Problemfall";
        if(!list.contains(feld))
        {
            addField(docID, "Problemfall", true);
        }
    }

    /**
     * Wenn der Gewerblichbutton gedrückt wurde, wird entweder ein neues Feld "Rechtsexperten_istgewerblich" oder
     * "Rechtsexperten_istgewerblich2" angelegt und mit dem entsprechenden Wert gefüllt oder es wird nichts getan
     * @param docID die ID eines Datenbankeintrags
     * @param istGewerblich falls gewerblich true, falls privat false
     */
    public void gewerblichButtonsPushed(String docID, boolean istGewerblich)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+ docID);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docID);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        Collection<String> feldnamensliste = oldDoc.getFieldNames();
        ArrayList<String> list = new ArrayList<>();
        list.addAll(feldnamensliste);

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

    /**
     * Fügt in die Solr Datenbank ein neues Feld Problemfall_Gewerblich hinzu und setzt dieses auf true
     * @param docID die ID, den Primärschlüssel, als String
     */
    public void gewerblichProblemfallButtonPushed(String docID)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+ docID);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docID);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        Collection<String> feldnamensliste = oldDoc.getFieldNames();
        ArrayList<String> list = new ArrayList<>();
        list.addAll(feldnamensliste);

        String feld = "Problemfall_Gewerblich";
        if(!list.contains(feld))
        {
            addField(docID, "Problemfall_Gewerblich", true);
        }
    }

    /**
     * Der SolrUpdater fügt der Datenbank eine neues Feld Rechtsexperten_istmieter hinzu
     * @param  docID die ID, den Primärschlüssel, als String
     * @param  istMieter wenn es sich um einen Mieter handelt true, sonst false
     */
    void addRechtsexpertenfeldMieter(String docID, Object istMieter)
    {
        addField(docID, "Rechtsexperten_istmieter", istMieter);
    }

    /**
     * Der SolrUpdater fügt der Datenbank eine neues Feld Rechtsexperten_istmieter2 hinzu
     * @param  docID die ID, den Primärschlüssel, als String
     * @param  istMieter wenn es sich um einen Mieter handelt true, sonst false
     */
    void addRechtsexpertenfeldMieter2(String docID, Object istMieter)
    {
        addField(docID, "Rechtsexperten_istmieter2", istMieter);
    }

    /**
     * Der SolrUpdater fügt der Datenbank eine neues Feld Rechtsexperten_istgewerblich hinzu
     * @param docID die ID, den Primärschlüssel, als String
     * @param istGewerblich true falls gewerblich, false falls privat
     */
    void addRechtsexpertenfeldGewerblich(String docID, boolean istGewerblich)
    {
        addField(docID, "Rechtsexperten_istgewerblich", istGewerblich);
    }

    /**
     * Der SolrUpdater fügt der Datenbank eine neues Feld Rechtsexperten_istgewerblich2 hinzu
     * @param docID die ID, den Primärschlüssel, als String
     * @param istGewerblich true falls gewerblich, false falls privat
     */
    void addRechtsexpertenfeldGewerblich2(String docID, boolean istGewerblich)
    {
        addField(docID, "Rechtsexperten_istgewerblich2", istGewerblich);
    }

    /**
     * Es wird ein neues Feld in Solr erzeugt und mit einem eingegebenen Wert gefüllt
     * @param docID die DokumentenID, der Primärschlüssel
     * @param fieldName der Name des Feldes als String
     * @param object der Wert, der dem Feld hinzugefügt werden soll
     */
    void addField(String docID, String fieldName, Object object)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+ docID);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docID);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        SolrInputDocument inputDocument = new SolrInputDocument();
        Collection<String> feldnamensliste = oldDoc.getFieldNames();
        ArrayList<String> list = new ArrayList<>();
        list.addAll(feldnamensliste);

        for (String s : list)
        {
            inputDocument.addField(s, oldDoc.getFieldValue(s));
        }

        inputDocument.addField(fieldName, object);
        try
        {
            client.add(inputDocument);
        }
        catch (SolrServerException | IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            client.commit();
        }
        catch (SolrServerException | IOException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Fügt allen JSON Objekten in Solr ein neues Feld mit dem eingegebnen Feldnamen hinzu
     */
    public void addFieldForAllIDs()
    {
        ArrayList arrayList;
        arrayList = idEinleser();

        for (Object o : arrayList)
        {
            Object question = fragenAusgeber(o.toString());
            GewerblichClassifier gewerblichClassifier = new GewerblichClassifier();
            Object value = gewerblichClassifier.istHauptklasse(question.toString());
            addField(o.toString(), "Expertensystem_istgewerblich", value);
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
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docID);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        SolrInputDocument inputDocument = new SolrInputDocument();
        Collection<String> feldnamensliste = oldDoc.getFieldNames();
        ArrayList<String> list = new ArrayList<>();
        list.addAll(feldnamensliste);

        for (String s : list) {
            inputDocument.addField(s, oldDoc.getFieldValue(s));
        }

        inputDocument.getField(fieldName).setValue(object, 1.0f);
        try {
            client.add(inputDocument);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        try {
            client.commit();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Die Methode liest die Textdatei "outputID.txt" ein und gibt eine Arrayliste zurück
     * @return gibt eine Arraylist mit allen IDs zurück
     */
    public ArrayList idEinleser()
    {
        ArrayList arrayList = new ArrayList();

        InputStream input = getClass().getClassLoader().getResourceAsStream("outputID.txt");

        BufferedReader TSVFile = null;
        try {
            assert input != null;
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
     * @param docId die DokumentenID, der Primärschlüssel
     * @return gibt die Kundenanfrage zu einer gegebenen ID zurück
     */
    public Object fragenAusgeber(String docId)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+docId);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        assert response != null;
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
        ArrayList arrayList;
        arrayList = idEinleser();

        for (Object o : arrayList) {
            Object question = fragenAusgeber(o.toString());
            MieterClassifier mieterClassifier = new MieterClassifier();
            Object value = mieterClassifier.istHauptklasse(question.toString());
            Object value2 = mieterClassifier.classify(question.toString());
            changeValueByField(o.toString(), "Expertensystem_istmieter", value);
            changeValueByField(o.toString(), "Expertensystem_wert", value2);
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

        for (Object o : arrayList) {
            Object question = fragenAusgeber(o.toString());
            WatsonMieterClassifier watsonmieterClassifier = new WatsonMieterClassifier();
            Object value = watsonmieterClassifier.classify(question.toString());
            Object value2 = watsonmieterClassifier.istHauptklasse(question.toString());
            changeValueByField(o.toString(), "Watson_istmieter", value2);
            changeValueByField(o.toString(), "Watson", value);
        }
    }

    /**
     * Anhand einer ID wird das JSON-Objekt aus Solr gelöscht
     * @param docID eine ID als String
     */
    public void solrDeleteByID(String docID)
    {
        try {
            client.deleteById(docID);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        try {
            client.commit();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Eine allgemeine Methode um die Anzahl an Übereinstimmungen zwischen den Listen oder Watson mit den
     * Rechtsexperten zu erhalten
     * @param fieldname ein Watson- oder ein Expertensystemfeld
     * @param param1 einen true- oder false-Wert zu dem fieldnamen
     * @param param2 ein true- oder false-Wert zu dem Rechtsexperten_istmieter-Feld
     * @return gibt die Zahl der übereinstimmenden Fälle an
     */
    int getUebereinstimmung(String fieldname, Object param1, Object param2)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", ""+fieldname+":"+param1+" AND "+"Rechtsexperten_istmieter"+":"+param2);
        query.setRows(10001);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        assert response != null;
        SolrDocumentList results = response.getResults();
        long key = results.getNumFound();
        int keyInt = toIntExact(key);
        return keyInt;
    }

    /**
     *
     */
    public int getAnzahlProblemfaelleOhneRechtsexpertenfeldMieter()
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "Problemfall:* AND !Rechtsexperten_istmieter:*");
        query.setRows(10001);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        assert response != null;
        SolrDocumentList results = response.getResults();
        long key = results.getNumFound();
        int keyInt = toIntExact(key);
        System.out.println(keyInt);
        return keyInt;
    }

    /**
     * Die Methode nimmt einen Feldnamen entgegen und vergleicht ihn mit dem Feld "Preis"
     * @param fieldName Nimmt einen Feldnamen entgegen, um den Wert des Feldes dem Feld "Preis" zu vergleichen
     * @return gibt einen aufsteigend geordneten String im Format [eines Feldwert, Preis] ... zurück
     */
    String comparer(String fieldName) {
        StringBuilder sb = new StringBuilder();
        SolrQuery query = new SolrQuery();
        query.set("q", "*:*");
        query.setRows(10001);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        SolrDocumentList results = response.getResults();
        Object doc;
        Object doc1;
        ArrayList<Object> array1 = new ArrayList<>();
        ArrayList<Object> array2 = new ArrayList<>();
        for (SolrDocument document : results) {
            doc = ((List)document.getFieldValue(fieldName)).get(0);
            array1.add(doc);
            doc1 = ((List)document.getFieldValue("price")).get(0);
            array2.add(doc1);
        }

        HashMap<Integer, String> hmap = new HashMap<>();
        for(int i=0;i<array1.size(); i++)
        {
            int key = Integer.valueOf((array1.get(i)).toString());
            hmap.put(key, array2.get(i).toString());
        }

        hmap = removeXValuesFromHashMap(30, hmap);

        Map<Integer, String> map = new TreeMap<>(hmap);
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        while(iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry)iterator2.next();
            sb.append("[" + me2.getKey() + "," + me2.getValue()+ "],");
        }
        return sb.toString().substring(0,sb.length()-1);
    }

    /**
     * Entfernt eine eingegebene Anzahl an höchsten Werten aus einer Hashmap zur Entfernung von Ausreißern
     * @param anzahl eine Anzahl an zu entfernenden Werten
     * @param hmap eine Hashmap
     * @return die um die gegebene Anzahl reduzierte Hashmap
     */
    private HashMap removeXValuesFromHashMap(int anzahl, HashMap hmap)
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
     * Gibt die Gesamtzahl der Felder Rechtsexperten_istmieter zurück
     * @return die Gesamtzahl der Felder Rechtsexperten_istmieter
     */
    public int getAnzahlRechtsexpertenfelder()
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "Rechtsexperten_istmieter:*");
        query.setRows(10001);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        assert response != null;
        SolrDocumentList results = response.getResults();
        long key = results.getNumFound();
        int keyInt = toIntExact(key);
        return keyInt;
    }

    /**
     * Mithilfe der Methode lässt sich prüfen, ob es sich bei der gegebenen ID um einen Problemfall handelt
     * @param docId eine Id als Primärschlüssel
     * @return true, falls Problemfall
     */
    public boolean istProblemfall(String docId)
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:"+docId);
        query.setRows(10001);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        assert response != null;
        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);

        for (SolrDocument doc : docList)
        {
            assertEquals((String) doc.getFieldValue("id"), docId);
        }

        SolrDocument oldDoc = response.getResults().get(0);
        String fieldValue = oldDoc.getFieldValue("Expertensystem_wert").toString();
        return fieldValue.compareTo("[0.5]") == 0;
    }

    /**
     * Gibt die Anzahl an Mieterproblemfällen, bei denen im Expertensystem der Wert 0.5 beträgt, zurück
     * @return gibt Anzahl der Problemfälle für Mieter zurück
     */
    public int getAnzahlProblemfaelle()
    {
        SolrQuery query = new SolrQuery();
        query.set("q", "Expertensystem_wert:"+0.5+" AND "+"Rechtsexperten_istmieter:"+true);
        query.setRows(10001);
        QueryResponse response = null;
        try {
            response = client.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        assert response != null;
        SolrDocumentList results = response.getResults();
        long key = results.getNumFound();
        int keyInt = toIntExact(key);
        return keyInt;
    }

    /**
     * Stellt zu einem gegebenen Text fest, ob es sich um einen Mieterproblemfall handelt
     * @param text ein Text
     * @return true, falls Mieterproblemfall
     */
    private boolean istProblemfallMieter(String text)
    {
        MieterClassifier mieterClassifier = new MieterClassifier();
        double wert = mieterClassifier.classify(text);
        return wert == 0.5;
    }

    /**
     * Stellt zu einem gegebenen Text fest, ob es sich um einen Gewerblichproblemfall handelt
     * @return true, falls Gewerblichproblemfall
     */
    private Boolean istProblemfallGewerblich(String text)
    {
        GewerblichClassifier gewerblichClassifier = new GewerblichClassifier();
        double wert = gewerblichClassifier.classify(text);
        return wert == 0.5;
    }

    /**
     * Wenn der Absendebutton im Webinterface gedrückt wurde, wird diese Methode ausgeführt
     * @param text eine angegebene Frage
     * //@return eine Preisempfehlung und eventuelle Nachfragen
     */
    public String ueberpruefenButtonPushed(String text)
    {
        String param = "Vielen Dank für Ihre Frage";
        if(istProblemfallMieter(text)&& !istProblemfallGewerblich(text))
        {
            param = "Möchten Sie angeben ob sie Mieter/in oder Vermieter/in sind, falls Sie das noch " +
                    "nicht getan haben?";
        }

        else if(!istProblemfallMieter(text) && istProblemfallGewerblich(text))
        {
            param = "Möchten Sie angeben ob die Nutzung gewerblich oder privat ist, falls Sie das noch " +
                    "nicht getan haben und für relevant halten?";
        }

        else if(istProblemfallMieter(text) && istProblemfallGewerblich(text))
        {
            param = "Möchten Sie angeben ob sie Mieter/in oder Vermieter/in sind, falls Sie das noch " +
                    "nicht getan haben?" +
                    "Möchten Sie zusätzlich angeben ob die Nutzung gewerblich oder privat ist, falls Sie das noch " +
                    "nicht getan haben und für relevant halten?";
        }
        return param;
    }
}