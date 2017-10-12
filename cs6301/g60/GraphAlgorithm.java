
package cs6301.g60;
/**
 * @author Prasanth Kesava Pillai(pxk163630), Shivan Pandya(srp150330) & Varun Muthanna(vkm150030)
 */
public class GraphAlgorithm<T> {
    Graph g;
    // Algorithm uses a parallel array for storing information about vertices
    T[] node;

    public GraphAlgorithm(Graph g) {
	this.g = g;
    }

    T getVertex(Graph.Vertex u) {
	return Graph.Vertex.getVertex(node, u);
    }
}

