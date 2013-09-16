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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.ControlsUpdatedEvent;
import org.exoplatform.ide.client.framework.control.ControlsUpdatedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.preference.PreferencePerformer;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.messages.IdePreferencesLocalizationConstant;
import org.exoplatform.ide.client.model.SettingsService;

import java.util.*;

/**
 * Presenter for customize hotkeys form.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 */
public class CustomizeHotKeysPresenter implements HotKeyPressedListener, CustomizeHotKeysHandler, ViewOpenedHandler,
                                                  ViewClosedHandler, ApplicationSettingsReceivedHandler, ControlsUpdatedHandler,
                                                  PreferencePerformer {

    public interface Display extends IsView {

        HasClickHandlers getOkButton();

        HasClickHandlers getDefaultsButton();

        HasClickHandlers getBindButton();

        HasClickHandlers getUnbindButton();

        ListGridItem<HotKeyItem> getHotKeyItemListGrid();

        HasValue<String> getHotKeyField();

        HotKeyItem getSelectedItem();

        void setOkButtonEnabled(boolean enabled);

        void setBindButtonEnabled(boolean enabled);

        void setUnbindButtonEnabled(boolean enabled);

        void setHotKeyFieldEnabled(boolean enabled);

        void focusOnHotKeyField();

        void showError(String text);

    }

    /*
     * Title of group, that contains hotkeys, which used in editor (autocomplete, save etc.). Will be displayed in hotkeys
     * listgrid. Other groups will be formed from menu titles: File, Edit and so on.
     */
    private static final String EDITOR_GROUP = org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT.hotkeysEditorGroup();

    /*
     * Group for hotkeys, that don't belong to one of defined groups.
     */
    private static final String OTHER_GROUP = org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT.hotkeysOtherGroup();

    private static final String CANT_SAVE_HOTKEYS = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
                                                                                  .hotkeysCantSaveHotkeys();

    private static final IdePreferencesLocalizationConstant CONSTANTS =
            org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT;

    private Display display;

    private List<HotKeyItem> hotKeys;

    // Need for resetting the modified hotkeys back to the default hotkeys.
    private Map<SimpleControl, String> defaultHotkeys;

    private HotKeyItem selectedItem;

    private ApplicationSettings applicationSettings;

    private List<Control> controls;

    public CustomizeHotKeysPresenter() {
        // TODO IDE.getInstance().addControl(new CustomizeHotKeysControl());

        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
        IDE.addHandler(ControlsUpdatedEvent.TYPE, this);

        IDE.addHandler(CustomizeHotKeysEvent.TYPE, this);
        IDE.addHandler(ViewOpenedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    @Override
    public void onCustomizeHotKeys(CustomizeHotKeysEvent event) {
        if (display != null) {
            return;
        }

        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
            HotKeyManager.getInstance().setHotKeyPressedListener(null);
        }
    }

    @Override
    public void onViewOpened(ViewOpenedEvent event) {
        if (event.getView() instanceof Display) {
            HotKeyManager.getInstance().setHotKeyPressedListener(this);
        }
    }

    @Override
    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        applicationSettings = event.getApplicationSettings();
    }

    @Override
    public void onControlsUpdated(ControlsUpdatedEvent event) {
        controls = event.getControls();
        defaultHotkeys = getCurrentHotKeys();
    }

    public void bindDisplay() {
        display.getOkButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                saveHotKeys();
            }
        });

        display.getDefaultsButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                restoreDefaults();
            }
        });

        display.getHotKeyItemListGrid().addSelectionHandler(new SelectionHandler<HotKeyItem>() {
            public void onSelection(SelectionEvent<HotKeyItem> event) {
                selectedItem = display.getSelectedItem();
                if (selectedItem.getGroup().equals(EDITOR_GROUP)) {
                    selectedItem = null;
                    display.setBindButtonEnabled(false);
                    display.setUnbindButtonEnabled(false);
                    display.setHotKeyFieldEnabled(false);
                    display.showError(null);
                    display.getHotKeyField().setValue("");
                    return;
                }

                hotKeySelected(selectedItem);
                display.showError(null);
            }
        });

        display.getBindButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                bindHotKey();
            }
        });

        display.getUnbindButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                unbindHotKey();
            }
        });

        fillHotkeyListGrid(false);

        display.setHotKeyFieldEnabled(false);
    }

    /**
     * Fill hotkey list grid with hotkey items.
     * <p/>
     * Choose only SimpleControls from list of contols, create hotkey items from them and add to list.
     * <p/>
     * At the end get the list of editor non-changable controls (like Save, SaveAs ect), create hotkey items from them and add to
     * the end on hotkey list.
     * <p/>
     * Update value of hotkey list grid.
     *
     * @param filledWithDefaultValues
     *         - if <code>true</code> - hotkey list will be filled with default values, if
     *         <code>false</code> - hotkey list will be filled with current values
     */
    private void fillHotkeyListGrid(boolean filledWithDefaultValues) {
        HashMap<String, List<SimpleControl>> groups = new LinkedHashMap<String, List<SimpleControl>>();
        for (Control command : controls) {
            if (command instanceof SimpleControl) {
                if (((SimpleControl)command).getEvent() != null) {
                    addCommand(groups, (SimpleControl)command);
                }
            }
        }

        // if (hotKeys == null)
        // {
        // hotKeys = applicationSettings.getValueAsMap("hotkeys");
        // hotKeys keys.clear();
        // }

        hotKeys = new ArrayList<HotKeyItem>();
        for (String groupName : groups.keySet()) {
            hotKeys.add(new HotKeyItem(groupName, null, true, groupName));
            List<SimpleControl> commands = groups.get(groupName);
            for (SimpleControl command : commands) {
                hotKeys.add(new HotKeyItem(command, filledWithDefaultValues ? defaultHotkeys.get(command) : command
                        .getHotKey(), groupName));
            }
        }

        // /*
        // * fill default hotkeys
        // */
        // hotKeys.add(new HotKeyItem(EDITOR_GROUP, null, true, EDITOR_GROUP));
        // Iterator<Entry<String, String>> it = ReservedHotKeys.getHotkeys().entrySet().iterator();
        // while (it.hasNext())
        // {
        // Entry<String, String> entry = it.next();
        // String id = entry.getValue();
        // String hotkey = HotKeyHelper.convertToStringCombination(entry.getKey());
        // hotKeys.add(new HotKeyItem(id, hotkey, false, EDITOR_GROUP));
        // }

        display.getHotKeyItemListGrid().setValue(hotKeys);
    }

    private void addCommand(HashMap<String, List<SimpleControl>> groups, SimpleControl command) {
        String groupName = command.getId();
        if (groupName.indexOf("/") >= 0) {
            groupName = groupName.substring(0, groupName.lastIndexOf("/"));
        } else {
            groupName = OTHER_GROUP;
        }

        List<SimpleControl> commands = groups.get(groupName);
        if (commands == null) {
            commands = new ArrayList<SimpleControl>();
            groups.put(groupName, commands);
        }

        commands.add(command);
    }

    private void hotKeySelected(HotKeyItem hotKeyItem) {
        selectedItem = hotKeyItem;

        display.setBindButtonEnabled(false);
        display.setUnbindButtonEnabled(true);
        display.setHotKeyFieldEnabled(true);
        display.focusOnHotKeyField();
        display.getHotKeyField().setValue(selectedItem.getHotKey());
    }

    /** Bind hot key to selected item. */
    private void bindHotKey() {
        String newHotKey = display.getHotKeyField().getValue();
        String selectedCommandId =
                selectedItem.getCommand() == null ? selectedItem.getTitle() : selectedItem.getCommand().getId();
        for (HotKeyItem hotKey : hotKeys) {
            String commandId = hotKey.getCommand() == null ? hotKey.getTitle() : hotKey.getCommand().getId();
            if (commandId.equals(selectedCommandId)) {
                hotKey.setHotKey(newHotKey);
            }
        }

        updateState();
    }

    /** Unbind hot key from selected item. */
    private void unbindHotKey() {
        for (HotKeyItem hotKeyItem : hotKeys) {
            final String commandId =
                    hotKeyItem.getCommand() == null ? hotKeyItem.getTitle() : hotKeyItem.getCommand().getId();
            final String selectedCommandId =
                    selectedItem.getCommand() == null ? selectedItem.getTitle() : selectedItem.getCommand().getId();
            if (commandId.equals(selectedCommandId)) {
                hotKeyItem.setHotKey(null);
            }
        }

        updateState();
    }

    /**
     * Validates hotkeys.
     * <p/>
     * If key is null or empty return false and show info message.
     * <p/>
     * If combination of controlKey and key already exists, return false and show error message.
     * <p/>
     * If combination of hot keys doesn't start with Ctrl or Alt, return false and show error message.
     * <p/>
     * If you try to bind such hotkey return false and show info message
     * <p/>
     * Otherwise return true;
     *
     * @param newHotKey
     * @return is combination of hot key is valid
     */
    private boolean validateHotKey(boolean isCtrl, boolean isAlt, boolean isShift, int keyCode) {
        final String firstKeyCtrlOrAltMsg = CONSTANTS.msgFirstKeyCtrlOrAlt();

        final String hotkeyIsUsedInCkEditorMsg = CONSTANTS.msgHotkeyUsedInOtherEditor();

        final String pressControlKeyThenKey = CONSTANTS.msgPressControlKeyTheKey();

        final String boundToAnotherCommand = CONSTANTS.msgBoundToAnotherCommand();

        final String tryToBindTheSameHotKey = CONSTANTS.msgBoundToTheSameCommand();

        final String tryAnotherKey = CONSTANTS.msgTryAnotherHotkey();

        // --- check is control key pressed first ---
        // 16 - key code of Shift
        // 17 - key code of Ctrl
        // 18 - key code of Alt
        // 224 - key code of Alt when Shift is pressed
        // on Linux, if single Ctrl or Alt key pressed, than isCtrl or isAlt will be false,
        // but keyCode will contain 17 or 18 key. To check keyCode - is the one way
        // to know, that Ctrl or Alt single key was pressed on Linux
        if (!isCtrl && !isAlt && !isShift) {
            // if control is null, but keyCode is Ctrl or Alt,
            // than Ctrl or Alt is pressed first
            if (keyCode == 17 || keyCode == 18 || keyCode == 16 || keyCode == 224) {
                display.getHotKeyField().setValue(HotKeyHelper.getKeyName(String.valueOf(keyCode)) + "+");
                display.showError(pressControlKeyThenKey);
                return false;
            }
            // if keyCode is not Ctrl or Alt
            // than another key is pressed
            else {
                display.getHotKeyField().setValue("");
                display.showError(firstKeyCtrlOrAltMsg);
                return false;
            }
        }

        // --- controlKey must be Ctrl or Alt ---
        String shortcut = "";
        if (isCtrl) {
            shortcut = "Ctrl";
        }

        if (isAlt || keyCode == 224) {
            if (shortcut.isEmpty()) {
                shortcut = "Alt";
            } else {
                shortcut += "+Alt";
            }
        }

        if (isShift) {
            if (shortcut.isEmpty()) {
                shortcut = "Shift";
            } else {
                shortcut += "+Shift";
            }
        }

        // if control key is correct, but keyCode is not pressed yet
        if (keyCode == 0 || keyCode == 16 || keyCode == 17 || keyCode == 18 || keyCode == 224) {
            display.getHotKeyField().setValue(shortcut + "+");
            display.showError(pressControlKeyThenKey);
            return false;
        }

        // control key is correct, keyCode is pressed
        String keyString = HotKeyHelper.getKeyName(String.valueOf(keyCode));
        // --- check, is keyCode correct (maybe pressed not standard key on keyboard) ---
        if (keyString == null) {
            display.getHotKeyField().setValue(shortcut + "+");
            display.showError(tryAnotherKey);
            return false;
        }

        String stringHotKey = shortcut + "+" + keyString;

        // show hotkey in text field
        display.getHotKeyField().setValue(stringHotKey);

        // --- check, is stringHotKey is reserved by editor ---
        if (ReservedHotKeys.getHotkeys().containsKey(shortcut + "+" + keyCode)) {
            display.showError(hotkeyIsUsedInCkEditorMsg);
            return false;
        }

        // --- check, if you try to bind the same hotkey ---
        if (stringHotKey.equals(selectedItem.getHotKey())) {
            display.showError(tryToBindTheSameHotKey);
            return false;
        }

        // --- check, is hotkey alread bound to another command ---
        String controlId = selectedItem.getCommand().getId();

        for (HotKeyItem hotKeyIdentifier : hotKeys) {
            if (hotKeyIdentifier.getHotKey() != null && hotKeyIdentifier.getHotKey().equals(stringHotKey)
                && !hotKeyIdentifier.getCommand().getId().equals(controlId)) {
                display.showError(boundToAnotherCommand);
                return false;
            }
        }

        return true;
    }

    /** Save hot keys. */
    private void saveHotKeys() {
        final Map<String, String> keys = applicationSettings.getValueAsMap("hotkeys");
        keys.clear();

        for (HotKeyItem hotKeyItem : hotKeys) {
            if (!hotKeyItem.getGroup().equals(EDITOR_GROUP)) {
                String hotKey = hotKeyItem.getHotKey();

                if (hotKey != null && !"".equals(hotKey)) {
                    keys.put(hotKey, hotKeyItem.getCommand().getId());
                }
            }
        }

        try {
            SettingsService.getInstance().saveSettingsToServer(applicationSettings,
                                                               new AsyncRequestCallback<ApplicationSettings>() {
                                                                   @Override
                                                                   protected void onSuccess(ApplicationSettings result) {
                                                                       IDE.getInstance().closeView(display.asView().getId());
                                                                       HotKeyManager.getInstance().setHotKeys(keys);
                                                                   }

                                                                   @Override
                                                                   protected void onFailure(Throwable exception) {
                                                                       IDE.fireEvent(new ExceptionThrownEvent(CANT_SAVE_HOTKEYS));
                                                                   }
                                                               });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(CANT_SAVE_HOTKEYS));
        }
    }

    /** Update state after binding, unbinding or resetting. */
    private void updateState() {
        // selectedItem = null;

        display.setBindButtonEnabled(false);
        if (selectedItem != null && selectedItem.getHotKey() != null && !selectedItem.getHotKey().isEmpty()) {
            display.setUnbindButtonEnabled(true);
        } else {
            display.setUnbindButtonEnabled(false);
            // display.getHotKeyField().setValue("", true);
            // display.setHotKeyFieldEnabled(false);
            // display.getHotKeyItemListGrid().setValue(hotKeys);
        }

        display.getHotKeyItemListGrid().setValue(hotKeys);
        display.setOkButtonEnabled(true);
    }

    /** Resets the modified hotkeys back to the default hotkeys. */
    private void restoreDefaults() {
        fillHotkeyListGrid(true);
        updateState();
    }

    /**
     * When hot key pressed, display this hot key in input field.
     *
     * @see org.exoplatform.ide.client.hotkeys.HotKeyPressedListener#onHotKeyPressed(java.lang.String, java.lang.String)
     */
    public void onHotKeyPressed(boolean isCtrl, boolean isAlt, boolean isShift, int keyCode) {
        if (selectedItem == null) {
            return;
        }

        if (validateHotKey(isCtrl, isAlt, isShift, keyCode)) {
            display.showError(null);
            display.setBindButtonEnabled(true);
        } else {
            display.setBindButtonEnabled(false);
        }
    }

    /**
     * Returns current (default) hotkeys.
     *
     * @return current hotkeys
     */
    private Map<SimpleControl, String> getCurrentHotKeys() {
        Map<SimpleControl, String> defaultHotKeysMap = new LinkedHashMap<SimpleControl, String>();

        for (Control<?> control : controls) {
            if (control instanceof SimpleControl && ((SimpleControl)control).getHotKey() != null
                && !((SimpleControl)control).getHotKey().isEmpty()) {
                defaultHotKeysMap.put((SimpleControl)control, ((SimpleControl)control).getHotKey());
            }
        }

        return defaultHotKeysMap;
    }

    /** @see org.exoplatform.ide.client.framework.preference.PreferencePerformer#getPreference() */
    @Override
    public View getPreference() {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
        }
        HotKeyManager.getInstance().setHotKeyPressedListener(this);
        return display.asView();
    }
}
