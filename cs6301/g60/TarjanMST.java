package cs6301.g60;

import java.util.List;

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

}
