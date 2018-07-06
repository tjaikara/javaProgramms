package stdlib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// A fancy way to draw trees: http://stackoverflow.com/questions/10902745/enforcing-horizontal-node-ordering-in-a-dot-tree
// GraphvizBuilder.nodesToFile (stack.first, "rankdir=\"LR\"");

// TODO: Documentation
public final class GraphvizBuilder {
	private static enum Type { DIGRAPH, GRAPH }

	private static ArrayList<String> DOT_EXTENSIONS;
	static {
		DOT_EXTENSIONS = new ArrayList<> ();
		DOT_EXTENSIONS.add ("gv");
		DOT_EXTENSIONS.add ("dot");
	}
	private static HashSet<String> DOT_OUTPUT_FORMATS;
	static {
		DOT_OUTPUT_FORMATS = new HashSet<> ();
		DOT_OUTPUT_FORMATS.add ("bmp");
		DOT_OUTPUT_FORMATS.add ("cgimage");
		DOT_OUTPUT_FORMATS.add ("cmap");
		DOT_OUTPUT_FORMATS.add ("cmapx");
		DOT_OUTPUT_FORMATS.add ("eps");
		DOT_OUTPUT_FORMATS.add ("exr");
		DOT_OUTPUT_FORMATS.add ("fig");
		DOT_OUTPUT_FORMATS.add ("gif");
		DOT_OUTPUT_FORMATS.add ("icns");
		DOT_OUTPUT_FORMATS.add ("ico");
		DOT_OUTPUT_FORMATS.add ("imap");
		DOT_OUTPUT_FORMATS.add ("ismap");
		DOT_OUTPUT_FORMATS.add ("jp2");
		DOT_OUTPUT_FORMATS.add ("jpe");
		DOT_OUTPUT_FORMATS.add ("jpeg");
		DOT_OUTPUT_FORMATS.add ("jpg");
		DOT_OUTPUT_FORMATS.add ("pct");
		DOT_OUTPUT_FORMATS.add ("pdf");
		DOT_OUTPUT_FORMATS.add ("pic");
		DOT_OUTPUT_FORMATS.add ("pict");
		DOT_OUTPUT_FORMATS.add ("png");
		DOT_OUTPUT_FORMATS.add ("pov");
		DOT_OUTPUT_FORMATS.add ("ps");
		DOT_OUTPUT_FORMATS.add ("ps2");
		DOT_OUTPUT_FORMATS.add ("psd");
		DOT_OUTPUT_FORMATS.add ("sgi");
		DOT_OUTPUT_FORMATS.add ("svg");
		DOT_OUTPUT_FORMATS.add ("svgz");
		DOT_OUTPUT_FORMATS.add ("tga");
		DOT_OUTPUT_FORMATS.add ("tif");
		DOT_OUTPUT_FORMATS.add ("tiff");
		DOT_OUTPUT_FORMATS.add ("tk");
		DOT_OUTPUT_FORMATS.add ("vml");
		DOT_OUTPUT_FORMATS.add ("vmlz");
	}
	private static ArrayList<String> POSSIBLE_DOT_LOCATIONS;
	static {
		POSSIBLE_DOT_LOCATIONS = new ArrayList<> ();
		String os = System.getProperty ("os.name").toLowerCase ();
		if (os.startsWith ("win")) {
			POSSIBLE_DOT_LOCATIONS.add ("c:/Program Files (x86)/Graphviz2.38/bin/dot.exe");
			POSSIBLE_DOT_LOCATIONS.add (System.getProperty ("user.dir") + "/lib/graphviz-windows/bin/dot.exe");
		} else if (os.startsWith ("mac")) {
			POSSIBLE_DOT_LOCATIONS.add ("/usr/local/bin/dot");
			POSSIBLE_DOT_LOCATIONS.add ("/usr/bin/dot");
			POSSIBLE_DOT_LOCATIONS.add (System.getProperty ("user.dir") + "/lib/graphviz-mac/bin/dot");
		} else {
			POSSIBLE_DOT_LOCATIONS.add ("/usr/local/bin/dot");
			POSSIBLE_DOT_LOCATIONS.add ("/usr/bin/dot");
		}
	}
	public static void graphvizAddPossibleDotLocation (String value) {
		POSSIBLE_DOT_LOCATIONS.add (value);
	}

	private static interface Element {
		public String toString(Type type);
	}
	private static String getProperties (String label, String properties) {
		if (properties == null || "".equals (properties)) {
			if (label == null || "".equals (label)) {
				return ";";
			} else {
				return "[label=\"" + label + "\"];";
			}
		} else {
			if (label == null || "".equals (label)) {
				return "[" + properties + "];";
			} else {
				return "[label=\"" + label + "\"," + properties + "];";
			}
		}
	}
	private static final class Node implements Element {
		private final String id;
		private final String properties;
		public Node (String id, String label, String properties) {
			if (id == null || "".equals (id)) throw new IllegalArgumentException ();
			this.id = id;
			this.properties = getProperties (label, properties);
		}
		public String toString (Type type) {
			return "  " + id + properties;
		}
	}
	private static final class Edge implements Element {
		private final String src;
		private final String dst;
		private final String properties;
		public Edge (String src, String dst, String label, String properties) {
			if (src == null || "".equals (src)) throw new IllegalArgumentException ();
			if (dst == null || "".equals (dst)) throw new IllegalArgumentException ();
			this.src = src;
			this.dst = dst;
			this.properties = getProperties (label, properties);
		}
		public String toString (Type type) {
			String arrow = (type == Type.DIGRAPH ? " -> " : " -- ");
			return "  " + src + arrow + dst + properties;
		}
	}

	private static String defaultGraphProperties = "";
	private static String defaultNodeProperties = "";
	private static String defaultEdgeProperties = "fontsize=12";
	private static String nullNodeProperties = "shape=\"point\"";
	private static String nullEdgeProperties = "shape=\"point\"";

	private static int fileCount = 0;
	public static void binaryHeapToFile (Object[] heap, int N) {
		binaryHeapToFile (heap, N, "heap" + fileCount + ".png");
		fileCount++;
	}
	public static void ufToFile (int[] uf) {
		ufToFile (uf, "uf" + fileCount + ".png");
		fileCount++;
	}
	/**
	 * Shows simple recursive data structures.
	 * The class of the root parameter is taken to be the must be an element of the recursive node class.
	 *
	 * @param root
	 */
	public static void nodesToFile (Object root)                                    { nodesToFile (root, defaultNodeProperties, false); }
	public static void nodesToFile (Object root, String properties)                 { nodesToFile (root, properties, false); }
	public static void nodesToFileWithLabeledEdges (Object root)                    { nodesToFile (root, defaultNodeProperties, true); }
	public static void nodesToFileWithLabeledEdges (Object root, String properties) { nodesToFile (root, properties, true); }
	private static void nodesToFile (Object root, String properties, boolean withLabeledEdges) {
		nodesToFile (root, "nodes" + fileCount + ".png", properties, withLabeledEdges);
		fileCount++;
	}


	public static void binaryHeapToFile (Object[] heap, int N, String filename) {
		GraphvizBuilder gb = new GraphvizBuilder ();
		gb.addBinaryHeap (heap, N);
		gb.toFile (filename, "rankdir=\"BT\"");
	}
	public static void ufToFile (int[] uf, String filename) {
		GraphvizBuilder gb = new GraphvizBuilder ();
		gb.addUF (uf);
		gb.toFile (filename, "rankdir=\"BT\"");
	}
	public static void nodesToFile (Object root, String filename, String properties, boolean withLabeledEdges) {
		GraphvizBuilder gb = new GraphvizBuilder ();
		gb.addNodes (root, withLabeledEdges);
		gb.toFile (filename, properties);
	}


	private void addBinaryHeap (Object[] heap, int N) {
		if (heap == null) throw new IllegalArgumentException ("null heap");
		if (N<0 || N>heap.length) throw new IllegalArgumentException ("N=" + N + " is not in range [0.." + heap.length + "]");
		String prefix = "heap__" + heap.hashCode ();
		for (int i = 1; i <= N; i++) {
			addLabeledNodeString (prefix + i, heap[i].toString ());
		}
		for (int i = 2; i <= N; i++) {
			addEdgeString (prefix + i, prefix + (i/2));
		}
	}
	private void addUF (int[] uf) {
		if (uf == null) throw new IllegalArgumentException ("null uf");
		String prefix = "uf__" + uf.hashCode ();
		for (int i = 0; i < uf.length; i++) {
			addLabeledNodeString (prefix + i, Integer.toString (i));
		}
		for (int i = 0; i < uf.length; i++) {
			if (i != uf[i])
				addEdgeString (prefix + i, prefix + uf[i]);
		}
	}
	private void addNodes (Object root, boolean withLabeledEdges) {
		if (root == null) {
			addNullEdge (null);
		} else {
			addNodes (root, new HashSet<Object> (), root.getClass (), withLabeledEdges);
		}
	}
	private void addNodes (Object obj, Set<Object> visited, Class<?> nodeClass, boolean withLabeledEdges) {
		visited.add (obj);
		Class<?> clazz = obj.getClass ();
		if (clazz.isArray ()) {
			throw new Error ("Can't deal with arrays");
		} else {
			StringBuilder labelBuilder = new StringBuilder ();
			Map<Object,Field> children = new HashMap<> ();

			Field[] fields = clazz.getDeclaredFields ();
			AccessibleObject.setAccessible (fields, true);
			boolean first = true;
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				Object value;
				try { value = field.get (obj); } catch (IllegalAccessException e) { throw new Error (e); }
				if (field.getType () == nodeClass) {
					if (value == null) {
						if (withLabeledEdges)
							addLabeledNullEdge (obj, field.getName ());
						else
							addNullEdge (obj);
					} else {
						children.put (value, field);
					}
				} else {
					if (first) { first = false; } else { labelBuilder.append (", "); }
					labelBuilder.append (value == null ? "null" : value.toString ());
				}
			}
			addLabeledNode (obj, labelBuilder.toString ());
			for (Object child : children.keySet ()) {
				if (!visited.contains (child))
					addNodes (child, visited, nodeClass, withLabeledEdges);
			}
			for (Object child : children.keySet ()) {
				if (withLabeledEdges)
					addLabeledEdge (obj, child, children.get (child).getName ());
				else
					addEdge (obj, child);
			}
		}

	}

	private static String getId (Object o) {
		return "hash__" + Objects.hashCode (o);
	}
	private static String getId (int i) {
		return Integer.toString (i);
	}

	private static String getLabel (Object o) {
		return quote (Objects.toString (o));
	}
	private static String getLabel (String s) {
		return quote (s);
	}
	private static String getLabel (int i) {
		return Integer.toString (i);
	}
	private static String getLabel (double d) {
		return Double.toString (d);
	}
	private static String getLabel (float f) {
		return Float.toString (f);
	}
	//private static final String canAppearUnquotedInLabelChars = " /$&*@#!-+()^%;_[],;.=";
	private static boolean canAppearUnquotedInLabel (char c) {
		return true;
		//return canAppearUnquotedInLabelChars.indexOf (c) != -1 || Character.isLetter (c) || Character.isDigit (c);
	}
	private static final String quotable = "\\\"<>{}|";
	protected static String quote (String s) {
		s = unescapeJavaString (s);
		StringBuffer sb = new StringBuffer ();
		for (int i = 0, n = s.length (); i < n; i++) {
			char c = s.charAt (i);
			if (quotable.indexOf (c) != -1) sb.append ('\\').append (c);
			else if (canAppearUnquotedInLabel (c)) sb.append (c);
			else sb.append ("\\\\u").append (Integer.toHexString (c));
		}
		return sb.toString ();
	}
	/**
	 * Unescapes a string that contains standard Java escape sequences.
	 * <ul>
	 * <li><strong>\\b \\f \\n \\r \\t \\" \\'</strong> :
	 * BS, FF, NL, CR, TAB, double and single quote.</li>
	 * <li><strong>\\N \\NN \\NNN</strong> : Octal character
	 * specification (0 - 377, 0x00 - 0xFF).</li>
	 * <li><strong>\\uNNNN</strong> : Hexadecimal based Unicode character.</li>
	 * </ul>
	 *
	 * @param st
	 *            A string optionally containing standard java escape sequences.
	 * @return The translated string.
	 */
	// from http://udojava.com/2013/09/28/unescape-a-string-that-contains-standard-java-escape-sequences/
	private static String unescapeJavaString(String st) {
		StringBuilder sb = new StringBuilder(st.length());
		for (int i = 0; i < st.length(); i++) {
			char ch = st.charAt(i);
			if (ch == '\\') {
				char nextChar = (i == st.length() - 1) ? '\\' : st.charAt(i + 1);
				// Octal escape?
				if (nextChar >= '0' && nextChar <= '7') {
					String code = "" + nextChar;
					i++;
					if ((i < st.length() - 1) && st.charAt(i + 1) >= '0' && st.charAt(i + 1) <= '7') {
						code += st.charAt(i + 1);
						i++;
						if ((i < st.length() - 1) && st.charAt(i + 1) >= '0' && st.charAt(i + 1) <= '7') {
							code += st.charAt(i + 1);
							i++;
						}
					}
					sb.append((char) Integer.parseInt(code, 8));
					continue;
				}
				switch (nextChar) {
				case '\\': ch = '\\'; break;
				case 'b': ch = '\b'; break;
				case 'f': ch = '\f'; break;
				case 'n': ch = '\n'; break;
				case 'r': ch = '\r'; break;
				case 't': ch = '\t'; break;
				case '\"': ch = '\"'; break;
				case '\'': ch = '\''; break;
				// Hex Unicode: u????
				case 'u':
					if (i >= st.length() - 5) { ch = 'u'; break; }
					int code = Integer.parseInt(st.substring (i+2,i+6), 16);
					sb.append(Character.toChars(code));
					i += 5;
					continue;
				}
				i++;
			}
			sb.append(ch);
		}
		return sb.toString();
	}


	private final ArrayList<Element> elements = new ArrayList<> ();

	public void addNode (Object o)                                         { addLabeledNodeString (getId (o), getLabel (o), defaultNodeProperties); }
	public void addNode (Object o, String properties)                      { addLabeledNodeString (getId (o), getLabel (o), properties); }
	public void addLabeledNode (Object o, String label)                    { addLabeledNodeString (getId (o), getLabel (label), defaultNodeProperties); }
	public void addLabeledNode (Object o, String label, String properties) { addLabeledNodeString (getId (o), getLabel (label), properties); }

	public void addNode (int id)                                         { addLabeledNodeString (getId (id), getLabel (id), defaultNodeProperties); }
	public void addNode (int id, String properties)                      { addLabeledNodeString (getId (id), getLabel (id), properties); }
	public void addLabeledNode (int id, String label)                    { addLabeledNodeString (getId (id), getLabel (label), defaultNodeProperties); }
	public void addLabeledNode (int id, String label, String properties) { addLabeledNodeString (getId (id), getLabel (label), properties); }

	private void addNodeString (String id)                                         { addLabeledNodeString (id, id, defaultNodeProperties); }
	private void addNodeString (String id, String properties)                      { addLabeledNodeString (id, id, properties); }
	private void addLabeledNodeString (String id, String label)                    { addLabeledNodeString (id, getLabel (label), defaultNodeProperties); }
	private void addLabeledNodeString (String id, String label, String properties) { elements.add (new Node (id, getLabel (label), properties)); }

	public void addEdge (Object src, Object dst)                                         { addEdgeString (getId (src), getId (dst), defaultEdgeProperties); }
	public void addEdge (Object src, Object dst, String properties)                      { addEdgeString (getId (src), getId (dst), properties); }
	public void addLabeledEdge (Object src, Object dst, float label)                     { addLabeledEdgeString (getId (src), getId (dst), getLabel (label) + "\", length=\"" + getLabel (label), defaultEdgeProperties); }
	public void addLabeledEdge (Object src, Object dst, float label, String properties)  { addLabeledEdgeString (getId (src), getId (dst), getLabel (label) + "\", length=\"" + getLabel (label), properties); }
	public void addLabeledEdge (Object src, Object dst, double label)                    { addLabeledEdgeString (getId (src), getId (dst), getLabel (label) + "\", length=\"" + getLabel (label), defaultEdgeProperties); }
	public void addLabeledEdge (Object src, Object dst, double label, String properties) { addLabeledEdgeString (getId (src), getId (dst), getLabel (label) + "\", length=\"" + getLabel (label), properties); }
	public void addLabeledEdge (Object src, Object dst, int label)                       { addLabeledEdgeString (getId (src), getId (dst), getLabel (label) + "\", length=\"" + getId (label), defaultEdgeProperties); }
	public void addLabeledEdge (Object src, Object dst, int label, String properties)    { addLabeledEdgeString (getId (src), getId (dst), getLabel (label) + "\", length=\"" + getId (label), properties); }
	public void addLabeledEdge (Object src, Object dst, String label)                    { addLabeledEdgeString (getId (src), getId (dst), getLabel (label), defaultEdgeProperties); }
	public void addLabeledEdge (Object src, Object dst, String label, String properties) { addLabeledEdgeString (getId (src), getId (dst), getLabel (label), properties); }

	public void addEdge (int src, int dst)                                         { addEdgeString (getId (src), getId (dst), defaultEdgeProperties); }
	public void addEdge (int src, int dst, String properties)                      { addEdgeString (getId (src), getId (dst), properties); }
	public void addLabeledEdge (int src, int dst, float label)                     { addLabeledEdgeString (getId (src), getId (dst), getLabel (label) + "\", length=\"" + getLabel (label), defaultEdgeProperties); }
	public void addLabeledEdge (int src, int dst, float label, String properties)  { addLabeledEdgeString (getId (src), getId (dst), getLabel (label) + "\", length=\"" + getLabel (label), properties); }
	public void addLabeledEdge (int src, int dst, double label)                    { addLabeledEdgeString (getId (src), getId (dst), getLabel (label) + "\", length=\"" + getLabel (label), defaultEdgeProperties); }
	public void addLabeledEdge (int src, int dst, double label, String properties) { addLabeledEdgeString (getId (src), getId (dst), getLabel (label) + "\", length=\"" + getLabel (label), properties); }
	public void addLabeledEdge (int src, int dst, int label)                       { addLabeledEdgeString (getId (src), getId (dst), getLabel (label) + "\", length=\"" + getLabel (label), defaultEdgeProperties); }
	public void addLabeledEdge (int src, int dst, int label, String properties)    { addLabeledEdgeString (getId (src), getId (dst), getLabel (label) + "\", length=\"" + getLabel (label), properties); }
	public void addLabeledEdge (int src, int dst, String label)                    { addLabeledEdgeString (getId (src), getId (dst), getLabel (label), defaultEdgeProperties); }
	public void addLabeledEdge (int src, int dst, String label, String properties) { addLabeledEdgeString (getId (src), getId (dst), getLabel (label), properties); }

	private void addEdgeString (String src, String dst)                                         { addEdgeString (src, dst, defaultEdgeProperties); }
	private void addEdgeString (String src, String dst, String properties)                      { elements.add (new Edge (src, dst, null, properties)); }
	private void addLabeledEdgeString (String src, String dst, float label)                     { addLabeledEdgeString (src, dst, getLabel (label) + ", length=" + getLabel (label), defaultEdgeProperties); }
	private void addLabeledEdgeString (String src, String dst, float label, String properties)  { addLabeledEdgeString (src, dst, getLabel (label) + ", length=" + getLabel (label), properties); }
	private void addLabeledEdgeString (String src, String dst, double label)                    { addLabeledEdgeString (src, dst, getLabel (label) + ", length=" + getLabel (label), defaultEdgeProperties); }
	private void addLabeledEdgeString (String src, String dst, double label, String properties) { addLabeledEdgeString (src, dst, getLabel (label) + ", length=" + getLabel (label), properties); }
	private void addLabeledEdgeString (String src, String dst, int label)                       { addLabeledEdgeString (src, dst, getLabel (label) + ", length=" + getLabel (label), defaultEdgeProperties); }
	private void addLabeledEdgeString (String src, String dst, int label, String properties)    { addLabeledEdgeString (src, dst, getLabel (label) + ", length=" + getLabel (label), properties); }
	private void addLabeledEdgeString (String src, String dst, String label)                    { addLabeledEdgeString (src, dst, getLabel (label), defaultEdgeProperties); }
	private void addLabeledEdgeString (String src, String dst, String label, String properties) { elements.add (new Edge (src, dst, getLabel (label), properties)); }

	private int nullId = 0;
	public void addNullEdge (Object src) {
		String id = "null__" + nullId;
		nullId++;
		addLabeledNodeString (id, "", nullNodeProperties);
		if (src != null) addEdgeString (getId (src), id, nullEdgeProperties);
	}
	public void addLabeledNullEdge (Object src, String label) {
		String id = "null__" + nullId;
		nullId++;
		addLabeledNodeString (id, "", nullNodeProperties);
		if (src != null) addLabeledEdgeString (getId (src), id, getLabel (label), nullEdgeProperties);
	}

	private static PrintWriter getPrintWriter (File file) {
		PrintWriter out;
		try {
			out = new PrintWriter (new FileWriter (file));
		} catch (IOException e) {
			throw new Error ("\n!!!! Cannot open " + file + " for writing");
		}
		return out;
	}
	private static String getGvExecutable () {
		String executable = null;
		for (String s : POSSIBLE_DOT_LOCATIONS) {
			if (new File (s).canExecute ()) executable = s;
		}
		if (executable == null)
			throw new Error  ("\n!!!! Cannot find dot executable in " + POSSIBLE_DOT_LOCATIONS
					+ "\n!!!! Check the value of POSSIBLE_DOT_LOCATIONS in " + GraphvizBuilder.class.getCanonicalName ());
		return executable;
	}

	//public void toFileUndirected (boolean keepGvFile, String filename)                      { toFile (Type.GRAPH, keepGvFile, filename, defaultGraphProperties); }
	//public void toFileUndirected (boolean keepGvFile, String filename, String properties)   { toFile (Type.GRAPH, keepGvFile, filename, properties); }
	public void toFileUndirected (String filename)                                          { toFile (Type.GRAPH, false, filename, defaultGraphProperties); }
	public void toFileUndirected (String filename, String properties)                       { toFile (Type.GRAPH, false, filename, properties); }
	//public void toFile (boolean keepGvFile, String filename)                                { toFile (Type.DIGRAPH, keepGvFile, filename, defaultGraphProperties); }
	//public void toFile (boolean keepGvFile, String filename, String properties)             { toFile (Type.DIGRAPH, keepGvFile, filename, properties); }
	public void toFile (String filename)                                                    { toFile (Type.DIGRAPH, false, filename, defaultGraphProperties); }
	public void toFile (String filename, String properties)                                 { toFile (Type.DIGRAPH, false, filename, properties); }
	//public void toFile (Type type, boolean keepGvFile, String filename)                    { toFile (type, keepGvFile, filename, defaultGraphProperties); }
	private void toFile (Type type, boolean keepGvFile, String filename, String properties) {
		// keepGvFile = true;
		// Get basename and ext
		String basename;
		String ext;
		if (filename.indexOf ('.') < 0) {
			ext = "png";
			basename = filename;
		} else {
			int period = filename.lastIndexOf ('.');
			ext = filename.substring (period + 1);
			basename = filename.substring (0, period);
		}
		if (!new File (basename).isAbsolute ()) {
			if (dirName == null) setDirName ();
			basename = dirName + basename;
		}

		boolean extIsDot = DOT_EXTENSIONS.contains (ext);
		boolean extIsOut = DOT_OUTPUT_FORMATS.contains (ext);

		if ((!extIsDot) && (!extIsOut))
			throw new Error ("\n!!!! unrecognized extension \"" + ext + "\""
					+ "\n!!!! filename must end in \".ext\", where ext is \"gv\" or one of the following:\n!!!! " + DOT_OUTPUT_FORMATS);

		// Create dot input file
		File gvFile;
		if (extIsDot) {
			gvFile = new File (basename + "." + ext);
		} else {
			gvFile = new File (basename + ".gv");
		}
		PrintWriter out = getPrintWriter (gvFile);
		switch (type) {
		case DIGRAPH: out.print ("digraph"); break;
		case GRAPH:   out.print ("graph"); break;
		}
		out.println (" G {");
		if (properties != null && !"".equals (properties))
			out.println ("  graph [" + properties + "];");
		for (Element e : elements)
			out.println (e.toString (type));
		out.println ("}");
		out.close ();


		// Run dot
		if (extIsOut) {
			File outFile = new File (basename + "." + ext);

			String executable = getGvExecutable ();
			ProcessBuilder pb = new ProcessBuilder (executable, "-T", ext);
			pb.redirectInput (gvFile);
			pb.redirectOutput (outFile);
			int result = -1;
			try {
				result = pb.start ().waitFor ();
			} catch (IOException e) {
				throw new Error ("\n!!!! Cannot execute \"" + executable + "\"\n!!!! Make sure you have installed http://www.graphviz.org/"
						+ "\n!!!! Check the value of POSSIBLE_DOT_LOCATIONS in " + GraphvizBuilder.class.getCanonicalName ());
			} catch (InterruptedException e) {
				throw new Error ("\n!!!! Execution of \"" + executable + "\" interrupted");
			}
			if (result == 0) {
				if (!keepGvFile) gvFile.delete ();
			} else {
				outFile.delete ();
			}
		}
	}
	/**
	 * Graphics files are saved in directory baseDirName/Graphviz.
	 * baseDirName directory is created if it does not already exist.
	 * If baseDirName/Graphviz exists, then numbers are appended to the directory name:
	 * "baseDirName/Graphviz 1", "baseDirName/Graphviz 2", etc.
	 */
	public static void setBaseDirName (String baseDirName) {
		if (baseDirName == null) throw new Error ("\n!!!! no nulls please");		
		GraphvizBuilder.baseDirName = baseDirName;
	}
	private static String baseDirName = "Desktop" + File.separator + "GraphvizOutput";

	private static String dirName = null;
	private static void setDirName () {
		// create dir
		File dir = new File (baseDirName);
		if (!dir.isAbsolute ()) {
			baseDirName = System.getProperty ("user.home") + File.separator + baseDirName;
			dir = new File (baseDirName);
		}
		if (dir.exists ()) {
			if (!dir.isDirectory ()) 
				throw new Error ("\n!!!! \"" + dir + "\" is not a directory");
			if (!dir.canWrite ()) 
				throw new Error ("\n!!!! Unable to write directory: \"" + dir + "\"");
		} else {
			dir.mkdirs ();
		}
		
		// create newDir
		String mainClassName = "Graphviz";
		String prefix = baseDirName + File.separator;
		File newDir = new File (prefix + mainClassName);
		int suffix = 0;
		while (newDir.exists()) { 
			suffix++; 
			newDir = new File(prefix + mainClassName + " " + suffix);
		}
		newDir.mkdir ();
		
		if (!newDir.isDirectory () || !newDir.canWrite ())
			throw new Error ("Failed setOutputDirectory \"" + newDir + "\"");
		dirName = newDir + File.separator;
	}
}
