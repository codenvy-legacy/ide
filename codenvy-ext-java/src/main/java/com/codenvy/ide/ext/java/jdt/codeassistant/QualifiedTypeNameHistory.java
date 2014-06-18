/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.codeassistant;

import com.codenvy.ide.ext.java.worker.Preferences;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:26:43 PM Mar 29, 2012 evgen $
 */
public class QualifiedTypeNameHistory {
    private static QualifiedTypeNameHistory instance;

    private final Map<Object, Object> fHistory;

    private final HashMap<Object, Integer> fPositions;

    private static final int MAX_HISTORY_SIZE = 60;

    private Preferences preferences;

    private String key;

    /**
     *
     */
    public QualifiedTypeNameHistory() {
        fHistory = new LinkedHashMap<Object, Object>(80, 0.75f, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                return size() > MAX_HISTORY_SIZE;
            }
        };
        fPositions = new HashMap<Object, Integer>(MAX_HISTORY_SIZE);
        this.preferences = new Preferences();
        this.key = Preferences.QUALIFIED_TYPE_NAMEHISTORY;
        load();
    }

    /**
     *
     */
    public void load() {
        String string = preferences.getString(key);
        if (string == null)
            return;

        JSONArray jsonArray = JSONParser.parseLenient(string).isArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            String key = jsonArray.get(i).isString().stringValue();
            fHistory.put(key, key);
        }
        rebuildPositions();
    }

    public void save() {
        JSONArray array = new JSONArray();
        int i = 0;
        for (Object o : fHistory.values()) {
            array.set(i, new JSONString(o.toString()));
            i++;
        }
        preferences.setValue(key, array.toString());
    }

    /** @return  */
    public static QualifiedTypeNameHistory getDefault() {
        if (instance == null)
            instance = new QualifiedTypeNameHistory();

        return instance;
    }

    public static int getBoost(String fullyQualifiedTypeName, int min, int max) {
        float position = getDefault().getNormalizedPosition(fullyQualifiedTypeName);
        int dist = max - min;
        return Math.round(position * dist) + min;
    }

    /**
     * Normalized position in history of object denoted by key. The position is a value between zero and one where zero means not
     * contained in history and one means newest element in history. The lower the value the older the element.
     *
     * @param key
     *         The key of the object to inspect
     * @return value in [0.0, 1.0] the lower the older the element
     */
    public float getNormalizedPosition(String key) {
        if (!fHistory.containsKey(key))
            return 0.0f;

        int pos = fPositions.get(key).intValue() + 1;

        // containsKey(key) implies fHistory.size()>0
        return (float)pos / (float)fHistory.size();
    }

    public static void remember(String fullyQualifiedTypeName) {
        getDefault().accessed(fullyQualifiedTypeName);
    }

    /** @param fullyQualifiedTypeName */
    private void accessed(String fullyQualifiedTypeName) {
        fHistory.put(fullyQualifiedTypeName, fullyQualifiedTypeName);
        rebuildPositions();
    }

    private void rebuildPositions() {
        fPositions.clear();
        Collection<Object> values = fHistory.values();
        int pos = 0;
        for (Iterator<Object> iter = values.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            fPositions.put(element, new Integer(pos));
            pos++;
        }
    }
}
