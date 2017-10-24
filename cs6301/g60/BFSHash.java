/** Breadth-first search, code modified to use GraphHash class
 *  @author rbk
 *  Version 1.0: 2017/10/11
 */


package cs6301.g60;
import cs6301.g60.Graph.Vertex;
import cs6301.g60.Graph.Edge;
import cs6301.g60.BFSHash.BFSVertex;

import cs6301.g60.Graph;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class BFSHash extends GraphHash<BFSVertex,Boolean> {
    public static final int INFINITY = Integer.MAX_VALUE;
    // Class to store information about a vertex in this algorithm
    static class BFSVertex {
        boolean seen;
        Vertex parent;
        int distance;  // distance of vertex from source
        BFSVertex(Vertex u) {
            seen = false;
            parent = null;
            distance = INFINITY;
        }

        void reinitialize() {
            seen = false;
            parent = null;
            distance = INFINITY;
        }

        void setDistance(int d) {
            distance = d;
        }

        int getDistance() {
            return distance;
        }

        void set(boolean seen, Vertex parent, int distance) {
            this.seen = seen;
            this.parent = parent;
            this.distance = distance;
        }
    }

    Vertex src;

    public BFSHash(Graph g) {
        super(g);
        for(Graph.Vertex u: g) {
            putVertex(u, new BFSVertex(u));
        }
    }

    // reinitialize allows running BFS many times, with different sources
    void reinitialize() {
        for(Vertex u: g) {
            BFSVertex bu = getVertex(u);
            bu.reinitialize();
        }
    }

    void bfs(Vertex root) {
        src = root;
        Queue<Vertex> q = new LinkedList<>();
        q.add(src);
        // Set source to be at distance 0
        getVertex(src).set(true, null, 0);
        while(!q.isEmpty()) {
            Vertex u = q.remove();
            for(Edge e: u) {
                Vertex v = e.otherEnd(u);
                if(!seen(v)) {
                    visit(u,v);
                    q.add(v);
                }
            }
        }
    }

    boolean seen(Vertex u) {
        return getVertex(u).seen;
    }

    Graph.Vertex getParent(Vertex u) {
        return getVertex(u).parent;
    }

    int getDistance(Vertex u) {
        return getVertex(u).getDistance();
    }

    void setDistance(Vertex u, int d) {
        getVertex(u).setDistance(d);
    }

    // Visit a node v from u
    void visit(Vertex u, Vertex v) {
        BFSVertex bv = getVertex(v);
        bv.set(true, u, getDistance(u) + 1);
    }

    void runAndPrint(Vertex src) {
        bfs(src);
        /*for(Vertex u: g) {
            System.out.println(u + " : " + getDistance(u));
        }*/
    }

    boolean reachable(){
        for(Vertex u: g) {
            if(getDistance(u)==INFINITY){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Graph g = Graph.readDirectedGraph(new Scanner(System.in));

        System.out.println("BFS with vertex 1 as start:");
        BFSHash bh = new BFSHash(g);
        bh.runAndPrint(g.getVertex(1));

        bh.reinitialize();
        System.out.println("\nBFS with vertex 3 as start:");
        bh.runAndPrint(g.getVertex(3));
    }
}