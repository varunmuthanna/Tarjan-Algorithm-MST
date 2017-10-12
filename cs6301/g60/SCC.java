package cs6301.g60;
import java.io.File;
import java.io.FileNotFoundException;
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
		for(int i = 0; i < totalScc; i++){
			List<Graph.Vertex> newList = new ArrayList<>();
			list.add(newList);
		}
		for(Graph.Vertex u : g){
			DFS.DFSVertex vert = d.getDFSVertex(u); 
			list.get(vert.cno-1).add(u);
		}
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
        
        SCC scc = new SCC();
        scc.getAllScc(g, startVertex);
        System.out.println("total SCC is" + scc.totalScc);
        int ind = 1;
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
