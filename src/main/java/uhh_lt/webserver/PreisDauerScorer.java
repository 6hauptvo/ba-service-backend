package uhh_lt.webserver;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class PreisDauerScorer
{

    //public int scoring()
    public static void main(String[] args)
    {
        JSONParser parser = new JSONParser();
        Object obj = null;
        try
        {
            obj = parser.parse(new FileReader("resources/mietrechtexport1000-2.json"));
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        catch (ParseException e)
        {
            e.printStackTrace();
        }

        JSONObject jsonObject = (JSONObject) obj;
        JSONArray arr = (JSONArray) jsonObject.get("data");

        for (int i = 0; i < arr.size(); i++) //liest die ganze array von objekt
        {
            JSONObject objekt  = (JSONObject) arr.get(i);

                System.out.println(objekt.toJSONString());

        }


    }
}
