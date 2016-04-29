package application;
import weka.attributeSelection.*;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.Debug;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

public class FilteredClassifierBuiler {
/**
 * Class to test tokenization and indexing of text with WEKA.
 */
	/**
	 * Instances to be indexed.
	 */
	Instances inputInstances;
	static Classifier cls;
	/**
	 * Instances after indexing.
	 */
	Instances outputInstances;
	static Instances result;
	
	/**
	 * Loads an ARFF file into an instances object.
	 * @param fileName The name of the file to be loaded.
	 */
	public void loadARFF(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
	        inputInstances = arff.getData();
			System.out.println("Loaded dataset: " + fileName);
			reader.close();
		}
		catch (IOException e) {
			System.out.println("Problem found when reading: " + fileName + e);
		}
	}

	/**
	 * Index the inputInstances string features using the StringToWordVector filter. Used for debugging
	 */
	public void buildClassifier() {
		try {
			inputInstances.setClassIndex(0);
			// string2WordVector - tokenizer
			NGramTokenizer tokenizer = new NGramTokenizer();
			tokenizer.setNGramMinSize(1);
			tokenizer.setNGramMaxSize(1);
			tokenizer.setDelimiters("\\W");
			//TODO - stopwords e stemming
			StringToWordVector s2wv = new StringToWordVector();
			s2wv.setTokenizer(tokenizer);
			s2wv.setWordsToKeep(1000000);
			s2wv.setDoNotOperateOnPerClassBasis(true);
			s2wv.setLowerCaseTokens(true);
			//filter.setStemmer();
			//s2wv.setsetUseStoplist(true);
			//s2wv.setStopwords(new File("stopwords.txt"));
			s2wv.setInputFormat(inputInstances);
			// Filter the input instances into the output ones
			//outputInstances = Filter.useFilter(inputInstances,s2wv);
			//System.out.println("Filtering finished");
			
			// attribute selection
			AttributeSelection attSel = new AttributeSelection();  // package weka.filters.supervised.attribute!
			InfoGainAttributeEval eval = new InfoGainAttributeEval();
			Ranker search = new Ranker();
			search.setThreshold(0);
			attSel.setEvaluator(eval);
			attSel.setSearch(search);

			//attSel.setInputFormat(inputInstances);
			
			// classifier
			Classifier cModel = (Classifier)new NaiveBayes();
			//cModel.buildClassifier();
			
			// filter
			MultiFilter mf = new MultiFilter();
	        Filter Filters[]={s2wv,attSel};
			mf.setFilters(Filters);
			
			// meta-classifier
			FilteredClassifier fc = new FilteredClassifier();
			fc.setFilter(mf);
			fc.setClassifier(cModel);

			// train and make predictions
			fc.buildClassifier(inputInstances);
			
			// save the built model to file
			Debug.saveToFile("/data/WEKA_Models", fc);
		}
		catch (Exception e) {
			System.out.println("Problem found when training"+e);
		}
	}
	
	/**
	 * Save an instances object into an ARFF file.
	 * @param fileName The name of the file to be saved.
	 */	
	public void saveARFF(String fileName) {
	
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(fileName));
			writer.print(outputInstances);
			System.out.println("Saved dataset: " + fileName );
			writer.close();
		}
		catch (IOException e) {
			System.out.println("Problem found when writing: " + fileName);
		}
	}
	
	public void preprocessRaw(String path, String outputPath){
		Preprocessor pre = new Preprocessor();
		String strLine = null,str,pol;
		List<String> result = new LinkedList<String>();
		try{
            
            FileInputStream fstream1 = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));;
       
            while ((strLine = br.readLine()) != null) {
            	String[] items = strLine.split(";;");
            	str = items[5].toLowerCase();
	    		pol = items[0];
                result.add(pol+","+Utils.quote(pre.preprocessDocument(str)));
            }
            in.close();
        }catch (Exception e){
            System.err.println("Error while parsing the input raw file: " + e.getMessage());
        }
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(outputPath));
			ListIterator itr = result.listIterator();
			String temp;
			writer.print("@relation prova\n\n");
			writer.print("@attribute classPol {0,4}\n");
			writer.print("@attribute text String\n\n");
			writer.print("@data\n");
			while (itr.hasNext()) {
                String element = (String) itr.next();
                temp = element.replaceAll("_SMILEHAPPY_", "");
                temp = element.replaceAll("_SMILESAD_", "");
                temp = element.replaceAll("USERNAME", "");
                writer.print(temp + "\n");    				
            }
			System.out.println("Saved preprocessed dataset "+ outputPath);
			writer.close();
		}
		catch (IOException e) {
			System.out.println("Problem found when writing " + outputPath);
		}
		System.out.println(Arrays.toString(result.toArray()));
			
		System.out.println("Preprocessing completed");
	}
	/**
	 * Returns predicted class of a string, given a model
	 * @param string the string to be classified
	 * @param cls the model to use to evaluate the string
	 * @throws Exception 
	 */
	private double[] classifyString(String stringToClassify, Classifier model) throws Exception {
		double prediction[] = null;
		Preprocessor pp = new Preprocessor();
		String tmp = pp.preprocessDocument(stringToClassify);
		//StringTokenizer st = new StringTokenizer(tmp, " ");
		Instances unlabeled = new Instances( new BufferedReader( new FileReader("./src/data/testSingleInstance.arff")));
		//TODO
		Instance inst = new DenseInstance(unlabeled.numAttributes());
		inst.setDataset(unlabeled);
		int j = 0;
		/*while(j<unlabeled.numAttributes()) {
			inst.setValue(j, "0");
			j++;
		}
		while(st.hasMoreTokens()) {
			tmp = st.nextToken();
			if(unlabeled.attribute(tmp)!=null)
				inst.setValue(unlabeled.attribute(tmp), "1");
		}
		unlabeled.add(inst);
		*/
		// set class attribute
		unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
		
		for (int i = 0; i < unlabeled.numInstances(); i++) {
			   prediction = model.distributionForInstance(unlabeled.instance(i)); //inst here
			 }

		return prediction;		
	}
	
	private ArrayList<double[]> classifyARFF(String filePath, Classifier model) throws Exception {
		ArrayList<double[]> labeledAL = new ArrayList<double[]>();
		double[] prediction = null;
		Instances unlabeled = new Instances( new BufferedReader( new FileReader(filePath)));
		Instances labeled = new Instances(unlabeled);
		// set class attribute
		unlabeled.setClassIndex(unlabeled.numAttributes() - 1);	
		labeled.setClassIndex(0);	
		
		for (int i = 0; i < unlabeled.numInstances(); i++) {
			//double clsLabel = model.classifyInstance(unlabeled.instance(i));
			
			prediction = model.distributionForInstance(unlabeled.instance(i));
			labeledAL.add(prediction);
			if (prediction[0] > 0.5f) labeled.instance(i).setClassValue("0");
			else labeled.instance(i).setClassValue("4");
			System.out.println("labeled instance "+unlabeled.instance(i)+" - NEG: "+prediction[0] +" POS: "+ prediction[1]);
			 }
		 BufferedWriter writer = new BufferedWriter(
                 new FileWriter("labeled.arff"));
		 writer.write(labeled.toString());
		 writer.newLine();
		 writer.flush();
		 writer.close();
		return labeledAL;		
	}

	public ArrayList<double[]> classifyInstances() throws Exception{
		//load model
		cls = (Classifier) weka.core.SerializationHelper.read("./src/data/SMOModel.model");
		//use it to classify the test ARFF
		ArrayList<double[]> hopefullyitwillwork = this.classifyARFF("testTweets.arff", cls);
		return hopefullyitwillwork;
	}
	
	public ArrayList<double[]> classifyInstances(String modelName) throws Exception{
		//load model
		cls = (Classifier) weka.core.SerializationHelper.read("./src/data/"+modelName+".model");
		//use it to classify the test ARFF
		ArrayList<double[]> hopefullyitwillwork = this.classifyARFF("testTweets.arff", cls);
		return hopefullyitwillwork;
	}
	
	/**
	 * Main method. Currently used for debug.
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		long time1, time2;
		FilteredClassifierBuiler fcb = new FilteredClassifierBuiler();
		fcb.preprocessRaw("ToProcess.txt","Preprocessed.arff");
		System.out.println("Done!");
		//TODO - fix enormous amount of time needed for building the filtered classifier
		/*
		fcb.loadARFF("outputparser.arff");
		time1 = System.currentTimeMillis();
		System.out.println("Started building model at: " + time1);
		fcb.buildClassifier();
		time2 = System.currentTimeMillis();
		System.out.println("Finished building model at: " + time2);
		System.out.println("Total building time: " + (time2-time1));
		System.out.println("Model saved at /data/WEKA_Models ");*/
		
		//TODO - evaluate best classifier between SVM, Naive multinomial and standard naive
		//load model build in weka
		cls = (Classifier) weka.core.SerializationHelper.read("./src/data/dummy.model");
		//double[] prediction = fcb.classifyString("awful", cls);
		//get the name of the class value
		//System.out.println("The predicted value of instance : "); 
		//NumberFormat nf = new DecimalFormat("0.#");
        //for (double i : prediction) {
        //    System.out.println(i);
        //}
        		//System.out.println("Classifing file "+"testTweets.arff");
        		//ArrayList<double[]> hopefullyitwillwork = fcb.classifyARFF("testTweets.arff", cls);
        //for (double[] i : predicted) {
        //    System.out.println(i);
        //}
        
	}


}
