// dijkstra.java

public class dijkstra extends sensorNetwork {
	final static int unseen = 4563;
	final static int fringe = 4356;
	final static int tree = 4556;
	
	public static final NodeList nil = null;
	
	public static void shortestEnergyPaths(NodeList[] adjInfo, int n, int s, int[] parent, float[] fringeWgt) {
		int[] status = new int[n+1];
		MinPQ pq = MinPQ.create(n, status, parent, fringeWgt);
		
		MinPQ.insert(pq,s,-1,0.0f);
		while (MinPQ.isEmpty(pq)==false) {
			int v = MinPQ.getMin(pq);
			MinPQ.deleteMin(pq);
			updateFringe2(pq, adjInfo[v], v);
		}
		return;
	}
	
	// shortest energy paths
	public static void updateFringe2 (MinPQ pq, NodeList adjInfoOfV, int v) {
		float myDist = pq.fringeWgt[v];
		NodeList remAdj;
		remAdj = adjInfoOfV;
		while (remAdj != nil) {
			EdgeNode wInfo = NodeList.first(remAdj);
			int w = wInfo.to;
			float newDist = myDist + wInfo.energyCost;
			if (pq.status[w] == unseen) {
				MinPQ.insert(pq,w,v,newDist);
			} else if (pq.status[w] == fringe) {
				if (newDist < MinPQ.getPriority(pq,w)) {
					MinPQ.decreaseKey(pq, w,v,newDist);
				}
			}
			remAdj = NodeList.rest(remAdj);
		}
		return;
	}
	
	public static void shortestPaths(NodeList[] adjInfo, int n, int s, int[] parent, float[] fringeWgt) {
		int[] status = new int[n+1];
		MinPQ pq = MinPQ.create(n, status, parent, fringeWgt);
		
		MinPQ.insert(pq,s,-1,0.0f);
		while (MinPQ.isEmpty(pq)==false) {
			int v = MinPQ.getMin(pq);
			MinPQ.deleteMin(pq);
			updateFringe(pq, adjInfo[v], v);
		}
		return;
	}
	
	// shortest paths
	public static void updateFringe (MinPQ pq, NodeList adjInfoOfV, int v) {
		float myDist = pq.fringeWgt[v];
		NodeList remAdj;
		remAdj = adjInfoOfV;
		while (remAdj != nil) {
			EdgeNode wInfo = NodeList.first(remAdj);
			int w = wInfo.to;
			float newDist = myDist + wInfo.distance;
			if (pq.status[w] == unseen) {
				MinPQ.insert(pq,w,v,newDist);
			} else if (pq.status[w] == fringe) {
				if (newDist < MinPQ.getPriority(pq,w)) {
					MinPQ.decreaseKey(pq, w,v,newDist);
				}
			}
			remAdj = NodeList.rest(remAdj);
		}
		return;
	}
	
	public static void shortestHopPaths(NodeList[] adjInfo, int n, int s, int[] parent, float[] fringeWgt) {
		int[] status = new int[n+1];
		MinPQ pq1 = MinPQ.create(n, status, parent, fringeWgt);
		
		MinPQ.insert(pq1,s,-1,0.0f);
		while (MinPQ.isEmpty(pq1)==false) {
			int v = MinPQ.getMin(pq1);
			MinPQ.deleteMin(pq1);
			updateFringe1(pq1, adjInfo[v], v);
		}
		return;
	}
	
	// shortest hop paths
	public static void updateFringe1 (MinPQ pq, NodeList adjInfoOfV, int v) {
		float myDist = pq.fringeWgt[v];
		NodeList remAdj;
		remAdj = adjInfoOfV;
		while (remAdj != nil) {
			EdgeNode wInfo = NodeList.first(remAdj);
			int w = wInfo.to;
			float newDist = myDist + 1.0f;
			if (pq.status[w] == unseen) {
				MinPQ.insert(pq,w,v,newDist);
			} else if (pq.status[w] == fringe) {
				if (newDist < MinPQ.getPriority(pq,w)) {
					MinPQ.decreaseKey(pq, w,v,newDist);
				}
			}
			remAdj = NodeList.rest(remAdj);
		}
		return;
	}
	
	
	
	   // Should find the shortest paths, such that each edge of that path has a capacity>0
		public static void shortestHopPaths1(NodeList[] adjInfo, int n, int s, int[] parent, float[] fringeWgt) {
			int[] status = new int[n+1];
			MinPQ pq1 = MinPQ.create(n, status, parent, fringeWgt);
			
			MinPQ.insert(pq1,s,-1,0.0f);
			while (MinPQ.isEmpty(pq1)==false) {
				int v = MinPQ.getMin(pq1);
				MinPQ.deleteMin(pq1);
				updateFringe11(pq1, adjInfo[v], v);
			}
			return;
		}
		
		// shortest hop paths
		public static void updateFringe11 (MinPQ pq, NodeList adjInfoOfV, int v) {
			float myDist = pq.fringeWgt[v];
			NodeList remAdj;
			remAdj = adjInfoOfV;
			while (remAdj != nil) {
				EdgeNode wInfo = NodeList.first(remAdj);
				if (wInfo.capacity>0) {
					int w = wInfo.to;
					float newDist = myDist + 1.0f;
					if (pq.status[w] == unseen) {
						MinPQ.insert(pq,w,v,newDist);
					} else if (pq.status[w] == fringe) {
						if (newDist < MinPQ.getPriority(pq,w)) {
							MinPQ.decreaseKey(pq, w,v,newDist);
						}
					}
				}
				remAdj = NodeList.rest(remAdj);
			}
			return;
		}
	
}