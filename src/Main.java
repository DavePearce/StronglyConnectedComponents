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
				PeaFindScc1.Recursive r_pscc1 = new PeaFindScc1.Recursive(graph);
				PeaFindScc1.Imperative i_pscc1 = new PeaFindScc1.Imperative(graph);
				PeaFindScc2.Recursive r_pscc2 = new PeaFindScc2.Recursive(graph);
				PeaFindScc2.Imperative i_pscc2 = new PeaFindScc2.Imperative(graph);
				HashSet<Integer>[] r1_components = r_pscc1.visit();
				HashSet<Integer>[] i1_components = i_pscc1.visit();
				HashSet<Integer>[] r2_components = r_pscc2.visit();
				HashSet<Integer>[] i2_components = i_pscc2.visit();
				printComponents("PEA_FIND_SCC1 (recursive)",r1_components);
				printComponents("PEA_FIND_SCC1 (imperative)",i1_components);
				printComponents("PEA_FIND_SCC2 (recursive)",r2_components);
				printComponents("PEA_FIND_SCC2 (imperative)",i2_components);
				if(!Arrays.equals(r1_components,i1_components)) {
					System.out.println("*** Difference detected between PEA_FIND_SCC1(recursive) and PEA_FIND_SCC1(imperative)");
				}
				if(!Arrays.equals(r1_components,r2_components)) {
					System.out.println("*** Difference detected between PEA_FIND_SCC1(recursive) and PEA_FIND_SCC2(recursive)");
				}
				if(!Arrays.equals(r1_components,i2_components)) {
					System.out.println("*** Difference detected between PEA_FIND_SCC1(recursive) and PEA_FIND_SCC2(imperative)");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printComponents(String name, HashSet<Integer>[] components) {
		System.out.print(name + ": ");
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
