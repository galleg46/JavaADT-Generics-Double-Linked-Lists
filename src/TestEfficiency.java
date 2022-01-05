import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import edu.uwm.cs351.LinkedCollection;
import junit.framework.TestCase;


public class TestEfficiency extends TestCase {
	Collection<String> cs;
	Random r;
	
	@Override
	public void setUp() {
		cs = new LinkedCollection<String>();
		r = new Random();
		try {
			assert cs.iterator().next() == "OK";
			assertTrue(true);
		} catch (NoSuchElementException ex) {
			System.err.println("You must disable assertions to run this test.");
			System.err.println("Go to Run > Run Configurations. Select the 'Arguments' tab");
			System.err.println("Then remove '-ea' from the VM Arguments box.");
			assertFalse("Assertions must NOT be enabled while running efficiency tests.",true);
		}
	}

	private static final int MAX_LENGTH = 1000000;
	private static final int SAMPLE = 100;
	
	public void testLong() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			cs.add(i+"");
		}
		
		int sum = 0;
		Iterator<String> it = cs.iterator();
		for (int j=0; j < SAMPLE; ++j) {
			int n = r.nextInt(MAX_LENGTH/SAMPLE);
			for (int i=0; i < n; ++i) {
				it.next();
			}
			sum += n;
			assertEquals(sum+"",it.next());
			++sum;
		}
	}
	
	private static final int MAX_WIDTH = 100000;
	
	public void testWide() {
		@SuppressWarnings("unchecked")
		Collection<String>[] a = new Collection[MAX_WIDTH];
		for (int i=0; i < MAX_WIDTH; ++i) {
			a[i] = cs = new LinkedCollection<String>();
			int n = r.nextInt(SAMPLE);
			for (int j=0; j < n; ++j) {
				cs.add(j+"");
			}
		}
		
		for (int j = 0; j < SAMPLE; ++j) {
			int i = r.nextInt(a.length);
			cs = a[i];
			if (cs.size() == 0) continue;
			int n = r.nextInt(cs.size());
			Iterator<String> it = cs.iterator();
			for (int k=0; k < n; ++k) {
				it.next();
			}
			assertEquals(n+"",it.next());
		}
	}
	
	public void testStochastic() {
		List<Collection<String>> ss = new ArrayList<Collection<String>>();
		ss.add(cs);
		int max = 1;
		for (int i=0; i < MAX_LENGTH; ++i) {
			if (r.nextBoolean()) {
				cs = new LinkedCollection<String>();
				cs.add("Hello");
				ss.add(cs);
			} else {
				cs.addAll(new ArrayList<String>(cs)); // double size of s
				if (cs.size() > max) {
					max = cs.size();
					// System.out.println("Reached " + max);
				}
			}
		}
	}
	
	public void testMiddle() {
		// this would be too slow with dynamic arrays
		for (int i=0; i < MAX_LENGTH; ++i) {
			cs.add(i+"");
		}
		
		Iterator<String> it = cs.iterator();
		int removed = 0;
		for (int i=0; i < MAX_LENGTH; ++i) {
			it.next();
			if (r.nextBoolean()) {
				it.remove();
				++removed;
			}
		}
		
		assertEquals(MAX_LENGTH-removed,cs.size());
	}
	
	public void testAll() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			cs.add(i+"");
		}
		cs.addAll(cs);
		cs.removeAll(cs);
		Collection<String> cs2 = new LinkedCollection<String>();
		for (int i=0; i < MAX_LENGTH; ++i) {
			cs.add(i+"");
		}
		for (int i=0; i < SAMPLE; ++i) {
			cs2.add(i+"a");
		}
		cs.removeAll(cs2);
		cs.addAll(cs2);
		cs.removeAll(cs2);
	}
}
