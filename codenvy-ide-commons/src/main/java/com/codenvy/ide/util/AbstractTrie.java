/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */

package com.codenvy.ide.util;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.runtime.Assert;

/**
 * Trie-based implementation of the prefix index.
 *
 * @param <T>
 *         value object type
 */
public class AbstractTrie<T> implements PrefixIndex<T> {

    // TODO: This member should be static and unmodifiable.
    private final JsonArray<T> emptyList = JsonCollections.createArray();

    private TrieNode<T> root;

    public AbstractTrie() {
        clear();
    }

    public TrieNode<T> getRoot() {
        return root;
    }

    public void clear() {
        this.root = TrieNode.<T>makeNode("");
    }

    /** @return {@code true} is this trie is empty */
    public boolean isEmpty() {
        return this.root.getChildren().isEmpty();
    }

    public TrieNode<T> put(String key, T value) {
        return insertIntoTrie(key, root, value);
    }

    @Override
    public JsonArray<T> search(String prefix) {
        TrieNode<T> searchRoot = findNode(prefix, root);
        return (searchRoot == null) ? emptyList : collectSubtree(searchRoot);
    }

    /**
     * Returns all leaf nodes from a subtree rooted by {@code searchRoot} node
     * and restricted with a {@code stopFunction}
     *
     * @param searchRoot
     *         node to start from
     * @return all leaf nodes in the matching subtree
     */
    public static <T> JsonArray<T> collectSubtree(TrieNode<T> searchRoot) {
        JsonArray<TrieNode<T>> leaves = JsonCollections.createArray();
        getAllLeavesInSubtree(searchRoot, leaves);
        JsonArray<T> result = JsonCollections.createArray();
        for (int i = 0; i < leaves.size(); i++) {
            result.add(leaves.get(i).getValue());
        }
        return result;
    }

    /**
     * Traverses the subtree rooted at {@code root} and collects all nodes in the
     * subtree that are leaves (i.e., valid elements, not only prefixes).
     *
     * @param root
     *         a node in the trie from which to start collecting
     * @param leaves
     *         output array
     */
    private static <T> void getAllLeavesInSubtree(TrieNode<T> root, JsonArray<TrieNode<T>> leaves) {
        if (root.getIsLeaf()) {
            leaves.add(root);
        }

        for (int i = 0, size = root.getChildren().size(); i < size; i++) {
            TrieNode<T> child = root.getChildren().get(i);
            getAllLeavesInSubtree(child, leaves);
        }
    }

    private TrieNode<T> insertIntoTrie(String prefix, TrieNode<T> node, T value) {
        String nodePrefix = node.getPrefix();
        if (nodePrefix.equals(prefix)) {
            node.setValue(value);
            return node;
        } else {
            TrieNode<T> branch = node.findInsertionBranch(prefix);
            if (branch != null) {
                return insertIntoTrie(prefix, branch, value);
            } else {
                // create new trie nodes
                JsonArray<TrieNode<T>> suffixChain = makeSuffixChain(node, prefix.substring(nodePrefix.length()), value);
                return suffixChain.peek();
            }
        }
    }

    /**
     * Inserts a chain of children into the given node.
     *
     * @param root
     *         node to insert into
     * @param suffix
     *         suffix of the last node in the chain
     * @param value
     *         value of the last node in the chain
     * @return the inserted chain in direct order (from the root to the leaf)
     */
    JsonArray<TrieNode<T>> makeSuffixChain(TrieNode<T> root, String suffix, T value) {
        JsonArray<TrieNode<T>> result = JsonCollections.createArray();
        String rootPrefix = root.getPrefix();
        for (int i = 1, suffixSize = suffix.length(); i <= suffixSize; i++) {
            String newPrefix = rootPrefix + suffix.substring(0, i);
            TrieNode<T> newNode = TrieNode.makeNode(newPrefix);
            result.add(newNode);
            root.addChild(newNode);
            root = newNode;
        }
        root.setValue(value);
        return result;
    }

    /**
     * Searches the subtree rooted at {@code searchRoot} for a node
     * corresponding to the prefix.
     * <p/>
     * There can only ever be one such node, or zero. If no node is found, returns
     * null.
     * <p/>
     * Note that the {@code prefix} is relative to the whole trie root, not
     * to the {@code searchRoot}.
     *
     * @param prefix
     *         the prefix to be found
     * @param searchRoot
     *         the root of the subtree that is searched
     * @return the node in the tree corresponding to prefix, or null if no such
     *         node exists
     */
    public static <T> TrieNode<T> findNode(String prefix, TrieNode<T> searchRoot) {
        Assert.isNotNull(prefix);
        if (prefix.equals(searchRoot.getPrefix())) {
            return searchRoot;
        }
        TrieNode<T> closestAncestor = findClosestAncestor(prefix, searchRoot);
        return (closestAncestor.getPrefix().equals(prefix)) ? closestAncestor : null;
    }

    /**
     * Finds the closest ancestor of a search key. Formally it returns
     * a node x, such that: {@code prefix.startsWith(x.prefix)}
     * and there is no other node y, such that
     * {@code (prefix.startsWith(y.prefix) and y.prefix.length > x.prefix)}
     *
     * @param key
     *         search key
     * @param searchRoot
     *         node to start from
     * @return closest ancestor
     */
    static <T> TrieNode<T> findClosestAncestor(String key, TrieNode<T> searchRoot) {
        Assert.isNotNull(key);
        Assert.isLegal(key.startsWith(searchRoot.getPrefix()), "key=" + key + " root prefix=" + searchRoot.getPrefix());

        TrieNode<T> result = searchRoot;
        for (TrieNode<T> child = searchRoot; child != null; child = child.findInsertionBranch(key)) {
            result = child;
        }
        return result;
    }
}
