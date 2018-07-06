package stdlib;

/**
 * Provides methods to generate arrays of Integer objects,
 * arrays of doubles in [0.0,1.0), and arrays of characters.
 */
public class ArrayGenerator {
	/**
	 * Generate an array of strings from a string.  Each array element will be a string of length one.
	 * For example
	 * <pre>
	 *   fromString("DOG") generates the array { "D", "O", "G" }
	 * </pre>
	 *
	 * @see In#readAllStrings()
	 * @see StdIn#readAllStrings()
	 */
	public static String[] fromString (String s) {
		int N = s.length();
		String[] a = new String[N];
		for (int i = 0; i < N; i++)
			a[i] = s.substring(i, i+1);
		return a;
	}
	/**
	 * Generate an array of length N whose values are chosen uniformly from the range [minValue,maxValue).
	 */
	public static int[] intRandom (int N, int minValue, int maxValue) {
		if (N < 0) throw new IllegalArgumentException ();
		int[] a = new int[N];
		for (int i = 0; i < N; i++) {
			a[i] = StdRandom.uniform (minValue, maxValue);
		}
		return a;
	}
	/**
	 * Generate an array of length N whose values are chosen uniformly from the range [0,numValues).
	 */
	public static int[] intRandom (int N, int numValues) {
		if (N < 0) throw new IllegalArgumentException ();
		int[] a = new int[N];
		for (int i = 0; i < N; i++) {
			a[i] = StdRandom.uniform (numValues);
		}
		return a;
	}
	/**
	 * Generate an array of length N with values 0, 1, ..., N-1.
	 */
	public static int[] intSortedUnique (int N) {
		if (N < 0) throw new IllegalArgumentException ();
		int[] a = new int[N];
		for (int i = 0; i < N; i++) {
			a[i] = i;
		}
		return a;
	}
	/**
	 * Generate an array of length N with values N-1, N-2, ... 0.
	 */
	public static int[] intReverseSortedUnique (int N) {
		if (N < 0) throw new IllegalArgumentException ();
		int[] a = new int[N];
		for (int i = 0; i < N; i++) {
			a[i] = N - 1 - i;
		}
		return a;
	}
	/**
	 * Generate a shuffled array of length N with unique values 0, 1, ... N-1
	 */
	public static int[] intRandomUnique (int N) {
		int[] a = intSortedUnique (N);
		StdRandom.shuffle (a);
		return a;
	}
	/**
	 * Generate a partially sorted array with unique elements.
	 * The number of inversions will be between N and 2N.
	 * This algorithm moves random elements an arbitrary amount until the threshold is achieved.
	 */
	public static int[] intPartiallySortedUnique (int N) {
		if (N < 6) throw new IllegalArgumentException ("array too small");
		int[] a = intSortedUnique (N);
		int totalDistance = 0; // this is an approximation of the number of inversions
		int range = (int) (Math.sqrt (N));
		while (totalDistance < N) {
			int i = StdRandom.uniform (N);
			int r = StdRandom.uniform (Math.max (0, i-range), Math.min (N-1, i+range));
			totalDistance += Math.abs (i - r);
			int temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
		return a;
	}
	/**
	 * Generate a partially sorted array with unique elements.
	 * The number of inversions will be between N and 2N.
	 * This algorithm moves all elements a small amount.
	 */
	public static int[] intPartiallySortedUnique2 (int N) {
		if (N < 6) throw new IllegalArgumentException ("array too small");
		int[] a = intSortedUnique (N);
		int range = 4;
		for (int i=0; i<N; i++) {
			int r = StdRandom.uniform (Math.max (0, i-range), Math.min (N-1, i+range));
			int temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
		return a;
	}

	/**
	 * Generate an array of length N whose values are chosen uniformly from the range [minValue,maxValue).
	 */
	public static Integer[] integerRandom (int N, int minValue, int maxValue) {
		if (N < 0) throw new IllegalArgumentException ();
		Integer[] a = new Integer[N];
		for (int i = 0; i < N; i++) {
			a[i] = StdRandom.uniform (minValue, maxValue);
		}
		return a;
	}
	/**
	 * Generate an array of length N whose values are chosen uniformly from the range [0,numValues).
	 */
	public static Integer[] integerRandom (int N, int numValues) {
		if (N < 0) throw new IllegalArgumentException ();
		Integer[] a = new Integer[N];
		for (int i = 0; i < N; i++) {
			a[i] = StdRandom.uniform (numValues);
		}
		return a;
	}
	/**
	 * Generate an array of length N with values 0, 1, ..., N-1.
	 */
	public static Integer[] integerSortedUnique (int N) {
		if (N < 0) throw new IllegalArgumentException ();
		Integer[] a = new Integer[N];
		for (int i = 0; i < N; i++) {
			a[i] = i;
		}
		return a;
	}
	/**
	 * Generate an array of length N with values N-1, N-2, ... 0.
	 */
	public static Integer[] integerReverseSortedUnique (int N) {
		if (N < 0) throw new IllegalArgumentException ();
		Integer[] a = new Integer[N];
		for (int i = 0; i < N; i++) {
			a[i] = N - 1 - i;
		}
		return a;
	}
	/**
	 * Generate a shuffled array of length N with unique values 0, 1, ... N-1
	 */
	public static Integer[] integerRandomUnique (int N) {
		Integer[] a = integerSortedUnique (N);
		StdRandom.shuffle (a);
		return a;
	}
	/**
	 * Generate a partially sorted array with unique elements.
	 * The number of inversions will be between N and 2N.
	 * This algorithm moves random elements an arbitrary amount until the threshold is achieved.
	 */
	public static Integer[] integerPartiallySortedUnique (int N) {
		if (N < 6) throw new IllegalArgumentException ("array too small");
		Integer[] a = integerSortedUnique (N);
		int totalDistance = 0; // this is an approximation of the number of inversions
		int range = (int) (Math.sqrt (N));
		while (totalDistance < N) {
			int i = StdRandom.uniform (N);
			int r = StdRandom.uniform (Math.max (0, i-range), Math.min (N-1, i+range));
			totalDistance += Math.abs (i - r);
			Integer temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
		return a;
	}
	/**
	 * Generate a partially sorted array with unique elements.
	 * The number of inversions will be between N and 2N.
	 * This algorithm moves all elements a small amount.
	 */
	public static Integer[] integerPartiallySortedUnique2 (int N) {
		if (N < 6) throw new IllegalArgumentException ("array too small");
		Integer[] a = integerSortedUnique (N);
		int range = 4;
		for (int i=0; i<N; i++) {
			int r = StdRandom.uniform (Math.max (0, i-range), Math.min (N-1, i+range));
			Integer temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
		return a;
	}

	//    public static void main (String[] args) {
	//        StdOut.println (java.util.Arrays.toString (integerRandom (20, 1)));
	//        StdOut.println (java.util.Arrays.toString (integerRandom (20, 2)));
	//        StdOut.println (java.util.Arrays.toString (integerRandom (20, 4)));
	//
	//        // This is a unit test for partiallySortedUnique, to ensure that the result has inversions between N and 2N.
	//        int N = 80;
	//        for (int i = 0; i < 100; i++) {
	//            Integer[] a = integerPartiallySortedUnique2 (N);
	//            Integer[] b = integerRandomUnique (N);
	//            StdOut.format ("N = %3d, partiallySorted = %3d, random = %3d\n", N, algs22.XInversions.count (a), algs22.XInversions.count (b));
	//        }
	//        for (int j = 0; j < 12; j++) {
	//            N = N*2;
	//            for (int i = 0; i < 100; i++) {
	//                Integer[] a = integerPartiallySortedUnique2 (N);
	//                int inversions = algs22.XInversions.count (a);
	//                if (inversions < N || inversions > 2*N)
	//                    StdOut.format ("N = %3d, partiallySorted = %3d\n", N, inversions);
	//            }
	//        }
	//    }

	/**
	 * Generate an array of length N whose values are chosen uniformly from the range [0,numValues).
	 */
	public static double[] doubleRandom (int N, int numValues) {
		if (N < 0) throw new IllegalArgumentException ();
		double[] a = new double[N];
		for (int i = 0; i < N; i++) {
			a[i] = Math.random ();
		}
		return a;
	}
	/**
	 * Generate an array of length N with values 0, 1, ..., N-1.
	 */
	public static double[] doubleSortedUnique (int N) {
		if (N < 0) throw new IllegalArgumentException ();
		double[] a = new double[N];
		for (int i = 0; i < N; i++) {
			a[i] = ((double)i)/N;
		}
		return a;
	}
	/**
	 * Generate an array of length N with values N-1, N-2, ... 0.
	 */
	public static double[] doubleReverseSortedUnique (int N) {
		if (N < 0) throw new IllegalArgumentException ();
		double[] a = new double[N];
		for (int i = 0; i < N; i++) {
			a[i] = ((double)(N - 1 - i))/N;
		}
		return a;
	}
	/**
	 * Generate a shuffled array of length N with unique values 0, 1, ... N-1
	 */
	public static double[] doubleRandomUnique (int N) {
		double[] a = doubleSortedUnique (N);
		StdRandom.shuffle (a);
		return a;
	}
	/**
	 * Generate a partially sorted array with unique elements.
	 * The number of inversions will be between N and 2N.
	 * This algorithm moves random elements an arbitrary amount until the threshold is achieved.
	 */
	public static double[] doublePartiallySortedUnique (int N) {
		if (N < 6) throw new IllegalArgumentException ("array too small");
		double[] a = doubleSortedUnique (N);
		int totalDistance = 0; // this is an approximation of the number of inversions
		int range = (int) (Math.sqrt (N));
		while (totalDistance < N) {
			int i = StdRandom.uniform (N);
			int r = StdRandom.uniform (Math.max (0, i-range), Math.min (N-1, i+range));
			totalDistance += Math.abs (i - r);
			double temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
		return a;
	}
	/**
	 * Generate a partially sorted array with unique elements.
	 * The number of inversions will be between N and 2N.
	 * This algorithm moves all elements a small amount.
	 */
	public static double[] doublePartiallySortedUnique2 (int N) {
		if (N < 6) throw new IllegalArgumentException ("array too small");
		double[] a = doubleSortedUnique (N);
		int range = 4;
		for (int i=0; i<N; i++) {
			int r = StdRandom.uniform (Math.max (0, i-range), Math.min (N-1, i+range));
			double temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
		return a;
	}

	/**
	 * Read in and return an array of Strings from fileName.  Input must begin with dimensions.
	 *
	 * @see In#readAllStrings()
	 * @see StdIn#readAllStrings()
	 */
	public static String[] readString1D(String fileName) {
		return readString1D (new In(fileName));
	}
	/**
	 * Read in and return an array of Strings from in. Input must begin with dimensions.
	 *
	 * @see In#readAllStrings()
	 * @see StdIn#readAllStrings()
	 */
	public static String[] readString1D(In in) {
		int N = in.readInt();
		String[] a = new String[N];
		for (int i = 0; i < N; i++) {
			a[i] = in.readString();
		}
		return a;
	}

	/**
	 * Print an array of Strings to standard output.
	 */
	public static void print(Object[] a) {
		int N = a.length;
		StdOut.println(N);
		for (int i = 0; i < N; i++) {
			StdOut.format("%s ", a[i]);
		}
		StdOut.println();
	}

	/**
	 * Read in and return an M-by-N array of Strings from fileName. Input must begin with dimensions.
	 */
	public static String[][] readString2D(String fileName) {
		return readString2D (new In(fileName));
	}
	/**
	 * Read in and return an M-by-N array of Strings from in. Input must begin with dimensions.
	 */
	public static String[][] readString2D(In in) {
		int M = in.readInt();
		int N = in.readInt();
		String[][] a = new String[M][N];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				a[i][j] = in.readString();
			}
		}
		return a;
	}

	/**
	 * Print the M-by-N array of Strings to standard output.
	 */
	public static void print(Object[][] a) {
		int M = a.length;
		int N = a[0].length;
		StdOut.println(M + " " + N);
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				StdOut.format("%s ", a[i][j]);
			}
			StdOut.println();
		}
	}

	/**
	 * Read in and return an array of doubles from fileName. Input must begin with dimensions.
	 *
	 * @see In#readAllDoubles()
	 * @see StdIn#readAllDoubles()
	 */
	public static double[] readDouble1D(String fileName) {
		return readDouble1D (new In(fileName));
	}
	/**
	 * Read in and return an array of doubles from in. Input must begin with dimensions.
	 *
	 * @see In#readAllDoubles()
	 * @see StdIn#readAllDoubles()
	 */
	public static double[] readDouble1D(In in) {
		int N = in.readInt();
		double[] a = new double[N];
		for (int i = 0; i < N; i++) {
			a[i] = in.readDouble();
		}
		return a;
	}

	/**
	 * Print an array of doubles to standard output.
	 */
	public static void print(double[] a) {
		int N = a.length;
		StdOut.println(N);
		for (int i = 0; i < N; i++) {
			StdOut.format("%9.5f ", a[i]);
		}
		StdOut.println();
	}


	/**
	 * Read in and return an M-by-N array of doubles from fileName. Input must begin with dimensions.
	 */
	public static double[][] readDouble2D(String fileName) {
		return readDouble2D (new In(fileName));
	}
	/**
	 * Read in and return an M-by-N array of doubles from in. Input must begin with dimensions.
	 */
	public static double[][] readDouble2D(In in) {
		int M = in.readInt();
		int N = in.readInt();
		double[][] a = new double[M][N];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				a[i][j] = in.readDouble();
			}
		}
		return a;
	}

	/**
	 * Print the M-by-N array of doubles to standard output.
	 */
	public static void print(double[][] a) {
		int M = a.length;
		int N = a[0].length;
		StdOut.println(M + " " + N);
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				StdOut.format("%9.5f ", a[i][j]);
			}
			StdOut.println();
		}
	}


	/**
	 * Read in and return an array of ints from fileName. Input must begin with dimensions.
	 *
	 * @see In#readAllInts()
	 * @see StdIn#readAllInts()
	 */
	public static int[] readInt1D(String fileName) {
		return readInt1D (new In(fileName));
	}
	/**
	 * Read in and return an array of ints from in. Input must begin with dimensions.
	 *
	 * @see In#readAllInts()
	 * @see StdIn#readAllInts()
	 */
	public static int[] readInt1D(In in) {
		int N = in.readInt();
		int[] a = new int[N];
		for (int i = 0; i < N; i++) {
			a[i] = in.readInt();
		}
		return a;
	}

	/**
	 * Print an array of ints to standard output.
	 */
	public static void print(int[] a) {
		int N = a.length;
		StdOut.println(N);
		for (int i = 0; i < N; i++) {
			StdOut.format("%9d ", a[i]);
		}
		StdOut.println();
	}


	/**
	 * Read in and return an M-by-N array of ints from fileName. Input must begin with dimensions.
	 */
	public static int[][] readInt2D(String fileName) {
		return readInt2D (new In(fileName));
	}
	/**
	 * Read in and return an M-by-N array of ints from in. Input must begin with dimensions.
	 */
	public static int[][] readInt2D(In in) {
		int M = in.readInt();
		int N = in.readInt();
		int[][] a = new int[M][N];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				a[i][j] = in.readInt();
			}
		}
		return a;
	}

	/**
	 * Print the M-by-N array of ints to standard output.
	 */
	public static void print(int[][] a) {
		int M = a.length;
		int N = a[0].length;
		StdOut.println(M + " " + N);
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				StdOut.format("%9d ", a[i][j]);
			}
			StdOut.println();
		}
	}


	/**
	 * Read in and return an array of booleans from fileName. Input must begin with dimensions.
	 */
	public static boolean[] readBoolean1D(String fileName) {
		return readBoolean1D (new In(fileName));
	}
	/**
	 * Read in and return an array of booleans from in. Input must begin with dimensions.
	 */
	public static boolean[] readBoolean1D(In in) {
		int N = in.readInt();
		boolean[] a = new boolean[N];
		for (int i = 0; i < N; i++) {
			a[i] = in.readBoolean();
		}
		return a;
	}

	/**
	 * Print an array of booleans to standard output.
	 */
	public static void print(boolean[] a) {
		int N = a.length;
		StdOut.println(N);
		for (int i = 0; i < N; i++) {
			if (a[i]) StdOut.print("1 ");
			else      StdOut.print("0 ");
		}
		StdOut.println();
	}

	/**
	 * Read in and return an M-by-N array of booleans from fileName. Input must begin with dimensions.
	 */
	public static boolean[][] readBoolean2D(String fileName) {
		return readBoolean2D (new In(fileName));
	}
	/**
	 * Read in and return an M-by-N array of booleans from in. Input must begin with dimensions.
	 */
	public static boolean[][] readBoolean2D(In in) {
		int M = in.readInt();
		int N = in.readInt();
		boolean[][] a = new boolean[M][N];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				a[i][j] = in.readBoolean();
			}
		}
		return a;
	}

	/**
	 * Print the  M-by-N array of booleans to standard output.
	 */
	public static void print(boolean[][] a) {
		int M = a.length;
		int N = a[0].length;
		StdOut.println(M + " " + N);
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				if (a[i][j]) StdOut.print("1 ");
				else         StdOut.print("0 ");
			}
			StdOut.println();
		}
	}
}
