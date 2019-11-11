// NodeList.java

import java.util.*;

class EdgeNode {
	public int to;
	public float distance;
	public float energyCost;
	public int capacity;
	
	public EdgeNode (int relativeNode, float dist, int capacitee) {
		to = relativeNode;
		distance = dist;
		energyCost = ((0.00032f)+(0.00000032f)*(dist*dist));
		capacity = capacitee;
	}
}

public class NodeList {
	public EdgeNode element;
	public NodeList next;
	
	public static final NodeList nil = null;
	
	public static EdgeNode first(NodeList L) { 
		return L.element;
	}
	
	public static NodeList rest(NodeList L) { 
		return L.next; 
	}
	
	public static NodeList cons(EdgeNode newElement, NodeList oldList) {
		NodeList newL = new NodeList();
		
		newL.element = newElement;
		newL.next = oldList;
		return newL;
	}
	
	public static void BFS_to_compose_the_discovered_array() {
		NodeList[] adjListCopy = sensorNetwork.adjList.clone();
		
    	for (int i=0; i<sensorNetwork.number_of_DGs+sensorNetwork.number_of_sensor_nodes; i++) {
    		sensorNetwork.discovered[i] = false;
    	}
		
    	int i=0;
    	sensorNetwork.discovered[0] = true;
    	LinkedList<Integer> L = (LinkedList<Integer>) new LinkedList();
    	L.addLast(0);
 
    	System.out.println(L);
    	while (!L.isEmpty()) {
    		NodeList list = adjListCopy[L.removeFirst()+1];
    		while(list!=null) {
    			EdgeNode node = first(list);
    			list = rest(list);
    			if (!sensorNetwork.discovered[node.to-1]) {
    				sensorNetwork.discovered[node.to-1]=true;
    				L.addLast(node.to-1);	
    			}
    		}
    	}
    }
}