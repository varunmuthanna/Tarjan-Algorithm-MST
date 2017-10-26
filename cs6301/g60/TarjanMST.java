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
    HashMap<Graph.Vertex, Graph.Vertex> vertexToComponentRep;
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
            //System.out.println(component);
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

    protected void expandGraph(){
        Graph.Vertex[] arr = xGraph.getVertexArray();


        //TODO: write an iterator for this
        for(int i=xGraph.n-1;i>0;i--) {
            if (vertexToComponentList.containsKey(arr[i])) {
                Graph.Vertex nodeToDisable = arr[i];
                List<Graph.Vertex> nodesToEnable = vertexToComponentList.get(arr[i]);

                for (Graph.Edge edge : nodeToDisable) {
                    if (newEdgeToOldEdge.containsKey(edge)) {
                        newEdgeToOldEdge.get(edge).setWeight(edge.getWeight());
                    }
                }
                XGraph.getRevAdj = true;
                for (Graph.Edge edge : nodeToDisable) {
                    if (newEdgeToOldEdge.containsKey(edge)) {
                        newEdgeToOldEdge.get(edge).setWeight(edge.getWeight());
                    }
                }
                XGraph.getRevAdj = false;

                disableNode(nodeToDisable);
                enableNodes(nodesToEnable);
            }else {
                break;
            }
        }
    }

    protected void disableNode(Graph.Vertex vertex){
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

    protected void disableNodes(List<Graph.Vertex> vertices){
        for (Graph.Vertex vertex : vertices) {
            disableNode(vertex);
        }
    }

    protected void enableNodes(List<Graph.Vertex> vertices){
        for (Graph.Vertex vertex : vertices) {
            XGraph.getAllEdges = true;
            XGraph.XVertex xVertex = xGraph.getVertex(vertex);

            for (Graph.Edge edge : xVertex) {
                ((XGraph.XEdge)edge).disabled = false;
            }
            XGraph.getRevAdj = true;
            for (Graph.Edge edge : xVertex) {
                ((XGraph.XEdge)edge).disabled = false;
            }
            XGraph.getRevAdj = false;
            xVertex.disabled = false;
            XGraph.getAllEdges = false;
        }
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
}
