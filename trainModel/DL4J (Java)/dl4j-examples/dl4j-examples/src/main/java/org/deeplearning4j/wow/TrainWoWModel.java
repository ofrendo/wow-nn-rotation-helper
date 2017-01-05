package org.deeplearning4j.wow;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.datavec.api.io.WritableConverter;
import org.datavec.api.io.converters.WritableConverterException;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nnrh.Config;

/**
 * Sequence Classification Example Using a LSTM Recurrent Neural Network
 *
 * This example learns how to classify univariate time series as belonging to one of six categories.
 * Categories are: Normal, Cyclic, Increasing trend, Decreasing trend, Upward shift, Downward shift
 *
 * Data is the UCI Synthetic Control Chart Time Series Data Set
 * Details:     https://archive.ics.uci.edu/ml/datasets/Synthetic+Control+Chart+Time+Series
 * Data:        https://archive.ics.uci.edu/ml/machine-learning-databases/synthetic_control-mld/synthetic_control.data
 * Image:       https://archive.ics.uci.edu/ml/machine-learning-databases/synthetic_control-mld/data.jpeg
 *
 * This example proceeds as follows:
 * 1. Download and prepare the data (in downloadUCIData() method)
 *    (a) Split the 600 sequences into train set of size 450, and test set of size 150
 *    (b) Write the data into a format suitable for loading using the CSVSequenceRecordReader for sequence classification
 *        This format: one time series per file, and a separate file for the labels.
 *        For example, train/features/0.csv is the features using with the labels file train/labels/0.csv
 *        Because the data is a univariate time series, we only have one column in the CSV files. Normally, each column
 *        would contain multiple values - one time step per row.
 *        Furthermore, because we have only one label for each time series, the labels CSV files contain only a single value
 *
 * 2. Load the training data using CSVSequenceRecordReader (to load/parse the CSV files) and SequenceRecordReaderDataSetIterator
 *    (to convert it to DataSet objects, ready to train)
 *    For more details on this step, see: http://deeplearning4j.org/usingrnns#data
 *
 * 3. Normalize the data. The raw data contain values that are too large for effective training, and need to be normalized.
 *    Normalization is conducted using NormalizerStandardize, based on statistics (mean, st.dev) collected on the training
 *    data only. Note that both the training data and test data are normalized in the same way.
 *
 * 4. Configure the network
 *    The data set here is very small, so we can't afford to use a large network with many parameters.
 *    We are using one small LSTM layer and one RNN output layer
 *
 * 5. Train the network for 40 epochs
 *    At each epoch, evaluate and print the accuracy and f1 on the test set
 *
 * @author Alex Black
 */
public class TrainWoWModel {
    private static final Logger log = LoggerFactory.getLogger(TrainWoWModel.class);

    //'baseDir': Base directory for the data. Change this if you want to save the data somewhere else
    //private static String baseDir = "C:\\Users\\D059373\\workspace git\\wow-nn-rotation-helper\\data\\";
    /*private static File baseTrainDir = new File(baseDir, "train");
    private static File featuresDirTrain = new File(baseTrainDir, "features");
    private static File labelsDirTrain = new File(baseTrainDir, "labels");
    private static File baseTestDir = new File(baseDir, "test");
    private static File featuresDirTest = new File(baseTestDir, "features");
    private static File labelsDirTest = new File(baseTestDir, "labels");*/

    //http://semantive.com/deep-learning-examples/
    private static final List<String> LABELS = 
    		Arrays.asList("pyroblast", "rune_of_power", "meteor", "combustion", 
    				"use_item_wriggling_sinew", "fire_blast", "phoenixs_flames", "fireball", "NULL",
    				"potion", "scorch", "dragons_breath");

    public static void main(String[] args) throws Exception {
    	
    	double split = 0.75; // use 75% for training, rest for testing
    	int trainAmount = (int) (Config.LOGS_AMOUNT * split);
    	
    	int labelIndex = 23;
		int miniBatchSize = 1000;   
		
		int numLines = 82523;
		DataSet allData = readCSVDataset(Config.pathCSVCombined, numLines, labelIndex, LABELS.size()).next();
		
		SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.65);  //Use 65% of data for training

        DataSet trainData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();
		
		//trainingData 0, trainAmount-1
		//testData trainAmount, Config.LOGS_AMOUNT-1
		
        
		//System.out.println(trainData.getFeatures().columns());
        
        int lstmLayer0Neurons = 100;
        int normalNetworkNeurons = 100;
        
        // ----- Configure the network -----
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)    //Random number generator seed for improved repeatability. Optional.
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1)
                .weightInit(WeightInit.XAVIER)
                .updater(Updater.NESTEROVS).momentum(0.9)
                .learningRate(0.005)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)  //Not always required, but helps with this data set
                .gradientNormalizationThreshold(0.5)
                .list()
                
                // LSTM 
                .layer(0, new GravesLSTM.Builder().activation(Activation.TANH).nIn(trainData.getFeatures().columns()).nOut(lstmLayer0Neurons).build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX).nIn(lstmLayer0Neurons).nOut(LABELS.size()).build())
                
                // Normal NN
                //.layer(0, new DenseLayer.Builder().nIn(trainData.getFeatures().columns()).nOut(normalNetworkNeurons)
                //        .build())
                //.layer(1, new DenseLayer.Builder().nIn(normalNetworkNeurons).nOut(normalNetworkNeurons)
                //    .build())
                //.layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                //    .activation(Activation.SOFTMAX)
                //    .nIn(normalNetworkNeurons).nOut(LABELS.size()).build())
                
                .pretrain(false).backprop(true).build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        model.setListeners(new ScoreIterationListener(1));   //Print the score (loss function value) every 20 iterations
        
        //trainData.reset(); // reset because we peeked at the first one        

        // ----- Train the network, evaluating the test set performance at each epoch -----
        int nEpochs = 5;
        String strTrain = "Test set evaluation at epoch %d: Accuracy = %.2f, F1 = %.2f";
        String strTest = "Test set evaluation at epoch %d: Accuracy = %.2f, F1 = %.2f";
        for (int i = 0; i < nEpochs; i++) {
           model.fit(trainData);

           //Evaluate on the test set: for iterator
           //Evaluation eval = model.evaluate(testData);
            
           // evaluate on test set: for dataset
           Evaluation eval = new Evaluation(LABELS.size());
           INDArray output = model.output(testData.getFeatureMatrix());
           eval.eval(testData.getLabels(), output);
           
           log.info(String.format(strTest, i, eval.accuracy(), eval.f1()));
            
           
           //log.info(eval.stats());
            
           //testData.reset();
           //trainData.reset();
        }
        //Layer layerLSTM = model.getLayer(0);
        //System.out.println(layerLSTM.params());
        //System.out.println(layerLSTM.paramTable());
        log.info("----- Example Complete -----");
        
        //firstTrainSet = trainData.next();
        
        System.out.println("Network:");
        System.out.println("Examples: " + trainData.getFeatureMatrix().rows());
        System.out.println("Features: " + trainData.numInputs());
        System.out.println("Params: " + model.params().length());
        
    }

    
    public void deprecatedTrainTestRead(int from, int to, int miniBatchSiz) {
    	/*int linesToSkip = 1;
    	SequenceRecordReader files = new CSVSequenceRecordReader(linesToSkip, Util.CSV_DELIMITER);
        files.initialize(new NumberedFileInputSplit(
        		Config.pathDirCSV + Config.baseCSVName + "%d" + Config.baseCSVEnding, 
        		));
        DataSetIterator trainData = new SequenceRecordReaderDataSetIterator(
        		trainFiles, // reader
        		miniBatchSize,  // minibatchsize
        		LABELS.size(), //possible labels
        		labelIndex); // labelIndex
        //int test = 0;
        /*DataSetIterator trainData  = new RecordReaderDataSetIterator(trainFiles, new WritableConverter() {
            @Override
            public Writable convert(Writable writable) throws WritableConverterException {
                if (writable instanceof Text) {
                    String label = writable.toString();//.replaceAll("\u0000", "");
                    int index = LABELS.indexOf(label);
                    if (index == -1)
                    	System.out.println(label);
                    return new IntWritable(index);
                }
                return writable;
            }
        }, miniBatchSize, labelIndex, LABELS.size());
        
        
        SequenceRecordReader testFiles = new CSVSequenceRecordReader(linesToSkip, Util.CSV_DELIMITER);
        testFiles.initialize(new NumberedFileInputSplit(
        		Config.pathDirCSV + Config.baseCSVName + "/%d" + Config.baseCSVEnding, 
        		from, to));
        DataSetIterator testData = new SequenceRecordReaderDataSetIterator(
        		testFiles, // reader
        		miniBatchSize,  // minibatchsize
        		LABELS.size(), //possible labels
        		labelIndex); // labelIndex
        
		DataSet firstTrainSet = trainData.next();*/
    }
    
    /**
     * used for testing and training
     *
     * @param csvFileClasspath
     * @param batchSize
     * @param labelIndex
     * @param numClasses
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static DataSetIterator readCSVDataset(
            String csvFileClasspath, int batchSize, int labelIndex, int numClasses)
            throws IOException, InterruptedException{

    	int numLinesToSkip = 1;
        RecordReader rr = new CSVRecordReader(numLinesToSkip, ";");
        rr.initialize(new FileSplit(new File(csvFileClasspath)));
        DataSetIterator iterator = new RecordReaderDataSetIterator(rr, new WritableConverter() {
            @Override
            public Writable convert(Writable writable) throws WritableConverterException {
                if (writable instanceof Text) {
                    String label = writable.toString();//.replaceAll("\u0000", "");
                    int index = LABELS.indexOf(label);
                    if (index == -1)
                    	System.out.println(label);
                    return new IntWritable(index);
                }
                return writable;
            }
        }, batchSize,labelIndex,numClasses);
        return iterator;
    }
}
