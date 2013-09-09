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

import com.google.gwt.user.client.ui.Image;

import java.util.List;

/**
 * Preference item describes the single preference, which can have sub-preferences and thus organize tree structure. Preference
 * items are registered as follows:<br>
 * <code>
 * Preferences.get().addPreferenceItem(new SamplePreference());
 * </code>
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 18, 2012 3:24:39 PM anya $
 */
public interface PreferenceItem {
    /**
     * Returns preference's name (title).
     *
     * @return {@link String} preference's name
     */
    String getName();

    /**
     * Sets preference's name (title).
     *
     * @param name
     *         preference's name (title)
     */
    void setName(String name);

    /**
     * Returns image associated with preference.
     *
     * @return {@link Image} preference's image
     */
    Image getImage();

    /**
     * Sets image associated with preference.
     *
     * @param image
     *         preference's image
     */
    void setImage(Image image);

    /**
     * Returns the list of sub preferences.
     *
     * @return {@link List} of {@link PreferenceItem} list of sub preferences
     */
    List<PreferenceItem> getChildren();

    /**
     * Sets the list of sub preferences.
     *
     * @param children
     *         list of sub preferences
     */
    void setChildren(List<PreferenceItem> children);

    /**
     * Returns the preference's performer (which will do defined actions, when preference is called).
     *
     * @return {@link PreferencePerformer} preference's performer
     */
    PreferencePerformer getPreferencePerformer();

    /**
     * Sets the preference's performer (which will do defined actions, when preference is called).
     *
     * @param preferencePerformer
     *         preference performer
     */
    void setPreferencePerformer(PreferencePerformer preferencePerformer);
}
