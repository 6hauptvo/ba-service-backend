package uhh_lt.webserver;

public class Komplexitätsberechner {
    public static void main(String[] args) {
        System.out.println(countWord("Das hier ist ein Beispielsatz mit unterschiedlichen Wörtern, unter anderem lange Nomen wie zum Beispiel Ordnungswidrigkeit"));
    }

    public static int countWord(String message) {

        int wordCount = 0;
        char ch[] = new char[message.length()];
        for (int i = 0; i < message.length(); i++) {
            ch[i] = message.charAt(i);
            if (((i > 0) && (ch[i] != ' ') && (ch[i - 1] == ' ')) || ((ch[0] != ' ') && (i == 0)))
                wordCount++;
        }
        return wordCount;
    }

    public static int complexNounCount(String message) {
        int nounCount = 0;
        String [] words = message.split(" ");
        for (String word:words)
        {
            if (!word.isEmpty() && Character.isUpperCase(word.charAt(0)) && word.length() > 15)
            {
                nounCount++;
            }
        }

        return nounCount;
    }
}
