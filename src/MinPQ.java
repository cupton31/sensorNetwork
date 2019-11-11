// MinPQ.java

public class MinPQ {
	final static int unseen = 4563;
	final static int fringe = 4356;
	final static int tree = 4556;
	
	// instance fields
	int numVerticies, numPQ;
	int minVertex;
	float oo;
	int[] status;
	int[] parent;
	float[] fringeWgt;
	
	// construct pq on n verticies as unseen
	public static MinPQ create(int n, int[] status, int[] parent, float[] fringeWgt) {
		MinPQ pq = new MinPQ();
		pq.parent = parent;
		pq.fringeWgt = fringeWgt;
		pq.status = status;
		for (int i=1; i<=n; i++) {
			status[i] = unseen;
		}
		pq.numVerticies = n;
		pq.numPQ = 0;
		pq.minVertex=-1;
		pq.oo = Float.POSITIVE_INFINITY;
		return pq;
	}
	
	// record new parent and new weight with priority v and make status[v] = fringe
	public static void insert(MinPQ pq, int v, int newPar, float newW) {
		pq.parent[v] = newPar;
		pq.fringeWgt[v] = newW;
		pq.status[v] = fringe;
		pq.minVertex = -1;
		pq.numPQ++;
		return;
	}
	
	// record new parent and new weight with priority v
	public static void decreaseKey(MinPQ pq, int v, int newPar, float newW) {
		pq.parent[v] = newPar;
		pq.fringeWgt[v] = newW;
		pq.minVertex = -1;
		return;
	}
	
	// delete fringe vertex with min weight from pq
	public static void deleteMin(MinPQ pq) {
		int oldMin = getMin(pq);
		
		pq.status[oldMin] = tree;
		pq.minVertex = -1;
		pq.numPQ--;
		return;
	}
	
	public static boolean isEmpty(MinPQ pq) {
		return (pq.numPQ == 0);
	}
	
	public static float getPriority(MinPQ pq, int v) {
		return pq.fringeWgt[v];
	}
	
	// return fringe vertex with min weight or -1 if no vertex remains
	public static int getMin(MinPQ pq) {
		if (pq.minVertex == -1) {
			findMin(pq);
		}
		return pq.minVertex;
	}
	
	public static void findMin(MinPQ pq) {
		int v;
		float minWgt;
		
		minWgt = pq.oo;
		for (v=1; v<=pq.numVerticies; v++) {
			if (pq.status[v] == fringe) {
				if (pq.fringeWgt[v] < minWgt) {
					pq.minVertex = v;
					minWgt = pq.fringeWgt[v];
				}
			}
			//continue loop
		}
		return;
	}
}