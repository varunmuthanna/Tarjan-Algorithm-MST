/**
 * Implementation of algorithm to find strongly connected components of a directed graph
 * DFS on given directed graph and DFS on transpose of the given directed graph
 * @author Prasanth Kesava Pillai(pxk163630), Shivan Pandya(srp150330) & Varun Muthanna(vkm150030)
 */

package cs6301.g60;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DFS extends GraphAlgorithm<DFS.DFSVertex> {
	
	int time = 0;
	List<Graph.Vertex> decFinList;
	int cno = 0;
	
	//Class to store information about DFS on vertex
	static class DFSVertex {
		boolean seen;
		Graph.Vertex parent;
		int discover;	//discovery time
		int finish; //finish time
		int cno;
		DFSVertex(Graph.Vertex u) {
			seen = false;
			parent = null;
			discover = 0;
			finish = 0;
			cno = -1;
		}
	}
	
	//Initializing array of type DFSVertex
	DFS(Graph g) {
		super(g);
		node = new DFSVertex[g.size()];
		// Create array for storing vertex properties
		for(Graph.Vertex u: g) {
			node[u.getName()] = new DFSVertex(u);
		}
	}
	
	//Reinitialize the graph so that it could be used for dfs again
	void reinitialize(){
		cno = 0;
		for(Graph.Vertex u: g){
			DFSVertex du = getVertex(u);
			du.seen = false;
			du.parent = null;
			du.discover = 0;
			du.finish = 0;
			du.cno = -1;
		}
	}
	
	/**
	 * DFS on given directed graph
	 */	
	public void dfs(Iterator<Graph.Vertex> it){
		decFinList = new LinkedList<>();
		while(it.hasNext()){
			Graph.Vertex u = it.next();
			if(!seen(u)){
				cno++;
				DFSVisit(u);
			}
		}	
	}
	
	public void dfs(Graph.Vertex u){
		decFinList = new LinkedList<>();
		cno++;
		DFSVisit(u);
		Iterator<Graph.Vertex> it = g.iterator();
		while(it.hasNext()){
			Graph.Vertex v = it.next();
			if(!seen(v)){
				cno++;
				DFSVisit(v);
			}
		}
	}
	
	/*
	 * This will visit all the nodes in the single component and 
	 * names its component with corresponding component number, also
	 * it marks the nodes visited as seen
	 */
	void DFSVisit(Graph.Vertex u){
		time = time + 1;
		DFSVertex du = getVertex(u);
		du.discover = time;
		du.seen = true;
		du.cno = cno;
		for(Graph.Edge e: u) {
			Graph.Vertex v = e.otherEnd(u);
			if(!seen(v)){
				getVertex(v).parent = u;
				DFSVisit(v);
			}
		}
		time = time + 1;
		visitAtFinish(u, time);
		decFinList.add(u);
	}
	
	/*
	 * Check if the all the nodes belong to same component,
	 * else it returns false
	 */
	public boolean isGraphConnected(){
		int cnoTemp = -1;
		for(Graph.Vertex u: g) {
			DFSVertex du= getVertex(u);
			if(cnoTemp == -1){
				cnoTemp = du.cno;
			}else if(du.cno != cnoTemp){
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Get all the nodes in the decreasing finish time
	 */
	public List<Graph.Vertex> getDecFinishList(){
		return this.decFinList;
	}
	
	/*
	 * Transpose of graph is done by making all the adjacency list
	 * to the reverse adjacency list and vice versa 
	 */
	public void transposeGraph(){
		for(Graph.Vertex u: g) {
			List<Graph.Edge> temp = u.adj;
			u.adj = u.revAdj;
			u.revAdj = temp;
		}
	}
	
	//Changing vertex attribute values after DFS on a vertex
	public void visitAtFinish(Graph.Vertex u, int time) {
		DFSVertex dv = getVertex(u);
		dv.finish = time;
	}
	
	//Check if the node is visited or not
	public boolean seen(Graph.Vertex u) {
		return getVertex(u).seen;
	}
	
	public DFS.DFSVertex getDFSVertex(Graph.Vertex u){
		return getVertex(u);
	}
	
}
