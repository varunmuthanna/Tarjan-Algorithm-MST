package cs6301.g60;

/**
 * Created by shivan on 10/12/17.
 */
public class TarjanMST {

    XGraph xg;
    Graph.Vertex root;
    public TarjanMST(Graph g, Graph.Vertex root){
        xg = new XGraph(g);
        this.root = root;
    }

    public void reduceEdgeWeights(){
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


}
