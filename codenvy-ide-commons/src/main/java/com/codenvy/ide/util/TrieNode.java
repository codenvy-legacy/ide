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

/**
 * A node in a trie that can be used for efficient autocompletion lookup.
 *
 * @param <T>
 *         value object type
 */
public final class TrieNode<T> {
    private final String prefix;

    private final JsonArray<TrieNode<T>> children;

    private T value;

    private TrieNode(String prefix) {
        this.prefix = prefix;
        this.value = null;
        this.children = JsonCollections.createArray();
    }

    public static <T> TrieNode<T> makeNode(String prefix) {
        return new TrieNode<T>(prefix);
    }

    public JsonArray<TrieNode<T>> getChildren() {
        return children;
    }

    TrieNode<T> findInsertionBranch(String prefix) {
        for (int i = 0, size = children.size(); i < size; i++) {
            TrieNode<T> child = children.get(i);
            if (prefix.startsWith(child.getPrefix())) {
                return child;
            }
        }
        return null;
    }

    public void addChild(TrieNode<T> child) {
        children.add(child);
    }

    public boolean getIsLeaf() {
        return this.value != null;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.prefix;
    }
}
