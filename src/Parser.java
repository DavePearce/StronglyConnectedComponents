import java.util.ArrayList;

/**
 * Simple input file parser.
 * 
 * @author David J. Pearce
 *
 */
public class Parser {
	private final String input;
	private int pos;

	public Parser(String input) {
		this.input = input;
		this.pos = 0;
	}

	public Digraph parse() {		
		ArrayList<Digraph.Edge> edges = new ArrayList<Digraph.Edge>();
		skipWhiteSpace();
		while (pos < input.length() && !isWhiteSpace(input.charAt(pos))) {
			int from = parseInt();
			skipLineSpace();
			int to = parseInt();
			skipLineSpace();
			edges.add(new Digraph.Edge(from, to));
		}
		return new Digraph(edges);
	}

	public void match(String r) {
		if ((pos + r.length()) <= input.length()) {
			String tmp = input.substring(pos, pos + r.length());
			if (tmp.equals(r)) {
				pos += r.length();
				return; // match
			}
			throw new RuntimeException("Expecting " + r + ", found " + tmp);
		}
		throw new RuntimeException("Unexpected end-of-file");
	}

	public int parseInt() {
		int start = pos;
		while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
			pos = pos + 1;
		}
		return Integer.parseInt(input.substring(start, pos));
	}

	public void skipWhiteSpace() {
		while (pos < input.length() && isWhiteSpace(input.charAt(pos))) {
			pos = pos + 1;
		}
	}

	public void skipLineSpace() {
		while (pos < input.length() && isLineSpace(input.charAt(pos))) {
			pos = pos + 1;
		}
	}

	public boolean isWhiteSpace(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\n';
	}

	public boolean isLineSpace(char c) {
		return c == ' ' || c == '\t';
	}
}
