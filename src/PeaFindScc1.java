import java.util.HashSet;
import java.util.Stack;

/**
 * Two example implementations of an algorithm for finding strongly connected
 * components in a directed graph. See the following paper for details of how
 * this algorithm works:
 * 
 * "An Improved Algorithm for Finding the Strongly Connected Components of a
 * Directed Graph", Information Processing Letters, David J. Pearce, 2015.
 * 
 * Both a recursive and imperative version of this algorithm are given for
 * completeness. These algorithms are not intended to be specifically efficient,
 * just to provide sample implementations. In particular, I've tried to keep
 * them as close to the presentation in the paper as possible.
 */
public class PeaFindScc1 {

	// ==============================================================
	// Recursive Version
	// ==============================================================

	/**
	 * A simple recursive version of the algorithm PeaFindScc1
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class Recursive extends Base {

		public Recursive(Digraph g) {
			super(g);
		}

		@Override
		public void visit(int v) {
			boolean root = true;
			visited[v] = true;
			rindex[v] = index;
			index = index + 1;
			inComponent[v] = false;

			for (int w : graph.edges(v)) {
				if (!visited[w]) {
					visit(w);
				}
				if (!inComponent[w] && rindex[w] < rindex[v]) {
					rindex[v] = rindex[w];
					root = false;
				}
			}

			if (root) {
				inComponent[v] = true;
				while (!S.isEmpty() && rindex[v] <= rindex[S.peek()]) {
					int w = S.pop();
					rindex[w] = c;
					inComponent[w] = true;
				}
				rindex[v] = c;
				c = c + 1;
			} else {
				S.push(v);
			}
		}
	}

	// ==============================================================
	// Imperative Version
	// ==============================================================

	/**
	 * A simple imperative version of the algorithm PeaFindScc1
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class Imperative extends Base {

		public Imperative(Digraph g) {
			super(g);
		}

		public void visit(int v) {

		}
	}

	// ==============================================================
	// Abstract Base Class
	// ==============================================================

	/**
	 * A base class for stashing common code
	 * 
	 * @author djp
	 *
	 */
	private static abstract class Base {
		protected Digraph graph;
		protected boolean[] visited;
		protected boolean[] inComponent;
		protected int[] rindex;
		protected Stack<Integer> S;
		protected int index;
		protected int c; // component number

		public Base(Digraph g) {
			this.graph = g;
			this.visited = new boolean[g.size()];
			this.inComponent = new boolean[g.size()];
			this.rindex = new int[g.size()];
			this.S = new Stack();
			this.index = 0;
			this.c = 0;
		}

		/**
		 * Construct strong components for the entire graph and an array of
		 * components
		 * 
		 * @return
		 */
		public HashSet<Integer>[] visit() {
			for (int i = 0; i != graph.size(); ++i) {
				if (!visited[i]) {
					visit(i);
				}
			}
			// now, post process to produce component sets
			HashSet<Integer>[] components = new HashSet[c];
			for (int i = 0; i != rindex.length; ++i) {
				int cindex = rindex[i];
				HashSet<Integer> component = components[cindex];
				if (component == null) {
					component = new HashSet<Integer>();
					components[cindex] = component;
				}
				component.add(i);
			}
			return components;
		}

		public abstract void visit(int v);
	}
}
