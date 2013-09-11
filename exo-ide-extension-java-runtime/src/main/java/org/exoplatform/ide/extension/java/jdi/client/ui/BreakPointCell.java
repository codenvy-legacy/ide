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
package org.exoplatform.ide.extension.java.jdi.client.ui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerClientBundle;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class BreakPointCell extends AbstractCell<BreakPoint> {
    @Override
    public void render(Context context, BreakPoint breakpoint, SafeHtmlBuilder sb) {
        if (breakpoint == null)
            return;
        Image image = getImage(DebuggerClientBundle.INSTANCE.breakpoint());
        sb.appendHtmlConstant(image.toString());
        sb.appendHtmlConstant(getClassName(breakpoint.getLocation().getClassName()).getString());
        sb.appendHtmlConstant(getLineNumber(breakpoint.getLocation().getLineNumber()).toString());
    }

    protected Image getImage(ImageResource imageResource) {
        Image image = new Image(imageResource);
        DOM.setStyleAttribute(image.getElement(), "cssFloat", "left");
        DOM.setStyleAttribute(image.getElement(), "marginRight", "5px");
        return image;
    }

    protected Element getClassName(String className) {
        Element span = DOM.createSpan();
        span.setInnerHTML(className);
        return span;
    }

    protected String getLineNumber(int line) {
        Element span = DOM.createSpan();
        span.setInnerText(" - [line : " + line + "]");
        span.getStyle().setColor("#644A17");
        return span.getString();
    }

    /** @see com.google.gwt.cell.client.AbstractCell#getConsumedEvents() */
    @Override
    public Set<String> getConsumedEvents() {
        Set<String> consumedEvents = super.getConsumedEvents();
        if (consumedEvents == null) {
            consumedEvents = new HashSet<String>();
        }
        consumedEvents.add("contextmenu");
        return consumedEvents;
    }

    /**
     * @see com.google.gwt.cell.client.AbstractCell#onBrowserEvent(com.google.gwt.cell.client.Cell.Context,
     *      com.google.gwt.dom.client.Element, java.lang.Object, com.google.gwt.dom.client.NativeEvent,
     *      com.google.gwt.cell.client.ValueUpdater)
     */
    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, BreakPoint value,
                               NativeEvent event, ValueUpdater<BreakPoint> valueUpdater) {
        switch (DOM.eventGetType((Event)event)) {
            case Event.ONCONTEXTMENU:
                NativeEvent nativeEvent =
                        Document.get().createClickEvent(-1, event.getScreenX(), event.getScreenY(), event.getClientX(),
                                                        event.getClientY(), event.getCtrlKey(), event.getAltKey(), event.getShiftKey(),
                                                        event.getMetaKey());
                DOM.eventGetTarget((Event)event).dispatchEvent(nativeEvent);
                event.preventDefault();
                IDE.fireEvent(new ShowContextMenuEvent(event.getClientX(), event.getClientY(), value));
                break;
            default:
                break;
        }
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
    }
}
