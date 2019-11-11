// sensorNetwork.java

import java.io.*;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.Graphics;  
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.event.*;
import java.awt.geom.Point2D;

import Jama.Matrix;
import Jama.QRDecomposition;

class sensorNode {
	int x_pos;
	int y_pos;
	int storage_capacity;
	public sensorNode () {
		Random rand = new Random();
		// generate random position
		x_pos = Math.abs(rand.nextInt() % (sensorNetwork.network_width));
		y_pos = Math.abs(rand.nextInt() % (sensorNetwork.network_height));
		storage_capacity = sensorNetwork.storage_capacity_of_each_non_DG;
	}
}

class dataGenerator {
	int x_pos;
	int y_pos;
	int number_of_data_items_to_offload;
	public dataGenerator () {
		Random rand = new Random();
		// generate random position
		x_pos = Math.abs(rand.nextInt() % (sensorNetwork.network_width));
		y_pos = Math.abs(rand.nextInt() % (sensorNetwork.network_height));
		number_of_data_items_to_offload = sensorNetwork.number_of_data_items_each_DG_has;
	}
}

class sensorNetwork extends JPanel  {
	public static int outputState;
	
	public static dataGenerator[] DGs;
	public static sensorNode[] sensors;
	public static float[][] edgeDistanceArray;
	public static float[][] edgeEnergyCostArray;
	
	public static NodeList[] adjList;
	public static NetworkFlow G;
	public static double max_flow;
	public static int[] number_of_edges_per_DG;
	public static int number_of_edges;
	
	public static int[] parent;
	public static float[] fringeWgt;
	
	public static int[] parent1;
	public static float[] fringeWgt1;
	public static int[][] capacity;
	public static int[] capacitee;
	
	public static int[] parent2;
	public static float[] fringeWgt2;
	
	public static boolean [] discovered;
	public static boolean is_connected;
	
	public static int dijkStartNode=0;
	public static int dijkEndNode=0;
	
	JTextField textfield1;
	JTextField textfield2;
	JTextField textfield3;
	JTextField textfield4;
	JTextField textfield5;
	JTextField textfield6;
	JTextField textfield7;
	JTextField textfield8;
	JTextField textfield9;
	
	JTextField textfield10;
	JTextField textfield11;
	
	// initialize variables
	public static int network_width = 50;
	public static int network_height = 50;
	public static int number_of_DGs = 20;
	public static int number_of_sensor_nodes = 20;
	public static int storage_capacity_of_each_non_DG = 512000;
	public static float transmission_range = 13.0f;
	public static int minimum_initial_energy = 0;
	public static int maximum_initial_energy = 0;
	public static int number_of_data_items_each_DG_has = 60;

	public static int ouputState;
	
	// objective 1
	
	// generate network
	public static void generate_the_sensor_network() {
		DGs = new dataGenerator[number_of_DGs];
		sensors = new sensorNode[number_of_sensor_nodes];
		
		for (int i=0; i<number_of_DGs; i++) {
			DGs[i] = new dataGenerator();
		}
		
		for (int i=0; i<number_of_sensor_nodes; i++) {
			sensors[i] = new sensorNode();
		}
	}
	
	public static void compute_edgeArray_adjList() {
		int size = number_of_DGs+number_of_sensor_nodes;
		edgeDistanceArray = new float[size][size];
		edgeEnergyCostArray = new float[size][size];
		number_of_edges_per_DG = new int[number_of_DGs];
		capacity = new int[size+2][size+2];
		capacitee = new int[size];
		G = new NetworkFlow(size+2);
		Random rand= new Random();
		double random, result;
		
		// initialize edge array
		for (int i=0; i<size; i++) {
			if (i<number_of_DGs) capacitee[i]=Math.min(rand.nextInt(maximum_initial_energy - minimum_initial_energy + 1) + minimum_initial_energy,number_of_data_items_each_DG_has);
			else capacitee[i]=Math.min(rand.nextInt(maximum_initial_energy - minimum_initial_energy + 1) + minimum_initial_energy,Math.min(storage_capacity_of_each_non_DG,number_of_data_items_each_DG_has));
			for (int j=0; j<size; j++) {
				edgeDistanceArray[i][j] = 0.0f;
				edgeEnergyCostArray[i][j] = 0.0f;
			}
		}
		// initialize the capacities of the nodes
		for (int i=0; i<size+2; i++) {
			for (int j=0; j<size+2; j++) {
				if (i==0 && (j>0 && j<=number_of_DGs)) capacity[i][j]=capacity[j][i]= number_of_data_items_each_DG_has;
				capacity[i][j] = 0;
			}
		}
		
		// value of [i][j] is the distance(i,j)
		for (int i=0; i<(number_of_DGs); i++) {
			for(int j=i+1; j<number_of_DGs; j++) {
				double dist = Math.sqrt(((DGs[j].x_pos-DGs[i].x_pos)*(DGs[j].x_pos-DGs[i].x_pos) + 
						(DGs[j].y_pos-DGs[i].y_pos)*(DGs[j].y_pos-DGs[i].y_pos)));
				if (dist < sensorNetwork.transmission_range) {
					edgeDistanceArray[i][j] = (float) dist;
					edgeEnergyCostArray[i][j] = (float)((0.00032f)+(0.00000032f)*(dist*dist));
					result = rand.nextInt(maximum_initial_energy - minimum_initial_energy + 1) + minimum_initial_energy;
					EdgeNode node1 = new EdgeNode(j+1, (float) dist, (int)result);
					EdgeNode node2 = new EdgeNode(i+1, (float) dist, (int)result);
					//random = new Random().nextDouble();
					//result = minimum_initial_energy + (random * (maximum_initial_energy - minimum_initial_energy));
					//FlowEdge e1 = new FlowEdge(i+1,j+1,edgeEnergyCostArray[i][j], result);
					//random = new Random().nextDouble();
					//result = minimum_initial_energy + (random * (maximum_initial_energy - minimum_initial_energy));
					//FlowEdge e2 = new FlowEdge(j+1,i+1,edgeEnergyCostArray[i][j], result);
					//NetworkFlow.addEdge(e1);
					//NetworkFlow.addEdge(e2);
					capacity[i+1][j+1]=capacity[j+1][i+1]=(int)result;
					FlowEdge e1 = new FlowEdge(i+1,j+1,/*edgeEnergyCostArray[i][number_of_DGs+j]*/1, /*Math.min(*/result/*, number_of_data_items_each_DG_has)*/, dist);
					FlowEdge e2 = new FlowEdge(j+1,i+1,/*edgeEnergyCostArray[i][number_of_DGs+j]*/1, /*Math.min(*/result/*, number_of_data_items_each_DG_has)*/, dist);
					G.addEdge(e1);G.addEdge(e2);
					// adjList.addEdge(i, node1);
					adjList[i+1] = NodeList.cons(node1, adjList[i+1]);
					// adjList.addEdge(j, node2);
					adjList[j+1] = NodeList.cons(node2, adjList[j+1]);
				}
			}
		}
		
		for (int i=0; i<(number_of_DGs); i++) {
			for(int j=0; j<number_of_sensor_nodes; j++) {
				double dist = Math.sqrt(((sensors[j].x_pos-DGs[i].x_pos)*(sensors[j].x_pos-DGs[i].x_pos) + 
						(sensors[j].y_pos-DGs[i].y_pos)*(sensors[j].y_pos-DGs[i].y_pos)));
				if (dist < sensorNetwork.transmission_range) {
					edgeDistanceArray[i][number_of_DGs+j] = (float) dist;
					edgeEnergyCostArray[i][number_of_DGs+j] = (float)((0.00032f)+(0.00000032f)*(dist*dist));
					// edgeDistanceArray[number_of_DGs+j][i] = (float) dist;
					// edgeEnergyCostArray[i][number_of_DGs+j] = (float)((320000.0f)+(320000.0f*(dist*dist)));
					result = rand.nextInt(maximum_initial_energy - minimum_initial_energy + 1) + minimum_initial_energy;
					EdgeNode node1 = new EdgeNode(number_of_DGs+j+1, (float) dist, (int)Math.min(result, storage_capacity_of_each_non_DG));
					EdgeNode node2 = new EdgeNode(i+1, (float) dist, (int)Math.min(result, storage_capacity_of_each_non_DG));
					/*random = new Random().nextDouble();
					result = minimum_initial_energy + (random * (maximum_initial_energy - minimum_initial_energy));
					*/
					capacity[i+1][number_of_DGs+j+1]=capacity[number_of_DGs+j+1][i+1]=(int)Math.min(result, storage_capacity_of_each_non_DG);
					FlowEdge e1 = new FlowEdge(i+1,number_of_DGs+j+1,/*edgeEnergyCostArray[i][number_of_DGs+j]*/1, /*Math.min(*/Math.min(result, /*number_of_data_items_each_DG_has),*/storage_capacity_of_each_non_DG), dist);
					FlowEdge e2 = new FlowEdge(number_of_DGs+j+1,i+1,/*edgeEnergyCostArray[i][number_of_DGs+j]*/1, /*Math.min(*/Math.min(result, /*number_of_data_items_each_DG_has),*/storage_capacity_of_each_non_DG), dist);
					//random = new Random().nextDouble();
					//result = minimum_initial_energy + (random * (maximum_initial_energy - minimum_initial_energy));
					//FlowEdge e2 = new FlowEdge(i+1,number_of_DGs+j+1,edgeEnergyCostArray[i][number_of_DGs+j], result);
					G.addEdge(e1);G.addEdge(e2);
					//NetworkFlow.addEdge(e2);
					
					// adjList.addEdge(i, node1);
					adjList[i+1] = NodeList.cons(node1, adjList[i+1]);
					// adjList.addEdge(number_of_DGs+j, node2);
					adjList[number_of_DGs+j+1] = NodeList.cons(node2, adjList[number_of_DGs+j+1]);
				}
			}
		}
		
		for (int i=0; i<(number_of_sensor_nodes); i++) {
			for(int j=i+1; j<number_of_sensor_nodes; j++) {
				double dist = Math.sqrt(((sensors[j].x_pos-sensors[i].x_pos)*(sensors[j].x_pos-sensors[i].x_pos) + 
						(sensors[j].y_pos-sensors[i].y_pos)*(sensors[j].y_pos-sensors[i].y_pos)));
				if (dist < sensorNetwork.transmission_range) {
					edgeDistanceArray[number_of_DGs+i][number_of_DGs+j] = (float) dist;
					edgeEnergyCostArray[number_of_DGs+i][number_of_DGs+j] = (float)((0.00032f)+(0.00000032f)*(dist*dist));
					// edgeEnergyCostArray[number_of_DGs+i][number_of_DGs+j] = (float)((320000.0f)+(320000.0f*(dist*dist)));
					EdgeNode node1 = new EdgeNode(number_of_DGs+j+1, (float) dist,0);
					EdgeNode node2 = new EdgeNode(number_of_DGs+i+1, (float) dist,0);
					//random = new Random().nextDouble();
					//result = minimum_initial_energy + (random * (maximum_initial_energy - minimum_initial_energy));
					//FlowEdge e1 = new FlowEdge(number_of_DGs+j+1,number_of_DGs+i+1,edgeEnergyCostArray[number_of_DGs+i][number_of_DGs+j], result);
					//random = new Random().nextDouble();
					//result = minimum_initial_energy + (random * (maximum_initial_energy - minimum_initial_energy));
					//FlowEdge e2 = new FlowEdge(number_of_DGs+i+1,number_of_DGs+j+1,edgeEnergyCostArray[number_of_DGs+i][number_of_DGs+j], result);
					//NetworkFlow.addEdge(e1);
					//NetworkFlow.addEdge(e2);
					
					// adjList.addEdge(number_of_DGs+i, node1);
					adjList[number_of_DGs+i+1] = NodeList.cons(node1, adjList[number_of_DGs+i+1]);
					// adjList.addEdge(number_of_DGs+j, node2);
					adjList[number_of_DGs+j+1] = NodeList.cons(node2, adjList[number_of_DGs+j+1]);
				}
			}
		}
		
		//System.out.println(G.toString());
		
		// Node t (node size+1) needs as many edges as sensors
		for (int i=number_of_DGs+1; i<size+1; i++) {
			result = rand.nextInt(maximum_initial_energy - minimum_initial_energy + 1) + minimum_initial_energy;
			FlowEdge e = new FlowEdge(i,size+1,0,Math.min(storage_capacity_of_each_non_DG,result), 0);
			FlowEdge e1 = new FlowEdge(size+1,i,0,Math.min(storage_capacity_of_each_non_DG,result), 0);
			G.addEdge(e);G.addEdge(e1);
		}
		
		//System.out.println(G.toString());
	}
	
	/* DEBUG FUNCTION 
	public static void print_Edge_Array() {
		System.out.println();
		for (int i=0; i<number_of_DGs+number_of_sensor_nodes; i++)
			for (int j=0; j<number_of_DGs+number_of_sensor_nodes; j++)
				System.out.print((int)edgeDistanceArray[i][j]+"\t");
			System.out.println();
	}*/
	
	JPanel labelAndComponent = new JPanel();
	public sensorNetwork() {	
		super(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
 
        JPanel buttonRow = new JPanel();
        // default FlowLayout
        //buttonRow.add(createButtonRow()); // uncomment this
        // tabbedPane.addTab("test", buttonRow)
 
        // default FlowLayout
        labelAndComponent.add(createLabelAndComponent(0));
        labelAndComponent.add(createLabelAndComponent(1));
        labelAndComponent.add(createLabelAndComponent(2));
        tabbedPane.addTab("Sensor Network", labelAndComponent);
 
        JPanel HeuristicVsPBA = new JPanel();
        HeuristicVsPBA.add(HeuristicVsPBA());
        tabbedPane.addTab("Heuristic Vs. PBA", HeuristicVsPBA);
        
        JPanel Plot1 = new JPanel();
        Plot1.add(HeuristicVsPBA1());
        tabbedPane.addTab("Heuristic Vs. PBA (1)", Plot1);
        
        JPanel Plot2 = new JPanel();
        Plot2.add(HeuristicVsPBA2());
        tabbedPane.addTab("Heuristic Vs. PBA (2)", Plot2);
        
        JPanel buttonAndComponent = new JPanel();
        buttonAndComponent.add(createButtonRow()); // uncomment this
        tabbedPane.addTab("Tr vs. Nmin", buttonAndComponent);
 
        // add tabbedPane to panel
        add(tabbedPane, BorderLayout.CENTER);
    }
	
	 // variable declaration
    private JPanel jPanel2;
    private JPanel jPanel3;
    JComboBox comboBox;
    JComboBox comboBox1;

    // class name also used as an attribute
    // class jPanel2 extends JPanel {
	class Panel2 extends JPanel {
        Panel2() {
            // set a preferred size for the custom panel
            setPreferredSize(new Dimension(network_width*13,network_height*13));
        }

        /*for (int v = 0; v < G.V(); v++) {
			for (FlowEdge e1 : G.adj(v)) {
				if ((v == e1.from()) && e1.flow() > 0 && e1.to()!=size+1) {
					System.out.println("   " + e1 + "   " + e1.flow + "   " + e1.to());
					for (FlowEdge e3 : G.adj(e1.to())) {
						//e3.capacity -= e1.flow()*1;
						//System.out.println("Decrement Capacity of edge "+e3+" adjacent to node "+e1.to()+" by "+e1.flow);
					}
				}
			}
		}*/
        
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
			if (outputState == 99) {
            	g.setColor(Color.BLACK);
            	g.setFont(new Font("sansserif", Font.BOLD, 16));
            	g.drawString("Input fails pq*400 <= (N_min-p)*m. Please re-enter.",8,network_height*13-27);
            }
            if (outputState!=0 && outputState != 99){
        		// draw edges
        		g.setColor(Color.BLACK);
        		for (int i=0; i<(number_of_DGs); i++) {
        			for(int j=i; j<number_of_DGs; j++) {
        				// System.out.print("#");
        				if ((DGs[j].x_pos-DGs[i].x_pos)*(DGs[j].x_pos-DGs[i].x_pos) + 
        						(DGs[j].y_pos-DGs[i].y_pos)*(DGs[j].y_pos-DGs[i].y_pos)	
        						< sensorNetwork.transmission_range*sensorNetwork.transmission_range) {
        					// System.out.print("$");
        					g.drawLine(DGs[j].x_pos*11+24, DGs[j].y_pos*11+39, DGs[i].x_pos*11+24, DGs[i].y_pos*11+39);
        				}
        			}
        		}
				
        		for (int i=0; i<(number_of_DGs); i++) {
        			for(int j=i; j<number_of_sensor_nodes; j++) {
        				if ((sensors[j].x_pos-DGs[i].x_pos)*(sensors[j].x_pos-DGs[i].x_pos) + 
        						(sensors[j].y_pos-DGs[i].y_pos)*(sensors[j].y_pos-DGs[i].y_pos)	
        						< sensorNetwork.transmission_range*sensorNetwork.transmission_range) {
        					g.drawLine(sensors[j].x_pos*11+24, sensors[j].y_pos*11+39, DGs[i].x_pos*11+24, DGs[i].y_pos*11+39);
        				}
        			}
        		}
				
        		for (int i=0; i<(number_of_sensor_nodes); i++) {
        			for(int j=i; j<number_of_DGs; j++) {
        				if ((DGs[j].x_pos-sensors[i].x_pos)*(DGs[j].x_pos-sensors[i].x_pos) + 
        						(DGs[j].y_pos-sensors[i].y_pos)*(DGs[j].y_pos-sensors[i].y_pos)	
        						< sensorNetwork.transmission_range*sensorNetwork.transmission_range) {
        					g.drawLine(DGs[j].x_pos*11+24, DGs[j].y_pos*11+39, sensors[i].x_pos*11+24, sensors[i].y_pos*11+39);
        				}
        			}
        		}
				
        		for (int i=0; i<(number_of_sensor_nodes); i++) {
        			for(int j=i; j<number_of_sensor_nodes; j++) {
        				if ((sensors[j].x_pos-sensors[i].x_pos)*(sensors[j].x_pos-sensors[i].x_pos) + 
        						(sensors[j].y_pos-sensors[i].y_pos)*(sensors[j].y_pos-sensors[i].y_pos)	
        						< sensorNetwork.transmission_range*sensorNetwork.transmission_range) {
        					g.drawLine(sensors[j].x_pos*11+24, sensors[j].y_pos*11+39, sensors[i].x_pos*11+24, sensors[i].y_pos*11+39);
        				}
        			}
        		}
        		
        		
        		for (int v = 1; v < G.V(); v++) {
        			for (FlowEdge e2: G.adj(v)) {
        				if ((v == e2.from()) && e2.flow() > 0 && e2.to()!=number_of_DGs+number_of_sensor_nodes+1) {
        					g.setColor(Color.ORANGE);
        					if (e2.to()>number_of_DGs) {
        						g.drawLine(sensors[e2.to()-number_of_DGs-1].x_pos*11+23, sensors[e2.to()-number_of_DGs-1].y_pos*11+38, DGs[e2.from()-1].x_pos*11+23, DGs[e2.from()-1].y_pos*11+38);
        						g.drawLine(sensors[e2.to()-number_of_DGs-1].x_pos*11+23, sensors[e2.to()-number_of_DGs-1].y_pos*11+39, DGs[e2.from()-1].x_pos*11+23, DGs[e2.from()-1].y_pos*11+39);
        						g.drawLine(sensors[e2.to()-number_of_DGs-1].x_pos*11+24, sensors[e2.to()-number_of_DGs-1].y_pos*11+38, DGs[e2.from()-1].x_pos*11+24, DGs[e2.from()-1].y_pos*11+38);
        						g.drawLine(sensors[e2.to()-number_of_DGs-1].x_pos*11+24, sensors[e2.to()-number_of_DGs-1].y_pos*11+39, DGs[e2.from()-1].x_pos*11+24, DGs[e2.from()-1].y_pos*11+39);//original
        						g.drawLine(sensors[e2.to()-number_of_DGs-1].x_pos*11+25, sensors[e2.to()-number_of_DGs-1].y_pos*11+39, DGs[e2.from()-1].x_pos*11+25, DGs[e2.from()-1].y_pos*11+39);
        						g.drawLine(sensors[e2.to()-number_of_DGs-1].x_pos*11+24, sensors[e2.to()-number_of_DGs-1].y_pos*11+40, DGs[e2.from()-1].x_pos*11+24, DGs[e2.from()-1].y_pos*11+40);
        						g.drawLine(sensors[e2.to()-number_of_DGs-1].x_pos*11+25, sensors[e2.to()-number_of_DGs-1].y_pos*11+40, DGs[e2.from()-1].x_pos*11+25, DGs[e2.from()-1].y_pos*11+40);
        					} else if (e2.from()<=number_of_DGs) {
        						g.drawLine(DGs[e2.to()-1].x_pos*11+23, DGs[e2.to()-1].y_pos*11+38, DGs[e2.from()-1].x_pos*11+23, DGs[e2.from()-1].y_pos*11+38);
        						g.drawLine(DGs[e2.to()-1].x_pos*11+23, DGs[e2.to()-1].y_pos*11+39, DGs[e2.from()-1].x_pos*11+23, DGs[e2.from()-1].y_pos*11+39);
        						g.drawLine(DGs[e2.to()-1].x_pos*11+24, DGs[e2.to()-1].y_pos*11+38, DGs[e2.from()-1].x_pos*11+24, DGs[e2.from()-1].y_pos*11+38);
        						g.drawLine(DGs[e2.to()-1].x_pos*11+24, DGs[e2.to()-1].y_pos*11+39, DGs[e2.from()-1].x_pos*11+24, DGs[e2.from()-1].y_pos*11+39);//original
        						g.drawLine(DGs[e2.to()-1].x_pos*11+25, DGs[e2.to()-1].y_pos*11+39, DGs[e2.from()-1].x_pos*11+25, DGs[e2.from()-1].y_pos*11+39);
        						g.drawLine(DGs[e2.to()-1].x_pos*11+24, DGs[e2.to()-1].y_pos*11+40, DGs[e2.from()-1].x_pos*11+24, DGs[e2.from()-1].y_pos*11+40);
        						g.drawLine(DGs[e2.to()-1].x_pos*11+25, DGs[e2.to()-1].y_pos*11+40, DGs[e2.from()-1].x_pos*11+25, DGs[e2.from()-1].y_pos*11+40);
        					}
        					g.setFont(new Font("sansserif", Font.PLAIN, 12));
        					//g.drawString(""+e2.flow, (sensors[j].x_pos*11+24 + DGs[i].x_pos*11+24)/2, (sensors[j].y_pos*11+39 + DGs[i].y_pos*11+39)/2);
        					g.setColor(Color.BLACK);
        					g.setFont(new Font("sansserif", Font.BOLD, 16));
        				}
        			}
        		}
        		
        		// draw nodes
        		for (int i=0; i<number_of_DGs; i++) {   
        			//System.out.print("\n" +DGs[i].x_pos + ", " + DGs[i].y_pos);
        			g.setColor(Color.BLACK);
        			g.drawOval(DGs[i].x_pos*11+20,DGs[i].y_pos*11+35,7,7);  
        			g.setColor(Color.BLUE);
        			g.fillOval(DGs[i].x_pos*11+20,DGs[i].y_pos*11+35,7,7);
        			g.setFont(new Font("sansserif", Font.BOLD, 16));
        			g.drawString(""+i, DGs[i].x_pos*11+20+5, DGs[i].y_pos*11+35);
        		}
        				
        		for (int i=0; i<number_of_sensor_nodes; i++) {
        			g.setColor(Color.BLACK);
        			g.drawOval(sensors[i].x_pos*11+20,sensors[i].y_pos*11+35,7,7);  
        		 	g.setColor(Color.RED);  
        		 	g.fillOval(sensors[i].x_pos*11+20,sensors[i].y_pos*11+35,7,7);
        		 	g.setFont(new Font("sansserif", Font.BOLD, 16));
        			g.drawString(""+i, sensors[i].x_pos*11+20+5, sensors[i].y_pos*11+35);
        		}
        		
        		// display connectivity
        		g.setColor(Color.BLACK);
        		if (is_connected) g.drawString("This is a connected graph. ",8,network_height*13-27);
        		else g.drawString("This is NOT a connected graph. ",8,network_height*13-27);
        		
        		// display Energy Consumption, Distance and number of hops from dijkStartNode to dijkEndNode
        		if (outputState==4)
					g.drawString("Minimum Distance from DG_"+dijkStartNode+" to Sensor_"+(dijkEndNode-number_of_DGs-1)+
								" = "+fringeWgt[dijkEndNode]+" Meters.",8,network_height*13-10);
        		if (outputState==3)
					g.drawString("Minimum Hops from DG_"+dijkStartNode+" to Sensor_"+(dijkEndNode-number_of_DGs-1)+
								" = "+(int)fringeWgt1[dijkEndNode]+" Hops.",8,network_height*13-10);
        		if (outputState==5)
        			g.drawString("Max Flow = "+(int)max_flow+" of "+(int)(number_of_DGs*number_of_data_items_each_DG_has),8,network_height*13-10);
                if (outputState==2)
                	if(fringeWgt[dijkEndNode] == 0)
                	{
                		g.drawString("Energy to send 1-data from DG_"+dijkStartNode+" to Sensor_"+(dijkEndNode-number_of_DGs-1)+
								" = 0.0 Joules.",8,network_height*13-10);
                	} else
					g.drawString("Energy to send 1-data from DG_"+dijkStartNode+" to Sensor_"+(dijkEndNode-number_of_DGs-1)+
								" = "+(float)((0.00032f)+(.00000032f*(fringeWgt[dijkEndNode]*fringeWgt[dijkEndNode])))+
								" Joules.",8,network_height*13-10);
			}
        }
    }
	
	// objective 2
    public double [] x;
    public double [] y;
    public double [] z;
	private static final Random r = new Random();
	private List coords1 = new ArrayList();
	private List coords2 = new ArrayList();
	public JPanel createButtonRow() {
        String title= "Tr vs. Nmin";
 
        JPanel pane = new JPanel();

        int size=0;
        
        XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series = new XYSeries("A Connected Graph");
        
        boolean[][] array = new boolean[125][100];
        for (int i=0;i<125;i++) {
        	for (int j=0; j<100;j++) {
        		array[i][j]=false;
        	}
        }
        
        for (int q=0; q<6; q++) {
            for (transmission_range =0.0f; transmission_range <=25.0f; transmission_range+=0.2f) {
            	for (number_of_DGs=3; number_of_DGs<100; number_of_DGs++) {
            		generate_the_sensor_network();
    				adjList = new NodeList[number_of_DGs+number_of_sensor_nodes+1];
    				compute_edgeArray_adjList();
    				discovered = new boolean[number_of_DGs+number_of_sensor_nodes+1];
    				NodeList.BFS_to_compose_the_discovered_array();
    				is_connected = BFS_connected();
    				
    				if (is_connected && !((transmission_range<12)&&(number_of_DGs<15))) {
    					size++;
    					float i= transmission_range*5;
    					array[(int)i][number_of_DGs] = true;
    					break;
    				}
            	}
            }
        }

        x = new double[size];
        y = new double[size];
        
        int l=0;
        for (int i=0;i<125;i++) {
        	for (int j=1; j<100;j++) {
        		if(array[i][j]==true && array[i][j-1]==false) {
        			series.add((float)i*0.2f, j);
        			x[l] = (double) i * 0.2f;
        			y[l] = (double) j;
        			l++;
        		}
        	}
        }
		
		// extra credit
        
        PolynomialRegression regression = new PolynomialRegression(x, y, 8);
        System.out.println(regression);
        double zero = regression.beta(0);
        double one = regression.beta(1);
        double two = regression.beta(2);
        double three = regression.beta(3);
        double four = regression.beta(4);
        double five = regression.beta(5);
        double six = regression.beta(6);
        double seven = regression.beta(7);
        double eight = regression.beta(8);  
        
        XYSeries series1 = new XYSeries("Polynomial Regressor");
        for (int i=30; i<125; i++) {
        	double x = i * 0.2;
        	double y = zero + one*x + two*x*x + three*x*x*x + four*x*x*x*x + five*x*x*x*x*x + 
						six*x*x*x*x*x*x + seven*x*x*x*x*x*x*x + eight*x*x*x*x*x*x*x*x;
        	series1.add(x,y);
        }
        
        JLabel label = new JLabel("N_min = "+regression.toString());
        
        result.addSeries(series1);
        result.addSeries(series);
        
        JFreeChart chart = ChartFactory.createScatterPlot(
                "", // chart title
                "Transmission Range", // x axis label
                "N min", // y axis label
                result, // XYdata
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
                );

        final ChartPanel panel = new ChartPanel(chart, true);
        panel.setPreferredSize(new java.awt.Dimension(640, 480));

        panel.setMinimumDrawHeight(10);
        panel.setMaximumDrawHeight(2000);
        panel.setMinimumDrawWidth(20);
        panel.setMaximumDrawWidth(2000);
        
        pane.add(panel);
        pane.add(label);
        
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        
        return pane;
    }
	
	// Project 2: Heuristic VS PBA
	public JPanel HeuristicVsPBA() {
		
		network_width = 50;
		network_height = 50;
		number_of_DGs = 20;
		number_of_sensor_nodes = 20;
		storage_capacity_of_each_non_DG = 99999;
		transmission_range = 13.0f;
		minimum_initial_energy = 0;
		maximum_initial_energy = 0;
		number_of_data_items_each_DG_has = 60;
		
		JLabel label = new JLabel ("Width: 50");
        JLabel label1 = new JLabel("Height: 50");
        JLabel label2 = new JLabel("Number of DGs: 20");
        JLabel label3 = new JLabel("Number of Sensors: 20");
        JLabel label4 = new JLabel("Storage Capacity of non-DG: 99999");
        JLabel label5 = new JLabel("Transmission Range: 13");
        JLabel label6 = new JLabel("Initial Energy: (var)");
        JLabel label7 = new JLabel("Data-Items per DG: 60");
		
		JPanel pane = new JPanel();
		// x-axis: vary the number_of_data_items_each_DG_has
		XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series = new XYSeries("PBA");
        XYSeries series1 = new XYSeries("Heuristic");
        double max=0.0;
        for (int j=0; j<10; j++) {
            for (minimum_initial_energy=maximum_initial_energy=1; maximum_initial_energy<=100; ) {
            		generate_the_sensor_network();
    				adjList = new NodeList[number_of_DGs+number_of_sensor_nodes+1];
    				// creates graph G
    				compute_edgeArray_adjList();
    				
    				//System.out.println("Compute Max Flow\n");
    				int size = number_of_DGs +number_of_sensor_nodes;
    				FordFulkerson maxflow = new FordFulkerson(G, 0, size+1);
    				//for (int a=0; a<5; a++) {
    				for (int i=1; i<=number_of_DGs; i++) {
    					for (FlowEdge e6: G.adj(0)) {
    						e6.capacity=0;
    					}
    					FlowEdge e7 = new FlowEdge(0,i,0,number_of_data_items_each_DG_has, 0);
    					FlowEdge e8 = new FlowEdge(i,0,0,number_of_data_items_each_DG_has, 0);
    					G.addEdge(e7);G.addEdge(e8);
    	
    					//System.out.println(G.toString());
    					//System.out.println("Max flow from " + 0 + " to " + (size+1));
    					maxflow = new FordFulkerson(G, 0, size+1);
    					if (i==number_of_DGs){
    					for (int v = 0; v < G.V(); v++) {
    						for (FlowEdge e1 : G.adj(v)) {
    							if ((v == e1.from()) && e1.flow() > 0 && e1.to()!=size+1) {
    								// System.out.println("   " + e1 + "\t" + e1.flow + "\t" + (e1.from()-1) +"\t"+ (e1.to()-1-20));
    								for (FlowEdge e3 : G.adj(e1.to())) {
    									e3.capacity -= e1.flow()*1;
    									//System.out.println("Decrement Capacity of edge "+e3+" adjacent to node "+e1.to()+" by "+e1.flow);
    								}
    							}
    						}
    					}
    				}
    				}
    				//System.out.println(maxflow.value());
    		        series.add((double)maximum_initial_energy,(double)maxflow.value());
    		        
    		        // Heuristics 
    		        //
    		        adjList = new NodeList[size+1];
    		        parent1 = new int[size+1];
					fringeWgt1 = new float[size+1];
    		        compute_edgeArray_adjList();
    		        int offloadTotal=0;
    		        for (int i=1; i<=number_of_DGs; i++) {
    		        	dijkstra.shortestHopPaths1(adjList,size,i,parent1,fringeWgt1);
    		        	while (true) {

    		        		// Is there a i-sensor path, if not, break
    		        		boolean existsPath;
    		        		existsPath =false;
    		        		/*System.out.println(""+i+":              ");
    		        		for (int p=0; p<parent1.length; p++) {
    		        			System.out.print("   "+p+":"+parent1[p]);
    		        		}*/
    		        		for (int l=number_of_DGs+1; l<size+1;l++) {
    		        			if (parent1[l]>0) existsPath=true;
    		        		}
    		        		if (!existsPath) break;
    		        		if (capacitee[i]<=0) break;
    		        	
    		        		double shortest=999999.99;
    		        		int shortest_index=999999;
    		        		for (int k=number_of_DGs+1;k<size+1;k++) {
    		        			// k is dijkEndNode
    		        			if (fringeWgt1[k]<shortest && parent1[k]!=0 && parent1[k]!=-2) { 
    		        				if (capacity[parent1[k]][k]>0)
    		        					shortest=fringeWgt1[k]; shortest_index=k;
    		        			}
    		        		}
    		        		// now that we know the DG-sensor path to send
    		        		//System.out.println("shortest_index = "+shortest_index);
    		        		
    		        		// find the egde that has the smallest capacity
    		        		int smallest_capacity=1000000;
    		        		int node=shortest_index;
    		        		if (node==999999) break;
    		        		if (parent1[node]>0) smallest_capacity = capacitee[parent1[node]-1];//=capacity[parent1[node]][node];
    		        		int index_of_smallest_capacity=0;
    		        		while (parent1[node]>0 && node>0) {
    		        			if (capacitee[parent1[node]-1] < smallest_capacity || capacitee[node-1] < smallest_capacity) {//if (capacity[parent1[node]][node] < smallest_capacity) {
    		        				smallest_capacity = Math.min(capacitee[parent1[node]-1],capacitee[node-1]);//capacity[parent1[node]][node];
    		        				index_of_smallest_capacity=node;
    		        			}
    		        			node = parent1[node];
    		        		}
    		        		if (capacitee[i]<smallest_capacity) smallest_capacity = capacitee[i];
    		        		if (smallest_capacity<=0) break;
    		        		//System.out.println("Edge with smallest capacity = "+index_of_smallest_capacity+"::: smallest capacity = "+smallest_capacity);
    		        		
    		        		// decrement all nodes on that path by smallest_capacity
    		        		node=shortest_index;
    		        		while (node >=0) {
    		        			
    		        			if (capacitee[node-1]>=smallest_capacity) {
    		        				capacitee[node-1]-=smallest_capacity; System.out.println("capacitee["+(node-1)+"] -= "+smallest_capacity+"...capacitee["+(node-1)+"]="+capacitee[node-1]);
    		        			} else {
    		        				offloadTotal+=capacitee[node-1];capacitee[node-1]-=capacitee[node-1];
    		        			}
    		        			node=parent1[node];
    		        		}
    		        		if (capacitee[i]>=smallest_capacity) {
    		        			capacitee[i]-=smallest_capacity;
    		        			offloadTotal+=smallest_capacity;
    		        		}
    		        		parent1[index_of_smallest_capacity]=-2;
    		        		fringeWgt1[index_of_smallest_capacity]=999999;
    		        		System.out.println("i="+i+"   offloadTotal="+offloadTotal+"+"+smallest_capacity);
    		        		
    		        	}
    		        }
    		        
    		        
    		        /*double total_heuristic_offload_value=0.0;
    		        for (int i=1; i<=number_of_DGs; i++) {
    		        	FlowEdge e1 = new FlowEdge(-1,-1,999999999999999999.9,999999999999999.9,99999999999999999999999.9);
    		        	boolean flag;
    		        	do {
    		        		flag=false; e1.distance = 99999999999999.99;
    		        		// after this chunck of code, e1 is the shortest edge adjaceny to DG[i] that has a capacity
    		        		for (FlowEdge e: G.adj(i)) {
								if (e.to() <= number_of_DGs) {
									e.capacity=0;
								} else {
									if (e.distance<e1.distance && e.capacity>0) {
										e1 = e; flag=true;
									}
								}
							}
    		        		// end chuunck of code
    		        		if (flag) {
    		        			double offload_this_much= Math.min(e1.capacity, DGs[i-1].number_of_data_items_to_offload);
    		        			DGs[i-1].number_of_data_items_to_offload -= offload_this_much;
    		        			e1.capacity -= offload_this_much;
    		        			total_heuristic_offload_value += offload_this_much;
    		        		}
    		        	} while (DGs[i-1].number_of_data_items_to_offload>0 && flag);
    		        }*/
    		        series1.add((double)maximum_initial_energy,(double)offloadTotal);
    		        maximum_initial_energy++;
    		        minimum_initial_energy++;
            }
        }
            result.addSeries(series);
            result.addSeries(series1);

            JFreeChart chart = ChartFactory.createScatterPlot(
                    "Initial Energy", // chart title
                    "Initial Energy", // x axis label
                    "Max Flow", // y axis label
                    result, // XYdata
                    PlotOrientation.VERTICAL,
                    true, // include legend
                    true, // tooltips
                    false // urls
                    );

            final ChartPanel panel = new ChartPanel(chart, true);
            panel.setPreferredSize(new java.awt.Dimension(640, 480));

            panel.setMinimumDrawHeight(10);
            panel.setMaximumDrawHeight(2000);
            panel.setMinimumDrawWidth(20);
            panel.setMaximumDrawWidth(2000);
            
            pane.add(panel);
            pane.add(label);
            pane.add(label1);
            pane.add(label2);
            pane.add(label3);
            pane.add(label4);
            pane.add(label5);
            pane.add(label6);
            pane.add(label7);
            pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		
		return pane;
	}
	
	public JPanel HeuristicVsPBA1() {
		
		network_width = 50;
		network_height = 50;
		number_of_DGs = 20;
		number_of_sensor_nodes = 20;
		storage_capacity_of_each_non_DG = 0;
		transmission_range = 13.0f;
		minimum_initial_energy = 999999;
		maximum_initial_energy = 999999;
		number_of_data_items_each_DG_has = 60;
		
		JLabel label = new JLabel ("Width: 50");
        JLabel label1 = new JLabel("Height: 50");
        JLabel label2 = new JLabel("Number of DGs: 20");
        JLabel label3 = new JLabel("Number of Sensors: 20");
        JLabel label4 = new JLabel("Storage Capacity of non-DG: (var)");
        JLabel label5 = new JLabel("Transmission Range: 13");
        JLabel label6 = new JLabel("Initial Energy: 999999");
        JLabel label7 = new JLabel("Data-Items per DG: 60");
		
		JPanel pane = new JPanel();
		// x-axis: vary the number_of_data_items_each_DG_has
		XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series = new XYSeries("PBA");
        XYSeries series1 = new XYSeries("Heuristic");
        for (int j=0; j<10; j++) {
            for (storage_capacity_of_each_non_DG=1; storage_capacity_of_each_non_DG<=100; storage_capacity_of_each_non_DG++) {
            		generate_the_sensor_network();
    				adjList = new NodeList[number_of_DGs+number_of_sensor_nodes+1];
    				// creates graph G
    				compute_edgeArray_adjList();
    				
    				//System.out.println("Compute Max Flow\n");
    				int size = number_of_DGs +number_of_sensor_nodes;
    				FordFulkerson maxflow = new FordFulkerson(G, 0, size+1);
    				for (int i=1; i<=number_of_DGs; i++) {
    					for (FlowEdge e6: G.adj(0)) {
    						e6.capacity=0;
    					}
    					FlowEdge e7 = new FlowEdge(0,i,0,number_of_data_items_each_DG_has, 0);
    					FlowEdge e8 = new FlowEdge(i,0,0,number_of_data_items_each_DG_has, 0);
    					G.addEdge(e7);G.addEdge(e8);
    				
    					//System.out.println(G.toString());
    					//System.out.println("Max flow from " + 0 + " to " + (size+1));
    					maxflow = new FordFulkerson(G, 0, size+1);
    					if (i==number_of_DGs){
    					for (int v = 0; v < G.V(); v++) {
    						for (FlowEdge e1 : G.adj(v)) {
    							if ((v == e1.from()) && e1.flow() > 0 && e1.to()!=size+1) {
    								// System.out.println("   " + e1 + "\t" + e1.flow + "\t" + (e1.from()-1) +"\t"+ (e1.to()-1-20));
    								for (FlowEdge e3 : G.adj(e1.to())) {
    									e3.capacity -= e1.flow()*1;
    									//System.out.println("Decrement Capacity of edge "+e3+" adjacent to node "+e1.to()+" by "+e1.flow);
    								}
    							}
    						}
    					}
    				}
    				}
    				//System.out.println(maxflow.value());
    		        series.add((double)storage_capacity_of_each_non_DG,(double)maxflow.value());
    		        
    		     // Heuristics 
    		        //
    		        adjList = new NodeList[size+1];
    		        parent1 = new int[size+1];
					fringeWgt1 = new float[size+1];
    		        compute_edgeArray_adjList();
    		        int offloadTotal=0;
    		        for (int i=1; i<=number_of_DGs; i++) {
    		        	dijkstra.shortestHopPaths1(adjList,size,i,parent1,fringeWgt1);
    		        	while (true) {

    		        		// Is there a i-sensor path, if not, break
    		        		boolean existsPath;
    		        		existsPath =false;
    		        		/*System.out.println(""+i+":              ");
    		        		for (int p=0; p<parent1.length; p++) {
    		        			System.out.print("   "+p+":"+parent1[p]);
    		        		}*/
    		        		for (int l=number_of_DGs+1; l<size+1;l++) {
    		        			if (parent1[l]>0) existsPath=true;
    		        		}
    		        		if (!existsPath) break;
    		        		if (capacitee[i]<=0) break;
    		        	
    		        		double shortest=999999.99;
    		        		int shortest_index=999999;
    		        		for (int k=number_of_DGs+1;k<size+1;k++) {
    		        			// k is dijkEndNode
    		        			if (fringeWgt1[k]<shortest && parent1[k]!=0 && parent1[k]!=-2) { 
    		        				if (capacity[parent1[k]][k]>0)
    		        					shortest=fringeWgt1[k]; shortest_index=k;
    		        			}
    		        		}
    		        		// now that we know the DG-sensor path to send
    		        		//System.out.println("shortest_index = "+shortest_index);
    		        		
    		        		// find the egde that has the smallest capacity
    		        		int smallest_capacity=1000000;
    		        		int node=shortest_index;
    		        		if (node==999999) break;
    		        		if (parent1[node]>0) smallest_capacity = capacitee[parent1[node]-1];//=capacity[parent1[node]][node];
    		        		int index_of_smallest_capacity=0;
    		        		while (parent1[node]>0 && node>0) {
    		        			if (capacitee[parent1[node]-1] < smallest_capacity || capacitee[node-1] < smallest_capacity) {//if (capacity[parent1[node]][node] < smallest_capacity) {
    		        				smallest_capacity = Math.min(capacitee[parent1[node]-1],capacitee[node-1]);//capacity[parent1[node]][node];
    		        				index_of_smallest_capacity=node;
    		        			}
    		        			node = parent1[node];
    		        		}
    		        		if (capacitee[i]<smallest_capacity) smallest_capacity = capacitee[i];
    		        		if (smallest_capacity<=0) break;
    		        		//System.out.println("Edge with smallest capacity = "+index_of_smallest_capacity+"::: smallest capacity = "+smallest_capacity);
    		        		
    		        		// decrement all nodes on that path by smallest_capacity
    		        		node=shortest_index;
    		        		while (node >=0) {
    		        			//capacity[parent1[node]][node]-=smallest_capacity;
    		        			//capacitee[parent1[node]-1]-=smallest_capacity;
    		        			if (capacitee[node-1]>=smallest_capacity) {
    		        				capacitee[node-1]-=smallest_capacity; System.out.println("capacitee["+(node-1)+"] -= "+smallest_capacity+"...capacitee["+(node-1)+"]="+capacitee[node-1]);
    		        			} else {
    		        				offloadTotal+=capacitee[node-1];capacitee[node-1]-=capacitee[node-1];
    		        			}
    		        			node=parent1[node];
    		        		}
    		        		if (capacitee[i]>=smallest_capacity) {
    		        			capacitee[i]-=smallest_capacity;
    		        			offloadTotal+=smallest_capacity;
    		        		}
    		        		parent1[index_of_smallest_capacity]=-2;
    		        		fringeWgt1[index_of_smallest_capacity]=999999;
    		        		System.out.println("i="+i+"   offloadTotal="+offloadTotal+"+"+smallest_capacity);
    		        		
    		        	}
    		        }
    		        series1.add((double)storage_capacity_of_each_non_DG,offloadTotal);
            }
        }
            result.addSeries(series);
            result.addSeries(series1);

            JFreeChart chart = ChartFactory.createScatterPlot(
                    "Storage Capacity", // chart title
                    "Storage Capacity", // x axis label
                    "Max Flow", // y axis label
                    result, // XYdata
                    PlotOrientation.VERTICAL,
                    true, // include legend
                    true, // tooltips
                    false // urls
                    );

            final ChartPanel panel = new ChartPanel(chart, true);
            panel.setPreferredSize(new java.awt.Dimension(640, 480));

            panel.setMinimumDrawHeight(10);
            panel.setMaximumDrawHeight(2000);
            panel.setMinimumDrawWidth(20);
            panel.setMaximumDrawWidth(2000);
            
            pane.add(panel);
            pane.add(label);
            pane.add(label1);
            pane.add(label2);
            pane.add(label3);
            pane.add(label4);
            pane.add(label5);
            pane.add(label6);
            pane.add(label7);
            pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		
		return pane;
	}
	
public JPanel HeuristicVsPBA2() {
		
		network_width = 50;
		network_height = 50;
		number_of_DGs = 20;
		number_of_sensor_nodes = 20;
		storage_capacity_of_each_non_DG = 60;
		transmission_range = 13.0f;
		minimum_initial_energy = 60;
		maximum_initial_energy = 60;
		number_of_data_items_each_DG_has = 0;
		
		JLabel label = new JLabel ("Width: 50");
        JLabel label1 = new JLabel("Height: 50");
        JLabel label2 = new JLabel("Number of DGs: 20");
        JLabel label3 = new JLabel("Number of Sensors: 20");
        JLabel label4 = new JLabel("Storage Capacity of non-DG: 60");
        JLabel label5 = new JLabel("Transmission Range: 13");
        JLabel label6 = new JLabel("Initial Energy: 60");
        JLabel label7 = new JLabel("Data-Items per DG: (var)");
		
		JPanel pane = new JPanel();
		// x-axis: vary the number_of_data_items_each_DG_has
		XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series = new XYSeries("PBA");
        XYSeries series1 = new XYSeries("Heuristic");
        for (int j=0; j<10; j++) {
            for (number_of_data_items_each_DG_has=1; number_of_data_items_each_DG_has<=100; number_of_data_items_each_DG_has++) {
            		generate_the_sensor_network();
    				adjList = new NodeList[number_of_DGs+number_of_sensor_nodes+1];
    				// creates graph G
    				compute_edgeArray_adjList();
    				
    				//System.out.println("Compute Max Flow\n");
    				int size = number_of_DGs +number_of_sensor_nodes;
    				FordFulkerson maxflow = new FordFulkerson(G, 0, size+1);
    				for (int i=1; i<=number_of_DGs; i++) {
    					for (FlowEdge e6: G.adj(0)) {
    						e6.capacity=0;
    					}
    					FlowEdge e7 = new FlowEdge(0,i,0,number_of_data_items_each_DG_has, 0);
    					FlowEdge e8 = new FlowEdge(i,0,0,number_of_data_items_each_DG_has, 0);
    					G.addEdge(e7);G.addEdge(e8);
    				
    					//System.out.println(G.toString());
    					//System.out.println("Max flow from " + 0 + " to " + (size+1));
    					maxflow = new FordFulkerson(G, 0, size+1);
    					if (i==number_of_DGs){
    					for (int v = 0; v < G.V(); v++) {
    						for (FlowEdge e1 : G.adj(v)) {
    							if ((v == e1.from()) && e1.flow() > 0 && e1.to()!=size+1) {
    								// System.out.println("   " + e1 + "\t" + e1.flow + "\t" + (e1.from()-1) +"\t"+ (e1.to()-1-20));
    								for (FlowEdge e3 : G.adj(e1.to())) {
    									e3.capacity -= e1.flow()*1;
    									//System.out.println("Decrement Capacity of edge "+e3+" adjacent to node "+e1.to()+" by "+e1.flow);
    								}
    							}
    						}
    					}
    				}
    				}
    				//System.out.println(maxflow.value());
    		        series.add((double)number_of_data_items_each_DG_has,(double)maxflow.value());
    		        
    		     // Heuristics 
    		        //
    		        adjList = new NodeList[size+1];
    		        parent1 = new int[size+1];
					fringeWgt1 = new float[size+1];
    		        compute_edgeArray_adjList();
    		        int offloadTotal=0;
    		        for (int i=1; i<=number_of_DGs; i++) {
    		        	dijkstra.shortestHopPaths1(adjList,size,i,parent1,fringeWgt1);
    		        	while (true) {

    		        		// Is there a i-sensor path, if not, break
    		        		boolean existsPath;
    		        		existsPath =false;
    		        		/*System.out.println(""+i+":              ");
    		        		for (int p=0; p<parent1.length; p++) {
    		        			System.out.print("   "+p+":"+parent1[p]);
    		        		}*/
    		        		for (int l=number_of_DGs+1; l<size+1;l++) {
    		        			if (parent1[l]>0) existsPath=true;
    		        		}
    		        		if (!existsPath) break;
    		        		if (capacitee[i]<=0) break;
    		        	
    		        		double shortest=999999.99;
    		        		int shortest_index=999999;
    		        		for (int k=number_of_DGs+1;k<size+1;k++) {
    		        			// k is dijkEndNode
    		        			if (fringeWgt1[k]<shortest && parent1[k]!=0 && parent1[k]!=-2) { 
    		        				if (capacity[parent1[k]][k]>0)
    		        					shortest=fringeWgt1[k]; shortest_index=k;
    		        			}
    		        		}
    		        		// now that we know the DG-sensor path to send
    		        		//System.out.println("shortest_index = "+shortest_index);
    		        		
    		        		// find the egde that has the smallest capacity
    		        		int smallest_capacity=1000000;
    		        		int node=shortest_index;
    		        		if (node==999999) break;
    		        		if (parent1[node]>0) smallest_capacity = capacitee[parent1[node]-1];//=capacity[parent1[node]][node];
    		        		int index_of_smallest_capacity=0;
    		        		while (parent1[node]>0 && node>0) {
    		        			if (capacitee[parent1[node]-1] < smallest_capacity || capacitee[node-1] < smallest_capacity) {//if (capacity[parent1[node]][node] < smallest_capacity) {
    		        				smallest_capacity = Math.min(capacitee[parent1[node]-1],capacitee[node-1]);//capacity[parent1[node]][node];
    		        				index_of_smallest_capacity=node;
    		        			}
    		        			node = parent1[node];
    		        		}
    		        		if (capacitee[i]<smallest_capacity) smallest_capacity = capacitee[i];
    		        		if (smallest_capacity<=0) break;
    		        		//System.out.println("Edge with smallest capacity = "+index_of_smallest_capacity+"::: smallest capacity = "+smallest_capacity);
    		        		
    		        		// decrement all nodes on that path by smallest_capacity
    		        		node=shortest_index;
    		        		while (node >=0) {
    		        			//capacity[parent1[node]][node]-=smallest_capacity;
    		        			//capacitee[parent1[node]-1]-=smallest_capacity;
    		        			if (capacitee[node-1]>=smallest_capacity) {
    		        				capacitee[node-1]-=smallest_capacity; System.out.println("capacitee["+(node-1)+"] -= "+smallest_capacity+"...capacitee["+(node-1)+"]="+capacitee[node-1]);
    		        			} else {
    		        				offloadTotal+=capacitee[node-1];capacitee[node-1]-=capacitee[node-1];
    		        			}
    		        			node=parent1[node];
    		        		}
    		        		if (capacitee[i]>=smallest_capacity) {
    		        			capacitee[i]-=smallest_capacity;
    		        			offloadTotal+=smallest_capacity;
    		        		}
    		        		parent1[index_of_smallest_capacity]=-2;
    		        		fringeWgt1[index_of_smallest_capacity]=999999;
    		        		System.out.println("i="+i+"   offloadTotal="+offloadTotal+"+"+smallest_capacity);
    		        		
    		        	}
    		        }
    		        series1.add((double)number_of_data_items_each_DG_has,offloadTotal);
            }
        }
            result.addSeries(series);
            result.addSeries(series1);

            JFreeChart chart = ChartFactory.createScatterPlot(
                    "Data Items", // chart title
                    "Data Items", // x axis label
                    "Max Flow", // y axis label
                    result, // XYdata
                    PlotOrientation.VERTICAL,
                    true, // include legend
                    true, // tooltips
                    false // urls
                    );

            final ChartPanel panel = new ChartPanel(chart, true);
            panel.setPreferredSize(new java.awt.Dimension(640, 480));

            panel.setMinimumDrawHeight(10);
            panel.setMaximumDrawHeight(2000);
            panel.setMinimumDrawWidth(20);
            panel.setMaximumDrawWidth(2000);
            
            pane.add(panel);
            pane.add(label);
            pane.add(label1);
            pane.add(label2);
            pane.add(label3);
            pane.add(label4);
            pane.add(label5);
            pane.add(label6);
            pane.add(label7);
            pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		
		return pane;
	}
	
	
	public JPanel createLabelAndComponent(int i) {	
		JPanel pane = new JPanel();
		if (i==1) {
			
        JLabel label1 = new JLabel("Input width of network: ");
        label1.setHorizontalAlignment(2);
        textfield1 = new JTextField();
        textfield1.setSize(30,12);
        textfield1.setHorizontalAlignment(JTextField.CENTER);
        textfield1.setText("50");
        textfield1.setEnabled(false);
        pane.add(label1);
        pane.add(textfield1);
        
        JLabel label2 = new JLabel("Input height of network: ");
        label2.setHorizontalAlignment(2);
        textfield2 = new JTextField();
        textfield2.setSize(30,12);
        textfield2.setHorizontalAlignment(JTextField.CENTER);
        textfield2.setText("50");
        textfield2.setEnabled(false);
        pane.add(label2);
        pane.add(textfield2);
        
        JLabel label3 = new JLabel("Input # of data generators (DGs): ");
        label3.setHorizontalAlignment(2);
        textfield3 = new JTextField();
        textfield3.setSize(30,12);
        textfield3.setHorizontalAlignment(JTextField.CENTER);
        textfield3.setText("20");
        pane.add(label3);
        pane.add(textfield3);
        
        JLabel label4 = new JLabel("Input # of sensor nodes: ");
        label4.setHorizontalAlignment(2);
        textfield4 = new JTextField();
        textfield4.setSize(30,12);
        textfield4.setHorizontalAlignment(JTextField.CENTER);
        textfield4.setText("20");
        pane.add(label4);
        pane.add(textfield4);
        
        JLabel label5 = new JLabel("Input transmission range: ");
        label5.setHorizontalAlignment(2);
        textfield5 = new JTextField();
        textfield5.setSize(30,12);
        textfield5.setHorizontalAlignment(JTextField.CENTER);
        textfield5.setText("13");
        pane.add(label5);
        pane.add(textfield5);
        
        JLabel label6 = new JLabel("Input # of data items for each DG: ");
        label6.setHorizontalAlignment(2);
        textfield6 = new JTextField();
        textfield6.setSize(30,12);
        textfield6.setHorizontalAlignment(JTextField.CENTER);
        textfield6.setText("60");
        pane.add(label6);
        pane.add(textfield6);
        
        JLabel label7 = new JLabel("Input storage capacity for each non-DG: ");
        label7.setHorizontalAlignment(2);
        textfield7 = new JTextField();
        textfield7.setSize(30,12);
        textfield7.setHorizontalAlignment(JTextField.CENTER);
        textfield7.setText("512000");
        pane.add(label7);
        pane.add(textfield7);
        
        JLabel label8 = new JLabel("Min initial energy of nodes: ");
        label8.setHorizontalAlignment(2);
        textfield8 = new JTextField();
        textfield8.setSize(30,12);
        textfield8.setHorizontalAlignment(JTextField.CENTER);
        textfield8.setText("46");
        pane.add(label8);
        pane.add(textfield8);
        
        JLabel label9 = new JLabel("Max initial energy of nodes: ");
        label9.setHorizontalAlignment(2);
        textfield9 = new JTextField();
        textfield9.setSize(30,12);
        textfield9.setHorizontalAlignment(JTextField.CENTER);
        textfield9.setText("57");
        pane.add(label9);
        pane.add(textfield9);
        
        JButton button = new JButton("Print Sensor Network");
        button.addActionListener(actionListener);
        label1.setHorizontalAlignment(2);
        pane.add(button);
        
        pane.add(new JLabel(" "));
        
        JButton button1 = new JButton("PBA -- Compute Max Flow");
        button1.addActionListener(actionListener);
        label1.setHorizontalAlignment(2);
        pane.add(button1);
        
        pane.setBorder(BorderFactory.createTitledBorder("Input Data"));
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));  
        } else if (i==0){
        	// custom Panel2, not a generic JPanel
        	jPanel2 = new Panel2();
        	jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        	jPanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        	// add component to frame to see it
        	pane.add(jPanel2);        
        	pane.setBorder(BorderFactory.createTitledBorder("Sensor Network"));
        	pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        } else if (i==2) {
        	GridLayout Layout = new GridLayout(2,1);
        	JPanel pane1 = new JPanel();
        	// String[] string = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19"};
        	// comboBox = new JComboBox(string);
        	// comboBox1 = new JComboBox(string);
        	// comboBox.setActionCommand("comboBox");
        	// comboBox1.setActionCommand("comboBox1");
        	// comboBox.addActionListener(actionListener);
        	// comboBox1.addActionListener(actionListener);
        	JButton button1 = new JButton("Minimum Energy Consumption");
        	JButton button2 = new JButton("Minimum Number of Hops");
        	JButton button3 = new JButton("Minimum Distance");
        	textfield10 = new JTextField();
        	textfield11 = new JTextField();
        	button1.setAlignmentX(CENTER_ALIGNMENT);
        	button2.setAlignmentX(CENTER_ALIGNMENT);
        	button3.setAlignmentX(CENTER_ALIGNMENT);
        	button1.addActionListener(actionListener);
        	button2.addActionListener(actionListener);
        	button3.addActionListener(actionListener);
        	pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));//pane.setLayout(Layout);
        	JPanel pane2 = new JPanel();
        	JLabel L1 = new JLabel("   Blue DG  ");
        	JLabel L2 = new JLabel("   Red Sensor ");
        	pane2.add(L1);
        	// pane2.add(comboBox);
        	textfield10.setSize(30,12);
        	pane2.add(textfield10);
        	pane2.add(L2);
        	// pane2.add(comboBox1);
        	textfield11.setSize(30,12);
        	pane2.add(textfield11);
        	pane2.setLayout(new BoxLayout(pane2, BoxLayout.Y_AXIS));
        	pane.add(pane2);
        	pane1.add(new JLabel(" "));
        	pane1.add(button1);
        	pane1.add(new JLabel(" "));
        	pane1.add(button2);
        	pane1.add(new JLabel(" "));
        	pane1.add(button3);
        	pane1.add(new JLabel(" "));
        	pane.add(pane1);
        	pane1.setLayout(new BoxLayout(pane1, BoxLayout.Y_AXIS));
            pane.setBorder(BorderFactory.createTitledBorder("Path"));   
        } 
        return pane;
    }
	
	ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Print Sensor Network")) {
				sensorNetwork.network_width = Integer.parseInt(textfield1.getText()); 
				sensorNetwork.network_height = Integer.parseInt(textfield2.getText());
				sensorNetwork.number_of_DGs = Integer.parseInt(textfield3.getText());
				sensorNetwork.number_of_sensor_nodes = Integer.parseInt(textfield4.getText());
				sensorNetwork.number_of_data_items_each_DG_has = Integer.parseInt(textfield6.getText());
				sensorNetwork.storage_capacity_of_each_non_DG = Integer.parseInt(textfield7.getText());
				sensorNetwork.transmission_range = (Float.valueOf(textfield5.getText())).floatValue();
				sensorNetwork.minimum_initial_energy = Integer.parseInt(textfield8.getText());//(Float.valueOf(textfield8.getText())).floatValue();
				sensorNetwork.maximum_initial_energy = Integer.parseInt(textfield9.getText());//(Float.valueOf(textfield9.getText())).floatValue();
				
				double N_min = 382.43 * Math.pow(2.71828, (-0.2159*transmission_range));
				
				//if((number_of_DGs*number_of_data_items_each_DG_has*400) <= ((N_min-number_of_DGs)*storage_capacity_of_each_non_DG)) {
					int n=number_of_DGs+number_of_sensor_nodes;
					parent = new int[n+1];
					fringeWgt = new float[n+1];
					parent1 = new int[n+1];
					fringeWgt1 = new float[n+1];
					parent2 = new int[n+1];
					fringeWgt2 = new float[n+1];
					
					generate_the_sensor_network();
					adjList = new NodeList[n+1];
					compute_edgeArray_adjList();
					//System.out.println(adjList);
					
					discovered = new boolean[number_of_DGs+number_of_sensor_nodes];
					NodeList.BFS_to_compose_the_discovered_array();
			
					is_connected = BFS_connected();
					
					dijkStartNode=0;
					dijkEndNode=21;
					outputState=1;
				//} else {
				//	outputState = 99;
				//}
			/*} else if (e.getActionCommand().equals("comboBox")) {
					JComboBox cb = (JComboBox)e.getSource();
					String temp = (String)cb.getSelectedItem();
					dijkStartNode = Integer.parseInt(temp);
		        	System.out.println(temp + "=" + dijkStartNode);
			} else if (e.getActionCommand().equals("comboBox1")) {
					JComboBox cb = (JComboBox)e.getSource();
					String temp = (String)cb.getSelectedItem();
					dijkEndNode = number_of_DGs + Integer.parseInt(temp) + 1;*/
			} else if (e.getActionCommand().equals("Minimum Energy Consumption")) {
				if (outputState!=0) {
					outputState = 2;
					dijkStartNode = Integer.parseInt(textfield10.getText());
					dijkEndNode = number_of_DGs + Integer.parseInt(textfield11.getText()) +1;
					int n = number_of_DGs+number_of_sensor_nodes;
					dijkstra.shortestPaths(adjList,n,dijkStartNode+1,parent,fringeWgt);
					dijkstra.shortestEnergyPaths(adjList,n,dijkStartNode+1,parent2,fringeWgt2);
				}
			} else if (e.getActionCommand().equals("Minimum Number of Hops")) {
				if (outputState!=0) {
					outputState = 3;
					dijkStartNode = Integer.parseInt(textfield10.getText());
					dijkEndNode = number_of_DGs + Integer.parseInt(textfield11.getText()) +1;
					int n = number_of_DGs+number_of_sensor_nodes;
					dijkstra.shortestHopPaths(adjList,n,dijkStartNode+1,parent1,fringeWgt1);
				}
			} else if (e.getActionCommand().equals("Minimum Distance")) {
				if (outputState!=0) {
					outputState = 4;
					dijkStartNode = Integer.parseInt(textfield10.getText());
					dijkEndNode = number_of_DGs + Integer.parseInt(textfield11.getText()) +1;
					int n = number_of_DGs+number_of_sensor_nodes;
					dijkstra.shortestPaths(adjList,n,dijkStartNode+1,parent,fringeWgt);
				}
			} else if (e.getActionCommand().equals("PBA -- Compute Max Flow")) {
				sensorNetwork.number_of_data_items_each_DG_has = Integer.parseInt(textfield6.getText());
				sensorNetwork.storage_capacity_of_each_non_DG = Integer.parseInt(textfield7.getText());
				sensorNetwork.minimum_initial_energy = Integer.parseInt(textfield8.getText());//(Float.valueOf(textfield8.getText())).floatValue();
				sensorNetwork.maximum_initial_energy = Integer.parseInt(textfield9.getText());//(Float.valueOf(textfield9.getText())).floatValue();
				//System.out.println("Compute Max Flow\n");
				int size = number_of_DGs+number_of_sensor_nodes;
				FordFulkerson maxflow = new FordFulkerson(G, 0, size+1);
				//for (int a=0; a<5; a++) {
				for (int i=1; i<=number_of_DGs; i++) {
					for (FlowEdge e6: G.adj(0)) {
						e6.capacity=0;
					}
					FlowEdge e7 = new FlowEdge(0,i,0,number_of_data_items_each_DG_has, 0);
					FlowEdge e8 = new FlowEdge(i,0,0,number_of_data_items_each_DG_has, 0);
					G.addEdge(e7);G.addEdge(e8);
				
					System.out.println(G.toString());
					//System.out.println("Max flow from " + 0 + " to " + (size+1));
					maxflow = new FordFulkerson(G, 0, size+1);
					if (i==number_of_DGs){
					for (int v = 0; v < G.V(); v++) {
						for (FlowEdge e1 : G.adj(v)) {
							if ((v == e1.from()) && e1.flow() > 0 && e1.to()!=size+1) {
								// System.out.println("   " + e1 + "\t" + e1.flow + "\t" + (e1.from()-1) +"\t"+ (e1.to()-1-20));
								for (FlowEdge e3 : G.adj(e1.to())) {
									e3.capacity -= e1.flow()*1;
									//System.out.println("Decrement Capacity of edge "+e3+" adjacent to node "+e1.to()+" by "+e1.flow);
								}
							}
						}
					}
					}
				}
				//}
				max_flow=maxflow.value();
				//System.out.println(maxflow.value());
				outputState=5;
			}
			repaint();
		}
	};
	
	public static boolean BFS_connected() {
		boolean flag = true;
		for (int i=0; i<sensorNetwork.number_of_DGs + sensorNetwork.number_of_sensor_nodes; i++) {
			if (!discovered[i])
				flag = false;
		}
		return flag;
	}
	
    public JPanel createYAlignmentExample(boolean doItRight) {
        JPanel pane = new JPanel();
        String title;
 
        JComponent component1 = new JPanel();
        Dimension size = new Dimension(100, 50);
        component1.setMaximumSize(size);
        component1.setPreferredSize(size);
        component1.setMinimumSize(size);
        TitledBorder border = new TitledBorder(
                                  new LineBorder(Color.black),
                                  "A JPanel",
                                  TitledBorder.CENTER,
                                  TitledBorder.BELOW_TOP);
        border.setTitleColor(Color.black);
        component1.setBorder(border);
 
        JComponent component2 = new JPanel();
        size = new Dimension(100, 50);
        component2.setMaximumSize(size);
        component2.setPreferredSize(size);
        component2.setMinimumSize(size);
        border = new TitledBorder(new LineBorder(Color.black),
                                  "A JPanel",
                                  TitledBorder.CENTER,
                                  TitledBorder.BELOW_TOP);
        border.setTitleColor(Color.black);
        component2.setBorder(border);
 
        if (doItRight) {
            title = "Matched";
        } else {
            component1.setAlignmentY(TOP_ALIGNMENT);
            title = "Mismatched";
        }
 
        pane.setBorder(BorderFactory.createTitledBorder(title));
        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
        pane.add(component1);
        pane.add(component2);
        return pane;
    }
	
	private static void createAndShowGUI() {
		// create and set up window
        JFrame frame = new JFrame("Sensor Network");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        // create and set up content pane
        sensorNetwork newContentPane = new sensorNetwork();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        // display window
        frame.pack();
        frame.setVisible(true);
    }
	
	public static void main(String [] args) {
		// schedule a job for the event-dispatching thread to create and show the application GUI
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
		while(true){}
	}
}