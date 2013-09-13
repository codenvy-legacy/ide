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
package org.exoplatform.ide.client.hotkeys;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.editor.client.api.event.EditorHotKeyPressedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorHotKeyPressedHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 */
public class HotKeyManager implements EditorHotKeyPressedHandler {

    /** Instance of {@link HotKeyManager} */
    private static HotKeyManager instance;

    /**
     * Returns instance of {@link HotKeyManager}
     *
     * @return
     */
    public static HotKeyManager getInstance() {
        return instance;
    }

    private HotKeyPressedListener hotKeyPressedListener;

    private Map<String, String> hotKeyMap;

    private Map<String, Control<?>> controlsMap = new LinkedHashMap<String, Control<?>>();

    private ApplicationSettings applicationSettings;

    public HotKeyManager(List<Control> controls, ApplicationSettings applicationSettings) {
        instance = this;

        for (Control control : controls) {
            controlsMap.put(control.getId(), control);
        }

        this.applicationSettings = applicationSettings;

        IDE.addHandler(EditorHotKeyPressedEvent.TYPE, this);

        Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                try {
                    if (event.getTypeInt() != Event.ONKEYDOWN)
                        return;
                } catch (Exception e) {
                    return;
                }

                onKeyDown(Event.as(event.getNativeEvent()));
            }
        });

        hotKeyMap = applicationSettings.getValueAsMap("hotkeys");
        if (hotKeyMap == null) {
            initDefaultHotKeys();
        } else {
            storeHotKeysToControls();
        }
    }

    public void setHotKeyPressedListener(HotKeyPressedListener listener) {
        hotKeyPressedListener = listener;
    }

    private void storeHotKeysToControls() {
      /*
       * Clear old HotKey values
       */
        for (Control control : controlsMap.values()) {
            if (!(control instanceof SimpleControl)) {
                continue;
            }

            SimpleControl simpleControl = (SimpleControl)control;
            simpleControl.setHotKey(null);
        }

      /*
       * Set new hotkeys
       */
        for (String key : hotKeyMap.keySet()) {
            String value = hotKeyMap.get(key);
            Control control = controlsMap.get(value);
            if (control == null || !(control instanceof SimpleControl)) {
                continue;
            }

            SimpleControl simpleControl = (SimpleControl)control;
            simpleControl.setHotKey(key);
        }
    }

    private void initDefaultHotKeys() {
        hotKeyMap = new LinkedHashMap<String, String>();
        applicationSettings.setValue("hotkeys", hotKeyMap, Store.SERVER);

        for (Control<?> control : controlsMap.values()) {
            if (control instanceof SimpleControl && ((SimpleControl)control).getHotKey() != null
                && !((SimpleControl)control).getHotKey().isEmpty()) {
                hotKeyMap.put(((SimpleControl)control).getHotKey(), ((SimpleControl)control).getId());
            }
        }
    }

    @Override
    public void onEditorHotKeyPressed(EditorHotKeyPressedEvent event) {
        if (handleKeyPressing(event.isCtrl(), event.isAlt(), event.isShift(), event.getKeyCode())) {
            event.setHotKeyHandled(true);
        }
    }

    /** @param event */
    public void onKeyDown(final Event event) {
        int keyCode = DOM.eventGetKeyCode(event);
        boolean ctrl = event.getCtrlKey();
        if (event.getMetaKey()) {
            ctrl = true;
        }

        boolean alt = event.getAltKey();
        boolean shift = event.getShiftKey();

        if (hotKeyPressedListener != null) {
            hotKeyPressedListener.onHotKeyPressed(ctrl, alt, shift, keyCode);
            event.preventDefault();
            event.stopPropagation();
        } else {
            if (handleKeyPressing(ctrl, alt, shift, keyCode)) {
                event.preventDefault();
                event.stopPropagation();
            }
        }
    }

    private boolean handleKeyPressing(boolean ctrl, boolean alt, boolean shift, int keyCode) {
        if (keyCode < HotKeyHelper.KeyCode.F1 || keyCode > HotKeyHelper.KeyCode.F12) {
            if (!ctrl && !alt) {
                return false;
            }
        }

        if (keyCode == 16 || keyCode == 17 || keyCode == 18 || keyCode == 224) {
            return false;
        }

        String shortcut = "";
        if (ctrl) {
            shortcut = "Ctrl";
        }

        if (alt) {
            if (shortcut.isEmpty()) {
                shortcut = "Alt";
            } else {
                shortcut += "+Alt";
            }
        }

        if (shift) {
            if (shortcut.isEmpty()) {
                shortcut = "Shift";
            } else {
                shortcut += "+Shift";
            }
        }

        if (shortcut.isEmpty()) {
            shortcut = HotKeyHelper.getKeyName(String.valueOf(keyCode));
        } else {
            shortcut += "+" + HotKeyHelper.getKeyName(String.valueOf(keyCode));
        }

        boolean hotKeyBinded = false;

        // search associated command
        if (hotKeyMap.containsKey(shortcut)) {
            String controlId = hotKeyMap.get(shortcut);
            Control control = controlsMap.get(controlId);
            if (control instanceof SimpleControl) {
                SimpleControl simpleControl = (SimpleControl)control;

                if (shortcut.equals(simpleControl.getHotKey())) {
                    hotKeyBinded = true;

                    if (simpleControl.getEvent() != null && (simpleControl.isEnabled() || simpleControl.isIgnoreDisable())) {
                        IDE.fireEvent(simpleControl.getEvent());
                        return true;
                    }
                }
            }
        }

        return hotKeyBinded;
    }

    public void setHotKeys(Map<String, String> newHotKeys) {
        Map<String, String> temp = new LinkedHashMap<String, String>();
        temp.putAll(newHotKeys);

        if (hotKeyMap == null) {
            hotKeyMap = new LinkedHashMap<String, String>();
            applicationSettings.setValue("hotkeys", hotKeyMap, Store.SERVER);
        } else {
            hotKeyMap.clear();
        }

        hotKeyMap.putAll(temp);
        storeHotKeysToControls();
    }

}
