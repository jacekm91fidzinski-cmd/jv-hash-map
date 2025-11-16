package core.basesyntax;

import java.util.Objects;

public class MyHashMap<K, V> implements MyMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Node<K, V>[] table;
    private int size;
    private int threshold;
    private final float loadFactor;

    public MyHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0) {
            initialCapacity = DEFAULT_INITIAL_CAPACITY;
        }
        if (loadFactor <= 0) {
            loadFactor = DEFAULT_LOAD_FACTOR;
        }
        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        this.loadFactor = loadFactor;
        this.table = newNodeArray(capacity);
        this.threshold = (int) (capacity * loadFactor);
        this.size = 0;
    }

    @Override
    public void put(K key, V value) {
        int index = indexFor(hash(key), table.length);
        Node<K, V> current = table[index];
        if (current == null) {
            table[index] = new Node<>(key, value, null);
            size++;
        } else {
            Node<K, V> node = current;
            while (true) {
                if (keysEqual(node.getKey(), key)) {
                    node.setValue(value);
                    return;
                }
                if (node.getNext() == null) {
                    node.setNext(new Node<>(key, value, null));
                    size++;
                    break;
                }
                node = node.getNext();
            }
        }
        if (size > threshold) {
            resize();
        }
    }

    @Override
    public V getValue(K key) {
        int index = indexFor(hash(key), table.length);
        Node<K, V> node = table[index];
        while (node != null) {
            if (keysEqual(node.getKey(), key)) {
                return node.getValue();
            }
            node = node.getNext();
        }
        return null;
    }

    @Override
    public int getSize() {
        return size;
    }

    /* ------------------ private helpers (after public) ------------------ */

    private static final class Node<K, V> {
        private final K key; // może być final
        private V value;
        private Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Node<K, V> getNext() {
            return next;
        }

        public void setNext(Node<K, V> next) {
            this.next = next;
        }
    }

    private int hash(K key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    private boolean keysEqual(K k1, K k2) {
        return Objects.equals(k1, k2);
    }

    private void resize() {
        Node<K, V>[] oldTable = table;
        int newCapacity = oldTable.length << 1;
        Node<K, V>[] newTable = newNodeArray(newCapacity);
        for (Node<K, V> head : oldTable) { // enhanced for zamiast tradycyjnego for
            Node<K, V> node = head;
            while (node != null) {
                Node<K, V> nextNode = node.getNext();
                int newIndex = indexFor(hash(node.getKey()), newCapacity);
                node.setNext(newTable[newIndex]);
                newTable[newIndex] = node;
                node = nextNode;
            }
        }
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
    }

    @SuppressWarnings("unchecked")
    private Node<K, V>[] newNodeArray(int capacity) {
        return (Node<K, V>[]) new Node[capacity];
    }
}
