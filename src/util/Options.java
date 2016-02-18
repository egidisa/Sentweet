package util;

import java.util.LinkedList;
import java.util.List;

/**
 * class to manage training options
 */
public class Options {
	
	private int numFeatures;
	private boolean selectedFeaturesByFrequency;
	private boolean removeEmoticons;
	private String classifierName;
	private List<String> wmClassifiersName;
	
	/**
	 * gets the list of classifiers' names in weighted majority
	 * @return the list of classifiers' names in weighted majority
	 */
	public List<String> getWmClassifiersName() {
		return wmClassifiersName;
	}
	
	/**
	 * sets a given list of classifiers' names in weighted majority
	 * @param wmClassifiersName a list of classifiers' names
	 */
	public void setWmClassifiersName(List<String> wmClassifiersName) {
		this.wmClassifiersName = wmClassifiersName;
	}
	
	public Options() {
		this.numFeatures = 0;
		this.selectedFeaturesByFrequency = false;
		this.removeEmoticons = true;
		this.wmClassifiersName = new LinkedList<String>();
	}
	
	/**
	 * gets the classifier's name
	 * @return the classifier's name
	 */
	public String getClassifierName() {
		return classifierName;
	}
	
	/**
	 * sets a given classifier's name
	 * @param classifierName a classifier's name
	 */
	public void setClassifierName(String classifierName) {
		this.classifierName = classifierName;
	}
	
	/**
	 * gets the number of features to select
	 * @return the number of features to select
	 */
	public int getNumFeatures() {
		return numFeatures;
	}
	/**
	 * sets a number of feature to select
	 * @param numFeatures a number of features to select
	 */
	public void setNumFeatures(int numFeatures) {
		this.numFeatures = numFeatures;
	}
	/**
	 * gets if features are selected by frequency
	 * @return true if features are selected by frequency
	 */
	public boolean isSelectedFeaturesByFrequency() {
		return selectedFeaturesByFrequency;
	}
	/**
	 * sets if features are selected by frequency
	 * @param selectedFeaturesByFrequency true if features are selected by frequency
	 */
	public void setSelectedFeaturesByFrequency(boolean selectedFeaturesByFrequency) {
		this.selectedFeaturesByFrequency = selectedFeaturesByFrequency;
	}
	/**
	 * gets if emoticons are to be removed
	 * @return true if emoticons are to be removed
	 */
	public boolean isRemoveEmoticons() {
		return removeEmoticons;
	}
	/**
	 * sets if emoticons are to be removed
	 * @param removeEmoticons true if emoticons are to be removed
	 */
	public void setRemoveEmoticons(boolean removeEmoticons) {
		this.removeEmoticons = removeEmoticons;
	}
}
