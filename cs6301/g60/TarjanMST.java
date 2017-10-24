package cs6301.g60;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by shivan on 10/12/17.
 */
public class TarjanMST {

    XGraph xGraph;
    Graph.Vertex start;

    //vertexToComponentList used to vertexToComponentList new Vertex to a list of vertices in a particular component
    HashMap<Graph.Vertex, List<Graph.Vertex>> vertexToComponentList;
    HashMap<XGraph.XVertex, XGraph.XVertex> vertexToComponentRep;
    HashMap<Graph.Edge, Graph.Edge> newEdgeToOldEdge;

    public TarjanMST(XGraph xGraph, Graph.Vertex root){
        this.xGraph = xGraph;
        this.start = root;
        vertexToComponentList = new HashMap<>();
        vertexToComponentRep = new HashMap<>();
        newEdgeToOldEdge = new HashMap<>();
    }

    protected void reduceEdgeWeights(){
        XGraph.getRevAdj = true;
        for(Graph.Vertex vertex: xGraph){
            if(!vertex.equals(start)) {
                vertex = xGraph.getVertex(vertex);
                int min = -1;

                for (Graph.Edge edge : vertex) {
                    if (min == -1){
                        min = edge.weight;
                    } else if(edge.getWeight()>=0 && edge.getWeight() < min){
                        min = edge.weight;
                    }
                }
                if(min!=-1) {
                    for (Graph.Edge edge : vertex) {
                        edge.setWeight(edge.getWeight() - min);
                    }
                }
            }
        }
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
            XGraph.XVertex newVertex = null;
            if(component.size()>1){
                newVertex = new XGraph.XVertex(xGraph.addVertex());
                xGraph.addVertex(newVertex);

                for(Graph.Vertex vertex : component) {
                    vertexToComponentRep.put((XGraph.XVertex) vertex, newVertex);
                }
            }
            vertexToComponentList.put(newVertex, component);

        }

        for(List<Graph.Vertex> component : components){
            addIncomingOutgoingEdges(component);
        }
        for(List<Graph.Vertex> component : components){
            if(component.size()>1) {
                disableNodes(component);
            }
        }

        return start;
    }

    protected void addIncomingOutgoingEdges(List<Graph.Vertex> vertices){
        //TODO: check if this map can be taken out
        // new component vertex to adj list mapping
        Map<Graph.Vertex, Graph.Edge> minEdgeForComponent = new HashMap<>();

        for(Graph.Vertex v : vertices) {
            XGraph.getRevAdj = true;
            for(Graph.Edge e : v){
                Graph.Vertex u = e.otherEnd(v);
                Graph.Vertex to = vertexToComponentRep.containsKey(v)?vertexToComponentRep.get(v):v;
                Graph.Vertex from = vertexToComponentRep.containsKey(u)?vertexToComponentRep.get(u):u;
                if((!vertexToComponentRep.containsKey(v) && !vertexToComponentRep.containsKey(u)) || from.equals(to)){
                    continue;
                }

                if(minEdgeForComponent.containsKey(from)) {
                    if(minEdgeForComponent.get(from).getWeight() > e.getWeight()) {
                        minEdgeForComponent.replace(from, e);
                    }
                }else{
                    minEdgeForComponent.put(from, e);
                }
            }
            XGraph.getRevAdj = false;
        }
        //minEdgeForComponent contains edges that have a component with size >1 involved as one of the edge endpoints.
        for(Map.Entry<Graph.Vertex, Graph.Edge> it :minEdgeForComponent.entrySet()) {
            Graph.Edge e = it.getValue();
            // this check avoids adding redundant edges
            Graph.Vertex from = vertexToComponentRep.containsKey(e.from)?vertexToComponentRep.get(e.from):e.from;
            Graph.Vertex to = vertexToComponentRep.containsKey(e.to)?vertexToComponentRep.get(e.to):e.to;
            Graph.Edge newEdge = xGraph.addEdge(xGraph.getVertex(from), xGraph.getVertex(to), e.weight, xGraph.edgeSize());
            newEdgeToOldEdge.put(newEdge, e);
        }
    }

    //TODO: Implement expand graph
    protected List<Graph.Edge> expandGraph(List<Graph.Edge> list){
        for(Graph.Edge edge : list) {
            Graph.Vertex from = edge.from;
            Graph.Vertex to = edge.to;
            if (vertexToComponentList.containsKey(to) && vertexToComponentList.containsKey(from)) {
                //expand graph
                /**
                 * remove edge from the list after expanding it and add the resultant edges
                 */
            } else if (vertexToComponentList.containsKey(to)) {

            } else if (vertexToComponentList.containsKey(from)) {

            }
        }
        return list;
    }

    protected void disableNodes(List<Graph.Vertex> vertices){
        for (Graph.Vertex vertex : vertices) {
            XGraph.XVertex xVertex = xGraph.getVertex(vertex);

            for (Graph.Edge edge : xVertex) {
                ((XGraph.XEdge)edge).disabled = true;
            }
            XGraph.getRevAdj = true;
            for (Graph.Edge edge : xVertex) {
                ((XGraph.XEdge)edge).disabled = true;
            }
            XGraph.getRevAdj = false;
            xVertex.disabled = true;
        }
    }

    protected void enableNodes(List<Graph.Vertex> vertices){
        XGraph.getRevAdj = true;
        for (Graph.Vertex vertex : vertices) {
            XGraph.XVertex xVertex = xGraph.getVertex(vertex);
            xVertex.disabled = false;
            for (Graph.Edge edge : xVertex) {
                ((XGraph.XEdge)edge).disabled = false;
            }
        }
        XGraph.getRevAdj = false;
    }

    /*protected Graph.Vertex addIncomingOutgoingEdges(List<Graph.Vertex> vertices){
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
    }*/

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
        int ind = 1;
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
