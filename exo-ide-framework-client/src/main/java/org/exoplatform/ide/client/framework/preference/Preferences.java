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
package org.exoplatform.ide.client.framework.preference;

import java.util.ArrayList;
import java.util.List;

/**
 * Is used to preferences registration. Add preferences for any extension: <br>
 * <code>
 * Preferences.get().addPreferenceItem(new SamplePreference());
 * </code>
 * <p/>
 * Get registered preferences:<br>
 * <code>
 * Preferences.get().getPreferences();
 * </code>
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 18, 2012 3:33:38 PM anya $
 */
public class Preferences {
    /** List of registered preferences. */
    private List<PreferenceItem> preferenceItems;

    /** Instance. */
    private static Preferences instance;

    protected Preferences() {
        instance = this;
        preferenceItems = new ArrayList<PreferenceItem>();
    }

    /** @return {@link Preferences} */
    public static Preferences get() {
        if (instance == null) {
            instance = new Preferences();
        }
        return instance;
    }

    /** @return {@link List} of {@link PreferenceItem} */
    public List<PreferenceItem> getPreferences() {
        return preferenceItems;
    }

    /**
     * Registers preference item.
     *
     * @param preferenceItem
     */
    public void addPreferenceItem(PreferenceItem preferenceItem) {
        getPreferences().add(preferenceItem);
    }
}
