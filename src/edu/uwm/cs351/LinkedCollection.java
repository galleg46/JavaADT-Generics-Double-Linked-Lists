package edu.uwm.cs351;


import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

// This is a Homework Assignment for CS 351 at UWM

/**
 * A cyclic doubly-linked list implementation of the Java Collection interface
 *  You will be required to override all of the collection methods aside from retainAll() and
 *  the overloaded method toArray(T[]). Note that toArray() will still need to be completed.
 *  All these methods should be declared "@Override".
 * <p>
 * The data structure is a cyclic doubly-linked list with one dummy node.
 * The fields should be:
 * <dl>
 * <dt>count</dt> Number of true elements in the collection.
 * <dt>version</dt> Version number (used for fail-fast semantics).
 *  <dt>dummy</dt> A reference to the dummy node.
 * </dl>
 * It should be cyclicly linked without any null pointers.
 * The code should have very few special cases (if statements regarding the data structure).
 * The class should define a private {@link #wellFormed()} method
 * and perform assertion checks in each method.
 * You should use a version stamp to implement <i>fail-fast</i> semantics
 * for the iterator.
 * @param <E>
 */
public class LinkedCollection<E> implements Collection<E> {


	/** 
	 * A data class used for the linked structure for the linked list implementation of LinkedCollection
	 */

	// Declare the private static generic Node class with fields data, prev and next.
	// The class should be private, static and generic.
	// Please use a different name for its generic type parameter Node<> <--
	// It should have a constructor or two (at least the default constructor) but no methods.
	// The no-argument constructor can construct a dummy node if you would like.
	// The fields of Node should have "default" access (neither public, nor private)
	// Remember the dummy node should have a type-cast reference to itself for its data
	// So we should have dummy.data == dummy
	private static class Node<T> {
		T data;
		Node<T> prev, next;
		
		@SuppressWarnings("unchecked")
		public Node() {
			next = prev = this;
			data = (T)this;
		}
		
		public Node(T data, Node<T> prev, Node<T> next) {
			this.data = data;
			this.prev = prev;
			this.next = next;
		}
		
	}

	// Declare the private fields of Sequences:
	// One for the dummy, one for the size, one for version
	// Do not declare any other fields.
	private Node<E> dummy;
	private int version;
	private int count;
	
	private LinkedCollection(boolean ignored) {} // DO NOT CHANGE THIS

	private boolean report(String s) {
		System.out.println(s);
		return false;
	}

	private boolean wellFormed() {
		// Invariant:
		// 1. dummy node is not null.  Its data should be itself, cast (unsafely) data = (T)this;.
		if(dummy == null) return report("dummy is null!");
		if(dummy.data != dummy) return report("dummy's data isn't null: " +dummy.data);
		// 2. each link must be correctly double linked.
		// 3. size is number of nodes in list, other than the dummy.
		// 4. the list must cycle back to the dummy node.
		Node<E> p = dummy;
		int size = 0;
		for(Node<E> n = dummy.next; n != dummy; n = n.next)
		{
			if(n == null) return report("found null in list after " +p);
			//if(n.data == null) return report("found null data after " +p);
			if(n.prev != p) return report("Node after " +p +"does not point back to it");
			++size;
			p = n;
		}
		
		if(dummy.prev != p) return report("dummy.prev does not point to the last node in the list");
		if(count != size) return report("count is: " +size +" which doesn't match the size: " +count);
		// Implement multiple checks
		// The solution does all the checks with only one loop.
		// In particular, if you check "prev" links, you can avoid getting stuck
		// in an illegal cycle (one that doesn't contain the dummy)
		
		
		// If no problems found, then return true:
		return true;
	}

	public LinkedCollection() {
		dummy = new Node<E>();
		count = version = 0;
		
		assert wellFormed() : "invariant failed in constructor";
	}

	@Override
	public boolean add(E x) {
		assert wellFormed() : "invariant broken at start of add()";
		
		Node<E> n = new Node<E>(x,dummy.prev,dummy);
		dummy.prev.next = n;
		dummy.prev = n;
		++count;
		++version;
		
		assert wellFormed() : "invariant brokem at end of add()";
		
		return true;
	}

	@Override
	public void clear() {
		assert wellFormed() : "invariant broken at start of clear()";
		
		if(isEmpty()) return;
		
		dummy.next = dummy.prev = dummy;
		count = 0;
		++version;
		
		assert wellFormed() : "invariant broken at end of clear";
	}

	@Override
	public int size() {
		assert wellFormed() : "invariant broken at start of size()";
		return count;
	}
	
	@Override
	public String toString() {
		return "{Collection of size: " + Integer.toString(size()) + "}";
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(Collection<? extends E> c) {
		assert wellFormed() : "invariant broken at start of addAll()";
		
		boolean added = false;
		
		for(Object o: c.toArray()) {
			if(add((E)o))
			{ 
				//this should always return true unless an exception is thrown
				added = true;
			}
		}
		
		assert wellFormed() : "invariant broken at end of addAll()";
		
		return added;
	}

	@Override
	public boolean contains(Object o) {
		assert wellFormed() : "invariant broken at start of contains()";
		
		Iterator<E> it = iterator();
		if(o == null) {
			while(it.hasNext()) {
				if(it.next() == null) return true;
			}
		}
		else {
			while(it.hasNext()) {
				if(o.equals(it.next())) return true;
			}
		}
		
		return false;
	}		

	@Override
	public boolean containsAll(Collection<?> c) {
		assert wellFormed() : "invariant broken at start of containsAll()";
		
		for(Object e : c) {
			if(!contains(e)) return false;
		}
		
		return true;
	}

	@Override
	public boolean isEmpty() {
		assert wellFormed() : "invariant broken at start of isEmpty()";
		
		return count == 0;
	}

	@Override
	public boolean remove(Object o) {
		assert wellFormed() : "invariant broken at start of remove()";
		
		boolean removed = false;
		Iterator<E> it = iterator();
		while(it.hasNext()) {
			E element = it.next();
			if(o == null) {
				if(element == null) {
					it.remove();
					removed = true;
					break;
				}
			}
			else if(o.equals(element)){
				it.remove();
				removed = true;
				break;
			}
		}
		
		assert wellFormed() : "invariant broken at end of remove()";
		
		return removed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		assert wellFormed() : "invariant broken at start of removeAll()";
		
		boolean modified = false;
		if(this == c) {
			modified = (size() > 0);
			clear();
		}
		else {
			Iterator<?> it = iterator();
			while(it.hasNext()) {
				if(c.contains(it.next())) {
					it.remove();
					modified = true;
				}
			}
		}
		
		assert wellFormed() : "invariant broken at end of removeAll()";
		
		return modified;
	}


	@Override
	public Object[] toArray() {
		assert wellFormed() : "invariant broken at start of toArray()";
		//make easier by using the iterator
		Object[] tArray = new Object[count];
		Iterator<E> it = iterator();
		
		for(int i = 0; i < tArray.length; ++i)
		{
			tArray[i] = it.next();
		}

		assert wellFormed() : "invariant broken at end of toArray()";
		
		return tArray;
	}

	//Java collection requires these two methods,
	//but they do not need to be completed for this assignment
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}


	//The Iterator
	@Override
	public Iterator<E> iterator() {
		assert wellFormed() : "invariant broken at start of iterator()";
		return new MyIterator();
	}


	// You need a nested (not static) class to implement the iterator:
	// Implement constructor and methods.
	// check for invariant in every method
	// check for concurrent modification exception in every method
	// no method should have loops (all constant-time operations).
	private class MyIterator implements Iterator<E> {
		public int myVersion = version;
		public boolean hasCurrent;
		public Node<E> precursor;

		MyIterator(boolean ignored) {} // DO NOT CHANGE THIS

		private boolean wellFormed() {
			// Invariant for recommended fields:

			// 0. The outer invariant holds, and versions match
			if (!LinkedCollection.this.wellFormed()) return false;
			if (myVersion == version) {
				// 1. precursor is never null
				if(precursor == null) return report("precursor is null");
				// 2. precursor is in the list
				boolean foundPC = false;
				for(Node<E> n = dummy.next; n != dummy; n = n.next)
				{
					if(n == precursor) foundPC = true;
				}
				if(!foundPC && precursor != dummy) return report("precursor is not in the list");
				// 3. if precursor is before the dummy, hasCurrent must be false
				if(precursor.next == dummy && hasCurrent == true) return LinkedCollection.this.report("precursor is at the end, there shouldn't be a current");
				// NB: Don't check 1,2 unless the version matches.
			}
			return true;
		}

		public MyIterator() {
			precursor = dummy;
			hasCurrent = false;
			
			assert wellFormed() : "invariant fails in iterator constructor";
		}


		public boolean hasNext() {
			assert wellFormed() : "invariant fails at start of hasNext()";
			
			if(version != myVersion) throw new ConcurrentModificationException("version mismatch");
			
			if(hasCurrent) {
				return precursor.next.next != dummy;
			}
			else {
				return precursor.next != dummy;
			}
			
		}

		public E next() {
			assert wellFormed() : "invariant fails at start of next()";
			
			if(version != myVersion) throw new ConcurrentModificationException("version mismatch");
			if(!hasNext()) throw new NoSuchElementException("there is no next");
			
			if(hasCurrent) {
				precursor = precursor.next;
			}
			
			hasCurrent = true;
			
			assert wellFormed() : "invariant fails at end of next()";
			
			return (E) precursor.next.data;
		}

		public void remove() {
			assert wellFormed() : "invariant fails at start of remove()";
			
			if(version != myVersion) throw new ConcurrentModificationException("version mismatch");
			if(!hasCurrent) throw new IllegalStateException("nothing to remove");
			
			precursor.next.next.prev = precursor;
			precursor.next = precursor.next.next;
			hasCurrent = false;
			--count;
			myVersion = ++version;
			
			assert wellFormed() : "invariant fails at end of remove()";
		}

	}

	public static class TestInvariant extends TestCase {
		private LinkedCollection<String> self;
		protected LinkedCollection<String>.MyIterator iterator;
		String e1 = "e1", e2 = "e2", e3 = "e3", e4 = "e4", e5 = "e5";
		private Node<String> d0, d1, d2;
		private Node<String> n0, n1, n2, n3, n4, n5, n1a, n2a, n3a, n4a, n5a;

		@SuppressWarnings("unchecked")
		protected <T> Node<T> makeDummy() {
			Node<T> n = new Node<T>();
			n.prev = n;
			n.next = n;
			n.data = (T)n;
			return n;
		}

		protected <T> Node<T> makeNode(T data) {
			Node<T> n = new Node<T>();
			n.prev = null;
			n.next = null;
			n.data = data;
			return n;
		}

		@Override
		protected void setUp() {
			self = new LinkedCollection<String>(false);
			iterator = self.new MyIterator(false);
			d0 = makeDummy();
			d1 = makeDummy();
			d2 = makeDummy();
			n0 = makeNode(null);
			n1 = makeNode("one");
			n2 = makeNode("two");
			n3 = makeNode("three");
			n4 = makeNode("four");
			n5 = makeNode("five");
			n1a = makeNode("one");
			n2a = makeNode("two");
			n3a = makeNode("three");
			n4a = makeNode("four");
			n5a = makeNode("five");
		}

		@SuppressWarnings("unchecked")
		private <T> void assignData(Node<T> n, Object data) {
			n.data = (T) data;
		}

		@SafeVarargs
		private final void linkUp(Node<String> dummy, Node<String>... nodes) {
			Node<String> last = dummy;
			for (Node<String> n : nodes) {
				n.prev = last;
				last.next = n;
				last = n;
			}
			dummy.prev = last;
			last.next = dummy;
			self.dummy = dummy;
			self.count = nodes.length;
			assertTrue(self.wellFormed());
		}

		public void test00() {
			assertFalse(self.wellFormed());
			self.dummy = d0;
			self.count = 0;
			d0.prev = null;
			assertFalse(self.wellFormed());
			d0.prev = d0;
			d0.next = null;
			assertFalse(self.wellFormed());
			d0.next = d0;
			d0.data = null;
			assertFalse(self.wellFormed());
			d0.data = "Dummy";
			assertFalse(self.wellFormed());
			self.dummy = d1;
			self.count = 1;
			assertFalse(self.wellFormed());
			self.count = 0;
			assertTrue(self.wellFormed());
		}

		public void test10() {
			linkUp(d0,n0);
			self.count = 0;
			assertFalse(self.wellFormed());
			self.count = -1;
			assertFalse(self.wellFormed());
			self.count = 2;
			assertFalse(self.wellFormed());
		}

		public void test11() {
			linkUp(d1,n1);
			assignData(d1,null);
			assertFalse(self.wellFormed());
			assignData(d1,d0);
			assertFalse(self.wellFormed());
			assignData(d1,n1);
			assertFalse(self.wellFormed());
		}

		public void test12() {
			linkUp(d2,n2);
			d2.next = null;
			assertFalse(self.wellFormed());
			d2.next = d2;
			assertFalse(self.wellFormed());
		}

		public void test13() {
			linkUp(d0,n3);
			n3.next = null;
			assertFalse(self.wellFormed());
			n3.next = d1;
			d1.prev = n3;
			d1.next = n3;
			assertFalse(self.wellFormed());
			n3.next = n3;
			assertFalse(self.wellFormed());
			n3.prev = n3;
			assertFalse(self.wellFormed());
		}

		public void test14() {
			linkUp(d1,n1);
			n1.prev = d0;
			d0.next = n1;
			d0.prev = n1;
			assertFalse(self.wellFormed());
			n1.prev = null;
			assertFalse(self.wellFormed());	
			n1.prev = n1;
			assertFalse(self.wellFormed());	
		}

		public void test15() {
			linkUp(d2,n2);
			d2.prev = null;
			assertFalse(self.wellFormed());
			d2.prev = n2a;
			n2a.next = d2;
			n2a.prev = d2;
			assertFalse(self.wellFormed());
			d2.prev = d2;
			assertFalse(self.wellFormed());
		}

		public void test16() {
			linkUp(d0,n5);
			iterator.precursor = null;
			assertFalse(iterator.wellFormed());
			iterator.precursor = d1;
			d1.next = n5;
			d1.prev = n5;
			assertFalse(iterator.wellFormed());
			iterator.precursor = n5a;
			n5a.next = d0;
			n5a.prev = d0;
			assertFalse(iterator.wellFormed());

			iterator.precursor = n5;
			assertTrue(iterator.wellFormed());
		}

		public void test20() {
			linkUp(d0,n1,n2);
			self.count = 0;
			assertFalse(self.wellFormed());
			self.count = 1;
			assertFalse(self.wellFormed());
			self.count = 3;
			assertFalse(self.wellFormed());
		}

		public void test21() {
			linkUp(d1,n3,n4);
			d1.prev = null;
			assertFalse(self.wellFormed());
			d1.prev = n4a;
			n4a.next = d1;
			n4a.prev = n3;
			assertFalse(self.wellFormed());
			d1.prev = n3;
			assertFalse(self.wellFormed());
			d1.prev = d1;
			assertFalse(self.wellFormed());
		}

		public void test22() {
			linkUp(d2,n5,n0);
			n5.prev = null;
			assertFalse(self.wellFormed());
			n5.prev = n0;
			assertFalse(self.wellFormed());
			n5.prev = n5;
			assertFalse(self.wellFormed());
			n5.prev = d0;
			d0.next = n5;
			d0.prev = n0;
			assertFalse(self.wellFormed());
		}

		public void test23() {
			linkUp(d1,n4,n5);
			n5.prev = null;
			assertFalse(self.wellFormed());
			n5.prev = n4a;
			n4a.next = n5;
			n4a.prev = d1;
			assertFalse(self.wellFormed());
			n5.prev = d1;
			assertFalse(self.wellFormed());
		}

		public void test24() {
			linkUp(d0,n1,n2);
			n1.next = n1;
			assertFalse(self.wellFormed());
			n1.next = null;
			assertFalse(self.wellFormed());
			n1.next = d0;
			assertFalse(self.wellFormed());
		}

		public void test25() {
			linkUp(d2,n0,n1);
			n1.next = n1;
			assertFalse(self.wellFormed());
			n1.next = d0;
			d0.prev = n1;
			d0.next = n0;
			assertFalse(self.wellFormed());
			n1.next = n0;
			assertFalse(self.wellFormed());
		}

		public void test26() {
			linkUp(d0,n2,n3);
			iterator.precursor = d1;
			d1.next = n2;
			d1.prev = n3;
			assertFalse(iterator.wellFormed());
			iterator.precursor = n2a;
			n2a.prev = d0;
			n2a.next = n3;
			assertFalse(iterator.wellFormed());
			iterator.precursor = n3a;
			n3a.prev = n2;
			n3a.next = d0;
			iterator.precursor = null;
			assertFalse(iterator.wellFormed());
		}

		public void test30() {
			linkUp(d1,n2,n3,n4);
			self.count = 2;
			assertFalse(self.wellFormed());
			self.count = -3;
			assertFalse(self.wellFormed());
			self.count = 4;
			assertFalse(self.wellFormed());
		}

		public void test31() {
			linkUp(d2,n5,n4,n3);
			n3.next = n5;
			assertFalse(self.wellFormed());
			n3.next = d0;
			d0.next = n5;
			d0.prev = n3;
			assertFalse(self.wellFormed());
			n3.next = n4;
			assertFalse(self.wellFormed());
		}

		public void test50() {
			linkUp(d0,n1,n2,n3,n4,n5);
			assignData(d0,null);
			assertFalse(self.wellFormed());
		}

		public void test51() {
			linkUp(d0,n1,n2,n3,n4,n5);
			d0.next = n1a;
			n1a.prev = d0;
			n1a.next = n2;
			assertFalse(self.wellFormed());
		}

		public void test52() {
			linkUp(d0,n1,n2,n3,n4,n5);
			n4.prev = n3a;
			n3a.next = n4;
			n3a.prev = n2;
			assertFalse(self.wellFormed());
		}

		public void test53() {
			linkUp(d0, n1, n3);
			assertFalse("null cursor",iterator.wellFormed());
			++self.version;
			assertTrue("version bad",iterator.wellFormed());
			iterator.precursor = self.dummy;
			++iterator.myVersion;
			assertTrue("cursor OK",iterator.wellFormed());
			iterator.precursor = n3;
			iterator.hasCurrent = true;
			assertFalse("cannot remove dummy",iterator.wellFormed());
		}


		public void test54() {
			linkUp(d0, n1, n2);
			n2.prev = self.dummy;
			iterator.precursor = n1;
			assertFalse("outer wrong", iterator.wellFormed());
		}
	}


}
