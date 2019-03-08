package uhh_lt.classifier;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.ClassifiedClass;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.ClassifyOptions;
import com.ibm.watson.developer_cloud.service.security.IamOptions;

public class WatsonWarmClassifier implements ClassifierInterface
    {
    private NaturalLanguageClassifier naturalLanguageClassifier;
    private Classification classification;
    public WatsonWarmClassifier() {
        IamOptions options = new IamOptions.Builder()
                .apiKey("nqeYBC1Rp7M7CpUpAwmr-cFBiQVHndCzNMz07-Yw3lKF")
                .build();
        naturalLanguageClassifier = new NaturalLanguageClassifier(options);
    }

    @Override
    public Double classify(String neueFrage)
    {
        String frage = neueFrage.substring(0, Math.min(neueFrage.length(), 1000));
        ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                .classifierId("1e0b80x506-nlc-145")
                .text(frage)
                .build();
        classification = naturalLanguageClassifier.classify(classifyOptions).execute();
        System.out.println(classification);
        for (ClassifiedClass mClass : classification.getClasses()) {
            if (mClass.getClassName().compareTo("Warm") == 0) {
                return mClass.getConfidence();
            }
        }
        return 0.0;
    }

    @Override
    public boolean istHauptklasse()
    {
        return classification.getTopClass().compareTo("Warm") == 0;
    }

    @Override
    public Object istHauptklasse(String text) {
        return classification.getTopClass().compareTo("Warm") == 0;
    }
}
