/*
 * Copyright 2014 bruce.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package classifier;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

/**
 *
 * @author bruce
 */
public class TreeClassifierVisualizer {

    private J48 j48Tree ;
    
    public TreeClassifierVisualizer(J48 j48Tree){
        this.j48Tree = j48Tree;
    }
    
    public void visualize() throws Exception {
        
        TreeVisualizer tv = new TreeVisualizer(null, this.j48Tree.graph(), new PlaceNode2());
        JFrame jf = new JFrame("Weka Classifier Tree Visualizer: J48");
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jf.setSize(800, 600);
        jf.getContentPane().setLayout(new BorderLayout());
        jf.getContentPane().add(tv, BorderLayout.CENTER);
        jf.setVisible(true);

        // adjust tree
        tv.fitToScreen();
    }
}
