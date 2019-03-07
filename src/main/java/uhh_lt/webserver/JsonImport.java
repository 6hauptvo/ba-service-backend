package uhh_lt.webserver;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import uhh_lt.datenbank.SolrConnect;

import java.io.FileReader;
import java.io.IOException;

//Data [
//{
//"Topic_id"
//"T_Date"
//"T_Subject"
//"T_Price":
//"T_Message
//"T_Summary"
//"R_posted
//"R_Message"
//}]
/**
 * Die Json File in the System einzulesen
 ** **/
public class JsonImport {


    public static void main(String[] args)

    {   JSONParser parser = new JSONParser();
        SolrConnect connect = new SolrConnect();

        Object obj = null;
        try {
            obj = parser.parse(new FileReader("resources/mietrechtexport1000-2-sample.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray arr = (JSONArray) jsonObject.get("data");

        for (int i = 0; i < arr.size(); i++) //liest die ganze array von objekt & speichern die in Solr
        {
            JSONObject objekt  = (JSONObject) arr.get(i);
            connect.store(objekt, false);
            if (i % 100 == 0) {
                connect.commit();
            }
        }
        connect.commit();
    }
}
