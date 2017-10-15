package cs6301.g60;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by shivan on 10/12/17.
 */
public class TarjanMST {

    XGraph xGraph;
    Graph.Vertex start;

    //map used to map new Vertex to a list of vertices in a particular component
    HashMap<Graph.Vertex, List<Graph.Vertex>> map;

    public TarjanMST(XGraph xGraph, Graph.Vertex root){
        this.xGraph = xGraph;
        this.start = root;
        map = new HashMap<>();
    }

    protected void reduceEdgeWeights(){
        XGraph.getRevAdj = true;
        for(Graph.Vertex vertex: xGraph){
            if(vertex!= start) {
                vertex = xGraph.getVertex(vertex);
                Graph.Edge min = null;

                for (Graph.Edge edge : vertex) {
                    if (min == null){
                        min = edge;
                    }
                    if (edge.getWeight()>0 && edge.getWeight()<min.getWeight()){
                        min = edge;
                    }
                }
                if(min!=null) {
                    for (Graph.Edge edge : vertex) {
                        edge.setWeight(edge.getWeight() - min.getWeight());
                    }
                }
            }
        }
        XGraph.getRevAdj = false;
    }

    protected void shrinkGraph(){
        SCC scc = new SCC();
        scc.getAllScc(xGraph, start);
        List<List<Graph.Vertex>> components = scc.list;

        for(List<Graph.Vertex> component : components){
            Graph.Vertex orginalVertex = null;
            if(component.size()>1){
                List<Graph.Edge> newEdgeList = findIncomingOutgoingEdges(component);
                for(Graph.Edge edges : newEdgeList){
                    // determine whether it's an outgoing or incoming edge.
                }

                //disabling node edges should be the last thing
                disableNodes(component);
                orginalVertex = xGraph.addVertex();
            }else{
                //component has only 1 vertex in it
                orginalVertex = component.get(0);
            }
            map.put(orginalVertex, component);
        }

    }

    //TODO: Implement expand graph
    protected void expandGraph(){

    }

    protected void disableNodes(List<Graph.Vertex> vertices){
        for (Graph.Vertex vertex : vertices) {
            XGraph.XVertex xVertex = xGraph.getVertex(vertex);
            xVertex.disabled = true;
            for (Graph.Edge edge : xVertex) {
                ((XGraph.XEdge)edge).disabled = true;
            }
        }
    }

    protected void enableNodes(List<Graph.Vertex> vertices){

        for (Graph.Vertex vertex : vertices) {
            XGraph.XVertex xVertex = xGraph.getVertex(vertex);
            xVertex.disabled = false;
            for (Graph.Edge edge : xVertex) {
                ((XGraph.XEdge)edge).disabled = false;
            }
        }
    }

    //TODO: Implement enableNodes -- check if it should be a list of egdes or xedges
    protected List<Graph.Edge> findIncomingOutgoingEdges(List<Graph.Vertex> vertices){
         return null;
    }
    
    
    public static void main(String[] args) throws FileNotFoundException{
    	Scanner in;
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }
        System.out.println("reading input");
	    int start = 1;
        if(args.length > 1) {
	        start = Integer.parseInt(args[1]);
	    }

        Graph g = Graph.readDirectedGraph(in);
        Graph.Vertex startVertex = g.getVertex(start);
        
        XGraph xg = new XGraph(g);
        TarjanMST tMST = new TarjanMST(xg,xg.getVertex(startVertex));
        System.out.println("Original Graph");
        SCC scc = new SCC();
        scc.getAllScc(tMST.xGraph, xg.getVertex(startVertex));
        int ind = 0;
        for(List<Graph.Vertex> l : scc.list){
        	System.out.println("component " + ind + " are");
        	for(Graph.Vertex v: l){
        		System.out.print(v.toString() + ",");
        	}
        	System.out.println();
        	ind++;
        }
        
        tMST.reduceEdgeWeights();
        tMST.xGraph.makeZeroGraph();
        
        System.out.println("Zero Graph");
        scc = new SCC();
        scc.getAllScc(tMST.xGraph, xg.getVertex(startVertex));
        ind = 0;
        for(List<Graph.Vertex> l : scc.list){
        	System.out.println("component " + ind + " are");
        	for(Graph.Vertex v: l){
        		System.out.print(v.toString() + ",");
        	}
        	System.out.println();
        	ind++;
        }
    }

}
