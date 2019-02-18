package LinkedList;


public class TestILL {

	public static void main(String[] args) {
		IndexedLinkedList<String> ill = new IndexedLinkedList<>();
		//StdIn.fromFile("data/tinyTale.txt");
		//String[] words = StdIn.readAllStrings();
		String[] words = {"this", "code", "need", "to", "update", "the", "error", "fields"};
		for (String word: words) {
			ill.insertBeforeIndex(word, ill.size());
		}
		//StdOut.println("First occurrence of 'the' is at index " + ill.getIndexOfValue("the") + ".");
		//StdOut.println("The word 'the' occurs " + ill.countValue("the") + " times.");
		//StdOut.println("The word at index 10 is " + ill.getValueAtIndex(10) + ".");
		ill.insertBeforeIndex("most", ill.getIndexOfValue("best"));
		ill.insertBeforeIndex("most", ill.getIndexOfValue("worst"));
		for (String word: ill.values()) {
	//		StdOut.print(word + " ");
		}
	//	StdOut.println();
		
		// Patterns that index values are properly checked.  Each try/catch
		// block should result in a thrown exception.  
		try {
	//		StdOut.println(ill.getValueAtIndex(-1));
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		try {
		//	StdOut.println(ill.getValueAtIndex(ill.size()));
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		try {
			ill.insertBeforeIndex("oops", -1);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		try {
			ill.insertBeforeIndex("oops", ill.size()+1);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
}
