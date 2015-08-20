import java.util.ArrayList;
import java.util.Collection;

/**
 * Simple representation of directed graph
 * 
 * @author David J. Pearce
 *
 */
public class Digraph {
	private final int[][] edges;
	private int nedges = 0;

	public Digraph(Collection<Edge> edges) {
		int n = max(edges);		
		this.edges = new int[n][];
		for(int i=0;i!=n;++i) {
			this.edges[i] = edges(edges,i);
		}
	}

	public int numEdges() {
		return nedges;
	}

	public int size() {
		return edges.length;
	}

	public int[] edges(int v) {
		return edges[v];
	}

	public String toString() {
		String r = "";
		for (int i = 0; i != edges.length; ++i) {
			for (int j : edges[i]) {
				r += i + "->" + j + " ";
			}
		}
		return r;
	}
	
	public static class Edge {
		public final int from;
		public final int to;
		
		public Edge(int from, int to) {
			this.from = from;
			this.to = to;
		}
	}
	
	// =============================================

	private static int[] edges(Collection<Edge> edges, int index) {
		ArrayList<Edge> i_edges = new ArrayList<Edge>();
		for (Edge e : edges) {
			if (e.from == index) {
				i_edges.add(e);
			}
		}
		int[] r = new int[i_edges.size()];
		for (int i = 0; i != i_edges.size(); ++i) {
			r[i] = i_edges.get(i).to;
		}
		return r;
	}
	
	private static int max(Collection<Edge> edges) {
		int r = 0;
		for(Edge e : edges) {
			r = Math.max(r, e.from+1);
			r = Math.max(r, e.to+1);
		}
		return r;
	}
}
