/** @author rbk
 *  Ver 1.0: 2017/09/29
 *  Example to extend Graph/Vertex/Edge classes to implement algorithms in which nodes and edges
 *  need to be disabled during execution.  Design goal: be able to call other graph algorithms
 *  without changing their codes to account for disabled elements.
 *
 *  Ver 1.1: 2017/10/09
 *  Updated iterator with boolean field ready. Previously, if hasNext() is called multiple
 *  times, then cursor keeps moving forward, even though the elements were not accessed
 *  by next().  Also, if program calls next() multiple times, without calling hasNext()
 *  in between, same element is returned.  Added UnsupportedOperationException to remove.
 **/

package cs6301.g60;

import java.util.Iterator;
import java.util.List;

import cs6301.g60.Graph.Edge;
import cs6301.g60.Graph.Vertex;

import java.util.LinkedList;



public class XGraph extends Graph {
	
    static boolean zeroGraph = false;
    static boolean getRevAdj = false;

    public static class XVertex extends Vertex {
        boolean disabled;
        List<XEdge> xadj, xrevadj;

        XVertex(Vertex u) {
            super(u);
            disabled = false;
            xadj = new LinkedList<>();
            xrevadj = new LinkedList<>();
        }

        boolean isDisabled() { return disabled; }

        void disable() { disabled = true; }

        @Override
        public Iterator<Edge> iterator() {
        	if(zeroGraph){
        		return new XZeroEdgeIterator(this); 
        	}else{
        		return new XVertexIterator(this);
        	}
        }

        class XVertexIterator implements Iterator<Edge> {
            XEdge cur;
            Iterator<XEdge> it;
            boolean ready;

            XVertexIterator(XVertex u) {
                if(!getRevAdj)
                    this.it = u.xadj.iterator();
                else
                    this.it = u.xrevadj.iterator();

                ready = false;
            }

            public boolean hasNext() {
                if(ready) { return true; }
                if(!it.hasNext()) { return false; }
                cur = it.next();
                while(cur.isDisabled() && it.hasNext()) {
                    cur = it.next();
                }
                ready = true;
                return !cur.isDisabled();
            }

            public Edge next() {
                if(!ready) {
                    if(!hasNext()) {
                        throw new java.util.NoSuchElementException();
                    }
                }
                ready = false;
                return cur;
            }

            public void remove() {
                throw new java.lang.UnsupportedOperationException();
            }
        }
        
        class XZeroEdgeIterator implements Iterator<Edge> {
            XEdge cur;
            Iterator<XEdge> it;
            boolean ready;

            XZeroEdgeIterator(XVertex u) {
                this.it = u.xadj.iterator();
                ready = false;
            }

            public boolean hasNext() {
                if(ready) { return true; }
                if(!it.hasNext()) { return false; }
                cur = it.next();
                while((cur.isDisabled() || cur.weight != 0 )&& it.hasNext()) {
                    cur = it.next();
                }
                ready = true;
                return !cur.isDisabled() && cur.weight == 0;
            }

            public Edge next() {
                if(!ready) {
                    if(!hasNext()) {
                        throw new java.util.NoSuchElementException();
                    }
                }
                ready = false;
                return cur;
            }

            public void remove() {
            	throw new java.lang.UnsupportedOperationException();
            }
        }
    }

    static class XEdge extends Edge {
        boolean disabled;

        XEdge(XVertex from, XVertex to, int weight, int name) {
            super(from, to, weight, name);
            disabled = false;
        }

        boolean isDisabled() {
            XVertex xfrom = (XVertex) from;
            XVertex xto = (XVertex) to;
            return disabled || xfrom.isDisabled() || xto.isDisabled();
        }
    }

    XVertex[] xv; // vertices of graph

    public XGraph(Graph g) {
        super(g);
        xv = new XVertex[2*g.size()];  // Extra space is allocated in array for nodes to be added later
        for(Vertex u: g) {
            xv[u.getName()] = new XVertex(u);
        }

        // Make copy of edges
        for(Vertex u: g) {
            for(Edge e: u) {
                Vertex v = e.otherEnd(u);
                XVertex x1 = getVertex(u);
                XVertex x2 = getVertex(v);
                XEdge newEdge = new XEdge(x1, x2, e.weight, e.name);
                x1.xadj.add(newEdge);
                x2.xrevadj.add(newEdge);
            }
        }
    }

    @Override
    public Iterator<Vertex> iterator() { return new XGraphIterator(this); }

    class XGraphIterator implements Iterator<Vertex> {
        Iterator<XVertex> it;
        XVertex xcur;

        XGraphIterator(XGraph xg) {
            this.it = new ArrayIterator<XVertex>(xg.xv, 0, xg.size()-1);  // Iterate over existing elements only
        }


        public boolean hasNext() {
            if(!it.hasNext()) { return false; }
            xcur = it.next();
            while(xcur.isDisabled() && it.hasNext()) {
                xcur = it.next();
            }
            return !xcur.isDisabled();
        }

        public Vertex next() {
            return xcur;
        }

        public void remove() {
        }

    }


    @Override
    public Vertex getVertex(int n) {
        return xv[n-1];
    }

    XVertex getVertex(Vertex u) {
        return Vertex.getVertex(xv, u);
    }

    void disable(int i) {
        XVertex u = (XVertex) getVertex(i);
        u.disable();
    }
    
    /** Method to reverse the edges of a graph.  Applicable to directed graphs only. */
    @Override
	public void reverseGraph() {
		if(directed) {
			Iterator<Graph.Vertex> it = this.iterator();
			while(it.hasNext()) {
				XVertex u = (XVertex)it.next();
				List<XEdge> tmp = u.xadj;
				u.xadj = u.xrevadj;
				u.xrevadj = tmp;
			}
		}
	}
    
    public void makeZeroGraph(){
    	zeroGraph = true;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        XGraph.getRevAdj = true;
        for (Graph.Vertex vertex : this){
            for(Graph.Edge edge : vertex ){
                result.append(edge.toString()+" "+edge.weight+" ");
            }
            result.append("\n");
        }
        XGraph.getRevAdj = false;
        return result.toString();
    }

}
