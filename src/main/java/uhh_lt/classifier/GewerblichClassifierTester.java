package uhh_lt.classifier;

public class GewerblichClassifierTester {
    public static void main(String[] args) {
        GewerblichClassifier GC = new GewerblichClassifier();
        System.out.println(GC.classify("Ich wohne in Hamburg."));
    }
}
