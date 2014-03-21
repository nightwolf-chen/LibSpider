/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    MessageClassifier.java
 *    Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 *
 */
package classifier;

import java.io.BufferedWriter;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReaderClassifierAdaptor implements Serializable {

    /**
     * for serialization.
     */
    private static final long serialVersionUID = -123455813150452885L;

    /**
     * The training data gathered so far.
     */
    private Instances m_Data = null;

    /**
     * The filter used to generate the word counts.
     */
    private StringToWordVector m_Filter = new StringToWordVector();

    /**
     * The actual classifier.
     */
    private J48 m_Classifier = new J48();

    /**
     * Whether the model is up to date.
     */
    private boolean m_UpToDate;

    final String userid = "userid";
    final String username = "username";
    final String major = "major";
    final String college = "college";
    final String bookname = "bookname";
    final String author = "author";
    final String topic = "topic";
    final String lang = "lang";

    final int attributeCount = 9;
    final int initialDateSize = 100;

    static public final int classCount = 5;
    static public final String classHumanScience = "人文科学";
    static public final String classSocialScience = "社会科学";
    static public final String classNatureScience = "自然科学";
    static public final String classScienceTech = "科学技术";
    static public final String classOther = "其他";
    /*
     人文科学：（A,马克思主义、列宁主义、毛泽东思想、邓小平理论）（B,哲学、宗教）（H,语言、文字）（I,文学）（J,艺术）（K,历史、地理）
     社会科学：（C,社会科学总论）（D,政治、法律）（E,军事）（F,经济）（G,文化、科学、教育、体育）
     自然科学：（N,自然科学总论）（O,数理科学和化学）（P,天文学、地球科学）（Q,生物科学）
     科学技术：（R,医药、卫生）（S,农业科学）（T,工业技术）（U,交通运输）（V,航空、航天）（X,环境科学、安全科学）
     其他：
     */

    public static String transferCategorycodeToClass(String categorycode) {
        String[] categoryClasses = {classHumanScience, classSocialScience, classNatureScience, classScienceTech, classOther};
        String[] tokens = {"ABHIJK", "CDEFG", "NOPQ", "RSTUVX"};

        int classIndex = 4;

        char startChar = categorycode.charAt(0);

        for (int i = 0; i < tokens.length; i++) {
            String tmpStr = tokens[i];
            boolean found = false;
            for (int j = 0; j < tmpStr.length(); j++) {
                if (startChar == tmpStr.charAt(j) || startChar == (tmpStr.charAt(j) + 32)) {
                    found = true;
                    classIndex = i;
                }
            }
            if (found) {
                break;
            }
        }

        return categoryClasses[classIndex];
    }

    /**
     * Constructs empty training dataset.
     */
    public ReaderClassifierAdaptor() {

        final String nameOfDataset = "LibReaderClassificationProblem";

        // Create vector of attributes.
        ArrayList<Attribute> attributes = new ArrayList<>(this.attributeCount);

        // Add attribute for holding messages.
        attributes.add(new Attribute(this.userid,(List<String>)null));
        attributes.add(new Attribute(this.username,(List<String>)null));
        attributes.add(new Attribute(this.major,(List<String>)null));
        attributes.add(new Attribute(this.college,(List<String>)null));
        attributes.add(new Attribute(this.bookname,(List<String>)null));
        attributes.add(new Attribute(this.author,(List<String>)null));
        attributes.add(new Attribute(this.topic,(List<String>)null));
        attributes.add(new Attribute(this.lang,(List<String>)null));

        // Add class attribute.
        ArrayList<String> classValues = new ArrayList<>(ReaderClassifierAdaptor.classCount);
        classValues.add(ReaderClassifierAdaptor.classHumanScience);
        classValues.add(ReaderClassifierAdaptor.classNatureScience);
        classValues.add(ReaderClassifierAdaptor.classSocialScience);
        classValues.add(ReaderClassifierAdaptor.classScienceTech);
        classValues.add(ReaderClassifierAdaptor.classOther);
        attributes.add(new Attribute("Class", classValues));

        // Create dataset with initial capacity of 100, and set index of class.
        m_Data = new Instances(nameOfDataset, attributes, this.initialDateSize);
        m_Data.setClassIndex(m_Data.numAttributes() - 1);
    }

    public void updateData(BorrowListItem dateItem, String classValue) {
        // Make message into instance.
        Instance instance = makeInstance(dateItem, m_Data);

        // Set class value for instance.
        instance.setClassValue(classValue);

        // Add instance to training data.
        m_Data.add(instance);

        m_UpToDate = false;
    }

    /**
     * Classifies a given message.
     *
     * @param readerItem
     * @throws Exception if classification fails
     */
    public void classifyReader(BorrowListItem readerItem) throws Exception {
        // Check whether classifier has been built.
        this.buildClassifier();

        // Make separate little test set so that message
        // does not get added to string attribute in m_Data.
        Instances testset = m_Data.stringFreeStructure();

        // Make message into test instance.
        Instance instance = makeInstance(readerItem, testset);

        // Filter instance.
        m_Filter.input(instance);
        Instance filteredInstance = m_Filter.output();

        // Get index of predicted class value.
        double predicted = m_Classifier.classifyInstance(filteredInstance);

        // Output class value.
        System.err.println("Reader classified as : "
                + m_Data.classAttribute().value((int) predicted));
    }

    public void buildClassifier() throws Exception {
        // Check whether classifier has been built.
        if (m_Data.numInstances() == 0) {
            throw new Exception("No classifier available.");
        }

        // Check whether classifier and filter are up to date.
        if (!m_UpToDate) {
            // Initialize filter and tell it about the input format.
            m_Filter.setInputFormat(m_Data);

            // Generate word counts from the training data.
            Instances filteredData = Filter.useFilter(m_Data, m_Filter);

            // Rebuild classifier.
            m_Classifier.buildClassifier(m_Data);

            m_UpToDate = true;
        }
    }

    /**
     * Method that converts a text message into an instance.
     *
     * @param text	the message content to convert
     * @param data	the header information
     * @return	the generated Instance
     */
    private Instance makeInstance(BorrowListItem dataItem, Instances data) {
        // Create instance of length two.
        Instance instance = new DenseInstance(this.attributeCount);

        // Set value for message attribute
        Attribute attribute = null;

        attribute = data.attribute(this.userid);
        
        instance.setValue(attribute, attribute.addStringValue(dataItem.userid));

        attribute = data.attribute(this.username);
        instance.setValue(attribute, attribute.addStringValue(dataItem.username));

        attribute = data.attribute(this.author);
        instance.setValue(attribute, attribute.addStringValue(dataItem.author));

        attribute = data.attribute(this.bookname);
        instance.setValue(attribute, attribute.addStringValue(dataItem.bookname));

        attribute = data.attribute(this.college);
        instance.setValue(attribute, attribute.addStringValue(dataItem.college));

        attribute = data.attribute(this.lang);
        instance.setValue(attribute, attribute.addStringValue(dataItem.lang));

        attribute = data.attribute(this.major);
        instance.setValue(attribute, attribute.addStringValue(dataItem.major));

        attribute = data.attribute(this.topic);
        instance.setValue(attribute, attribute.addStringValue(dataItem.topic));

        // Give instance access to attribute information from the dataset.
        instance.setDataset(data);

        return instance;
    }

    public J48 getM_Classifier() {
        return m_Classifier;
    }

    public void saveDataToArff(String filename) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filename));
            writer.write(m_Data.toString());
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ReaderClassifierAdaptor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(ReaderClassifierAdaptor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Main method. The following parameters are recognized:
     * <ul>
     * <li>
     * <code>-m messagefile</code><br/>
     * Points to the file containing the message to classify or use for updating
     * the model.
     * </li>
     * <li>
     * <code>-c classlabel</code><br/>
     * The class label of the message if model is to be updated. Omit for
     * classification of a message.
     * </li>
     * <li>
     * <code>-t modelfile</code><br/>
     * The file containing the model. If it doesn't exist, it will be created
     * automatically.
     * </li>
     * </ul>
     *
     * @param args	the commandline options
     */
    public static void main(String[] args) {
        try {

            // Check if class value is given.
            String classValue = Utils.getOption('c', args);

            // If model file exists, read it, otherwise create new one.
            String modelName = Utils.getOption('t', args);
            if (modelName.length() == 0) {
                throw new Exception("Must provide name of model file ('-t <file>').");
            }

            ReaderClassifierAdaptor classifier;
            try {
                classifier = (ReaderClassifierAdaptor) SerializationHelper.read(modelName);
            } catch (FileNotFoundException e) {
                classifier = new ReaderClassifierAdaptor();
            }

            // Process message.
//            if (classValue.length() != 0) {
//                classifier.updateData(null, classValue);
//            } else {
//                classifier.classifyMessage(null.toString());
//            }
            // Save message classifier object only if it was updated.
            if (classValue.length() != 0) {
                SerializationHelper.write(modelName, classifier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
