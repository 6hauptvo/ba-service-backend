package uhh_lt.classifier;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.ClassifiedClass;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.ClassifyOptions;
import com.ibm.watson.developer_cloud.service.security.IamOptions;

public class WatsonMieterClassifier implements ClassifierInterface
{
    private NaturalLanguageClassifier naturalLanguageClassifier;
    private Classification classification;

    public WatsonMieterClassifier()
    {
    IamOptions options = new IamOptions.Builder()
            .apiKey("IM_TRELLO")
            .build();
    naturalLanguageClassifier = new NaturalLanguageClassifier(options);
    }

    /**
     * klassifiziert Fragen nach Mieter oder Vermieter mithilfe der Watson Dienste
     * @param neueFrage die zu klassifizierende Frage
     */
    @Override
    public Double classify(String neueFrage)
    {
    String frage = neueFrage.substring(0, Math.min(neueFrage.length(), 1000));

    ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
            .classifierId("1e5b70x507-nlc-653")
            .text(frage)
            .build();
    classification = naturalLanguageClassifier.classify(classifyOptions).execute();
        for (ClassifiedClass mClass : classification.getClasses()) {
            if (mClass.getClassName().compareTo("Mieter") == 0) {
                return mClass.getConfidence();
            }
        }
        return 0.0;
    }

    @Override
    public boolean istHauptklasse()
    {
        return classification.getTopClass().compareTo("Mieter") == 0;
    }

    @Override
    public Object istHauptklasse(String text) {
        return classification.getTopClass().compareTo("Mieter") == 0;
    }
}
