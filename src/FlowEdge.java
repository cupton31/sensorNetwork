public class FlowEdge {
    private final int v;             // from
    private final int w;             // to 
    public double energy_cost;       // energy cost of 1 data item
    public double distance;				// distance
    public double capacity;   		// capacity
    public double flow;       		// flow
 
    public FlowEdge(int v, int w, double energy_cost, double capacity, double distance) {
        if (capacity < 0) throw new RuntimeException("Negative edge capacity");
        this.v         = v;
        this.w         = w;
        this.energy_cost = energy_cost;
        this.capacity  = capacity;
        this.flow      = 0;
        this.distance  = distance;
    }
    
    // accessor methods
    public int from()         { return v;        }  
    public int to()           { return w;        }  
    public double capacity()  { return capacity; }
    public double flow()      { return flow;     }
    public double energy_cost() { return energy_cost;}
    public double distance() { return distance;}
    
    public int other(int vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new RuntimeException("Illegal endpoint");
    }
    
    public double residualCapacityTo(int vertex) {
        if      (vertex == v) return flow;
        else if (vertex == w) return capacity - flow;
        else throw new RuntimeException("Illegal endpoint");
    }
    
    public void addResidualFlowTo(int vertex, double delta) {
        if      (vertex == v) flow -= delta;
        else if (vertex == w) flow += delta;
        else throw new RuntimeException("Illegal endpoint");
    }
}