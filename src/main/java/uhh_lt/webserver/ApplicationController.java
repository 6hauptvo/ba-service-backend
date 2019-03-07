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
import uhh_lt.datenbank.SolrConnect;
import uhh_lt.datenbank.Statistikmethoden;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        sc.mieterButtonsPushed(id, true);
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
        sc.mieterButtonsPushed(id, false);
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
        sc.mieterProblemfallButtonPushed(id);
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
        sc.gewerblichButtonsPushed(id, true);
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
        sc.gewerblichButtonsPushed(id, false);
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
        sc.gewerblichProblemfallButtonPushed(id);
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




    @RequestMapping("/stats")
    public String staty(Model model)
    {
        SolrConnect sc = new SolrConnect();
        Statistikmethoden sm = new Statistikmethoden();
        model.addAttribute("message", sm.dauerPreisComparer());
        model.addAttribute("message1", sm.fragelängePreisComparer());
        model.addAttribute("w11", sm.getWatson11());
        model.addAttribute("w12", sm.getWatson12());
        model.addAttribute("w21", sm.getWatson21());
        model.addAttribute("w22", sm.getWatson22());
        model.addAttribute("l11", sm.getListe11());
        model.addAttribute("l12", sm.getListe12());
        model.addAttribute("l21", sm.getListe21());
        model.addAttribute("l22", sm.getListe22());
        model.addAttribute("ep", sc.getAnzahlProblemfälle());
        model.addAttribute("etre", sm.getTrefferquoteListen());
        model.addAttribute("egen", sm.getGenauigkeitListen());
        model.addAttribute("wtre", sm.getTrefferquoteWatson());
        model.addAttribute("wgen", sm.getGenauigkeitWatson());
        model.addAttribute("ekor", sm.getKorrektklassifikationsrateListen());
        model.addAttribute("efal", sm.getFalschklassifikationsrateListen());
        model.addAttribute("wkor", sm.getKorrektklassifikationsrateWatson());
        model.addAttribute("wfal", sm.getFalschklassifikationsrateWatson());
        model.addAttribute("aekor", sm.getAlleKorrektklassifikationsrateListen());
        model.addAttribute("aefal", sm.getAlleFalschklassifikationsrateListen());
        model.addAttribute("aetre", sm.getAlleTrefferquoteListen());
        model.addAttribute("aegen", sm.getAlleGenauigkeitListen());
        return "stats"; //view
    }

    @RequestMapping("/charts")
    public String charty( Model model)
    {
        SolrConnect sc = new SolrConnect();
        Statistikmethoden sm = new Statistikmethoden();
        model.addAttribute("message", sm.dauerPreisComparer());

        return "charts"; //view
    }

    @RequestMapping("/table")
    public String mainy (Model model)
    {
        SolrConnect sc = new SolrConnect();
        Statistikmethoden sm = new Statistikmethoden();
        model.addAttribute("w11", sm.getWatson11());
        model.addAttribute("w12", sm.getWatson12());
        model.addAttribute("w21", sm.getWatson21());
        model.addAttribute("w22", sm.getWatson22());
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