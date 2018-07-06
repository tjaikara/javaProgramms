package LinkedList;

import java.util.ArrayList;



public class IndexedLinkedList<E> {

	private Node head;

	// Constructs a new empty list.
	public IndexedLinkedList() {

		head = null;  // null front means empty
	}

	public void insertBeforeIndex(E value, int index) {

		if (index < 0 || index > size()){
			throw new IndexOutOfBoundsException();
		}
		else if (index == 0) {
			//insert at the front
			head = new Node(value, head);
		}
		else {
			Node current = goTo(index - 1);
			Node newNode = new Node(value, current.next);

			current.next = newNode;
		}
	}

	public E getValueAtIndex(int index) {

		if (index < 0 || index > size()-1){
			throw new IndexOutOfBoundsException();
		}
		else {
			Node current = goTo(index);
			return current.data;
		}
	}

	public int getIndexOfValue(E value) {

		int index = 0;
		Node current = head;

		while (current != null) {
			if (current.data.equals(value)) {
				return index;
			}
			index++;
			current = current.next;
		}
		return -1;
	}

	public int countValue(E value) {

		Node current = head;
		int count = 0;

		while (current != null) {
			if (current.data.equals(value))
				count++;

			current = current.next;
		}
		return count;
	}

	public Iterable<E> values() {

		ArrayList<E> values = new ArrayList();
		Node current = head;

		while(current != null){
			values.add(current.data);
			current = current.next;
		}
		return values;
	}



	// Returns the number of elements in this list.
	public int size() {

		int count = 0;
		Node current = head;

		while (current != null) {
			current = current.next;
			count++;
		}
		return count;
	}

	private Node goTo(int index) {

		Node current = head;

		for (int i = 0; i < index; i++) {
			current = current.next;
		}
		return current;
	}

	public class Node {

		public E data;
		public Node next;

		public Node(E data) {

			this.data = data;
			this.next = null;
		}

		public Node(E data, Node next) {

			this.data = data;
			this.next = next;
		}
	}
}

