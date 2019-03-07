package uhh_lt.webserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uhh_lt.classifier.MieterClassifier;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

//@RestController
@Controller
@EnableAutoConfiguration
@SpringBootApplication
public class ApplicationController  extends SpringBootServletInitializer {

    private static MieterClassifier mieterClassifier = new MieterClassifier();
    private static SolrConnect solrConnect = new SolrConnect();

    /**
     * Runs the RESTful server.
     *
     * @param args execution arguments
     */
    public static void main(String[] args) {

        SpringApplication.run(ApplicationController.class, args);
    }

    @RequestMapping("/classify")
    String classify(@RequestParam(value = "text", defaultValue = "") String text, @RequestParam(value = "format", defaultValue = "text") String format)
    {

        text = text.replace("\r", " ").replace("\n", " ").trim();

        mieterClassifier.classify(text);

        return mieterClassifier.getMieterwahrscheinlichkeitAsString();
    }

    @RequestMapping("/search")
    String search(@RequestParam(value = "text", defaultValue = "") String text, @RequestParam(value = "format", defaultValue = "text") String format)
    {

        text = text.replace("\r", " ").replace("\n", " ").trim();

        return solrConnect.search(text);
    }

    private String givenList_shouldReturnARandomElement(List<String> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    private List<String> readIdFile(String filename) {

        Scanner s = null;
        s = new Scanner(getClass().getClassLoader().getResourceAsStream(filename));
        List<String> out = new ArrayList<>();
        while (s.hasNextLine()){
            out.add(s.nextLine());
        }
        s.close();

        return out;
    }

    @RequestMapping("/setMieter")
    public void setMieter(@RequestParam(value = "id", defaultValue = "") String id,  HttpServletResponse httpResponse) {
        System.out.println(id);
        SolrConnect sc = new SolrConnect();
        sc.MieterButtonsPushed(id, true);
        try {
            httpResponse.sendRedirect("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/setVermieter")
    public void setVerMieter(@RequestParam(value = "id", defaultValue = "") String id,  HttpServletResponse httpResponse) {
        System.out.println(id);
        SolrConnect sc = new SolrConnect();
        sc.MieterButtonsPushed(id, false);
        try {
            httpResponse.sendRedirect("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/setProblemfall")
    public void setProblemfall(@RequestParam(value = "id", defaultValue = "") String id,  HttpServletResponse httpResponse) {
        System.out.println(id);
        SolrConnect sc = new SolrConnect();
        sc.MieterProblemfallButtonPushed(id);
        try {
            httpResponse.sendRedirect("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/setGewerblich")
    public void setGewerblich(@RequestParam(value = "id", defaultValue = "") String id,  HttpServletResponse httpResponse) {
        System.out.println(id);
        SolrConnect sc = new SolrConnect();
        sc.GewerblichButtonsPushed(id, true);
        try {
            httpResponse.sendRedirect("./gewerblich");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/setPrivat")
    public void setPrivat(@RequestParam(value = "id", defaultValue = "") String id,  HttpServletResponse httpResponse) {
        System.out.println(id);
        SolrConnect sc = new SolrConnect();
        sc.GewerblichButtonsPushed(id, false);
        try {
            httpResponse.sendRedirect("./gewerblich");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/setProblemfallGewerblich")
    public void setProblemfallGewerblich(@RequestParam(value = "id", defaultValue = "") String id,  HttpServletResponse httpResponse) {
        System.out.println(id);
        SolrConnect sc = new SolrConnect();
        sc.GewerblichProblemfallButtonPushed(id);
        try {
            httpResponse.sendRedirect("./gewerblich");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/")
    String home()
    {
        List<String> ids = readIdFile("outputID.txt");
        StringBuilder sb = new StringBuilder();



        SolrConnect sc = new SolrConnect();
        String id = givenList_shouldReturnARandomElement(ids);
        String frage = sc.getFrage(id);

        sb.append("<html><body>");

        sb.append("<form action=\"/setMieter\" method=\"get\">\n")
                .append("<textarea name=\"id\" >")
                .append(id).append("</textarea><input type=\"submit\" value=\"Mieter\">\n" +
                "</form>");

        sb.append("<form action=\"/setVermieter\" method=\"get\">\n")
                .append("<textarea name=\"id\" >")
                .append(id).append("</textarea><input type=\"submit\" value=\"Vermieter\">\n" +
                "</form>");

        sb.append("<form action=\"/setProblemfall\" method=\"get\">\n")
                .append("<textarea name=\"id\" >")
                .append(id).append("</textarea><input type=\"submit\" value=\"Problemfall\">\n" +
                "</form>");


                sb.append("<p<ID: ").append(id).append("</p><p>Frage:</p><pre>");
        sb.append(frage);
        sb.append("</pre></body></html>");
        return sb.toString();
    }

    @RequestMapping("/gewerblich")
    String home2()
    {
        List<String> ids = readIdFile("outputID.txt");
        StringBuilder sb = new StringBuilder();


        SolrConnect sc = new SolrConnect();
        String id = givenList_shouldReturnARandomElement(ids);
        String frage = sc.getFrage(id);

        sb.append("<html><body>");

        sb.append("<form action=\"./setGewerblich\" method=\"get\">\n")
                .append("<textarea name=\"id\" >")
                .append(id).append("</textarea><input type=\"submit\" value=\"Gewerblich\">\n" +
                "</form>");

        sb.append("<form action=\"./setPrivat\" method=\"get\">\n")
                .append("<textarea name=\"id\" >")
                .append(id).append("</textarea><input type=\"submit\" value=\"Privat\">\n" +
                "</form>");

        sb.append("<form action=\"./setProblemfallGewerblich\" method=\"get\">\n")
                .append("<textarea name=\"id\" >")
                .append(id).append("</textarea><input type=\"submit\" value=\"Problemfall\">\n" +
                "</form>");


        sb.append("<p<ID: ").append(id).append("</p><p>Frage:</p><pre>");
        sb.append(frage);
        sb.append("</pre></body></html>");
        return sb.toString();
    }


    @RequestMapping("/hello")
    String stats()
    {
        SolrConnect sc = new SolrConnect();


        /**sb.append("<script type=\"text/javascript\">" +
            "google.charts.load('current', {'packages':['table']});\n" +
            "google.charts.setOnLoadCallback(drawTable);\n" +

            "function drawTable() {\n" +
            "var data = new google.visualization.DataTable();\n" +
            "data.addColumn('number', '1');\n" +
            "data.addColumn('number', '2');\n" +
            "data.addRows([\n" +
            "[sc.getWatson11();,  sc.getWatson12();],\n" +
            "[sc.getWatson21();,  sc.getWatson22();],\n" +
            "]);\n" +

            "var table = new google.visualization.Table(document.getElementById('table_div'));\n" +

            "table.draw(data, {showRowNumber: true, width: '100%', height: '100%'});\n"+
            "}\n" +
            "</script>  );

        sb.append("<h1> Dauer-Längen-Vergleich</h1>; \n");
        sb.append("<div id=table_div></div>\");\n");
*/
        String sb = "<html><head>" +
                "<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                "<script type=\"text/javascript\">" +
                "google.charts.load('current', {packages: ['corechart', 'line']});\n" +
                "google.charts.setOnLoadCallback(drawCurveTypes);\n" +
                "\n" +
                "function drawCurveTypes() {\n" +
                "      var data = new google.visualization.DataTable();\n" +
                "      data.addColumn('number', 'time');\n" +
                "      data.addColumn('number', 'price');\n" +
                "\n" +
                "      data.addRows([\n" +
                sc.DauerPreisComparer() + "\n" +
                "      ]);\n" +
                "\n" +
                "      var options = {\n" +
                "        hAxis: {\n" +
                "          title: 'Time'\n" +
                "        },\n" +
                "        vAxis: {\n" +
                "          title: 'Price'\n" +
                "        },\n" +
                "        series: {\n" +
                "          1: {curveType: 'function'}\n" +
                "        }\n" +
                "      };\n" +
                "\n" +
                "      var chart = new google.visualization.LineChart(document.getElementById('chart_div'));\n" +
                "      chart.draw(data, options);\n" +
                "    }" +
                "</script></head><body>\"  <div id='chart_div'></div>\");\n" +
                "<h2> Vergleich Watson mit </h2>); \n" +
                "</body></html>";
        return sb;
    }

    @RequestMapping("/stats")
    public String staty(Model model)
    {
        SolrConnect sc = new SolrConnect();
        model.addAttribute("message", sc.DauerPreisComparer());
        model.addAttribute("message1", sc.FragelängePreisComparer());
        model.addAttribute("w11", sc.getWatson11());
        model.addAttribute("w12", sc.getWatson12());
        model.addAttribute("w21", sc.getWatson21());
        model.addAttribute("w22", sc.getWatson22());
        model.addAttribute("l11", sc.getListe11());
        model.addAttribute("l12", sc.getListe12());
        model.addAttribute("l21", sc.getListe21());
        model.addAttribute("l22", sc.getListe22());
        model.addAttribute("ep", sc.getAnzahlProblemfälle());
        model.addAttribute("etre", sc.getTrefferquoteListen());
        model.addAttribute("egen", sc.getGenauigkeitListen());
        model.addAttribute("wtre", sc.getTrefferquoteWatson());
        model.addAttribute("wgen", sc.getGenauigkeitWatson());
        model.addAttribute("ekor", sc.getKorrektklassifikationsrateListen());
        model.addAttribute("efal", sc.getFalschklassifikationsrateListen());
        model.addAttribute("wkor", sc.getKorrektklassifikationsrateWatson());
        model.addAttribute("wfal", sc.getFalschklassifikationsrateWatson());
        model.addAttribute("aekor", sc.getAlleKorrektklassifikationsrateListen());
        model.addAttribute("aefal", sc.getAlleFalschklassifikationsrateListen());
        model.addAttribute("aetre", sc.getAlleTrefferquoteListen());
        model.addAttribute("aegen", sc.getAlleGenauigkeitListen());
        return "stats"; //view
    }

    @RequestMapping("/charts")
    public String charty( Model model)
    {
        SolrConnect sc = new SolrConnect();
        model.addAttribute("message", sc.DauerPreisComparer());

        return "charts"; //view
    }

    @RequestMapping("/table")
    public String mainy (Model model)
    {
        SolrConnect sc = new SolrConnect();
        model.addAttribute("w11", sc.getWatson11());
        model.addAttribute("w12", sc.getWatson12());
        model.addAttribute("w21", sc.getWatson21());
        model.addAttribute("w22", sc.getWatson22());
        return "table"; //view
    }

    @RequestMapping("/test")
    public String main(Model model)
    {
        model.addAttribute("message", "asdf");
        model.addAttribute("tasks", "quert");
        return "welcome"; //view
    }
}