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
package com.codenvy.ide.api.ui.action;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.UIUtil;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * The presentation of an action in a specific place in the user interface.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public final class Presentation {
    private StringMap<Object> userMap;
    /**
     * Defines tool tip for button at tool bar or text for element at menu
     * value: String
     */
    public static final String PROP_TEXT        = "text";
    /** value: String */
    public static final String PROP_DESCRIPTION = "description";
    /** value: Icon */
    public static final String PROP_ICON        = "icon";
    /** value: Boolean */
    public static final String PROP_VISIBLE     = "visible";
    /** The actual value is a Boolean. */
    public static final String PROP_ENABLED     = "enabled";
    public static final int    DEFAULT_WEIGHT   = 0;
    public static final int    HIGHER_WEIGHT    = 42;
    private ListenerManager<PropertyChangeListener> myChangeSupport;
    private String                                  myText;
    private String                                  myDescription;
    private ImageResource                           myIcon;
    private SVGResource                             mySVGIcon;
    private int                                     myMnemonic;
    private int myDisplayedMnemonicIndex = -1;
    private boolean myVisible;
    private boolean myEnabled;
    private int myWeight = DEFAULT_WEIGHT;

    public Presentation() {
        myChangeSupport = ListenerManager.create();
        myVisible = true;
        myEnabled = true;
    }

    public Presentation(final String text) {
        this();
        myText = text;
    }

    public static String restoreTextWithMnemonic(String text, final int mnemonic) {
        if (text == null) {
            return null;
        }
        for (int i = 0; i < text.length(); i++) {
            if (Character.toUpperCase(text.charAt(i)) == mnemonic) {
                return text.substring(0, i) + "_" + text.substring(i);
            }
        }
        return text;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        myChangeSupport.add(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        myChangeSupport.remove(l);
    }

    public String getText() {
        return myText;
    }

    public void setText(String text) {
        setText(text, true);
    }

    public void setText(String text, boolean mayContainMnemonic) {
        int oldMnemonic = myMnemonic;
        int oldDisplayedMnemonicIndex = myDisplayedMnemonicIndex;
        String oldText = myText;
        myMnemonic = 0;
        myDisplayedMnemonicIndex = -1;

        if (text != null) {
            if (text.indexOf(UIUtil.MNEMONIC) >= 0) {
                text = text.replace(UIUtil.MNEMONIC, '&');
            }

            if (mayContainMnemonic) {
                StringBuilder plainText = new StringBuilder();
                for (int i = 0; i < text.length(); i++) {
                    char ch = text.charAt(i);
                    if (myMnemonic == 0 && (ch == '_' || ch == '&')) {
                        //noinspection AssignmentToForLoopParameter
                        i++;
                        if (i >= text.length()) break;
                        ch = text.charAt(i);
                        if (ch != '_' && ch != '&') {
                            if (/*UISettings.getInstance().DISABLE_MNEMONICS_IN_CONTROLS*/ false) {
                                myMnemonic = 0;
                                myDisplayedMnemonicIndex = -1;
                            } else {
                                myMnemonic = Character.toUpperCase(ch);  // mnemonics are case insensitive
                                myDisplayedMnemonicIndex = i - 1;
                            }
                        }
                    }
                    plainText.append(ch);
                }
                myText = plainText.length() == 0 ? "" : plainText.toString();
            } else {
                myText = text.isEmpty() ? "" : text;
            }
        } else {
            myText = null;
        }

        firePropertyChange(PROP_TEXT, oldText, myText);
    }

    public String getTextWithMnemonic() {
        if (myText != null && myDisplayedMnemonicIndex > -1) {
            return myText.substring(0, myDisplayedMnemonicIndex) + "_" + myText.substring(myDisplayedMnemonicIndex);
        }
        return myText;
    }

    public void restoreTextWithMnemonic(Presentation presentation) {
        setText(presentation.getTextWithMnemonic());
    }

    public String getDescription() {
        return myDescription;
    }

    public void setDescription(String description) {
        String oldDescription = myDescription;
        myDescription = description;
        firePropertyChange(PROP_DESCRIPTION, oldDescription, myDescription);
    }

    public ImageResource getIcon() {
        return myIcon;
    }
    
    public SVGResource getSVGIcon() {
        return mySVGIcon;
    }

    public void setIcon(ImageResource icon) {
        ImageResource oldIcon = myIcon;
        myIcon = icon;
        firePropertyChange(PROP_ICON, oldIcon, myIcon);
    }
    
    public void setSVGIcon(SVGResource icon) {
        SVGResource oldIcon = mySVGIcon;
        mySVGIcon = icon;
        firePropertyChange(PROP_ICON, oldIcon, mySVGIcon);
    }

    public boolean isVisible() {
        return myVisible;
    }

    public void setVisible(boolean visible) {
        boolean oldVisible = myVisible;
        myVisible = visible;
        firePropertyChange(PROP_VISIBLE, oldVisible ? Boolean.TRUE : Boolean.FALSE, myVisible ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns the state of this action.
     *
     * @return <code>true</code> if action is enabled, <code>false</code> otherwise
     */
    public boolean isEnabled() {
        return myEnabled;
    }

    /**
     * Sets whether the action enabled or not. If an action is disabled, {@link Action#actionPerformed}
     * won't be called. In case when action represents a button or a menu item, the
     * representing button or item will be greyed out.
     *
     * @param enabled
     *         <code>true</code> if you want to enable action, <code>false</code> otherwise
     */
    public void setEnabled(boolean enabled) {
        boolean oldEnabled = myEnabled;
        myEnabled = enabled;
        firePropertyChange(PROP_ENABLED, oldEnabled ? Boolean.TRUE : Boolean.FALSE, myEnabled ? Boolean.TRUE : Boolean.FALSE);
    }

    public final void setEnabledAndVisible(boolean enabled) {
        setEnabled(enabled);
        setVisible(enabled);
    }

    void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        final PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        myChangeSupport.dispatch(new ListenerManager.Dispatcher<PropertyChangeListener>() {
            @Override
            public void dispatch(PropertyChangeListener listener) {
                listener.onPropertyChange(event);
            }
        });
    }

    public Presentation clone() {
        Presentation presentation = new Presentation(getText());
        presentation.myDescription = myDescription;
        presentation.myDisplayedMnemonicIndex = myDisplayedMnemonicIndex;
        presentation.myEnabled = myEnabled;
        presentation.myIcon = myIcon;
        presentation.mySVGIcon = mySVGIcon;
        presentation.myVisible = myVisible;
        presentation.myWeight = myWeight;
        return presentation;
    }

    public void copyFrom(Presentation presentation) {
        setText(presentation.getTextWithMnemonic());
        setDescription(presentation.getDescription());
        setIcon(presentation.getIcon());
        setSVGIcon(presentation.getSVGIcon());
        setVisible(presentation.isVisible());
        setEnabled(presentation.isEnabled());
    }

    public void putClientProperty(@NotNull String key, @Nullable Object value) {
        if (userMap == null) {
            userMap = Collections.createStringMap();
        }

        Object oldValue = userMap.get(key);
        userMap.put(key, value);
        firePropertyChange(key, oldValue, value);
    }

    public int getWeight() {
        return myWeight;
    }

    /**
     * Some action groups (like 'New...') may filter out actions with non-highest priority.
     *
     * @param weight
     *         please use {@link #HIGHER_WEIGHT}
     */
    public void setWeight(int weight) {
        myWeight = weight;
    }

    @Override
    public String toString() {
        return myText + " (" + myDescription + ")";
    }
}
