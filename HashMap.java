import java.util.Iterator;

/**
 * Maps from arbitrary keys to arbitrary values.
 *
 * HashMap implementation that hashes values using
 * linear probing.
 *
 * @param <K> Type for keys.
 * @param <V> Type for values.
 */
public class HashMap<K, V> implements Map<K, V> {

    private static class Entry<K, V> {
        K key;
        V value;

        Entry(K k, V v) {
            this.key = k;
            this.value = v;
        }

        public boolean equals(Object other) {
            return (other instanceof HashMap.Entry)
                && (this.key.equals(((Entry) other).key));
        }

        public int hashCode() {
            return this.key.hashCode();
        }
    }

    private class HashMapIterator<K, V> implements Iterator<K> {
        int cur;
        int n;

        @Override
        public K next() {
            while (HashMap.this.hashTable[this.cur] == null) {
                this.cur++;
            }
            this.n++;
            return (K) HashMap.this.hashTable[this.cur++].key;
        }

        @Override
        public boolean hasNext() {
            return this.n < HashMap.this.size;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private Entry<K, V>[] hashTable;
    private int capacity = 8;
    private int size;

    /**
     * Create an empty map.
     */
    public HashMap() {
        this.hashTable = (Entry<K, V>[]) new Entry[this.capacity];
    }

    /**
     * Find entry for key k
     * @param k key to search
     * @return Entry of k if k exists, null if k does not exist in
     *     hash table
     * @throws IllegalArgumentException if searching null key
     */
    private Entry find(K k) {
        if (k == null) {
            throw new IllegalArgumentException("invalid null key");
        }

        for (int i = this.hash(k); this.hashTable[i] != null;
            i = (i + 1) % this.capacity) {
            if (k.equals(this.hashTable[i].key)) {
                return this.hashTable[i];
            }
        }

        return null;
    }

    /**
     * Finds entry for key k, throws exception if
     * k does not exist.
     * @param k key
     * @return Entry k if k is mapped, exception if not
     */
    private Entry findForSure(K k) {
        Entry e = this.find(k);
        if (e == null) {
            throw new IllegalArgumentException("cannot find key " + k);
        }
        return e;
    }

    /**
     * Calculate and return the load factor.
     * @return load factor in decimal form
     */
    private double loadFactor() {
        return (1.0 * this.size) / (this.capacity);
    }

    /**
     * Double the size of hash tables and rehash.
     * Amortized time.
     */
    private void rehash() {
        Entry[] temp = new Entry[this.capacity * 2];

        this.capacity *= 2;

        for (int i = 0; i < this.capacity / 2; i++) {
            if (this.hashTable[i] != null) {
                Entry e = this.hashTable[i];
                int j = this.hash((K) e.key);
                while (temp[j] != null) {
                    j = (j + 1) % this.capacity;
                }
                temp[j] = e;
            }
        }
        this.hashTable = temp;
    }

    /**
     * Hash a key.
     * @param k key
     * @return hash index within the hash table
     */
    private int hash(K k) {
        return Math.abs(k.hashCode()) % this.capacity;
    }

    @Override
    public void insert(K k, V v) {
        if (this.has(k)) {
            throw new IllegalArgumentException("duplicate key " + k);
        }

        if (this.loadFactor() >= 0.5) {
            this.rehash();
        }

        int i = this.hash(k);
        while (this.hashTable[i] != null) {
            i = (i + 1) % this.capacity;
        }

        this.hashTable[i] = new Entry(k, v);
        this.size++;
    }

    @Override
    public V remove(K k) {
        Entry e = this.findForSure(k);
        V result = (V) e.value;

        for (int i = this.hash(k); this.hashTable[i] != null;
                i = (i + 1) % this.capacity) {
            if (e.key.equals(this.hashTable[i].key)) {
                e.key = null;
                e.value = null;
                this.size--;
                return result;
            }
        }
        return null;
    }

    @Override
    public void put(K k, V v) {
        Entry e = this.findForSure(k);
        e.value = v;
    }

    @Override
    public V get(K k) {
        Entry e = this.findForSure(k);
        return (V) e.value;
    }

    @Override
    public boolean has(K k) {
        if (k == null) {
            return false;
        }
        return this.find(k) != null;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<K> iterator() {
        return new HashMapIterator();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("{");
        for (Entry e : this.hashTable) {
            if (e != null) {
                s.append("" + e.key + ": " + e.value);
            }
        }
        s.deleteCharAt(s.lastIndexOf(","));
        s.deleteCharAt(s.lastIndexOf(" "));
        s.append("}");
        return s.toString();
    }
}
