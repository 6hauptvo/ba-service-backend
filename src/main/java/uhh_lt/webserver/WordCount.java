package uhh_lt.webserver;
public class WordCount {

    public static void main(String[] args)
    {
        System.out.println(WordCount.countWord("Ich liebe es efdc ewfsdc wdw JKJK K."));
    }

    /**
     * Die Methode zählt die Anzahl der Wörter in einem gegebenen Text
     * @param message Ein eingegebener Text
     * @return Gibt die Anzahl der Wörter in dem gegebenen Text zurück
     */
    public static int countWord(String message) {

        int count = 0;
        char ch[] = new char[message.length()];
        for (int i = 0; i < message.length(); i++) {
            ch[i] = message.charAt(i);
            if (((i > 0) && (ch[i] != ' ') && (ch[i - 1] == ' ')) || ((ch[0] != ' ') && (i == 0)))
                count++;
        }
        return count;
    }

}


