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
package org.exoplatform.ide.shell.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONString;

import java.util.ArrayList;

/**
 * Buffer of the shell commands.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 4, 2011 10:14:20 AM anya $
 */
public class ShellComandBuffer extends ArrayList<String> {
    private final int MAX_SIZE = 500;

    private static final long serialVersionUID = 1L;

    private int iterator = -1;

    /** @see java.util.ArrayList#add(java.lang.Object) */
    @Override
    public boolean add(String e) {
        if (MAX_SIZE == size()) {
            remove(0);
        }
        if (size() > 0 && e.equals(get(size() - 1))) {
            resetIterator();
            return false;
        }

        boolean result = super.add(e);
        resetIterator();
        return result;
    }

    /** Reset the list iterator. */
    public void resetIterator() {
        iterator = size();
    }

    /**
     * Get the next value upper in the list.
     *
     * @return {@link String}
     */
    public String goUp() {
        if (iterator <= 0)
            return null;
        iterator--;
        return get(iterator);
    }

    /**
     * Get the next value down the list.
     *
     * @return {@link String}
     */
    public String goDown() {
        if (iterator < 0 || iterator == size() - 1) {
            resetIterator();
            return null;
        }
        if (iterator < size() - 1) {
            iterator++;
        }
        return get(iterator);
    }

    /**
     * Transform buffer to JSON Array
     *
     * @return String of JSON Array
     */
    public String toJSON() {
        JSONArray a = new JSONArray();
        for (int i = 0; i < size(); i++) {
            a.set(i, new JSONString(get(i)));
        }
        return a.toString();
    }

    /**
     * Init buffer from JSON Array
     *
     * @param array
     */
    public void init(JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            add(array.get(i).isString().stringValue());
        }
    }

}
