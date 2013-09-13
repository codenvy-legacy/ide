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
package org.exoplatform.ide.client;

import com.google.gwt.gadgets.client.DynamicHeightFeature;
import com.google.gwt.gadgets.client.Gadget;
import com.google.gwt.gadgets.client.Gadget.ModulePrefs;
import com.google.gwt.gadgets.client.NeedsDynamicHeight;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.ide.client.framework.util.Utils;

/**
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
 */
@ModulePrefs(title = "IDE", author = "eXo Platform", author_email = "info@exoplatform.com.ua", height = 500, description = "IDE")
public class IDEGadget extends Gadget<UserPreferences> implements NeedsDynamicHeight {
    private DynamicHeightFeature dynamicHeightFeature;

    @Override
    protected void init(UserPreferences preferences) {
        final VerticalPanel idePanel = new VerticalPanel();

        RootPanel.get().add(idePanel);
        if (BrowserResolver.CURRENT_BROWSER == Browser.CHROME) {
            Utils.expandGadgetHeight();
        } else {
            Integer h = getFixHeight();
            if (h != null) {
                idePanel.setHeight(h + "px");
                dynamicHeightFeature.adjustHeight();
            }
        }

        new IDE();
    }

    public void initializeFeature(DynamicHeightFeature feature) {
        this.dynamicHeightFeature = feature;
    }

    // Fix gadget height work only in Gatein
    // get height of parent element in the DOM.
    private static native String expandGadgetHeight() /*-{
        var y = $wnd.parent.document.getElementById("UIGadgetPortlet").parentNode;
        return y.style.height;
    }-*/;

    private Integer getFixHeight() {
        String height = expandGadgetHeight();
        Integer newHeight;
        if (height.contains("px")) {
            newHeight = Integer.parseInt(height.replace("px", "")) - 30;
            return newHeight;
        } else {
            return null;
        }
    }

}
