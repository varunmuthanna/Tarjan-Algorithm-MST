package cs6301.g60;
import java.util.*;

public class SCC {
	
	List<List<Graph.Vertex>> list;
	int totalScc = -1;
	
	SCC(){
		list = new ArrayList<>();
	}
	
	public void getAllScc(Graph g, Graph.Vertex startVertex){
		DFS d = new DFS(g);
		//Run DFS and check if it is connected
		d.dfs(g.iterator());
		d.reinitialize();
		//Reverse the graph by transposing and run DFS, If it is
		// still connected then the graph is strongly connected
		d.transposeGraph();
		d.dfs(d.getDecFinishList().iterator());
		totalScc = d.cno;
		//for(Graph.Vertex u : g){
		//	DFS.DFSVertex vert = d.getDFSVertex(u);
			 
		//}
		d.transposeGraph();
	}
	
	public int getTotalScc(){
		return totalScc;
	}

	boolean checkIfSCC(Graph g, Graph.Vertex startVertex) {
		DFS d = new DFS(g);
		//Run DFS and check if it is connected
		d.dfs(g.iterator());
		boolean isConnected = d.isGraphConnected();
		if(isConnected == true) {
			d.reinitialize();
			//Reverse the graph by transposing and run DFS, If it is
			// still connected then the graph is strongly connected
			d.transposeGraph();
			d.dfs(d.getDecFinishList().iterator());
			d.transposeGraph();
			isConnected = d.isGraphConnected();
			if(isConnected == true) {
				return true;
			}
		}
    	return false;	
    }
}
