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
		protected DoubleStack S;
		
		public Recursive(Digraph g) {
			super(g);
			this.S = new DoubleStack(g.size());             // n words
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
				while (!S.isEmptyFront() && rindex[v] <= rindex[S.topFront()]) {
					int w = S.popFront();
					rindex[w] = c;
					index = index - 1;
				}
				rindex[v] = c;
				c = c - 1;
			} else {
				S.pushFront(v);
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
		DoubleStack vS;  // vertex stack
		DoubleStack iS;  // iterator stack
		boolean[] root;
		
		public Imperative(Digraph g) {
			super(g);
			vS = new DoubleStack(graph.size());         // n words
			iS = new DoubleStack(graph.size());         // n words
			root = new boolean[graph.size()];           // n bits
			                                  // subtotal: n(1 + 2w) bits
			                                  //     base:        n words
			                                  //    total: n(1 + 3w) bits
		}

		public void visit(int v) {
			beginVisiting(v);
			
			while (!vS.isEmptyFront()) {
				visitLoop();
			}
		}
		
		public void visitLoop() {			
			int v = vS.topFront();
			int i = iS.topFront();
			int[] g_edges = graph.edges(v);
			
			// Continue traversing out-edges until none left.			
			while(i <= g_edges.length){			
				// Continuation
				if(i > 0) {
					// Update status for previously traversed out-edge
					finishEdge(v, i-1);
				} 				
				if (i < g_edges.length && beginEdge(v, i)) {
					return;					
				} 
				i = i + 1;				
			}
			
			// Finished traversing out edges, update component info
			finishVisiting(v);		
		}

		public void beginVisiting(int v) {
			// First time this node encountered			
			vS.pushFront(v);
			iS.pushFront(0);
			root[v] = true;
			rindex[v] = index;
			index = index + 1;
		}
		
		public void finishVisiting(int v) {
			// Take this vertex off the call stack
			vS.popFront();
			iS.popFront();
			// Update component information
			if (root[v]) {
				index = index - 1;
				while (!vS.isEmptyBack() && rindex[v] <= rindex[vS.topBack()]) {
					int w = vS.popBack();
					rindex[w] = c;
					index = index - 1;
				}
				rindex[v] = c;
				c = c - 1;
			} else {
				vS.pushBack(v);
			}					
		}

		public boolean beginEdge(int v, int k) {
			int[] g_edges = graph.edges(v); 
			int w = g_edges[k];					
			if (rindex[w] == 0) {
				iS.popFront();
				iS.pushFront(k+1);
				beginVisiting(w);
				return true;
			} else {
				return false;
			}
		}
		
		public void finishEdge(int v, int k) {
			int[] g_edges = graph.edges(v); 
			int w = g_edges[k];
			if (rindex[w] < rindex[v]) {
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
		protected int[] rindex;

		protected int index;
		protected int c; // component number

		public Base(Digraph g) {
			this.graph = g;
			this.rindex = new int[g.size()];          // n words			
			this.index = 1;                   // total:  n words
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
	
	/**
	 * Implements two stacks which occupy the same space. This is only safe if
	 * it is known that one is always smaller than the other, and that their
	 * combined lengths never exceeds the capacity.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class DoubleStack {
		private final int[] items;
		private int fp; // front pointer
		private int bp; // back pointer
		
		public DoubleStack(int capacity) {			
			this.fp = 0;
			this.bp = capacity;
			this.items = new int[capacity];
		}

		// ============================
		// Front stack
		// ============================
		
		public boolean isEmptyFront() {
			return fp == 0;
		}
		
		/**
		 * Get top item on the stack
		 * 
		 * @return
		 */
		public int topFront() {
			return items[fp-1];
		}

		/**
		 * Pop item off stack
		 * 
		 * @return
		 */
		public int popFront() {
			fp = fp-1;
			return items[fp];
		}

		/**
		 * Push an item onto the stack
		 * 
		 * @param item
		 * @return
		 */
		public void pushFront(int item) {
			items[fp] = item;
			fp = fp + 1;			
		}
		
		// ============================
		// Back stack
		// ============================
		
		public boolean isEmptyBack() {
			return bp == items.length;
		}
		
		/**
		 * Get top item on the stack
		 * 
		 * @return
		 */
		public int topBack() {
			return items[bp];
		}

		/**
		 * Pop item off stack
		 * 
		 * @return
		 */
		public int popBack() {			
			return items[bp++];
		}
		
		/**
		 * Push an item onto the stack
		 * 
		 * @param item
		 * @return
		 */
		public void pushBack(int item) {
			bp = bp - 1;
			items[bp] = item;			
		}
	}
	
}
