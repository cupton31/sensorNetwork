public class NetworkFlow {
	public static int V;
    public static int E;
	public static Bag<FlowEdge>[] adj;
	
	// empty graph with V vertices
    public NetworkFlow(int V) {
        this.V = V;
        this.E = 0;
        adj = (Bag<FlowEdge>[]) new Bag[V];
        for (int v = 0; v < V; v++)
            adj[v] = new Bag<FlowEdge>();
    }
    
    public static void addEdge(FlowEdge e) {
        E++;
        int v = e.from();
        int w = e.to();
        adj[v].add(e);
        adj[w].add(e);
    }
    
    // number of vertices and edges
    public int V() { return V; }
    public int E() { return E; }
    
    // return list of edges incident to  v
    public Iterable<FlowEdge> adj(int v) {
        return adj[v];
    }
    
    
    
    
    
    // string representation of Graph (excludes self loops) - takes quadratic time
    public String toString() {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ":  ");
            for (FlowEdge e : adj[v]) {
                if (e.to() != v) s.append(e.to() + "-" + e.capacity + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }
}