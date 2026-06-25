package com.zachery.customcalendar;

import java.util.ArrayList;
import java.util.List;

class BTreeNode {
    String[] keys;
    BTreeNode[] children;
    int numKeys;
    boolean isLeaf;

    public BTreeNode(int t, boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new String[2 * t - 1];
        this.children = new BTreeNode[2 * t];
        this.numKeys = 0;
    }
}

class BTree {
    private BTreeNode root;
    private final int t;

    public BTree(int t) {
        this.root = null;
        this.t = t;
    }

    // <---- SEARCH ----> 
    // Returns the full entry string if found, null if not
    public String search(String displayName) {
        return searchNode(root, displayName);
    }

    private String searchNode(BTreeNode node, String name) {
        if (node == null) return null;

        int i = 0;
        // Walk right while the target is greater than current key's display name
        while (i < node.numKeys && name.compareTo(getDisplayName(node.keys[i])) > 0)
            i++;

        // Exact match found
        if (i < node.numKeys && name.equals(getDisplayName(node.keys[i])))
            return node.keys[i];

        if (node.isLeaf) return null;
        return searchNode(node.children[i], name);
    }

    // <---- INSERT ---->
    public void insert(String entry) {
        if (root == null) {
            root = new BTreeNode(t, true);
            root.keys[0] = entry;
            root.numKeys = 1;
            return;
        }

        // If root is full, split it first (this is what keeps the tree balanced)
        if (root.numKeys == 2 * t - 1) {
            BTreeNode newRoot = new BTreeNode(t, false);
            newRoot.children[0] = root;
            splitChild(newRoot, 0, root);
            root = newRoot;
        }

        insertNonFull(root, entry);
    }

    private void insertNonFull(BTreeNode node, String entry) {
        int i = node.numKeys - 1;
        String name = getDisplayName(entry);

        if (node.isLeaf) {
            // Shift keys right to make room, then insert in sorted order
            while (i >= 0 && name.compareTo(getDisplayName(node.keys[i])) < 0) {
                node.keys[i + 1] = node.keys[i];
                i--;
            }
            node.keys[i + 1] = entry;
            node.numKeys++;
        } else {
            // Find which child to descend into
            while (i >= 0 && name.compareTo(getDisplayName(node.keys[i])) < 0)
                i--;
            i++;

            // Split the child if it's full before descending
            if (node.children[i].numKeys == 2 * t - 1) {
                splitChild(node, i, node.children[i]);
                if (name.compareTo(getDisplayName(node.keys[i])) > 0)
                    i++;
            }
            insertNonFull(node.children[i], entry);
        }
    }

    private void splitChild(BTreeNode parent, int i, BTreeNode fullChild) {
        BTreeNode newChild = new BTreeNode(t, fullChild.isLeaf);
        newChild.numKeys = t - 1;

        // Copy the right half of fullChild's keys into newChild
        for (int j = 0; j < t - 1; j++)
            newChild.keys[j] = fullChild.keys[j + t];

        // Copy the right half of children too (if not a leaf)
        if (!fullChild.isLeaf)
            for (int j = 0; j < t; j++)
                newChild.children[j] = fullChild.children[j + t];

        fullChild.numKeys = t - 1;

        // Shift parent's children right to insert newChild
        for (int j = parent.numKeys; j >= i + 1; j--)
            parent.children[j + 1] = parent.children[j];
        parent.children[i + 1] = newChild;

        // Promote the middle key up to the parent
        for (int j = parent.numKeys - 1; j >= i; j--)
            parent.keys[j + 1] = parent.keys[j];
        parent.keys[i] = fullChild.keys[t - 1];
        parent.numKeys++;
    }

    // <---- DELETE ---->
    public void delete(String displayName) {
        if (root == null) return;
        deleteFromNode(root, displayName);

        // If root became empty after deletion, shrink the tree
        if (root.numKeys == 0)
            root = root.isLeaf ? null : root.children[0];
    }

    private void deleteFromNode(BTreeNode node, String name) {
        int i = findKeyIndex(node, name);

        if (i < node.numKeys && name.equals(getDisplayName(node.keys[i]))) {
            // Key is in this node
            if (node.isLeaf)
                removeFromLeaf(node, i);
            else
                removeFromInternalNode(node, i);
        } else {
            if (node.isLeaf) return; // Not found

            boolean isLastChild = (i == node.numKeys);
            if (node.children[i].numKeys < t)
                fill(node, i); // Ensure child has enough keys before descending

            // After fill, the child positions may have shifted
            if (isLastChild && i > node.numKeys)
                deleteFromNode(node.children[i - 1], name);
            else
                deleteFromNode(node.children[i], name);
        }
    }

    private int findKeyIndex(BTreeNode node, String name) {
        int i = 0;
        while (i < node.numKeys && name.compareTo(getDisplayName(node.keys[i])) > 0)
            i++;
        return i;
    }

    private void removeFromLeaf(BTreeNode node, int i) {
        for (int j = i + 1; j < node.numKeys; j++)
            node.keys[j - 1] = node.keys[j];
        node.numKeys--;
    }

    private void removeFromInternalNode(BTreeNode node, int i) {
        String key = node.keys[i];

        if (node.children[i].numKeys >= t) {
            // Replace with in-order predecessor
            String pred = getPredecessor(node, i);
            node.keys[i] = pred;
            deleteFromNode(node.children[i], getDisplayName(pred));
        } else if (node.children[i + 1].numKeys >= t) {
            // Replace with in-order successor
            String succ = getSuccessor(node, i);
            node.keys[i] = succ;
            deleteFromNode(node.children[i + 1], getDisplayName(succ));
        } else {
            // Merge children[i] and children[i+1], then delete from merged node
            mergeChildren(node, i);
            deleteFromNode(node.children[i], getDisplayName(key));
        }
    }

    private String getPredecessor(BTreeNode node, int i) {
        BTreeNode cur = node.children[i];
        while (!cur.isLeaf) cur = cur.children[cur.numKeys];
        return cur.keys[cur.numKeys - 1];
    }

    private String getSuccessor(BTreeNode node, int i) {
        BTreeNode cur = node.children[i + 1];
        while (!cur.isLeaf) cur = cur.children[0];
        return cur.keys[0];
    }

    private void fill(BTreeNode node, int i) {
        if (i != 0 && node.children[i - 1].numKeys >= t)
            borrowFromPrev(node, i);
        else if (i != node.numKeys && node.children[i + 1].numKeys >= t)
            borrowFromNext(node, i);
        else {
            if (i != node.numKeys) mergeChildren(node, i);
            else mergeChildren(node, i - 1);
        }
    }

    private void borrowFromPrev(BTreeNode node, int i) {
        BTreeNode child = node.children[i];
        BTreeNode sibling = node.children[i - 1];

        // Shift child's keys right
        for (int j = child.numKeys - 1; j >= 0; j--)
            child.keys[j + 1] = child.keys[j];
        if (!child.isLeaf)
            for (int j = child.numKeys; j >= 0; j--)
                child.children[j + 1] = child.children[j];

        child.keys[0] = node.keys[i - 1];
        if (!child.isLeaf)
            child.children[0] = sibling.children[sibling.numKeys];

        node.keys[i - 1] = sibling.keys[sibling.numKeys - 1];
        child.numKeys++;
        sibling.numKeys--;
    }

    private void borrowFromNext(BTreeNode node, int i) {
        BTreeNode child = node.children[i];
        BTreeNode sibling = node.children[i + 1];

        child.keys[child.numKeys] = node.keys[i];
        if (!child.isLeaf)
            child.children[child.numKeys + 1] = sibling.children[0];

        node.keys[i] = sibling.keys[0];
        for (int j = 1; j < sibling.numKeys; j++)
            sibling.keys[j - 1] = sibling.keys[j];
        if (!sibling.isLeaf)
            for (int j = 1; j <= sibling.numKeys; j++)
                sibling.children[j - 1] = sibling.children[j];

        child.numKeys++;
        sibling.numKeys--;
    }

    private void mergeChildren(BTreeNode node, int i) {
        BTreeNode left = node.children[i];
        BTreeNode right = node.children[i + 1];

        // Pull the separator key down from parent into left
        left.keys[t - 1] = node.keys[i];

        // Copy right's keys and children into left
        for (int j = 0; j < right.numKeys; j++)
            left.keys[j + t] = right.keys[j];
        if (!left.isLeaf)
            for (int j = 0; j <= right.numKeys; j++)
                left.children[j + t] = right.children[j];

        left.numKeys += right.numKeys + 1;

        // Remove separator key and right child pointer from parent
        for (int j = i + 1; j < node.numKeys; j++)
            node.keys[j - 1] = node.keys[j];
        for (int j = i + 2; j <= node.numKeys; j++)
            node.children[j - 1] = node.children[j];

        node.numKeys--;
    }

    // Collect all entries in sorted order (in-order traversal)
    public List<String> getAllEntries() {
        List<String> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(BTreeNode node, List<String> result) {
        if (node == null) return;
        for (int i = 0; i < node.numKeys; i++) {
            if (!node.isLeaf) inOrder(node.children[i], result);
            result.add(node.keys[i]);
        }
        if (!node.isLeaf) inOrder(node.children[node.numKeys], result);
    }

    private String getDisplayName(String entry) {
        if (entry == null) return "";
        String[] parts = entry.split("\\|&", 2);
        return parts[0];
    }
}