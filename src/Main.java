import java.util.*;
import java.io.*;

/**
 * Responsible for parsing input files and passing on to the appropriate
 * algorithm(s).
 * 
 * @author David J. Pearce
 *
 */
public class Main {
	
	
	public static List<Digraph> readFile(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		ArrayList<Digraph> graphs = new ArrayList();
		while (reader.ready()) {
			String input = reader.readLine();
			if (!input.equals("")) {
				graphs.add(new Parser(input).parse());
			}
		}
		return graphs;
	}

	public static void main(String[] args) {
		try {
			int count = 0;
			for (Digraph graph : readFile(args[0])) {
				System.out.println("=== Graph #" + count++ + " ===");
				PeaFindScc1.Recursive r_pscc = new PeaFindScc1.Recursive(graph);
				PeaFindScc1.Imperative i_pscc = new PeaFindScc1.Imperative(graph);
				HashSet<Integer>[] r_components = r_pscc.visit();
				HashSet<Integer>[] i_components = i_pscc.visit();				
				printComponents(r_components);
				printComponents(i_components);				
				if(!Arrays.equals(r_components,i_components)) {
					System.out.println("*** DIFFERENCE DETECTED ***");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printComponents(HashSet<Integer>[] components) {
		for (HashSet<Integer> component : components) {
			printComponent(component);
		}				
		System.out.println();
	}
	
	public static void printComponent(HashSet<Integer> component) {
		System.out.print("{");
		boolean firstTime = true;
		for (Integer i : component) {
			if (!firstTime) {
				System.out.print(",");
			}
			firstTime = false;
			System.out.print(i);
		}
		System.out.print("}");
	}
}
