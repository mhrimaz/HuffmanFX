package huffmanfx;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mhrimaz
 */
public class Huffman {

    private Node root;
    private final Map<Character, Long> characterFreqMap;
    private final Map<Character, String> characterEncodeMap;

    /**
     * Eager constructor huffman encoder
     * @param characterFreqMap Map of characters frequency
     */
    public Huffman(Map<Character, Long> characterFreqMap) {
        this.characterFreqMap = characterFreqMap;
        characterEncodeMap = new HashMap<>();
        buildTree();
        buildCode(root, "");
    }
    /**
     * 
     * @return map of characters encoded value as string
     */
    public Map<Character, String> getCharacterEncodeMap() {
        return characterEncodeMap;
    }

    /**
     * Recursively build encode code for each character and put it to the map
     * @param x
     * @param s 
     */
    private void buildCode(Node x, String s) {
        if (!x.isLeaf()) {
            if (x.left != null) {
                buildCode(x.left, s + '0');
            }
            if (x.right != null) {
                buildCode(x.right, s + '1');
            }
        } else {
            if (x == root) {
                s += '0';
            }
            characterEncodeMap.put(x.getCharacter(), s);
        }
    }

    /**
     * characters map frequency
     * @return 
     */
    public Map<Character, Long> getCharacterFreqMap() {
        return characterFreqMap;
    }

    /**
     * Build Huffman Tree with Fibonacci Heap
     */
    private void buildTree() {
        FibonacciHeap<Node> priorityQueue = new FibonacciHeap<>();
        characterFreqMap.entrySet().forEach((entry) -> {
            priorityQueue.enqueue(new Node(entry.getKey(), entry.getValue(), null, null), entry.getValue());
        });

        while (priorityQueue.size() > 1) {
            Node left = priorityQueue.dequeueMin().getValue();
            Node right = priorityQueue.dequeueMin().getValue();
            Node parent = new Node('\0', left.getFreq() + right.getFreq(), left, right);
            priorityQueue.enqueue(parent, parent.getFreq());
        }
        root = priorityQueue.dequeueMin().getValue();
    }

    private class Node implements Comparable<Node> {

        /**
         *
         * @return character stored in this node
         */
        public char getCharacter() {
            return ch;
        }

        /**
         *
         * @return frequency of this node
         */
        public long getFreq() {
            return freq;
        }

        private final char ch;
        private final long freq;
        private final Node left, right;

        /**
         * constructor of Node
         *
         * @param ch character to store in this node
         * @param freq frequency of this node
         * @param left left child of this node
         * @param right right child of this node
         */
        Node(char ch, long freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        /**
         * check whether a node is leaf or not
         *
         * @return return true if this node is a leaf node
         */
        private boolean isLeaf() {
            return (left == null) && (right == null);
        }

        /**
         * compare a node with another node based on their frequencies
         *
         * @param that another node to compare to
         * @return compare using Long.compare method
         * @see Long#compare(long, long)
         */
        @Override
        public int compareTo(Node that) {
            return Long.compare(this.freq, that.freq);
        }
    }

}
