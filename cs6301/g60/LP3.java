package cs6301.g60;

// Starter code for LP3
// Do not rename this file or move it away from cs6301/g??

// change following line to your group number
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

import cs6301.g60.Graph.Vertex;
import cs6301.g60.Graph.Edge;

public class LP3 {
    static int VERBOSE = 0;
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in;
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }
        if(args.length > 1) {
            VERBOSE = Integer.parseInt(args[1]);
        }

        int start = in.nextInt();  // start node of the MST
        Graph g = Graph.readDirectedGraph(in);
        Vertex startVertex = g.getVertex(start);

        List<Edge> dmst = new ArrayList<>();

        Timer timer = new Timer();
        int wmst = directedMST(g, startVertex, dmst);
        timer.end();

        System.out.println(wmst);
        if(VERBOSE > 0) {
            System.out.println("_________________________");
            for(Edge e: dmst) {
                System.out.print(e);
            }
            System.out.println();
            System.out.println("_________________________");
        }
        System.out.println(timer);
    }

    /**
     * TO DO: List dmst is an empty list. When your algorithm finishes,
     *  it should have the edges of the directed MST of g rooted at the
     *  start vertex.  Edges must be ordered based on the vertex into
     *  which it goes, e.g., {(7,1),(7,2),null,(2,4),(3,5),(5,6),(3,7)}.
     *  In this example, 3 is the start vertex and has no incoming edges.
     *  So, the list has a null corresponding to Vertex 3.
     *  The function should return the total weight of the MST it found.
     */
    static TarjanMST tarjanMST;
    static XGraph xgraph;
    static List<Edge> dmstHelper;

    public static int directedMST(Graph g, Vertex start, List<Edge> dmst) {
    	BFSHash bfs = new BFSHash(g);
        bfs.runAndPrint(start);

        if (!bfs.reachable()) {
      	    System.out.println("All nodes are not reachable from the source");
      	    return 0;
        }
        xgraph = new XGraph(g);
        tarjanMST = new TarjanMST(xgraph, xgraph.getVertex(start));
        dmstHelper = new ArrayList<>();
        directedMSTHelper(start);

        int weight = 0;

        Map<Vertex, List<Edge>> map = new HashMap<>();

        for(Edge edge : dmstHelper) {
            if(map.containsKey(edge.to)) {
                List<Edge> list = map.get(edge.to);
                list.add(edge);
            }else{
                List<Edge> list = new ArrayList<>();
                list.add(edge);
                map.put(edge.to, list);
            }
            weight += ((XGraph.XEdge)edge).original_weight;
        }

        for(Vertex vertex : xgraph.vertex) {
            if(map.containsKey(vertex)) {
                dmst.addAll(map.get(vertex));
            }else{
                dmst.add(null);
            }
        }

        System.out.println("Final weight: "+weight);
        return dmstHelper.size();
    }

    private static void directedMSTHelper(Vertex start){
        while(true) {
            tarjanMST.reduceEdgeWeights();

            XGraph.zeroGraph = true;
            BFSHash bfs = new BFSHash(xgraph);
            bfs.runAndPrint(xgraph.getVertex(start));
            XGraph.zeroGraph = false;

            if (bfs.reachable()) {
                XGraph.zeroGraph = true;
                DFS d = new DFS(xgraph);
                d.dfs(xgraph.getVertex(start));
                dmstHelper = d.dfsEdgeList;
                XGraph.zeroGraph = false;
                break;
            } else {
                tarjanMST.shrinkGraph();
            }
        }
        
        while (dmstHelper.size()!=xgraph.vertex.length-1) {
            tarjanMST.expandGraph(xgraph, dmstHelper);
        }
    }
}

///Users/shivan/Downloads/lp2-test/lp2-t4.txt