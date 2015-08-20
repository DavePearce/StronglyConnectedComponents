import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple representation of directed graph
 * 
 * @author David J. Pearce
 *
 */
public class Digraph {
	private final ArrayList<HashSet<Integer>> edges;
	private int nedges = 0;

	public Digraph() {
		edges = new ArrayList();
	}

	public void add(int from, int to) {
		while (from >= edges.size() || to >= edges.size()) {
			edges.add(new HashSet());
		}

		if (edges.get(from).add(to)) {
			nedges++;
		}
	}

	public int numEdges() {
		return nedges;
	}

	public int size() {
		return edges.size();
	}

	public Set<Integer> edges(int v) {
		return edges.get(v);
	}

	public String toString() {
		String r = "";
		for (int i = 0; i != edges.size(); ++i) {
			for (int j : edges.get(i)) {
				r += i + "->" + j + " ";
			}
		}
		return r;
	}
}
