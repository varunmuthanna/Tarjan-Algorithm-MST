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
            if(!vertex.equals(start)) {
                vertex = xGraph.getVertex(vertex);
                Graph.Edge min = null;

                for (Graph.Edge edge : vertex) {
                    if (min == null){
                        min = edge;
                    } else if(edge.getWeight()>0 && edge.getWeight()<min.getWeight()){
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

        System.out.println("____Reduce Edge weights____");
        for(Graph.Vertex vertex: xGraph){
            for (Graph.Edge edge : vertex) {
                System.out.println(edge +" "+edge.weight);
            }
        }
        System.out.println("_____________");

        XGraph.getRevAdj = false;

    }

    protected Graph.Vertex shrinkGraph(){
        SCC scc = new SCC();
        XGraph.zeroGraph = true;
        scc.getAllScc(xGraph, start);
        List<List<Graph.Vertex>> components = scc.list;
        XGraph.zeroGraph = false;

        for(List<Graph.Vertex> component : components){
            System.out.println(component);
            Graph.Vertex newVertex = null;
            if(component.size()>1){
                newVertex = addIncomingOutgoingEdges(component);
                xGraph.addVertex(newVertex);

                //disabling node edges should be the last thing
                disableNodes(component);
                
            }else{
                //component has only 1 vertex in it
            	newVertex = component.get(0);
            }
            map.put(newVertex, component);
        }

        return start;
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
    protected Graph.Vertex addIncomingOutgoingEdges(List<Graph.Vertex> vertices){
    	Graph.Vertex newVertex = xGraph.addVertex();
    	HashMap<Graph.Vertex, Boolean> hash = new HashMap<>();
    	for(Graph.Vertex u : vertices){
    		hash.put(u, true);
    	}
    	
    	for(Graph.Vertex u : vertices){
    	    if(u==start){
    	        start = newVertex;
            }
    		for(Graph.Edge e : u){
    			Graph.Vertex v = e.otherEnd(u);
    			if(hash.containsKey(v)){
    				continue;
    			}
    			xGraph.addEdge(newVertex, v, e.weight, xGraph.edgeSize());
    		}
    		XGraph.getRevAdj = true;
    		for(Graph.Edge e : u){
    			Graph.Vertex v = e.otherEnd(u);
    			if(hash.containsKey(v)){
    				continue;
    			}
    			xGraph.addEdge(v, newVertex, e.weight, xGraph.edgeSize());
    		}
    		XGraph.getRevAdj = false;
    	}
    	return newVertex;
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
        System.out.println(tMST.xGraph);
        
        tMST.reduceEdgeWeights();
        //tMST.xGraph.makeZeroGraph();
        
//        System.out.println("Zero Graph");
//        scc = new SCC();
//        scc.getAllScc(tMST.xGraph, xg.getVertex(startVertex));
//        ind = 0;
//        for(List<Graph.Vertex> l : scc.list){
//        	System.out.println("component " + ind + " are");
//        	for(Graph.Vertex v: l){
//        		System.out.print(v.toString() + ",");
//        	}
//        	System.out.println();
//        	ind++;
//        }
        tMST.shrinkGraph();
        System.out.println("new graph");
        System.out.println(tMST.xGraph);
    }

}
