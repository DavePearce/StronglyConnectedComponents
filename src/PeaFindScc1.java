import java.util.HashSet;

/**
 * Two example implementations of an algorithm for finding strongly connected
 * components in a directed graph. See the following paper for details of how
 * this algorithm works:
 * 
 * "An Improved Algorithm for Finding the Strongly Connected Components of a
 * Directed Graph", David J. Pearce, 2015.
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
				while (!S.isEmpty() && rindex[v] <= rindex[S.top()]) {
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
		Stack vS;
		Stack iS;
		boolean[] root;
		
		public Imperative(Digraph g) {
			super(g);
			vS = new Stack(graph.size());         // n words
			iS = new Stack(graph.size());         // n words
			root = new boolean[graph.size()]; // n bits
		}

		public void visit(int v) {
			// Represents the call stack			
			beginVisiting(v);
			
			while (!vS.isEmpty()) {
				visit(root);
			}
		}
		
		public void visit(boolean[] root) {			
			int v = vS.top();
			int i = iS.top();
			
			int[] g_edges = graph.edges(v); 
			
			// Continue traversing out-edges until none left.
			while(i <= g_edges.length){			
				// Continuation
				if(i > 0) { finishEdge(v, i); } 				
				if (i < g_edges.length && beginEdge(v, i)) {
					return;
				} 
				i = i + 1;				
			}
			
			finishVisiting(v);		
		}

		public void beginVisiting(int v) {
			// First time this node encountered			
			vS.push(v);                          // total: n(1 + 2w)
			iS.push(0);
			root[v] = true;
			visited[v] = true;
			rindex[v] = index;
			index = index + 1;
			inComponent[v] = false;
		}
		
		public void finishVisiting(int v) {
			// Take this vertex off the stack
			vS.pop();
			iS.pop();
			// Finished traversing out edges, update component info
			if (root[v]) {
				inComponent[v] = true;
				while (!S.isEmpty() && rindex[v] <= rindex[S.top()]) {
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

		public boolean beginEdge(int v, int i) {
			int[] g_edges = graph.edges(v);
			int w = g_edges[i];					
			if (!visited[w]) {
				iS.pop();
				iS.push(i+1);
				beginVisiting(w);
				return true;
			} else {
				return false;
			}
		}

		public void finishEdge(int v, int k) {
			int[] g_edges = graph.edges(v); 
			// Update status for previously traversed out-edge
			int w = g_edges[k-1];
			if (!inComponent[w] && rindex[w] < rindex[v]) {
				rindex[v] = rindex[w];
				root[v] = false;
			}
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
		protected Stack S;
		protected int index;
		protected int c; // component number

		public Base(Digraph g) {
			this.graph = g;
			this.visited = new boolean[g.size()];     // n bits
			this.inComponent = new boolean[g.size()]; // n bits
			this.rindex = new int[g.size()];          // n words
			this.S = new Stack(g.size());             // n words
			this.index = 0;                    // total: n(2 + 2w)
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
	
	
	
	// ==============================================================
	// Stack Implementation
	// ==============================================================
	
	public static class Stack {
		private final int[] items;
		private int length;
		
		public Stack(int capacity) {
			this.length = 0;
			this.items = new int[capacity];
		}
		
		public boolean isEmpty() {
			return length == 0;
		}
		
		/**
		 * Get top item on the stack
		 * 
		 * @return
		 */
		public int top() {
			return items[length-1];
		}

		/**
		 * Pop item off stack
		 * 
		 * @return
		 */
		public int pop() {
			length = length-1;
			return items[length];
		}

		/**
		 * Push an item onto the stack
		 * 
		 * @param item
		 * @return
		 */
		public void push(int item) {
			items[length] = item;
			length = length + 1;			
		}
	}
	
}
