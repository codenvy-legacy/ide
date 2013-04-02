/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;

import org.exoplatform.gwtframework.ui.client.util.ExoStyle;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class TextButton extends Composite {

    public enum TextAlignment {

        LEFT, CENTER, RIGHT

    }

    private class LabelPanel extends FlowPanel {

        public LabelPanel() {
            sinkEvents(Event.ONDBLCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONCLICK | Event.ONMOUSEDOWN
                       | Event.ONMOUSEUP);
        }

        @Override
        public void onBrowserEvent(Event event) {
            switch (DOM.eventGetType(event)) {
                case Event.ONDBLCLICK:
                    onMouseDClick();
                    break;

                case Event.ONMOUSEOVER:
                    onMouseOver();
                    break;

                case Event.ONMOUSEOUT:
                    onMouseOut();
                    break;

                case Event.ONCLICK:
                    onMouseClick();
                    break;

                case Event.ONMOUSEDOWN:
                    onMouseDown();
                    break;

                case Event.ONMOUSEUP:
                    onMouseUp();
                    break;
            }
        }

    }

    public static interface Style {

        static final String PANEL = "exo-statusText-panel";

        static final String TABLE = "exo-statusText-table";

        static final String TABLE_LEFT = "exo-statusText-table-left";

        static final String TABLE_LEFT_DOWN = "exo-statusText-table-left-down";

        static final String TABLE_LEFT_OVER = "exo-statusText-table-left-over";

        static final String TABLE_MIDDLE = "exo-statusText-table-middle";

        static final String TABLE_MIDDLE_DOWN = "exo-statusText-table-middle-down";

        static final String TABLE_MIDDLE_OVER = "exo-statusText-table-middle-over";

        static final String TABLE_RIGHT = "exo-statusText-table-right";

        static final String TABLE_RIGHT_DOWN = "exo-statusText-table-right-down";

        static final String TABLE_RIGHT_OVER = "exo-statusText-table-right-over";

    }

    /** Command which will be executed when StatusText component will be clicked. */
    private Command command;

    /** Enable or disable execute assigned command on single mouse click. */
    private boolean executeCommandOnSingleClick = true;

    /** Root element. */
    private LabelPanel flowPanel = new LabelPanel();

    /** Status text. */
    private String text;

    /** Alignment of the text. */
    private TextAlignment textAlignment;

    /** Working table ( needs for building UI ). */
    private Grid textGrid;

    /**
     * Create StatusText component.
     *
     * @param text
     *         status text
     */
    public TextButton(String text) {
        this(text, TextAlignment.LEFT, null);
    }

    /**
     * Create StatusText component.
     *
     * @param text
     *         status text
     * @param command
     *         command which will be executed when StatusText command will be clicked
     */
    public TextButton(String text, Command command) {
        this(text, TextAlignment.LEFT, command);
    }

    /**
     * Create StatusText component.
     *
     * @param text
     *         status text
     * @param textAlignment
     *         alignment of the text
     */
    public TextButton(String text, TextAlignment textAlignment) {
        this(text, textAlignment, null);
    }

    /**
     * Create StatusText component.
     *
     * @param text
     *         status text
     * @param textAlignment
     *         alignment of the text
     * @param command
     *         command which will be executed when StatusText command will be clicked
     */
    public TextButton(String text, TextAlignment textAlignment, Command command) {
        this.text = text;
        this.textAlignment = textAlignment;
        this.command = command;

        initWidget(flowPanel);
        flowPanel.setStyleName(Style.PANEL);

        textGrid = new Grid(1, 3);

        textGrid.setStyleName(Style.TABLE);
        textGrid.setBorderWidth(0);
        textGrid.setCellPadding(0);
        textGrid.setCellSpacing(0);

        textGrid.setHTML(0, 0, ExoStyle.getBlankImage());
        textGrid.setHTML(0, 2, ExoStyle.getBlankImage());

        updateTextAlignment();

        textGrid.setHTML(0, 1, text);
        textGrid.getCellFormatter().getElement(0, 1).setAttribute("textbutton-text", "");

        flowPanel.add(textGrid);

        setStyleNormal();
    }

    /**
     * Get command.
     *
     * @return command which will be executed when component will be clicked
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Get status text.
     *
     * @return status text
     */
    public String getText() {
        return text;
    }

    /**
     * Get text alignment.
     *
     * @return text alignment
     */
    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    /**
     * Get is execute command on single mouse click.
     *
     * @return is execute command on single mouse click
     */
    public boolean isExecuteCommandOnSingleClick() {
        return executeCommandOnSingleClick;
    }

    /** Handler of MouseClick event. */
    protected void onMouseClick() {
        if (command == null) {
            return;
        }

        if (executeCommandOnSingleClick) {
            command.execute();
        }
    }

    /** Handler of MouseDoubleClick event. */
    protected void onMouseDClick() {
        if (command == null || executeCommandOnSingleClick) {
            return;
        }

        command.execute();
    }

    /** Handler of MouseDown event. */
    protected void onMouseDown() {
        if (command == null) {
            return;
        }

        setStyleSelected();
    }

    /** Handler of MouseOut event. */
    protected void onMouseOut() {
        setStyleNormal();
        DOM.setStyleAttribute(textGrid.getElement(), "cursor", "default");
    }

    /** Handler of MouseOver event. */
    protected void onMouseOver() {
        if (command == null) {
            return;
        }

        setStyleHovered();

        DOM.setStyleAttribute(textGrid.getElement(), "cursor", "pointer");
    }

    /** Handler of MouseUp event. */
    protected void onMouseUp() {
        if (command == null) {
            return;
        }

        setStyleHovered();
    }

    /**
     * Set command which will be executed when component will be clicked.
     *
     * @param command
     *         command which will be executed when component will be clicked
     */
    public void setCommand(Command command) {
        this.command = command;
    }

    /** Set style selected ( when user press mouse key ) */
    private void setStyleSelected() {
        textGrid.getCellFormatter().setStyleName(0, 0, Style.TABLE_LEFT_DOWN);
        textGrid.getCellFormatter().setStyleName(0, 1, Style.TABLE_MIDDLE_DOWN);
        textGrid.getCellFormatter().setStyleName(0, 2, Style.TABLE_RIGHT_DOWN);
    }

    /**
     * Enable or disable execution of command on single mouse clicking.
     *
     * @param executeCommandOnSingleClick
     *         enable or disable execution of command on single mouse clicking
     */
    public void setExecuteCommandOnSingleClick(boolean executeCommandOnSingleClick) {
        this.executeCommandOnSingleClick = executeCommandOnSingleClick;
    }

    /** Set default style. */
    private void setStyleNormal() {
        textGrid.getCellFormatter().setStyleName(0, 0, Style.TABLE_LEFT);
        textGrid.getCellFormatter().setStyleName(0, 1, Style.TABLE_MIDDLE);
        textGrid.getCellFormatter().setStyleName(0, 2, Style.TABLE_RIGHT);
    }

    /** Set style hovered. */
    private void setStyleHovered() {
        textGrid.getCellFormatter().setStyleName(0, 0, Style.TABLE_LEFT_OVER);
        textGrid.getCellFormatter().setStyleName(0, 1, Style.TABLE_MIDDLE_OVER);
        textGrid.getCellFormatter().setStyleName(0, 2, Style.TABLE_RIGHT_OVER);
    }

    /**
     * Set status text.
     *
     * @param text
     *         status text
     */
    public void setText(String text) {
        this.text = text;
        textGrid.setHTML(0, 1, text);
    }

    /**
     * Set text alignment.
     *
     * @param textAlignment
     *         text alignment
     */
    public void setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        updateTextAlignment();
    }

    /** Refresh alignment of the status text. */
    private void updateTextAlignment() {
        if (textAlignment == TextAlignment.LEFT) {
            DOM.setStyleAttribute(textGrid.getCellFormatter().getElement(0, 1), "textAlign", "left");
        } else if (textAlignment == TextAlignment.CENTER) {
            DOM.setStyleAttribute(textGrid.getCellFormatter().getElement(0, 1), "textAlign", "center");
        } else {
            DOM.setStyleAttribute(textGrid.getCellFormatter().getElement(0, 1), "textAlign", "right");
        }
    }

    /**
     * Sets new title of this TextButton.
     *
     * @see com.google.gwt.user.client.ui.UIObject#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        flowPanel.setTitle(title);
    }

    /**
     * Sets new width of this TextButton.
     *
     * @param width
     *         new width
     */
    public void setWidth(int width) {
        super.setWidth(width + "px");
        textGrid.setWidth(width + "px");
    }

}
