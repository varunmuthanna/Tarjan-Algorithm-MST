package cs6301.g60;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by shivan on 10/12/17.
 */
public class TarjanMST {

    XGraph xGraph;
    Graph.Vertex root;

    public TarjanMST(XGraph xGraph, Graph.Vertex root){
        this.xGraph = xGraph;
        this.root = root;
    }

    protected void reduceEdgeWeights(){

        for(Graph.Vertex vertex: xGraph){
            if(vertex!=root) {
                vertex = xGraph.getVertex(vertex);
                Graph.Edge min = vertex.revAdj.get(0);
                for (Graph.Edge edge : vertex.revAdj) {
                    if (edge.getWeight()<min.getWeight()){
                        min = edge;
                    }
                }
                 for (Graph.Edge edge : vertex.revAdj) {
                    edge.setWeight(edge.getWeight() - min.getWeight());
                }
            }
        }
    }


    //TODO: Implement shrink graph
    protected void shrinkGraph(){

    }

    //TODO: Implement expand graph
    protected void expandGraph(){

    }

    protected void disableNodesEdges(List<Graph.Vertex> vertices){
        for (Graph.Vertex vertex : vertices) {
            XGraph.XVertex xVertex = xGraph.getVertex(vertex);
            xVertex.disabled = true;
            for (Graph.Edge edge : xVertex) {
                ((XGraph.XEdge)edge).disabled = true;
            }
        }
    }

    protected void enableNodesEdges(List<Graph.Vertex> vertices){

        for (Graph.Vertex vertex : vertices) {
            XGraph.XVertex xVertex = xGraph.getVertex(vertex);
            xVertex.disabled = false;
            for (Graph.Edge edge : xVertex) {
                ((XGraph.XEdge)edge).disabled = false;
            }
        }
    }

    //TODO: Implement enableNodes -- check if it should be a list of egdes or xedges
    protected List<Graph.Edge> findIncomingOutgoingEdges(){
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
        
        TarjanMST tMST = new TarjanMST(g,startVertex);
        System.out.println("Original Graph");
        SCC scc = new SCC();
        scc.getAllScc(tMST.xg, startVertex);
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
        tMST.xg.makeZeroGraph();
        
        System.out.println("Zero Graph");
        scc = new SCC();
        scc.getAllScc(tMST.xg, startVertex);
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
