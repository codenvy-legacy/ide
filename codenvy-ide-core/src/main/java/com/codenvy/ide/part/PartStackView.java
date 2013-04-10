package com.codenvy.ide.part;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Image;


/** PartStack View interface */
public interface PartStackView extends View<PartStackView.ActionDelegate> {

    void setTabPosition(TabPosition tabPosition);

    /** Tab which can be clicked and closed */
    public interface TabItem extends HasCloseHandlers<PartStackView.TabItem>, HasClickHandlers {
    }

    public enum TabPosition{
        ABOVE, BELOW, LEFT, RIGHT
    }
    /** Add Tab */
    public PartStackView.TabItem addTabButton(Image icon, String title, String toolTip, boolean closable);

    /** Remove Tab */
    public void removeTabButton(int index);

    /** Set Active Tab */
    public void setActiveTabButton(int index);

    /** Get Content Panel */
    public AcceptsOneWidget getContentPanel();

    /** Set PartStack focused */
    public void setFocus(boolean focused);

    /** Update Tab */
    public void updateTabItem(int index, ImageResource icon, String title, String toolTip);

    /** Handles Focus Request Event. It is generated, when user clicks a stack anywhere */
    public interface ActionDelegate {
        /** PartStack is being clicked and requests Focus */
        void onRequestFocus();
    }
}