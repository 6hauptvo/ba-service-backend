package uhh_lt.classifier;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class LinearRegressionClassifier {

    public static void main(String args[]) throws Exception{
        //load dataset
        DataSource source = new DataSource("resources/price_training_2.csv");
        Instances dataset = source.getDataSet();
        //set class index to the last attribute
        System.out.println(dataset);
        dataset.setClassIndex(dataset.numAttributes()-1);

        //build model
        LinearRegression lr = new LinearRegression();
        lr.buildClassifier(dataset);
        //output model
        System.out.println(lr);


        double[] testCase = new double[]{53, 29};
        DenseInstance inst = new DenseInstance(1.0, testCase);

        double prediction = lr.classifyInstance(inst);
        System.out.print("Instance: ");
        for (double d : testCase) {
            System.out.print(d + " ");
        }
        System.out.println();
        System.out.println("Estimated test price for (orig: 25)" + ": " + prediction);


        testCase = new double[]{98, 42};
        inst = new DenseInstance(1.0, testCase);


        weka.core.SerializationHelper.write("resources/price.model", lr);
        Classifier lrLoaded = (Classifier) weka.core.SerializationHelper.read("resources/price.model");


        prediction = lrLoaded.classifyInstance(inst);
        System.out.print("Instance: ");
        for (double d : testCase) {
            System.out.print(d + " ");
        }
        System.out.println();
        System.out.println("Estimated test price for (orig: 40)" + ": " + prediction);
    }
}
