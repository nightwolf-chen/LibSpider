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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import object.Book;
import weka.classifiers.rules.ZeroR;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

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
//    private J48 m_Classifier = new J48();
    private ZeroR m_Classifier = new ZeroR();

    /**
     * Whether the model is up to date.
     */
    private boolean m_UpToDate;

    final String bookname = "bookname";
    final String author = "author";
    final String publisher = "publisher";
    final String topic = "topic";
    final String categorycode = "categorycode";
    final String acquirecode = "acquirecode";
    final String lang = "lang";

   
    final int initialDateSize = 100;

    final int attributeCount = 8;
//    static public final int classCount = 5;
//    static public final String classHumanScience = "人文科学";
//    static public final String classSocialScience = "社会科学";
//    static public final String classNatureScience = "自然科学";
//    static public final String classScienceTech = "科学技术";
//    static public final String classOther = "其他";
    
    private String[] classValues;
    /*
     人文科学：（A,马克思主义、列宁主义、毛泽东思想、邓小平理论）（B,哲学、宗教）（H,语言、文字）（I,文学）（J,艺术）（K,历史、地理）
     社会科学：（C,社会科学总论）（D,政治、法律）（E,军事）（F,经济）（G,文化、科学、教育、体育）
     自然科学：（N,自然科学总论）（O,数理科学和化学）（P,天文学、地球科学）（Q,生物科学）
     科学技术：（R,医药、卫生）（S,农业科学）（T,工业技术）（U,交通运输）（V,航空、航天）（X,环境科学、安全科学）
     其他：
     */

    public static String[] getClassValues() {
        String[] categoryClasses = {"A", "B", "C", "D", "E", "F", "G", 
                                    "H", "I", "J", "K", "L", "M", "N",
                                    "O", "P", "Q", "R", "S", "T", "U", 
                                    "V", "W", "X", "Y", "Z","0"};
        return categoryClasses;
    }

    public static String transferCategorycodeToClass(String categorycode) {
      
        char startChar = categorycode.charAt(0);
        String value = "";
        if(startChar >= 'a' && startChar <= 'z'){
            startChar -= 32;
            value += startChar;
        } else if(startChar >= 'A' && startChar <= 'Z'){
            value += startChar;
        }else{
            value = "0";
        }
        
       return value;
    }

    /**
     * Constructs empty training dataset.
     */
    public ReaderClassifierAdaptor() {

        final String nameOfDataset = "LibReaderClassificationProblem";

        this.classValues = ReaderClassifierAdaptor.getClassValues();
        // Create vector of attributes.
        ArrayList<Attribute> attributes = new ArrayList<>(this.attributeCount);

        // Add attribute for holding messages.
        attributes.add(new Attribute(this.bookname, (List<String>) null));
        attributes.add(new Attribute(this.author, (List<String>) null));
        attributes.add(new Attribute(this.publisher, (List<String>) null));
        attributes.add(new Attribute(this.topic, (List<String>) null));
        attributes.add(new Attribute(this.categorycode, (List<String>) null));
        attributes.add(new Attribute(this.acquirecode, (List<String>) null));
        attributes.add(new Attribute(this.lang, (List<String>) null));

        // Add class attribute.
        List<String> classValuesArray = new ArrayList<>(this.classValues.length);
        
        for(String classValue : this.classValues){
            classValuesArray.add(classValue);
        }
        
        
        attributes.add(new Attribute("Class", classValuesArray));

        // Create dataset with initial capacity of 100, and set index of class.
        m_Data = new Instances(nameOfDataset, attributes, this.initialDateSize);
        m_Data.setClassIndex(m_Data.numAttributes() - 1);
    }

    public void updateData(Book book, String classValue) {
        // Make message into instance.
        Instance instance = makeInstance(book, m_Data);

        // Set class value for instance.
        instance.setClassValue(classValue);

        // Add instance to training data.
        m_Data.add(instance);

        m_UpToDate = false;
    }

    /**
     * Classifies a given message.
     *
     * @param book
     * @throws Exception if classification fails
     */
    public String classifyReader(Book book) throws Exception {
        // Check whether classifier has been built.
        this.buildClassifier();

        // Make separate little test set so that message
        // does not get added to string attribute in m_Data.
        Instances testset = m_Data.stringFreeStructure();

        // Make message into test instance.
        Instance instance = makeInstance(book, testset);

        // Filter instance.
        m_Filter.input(instance);
        Instance filteredInstance = m_Filter.output();

        // Get index of predicted class value.
        double predicted = m_Classifier.classifyInstance(filteredInstance);

//        // Output class value.
//        System.err.println("Reader classified as : "
//                + m_Data.classAttribute().value((int) predicted));
        return m_Data.classAttribute().value((int) predicted);
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
    private Instance makeInstance(Book book, Instances data) {
        // Create instance of length two.
        Instance instance = new DenseInstance(this.attributeCount);

        // Set value for message attribute
        Attribute attribute = null;

        attribute = data.attribute(this.bookname);
        instance.setValue(attribute, attribute.addStringValue(book.getBookName()));

        attribute = data.attribute(this.author);
        instance.setValue(attribute, attribute.addStringValue(book.getAuthor()));

        attribute = data.attribute(this.publisher);
        instance.setValue(attribute, attribute.addStringValue(book.getPublisher()));

        attribute = data.attribute(this.topic);
        instance.setValue(attribute, attribute.addStringValue(book.getTopic()));

        attribute = data.attribute(this.categorycode);
        instance.setValue(attribute, attribute.addStringValue(book.getCategoryCode()));

        attribute = data.attribute(this.acquirecode);
        instance.setValue(attribute, attribute.addStringValue(book.getAcquireCode()));

        attribute = data.attribute(this.lang);
        instance.setValue(attribute, attribute.addStringValue(book.getLang()));

        // Give instance access to attribute information from the dataset.
        instance.setDataset(data);

        return instance;
    }

    public ZeroR getM_Classifier() {
        return m_Classifier;
    }

    public void saveDataToArff(String filename) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filename));

            String originDataStr = m_Data.toString();
            String utf8EncodedStr = new String(originDataStr.getBytes(), "utf8");
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
