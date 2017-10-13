package cs6301.g60;

import java.util.List;

/**
 * Created by shivan on 10/12/17.
 */
public class TarjanMST {

    XGraph xg;
    Graph g;
    Graph.Vertex root;

    public TarjanMST(Graph g, Graph.Vertex root){
        this.g = g;
        xg = new XGraph(g);
        this.root = root;
    }

    protected void reduceEdgeWeights(){
        for(Graph.Vertex vertex: xg){
            if(vertex!=root && vertex.revAdj.size()>0) {

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

    //TODO: Implement disableNodes
    protected void enableDisableNodesEdges(List<XGraph.XVertex> vertices){
        for (XGraph.XVertex vertex : vertices) {
            for (Graph.Edge edges : vertex) {
                // TODO
                ((XGraph.XEdge)edges).disabled = !((XGraph.XEdge)edges).disabled;
            }
        }
    }

    //TODO: Implement enableNodes -- check if it should be a list of egdes or xedges
    protected List<Graph.Edge> findIncomingOutgoingEdges(){
        return null;
    }

}
