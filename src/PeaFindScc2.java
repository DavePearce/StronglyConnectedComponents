import java.util.HashSet;

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
public class PeaFindScc2 {

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
			rindex[v] = index;
			index = index + 1;

			for (int w : graph.edges(v)) {
				if (rindex[w] == 0) {
					visit(w);
				}
				if (rindex[w] < rindex[v]) {
					rindex[v] = rindex[w];
					root = false;
				}
			}

			if (root) {
				index = index - 1;
				while (!S.isEmpty() && rindex[v] <= rindex[S.top()]) {
					int w = S.pop();
					rindex[w] = c;
					index = index - 1;
				}
				rindex[v] = c;
				c = c - 1;
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
			// Represents the call stack			
			Stack vS = new Stack(graph.size());         // n words
			Stack iS = new Stack(graph.size());         // n words
			boolean[] root = new boolean[graph.size()]; // n bits
			vS.push(v);                          // total: n(1 + 2w) bits
			iS.push(0);
			
			while (!vS.isEmpty()) {
				visit(vS,iS,root);
			}
		}
		
		public void visit(Stack vS, Stack iS, boolean[] root) {			
			int v = vS.top();
			int i = iS.top();
			
			int[] g_edges = graph.edges(v); 
			
			if(i == 0) {
				// First time this node encountered			
				root[v] = true;
				rindex[v] = index;
				index = index + 1;				
			} 
			
			// Continue traversing out-edges until none left.
			while(i <= g_edges.length){			
				// Continuation
				if(i > 0) {
					// Update status for previously traversed out-edge
					int ow = g_edges[i-1];
					if (rindex[ow] < rindex[v]) {
						rindex[v] = rindex[ow];
						root[v] = false;
					}
				} 				
				if (i < g_edges.length) {
					int w = g_edges[i];					
					if (rindex[w] == 0) {
						iS.pop();
						iS.push(i+1);
						vS.push(w);
						iS.push(0);
						return;
					}
				} 
				i = i + 1;				
			}
			
			// Finished traversing out edges, update component info
			if (root[v]) {
				index = index - 1;
				while (!S.isEmpty() && rindex[v] <= rindex[S.top()]) {
					int w = S.pop();
					rindex[w] = c;
					index = index - 1;
				}
				rindex[v] = c;
				c = c - 1;
			} else {
				S.push(v);
			}					

			// Take this vertex off the stack
			vS.pop();
			iS.pop();		
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
		protected int[] rindex;
		protected Stack S;
		protected int index;
		protected int c; // component number

		public Base(Digraph g) {
			this.graph = g;
			this.rindex = new int[g.size()];          // n words
			this.S = new Stack(g.size());             // n words
			this.index = 1;                    // total: 2n words
			this.c = g.size() - 1;
		}

		/**
		 * Construct strong components for the entire graph and an array of
		 * components
		 * 
		 * @return
		 */
		public HashSet<Integer>[] visit() {
			for (int i = 0; i != graph.size(); ++i) {
				if (rindex[i] == 0) {
					visit(i);
				}
			}
			// now, post process to produce component sets
			HashSet<Integer>[] components = new HashSet[(graph.size()-1) - c];			
			for (int i = 0; i != rindex.length; ++i) {
				int cindex = (graph.size()-1) - rindex[i];
				HashSet<Integer> component = components[cindex];
				if (component == null) {
					component = new HashSet<Integer>();
					components[cindex] = component;
				}
				component.add(i);
			}
			// now, compact components down
			
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
