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
package org.exoplatform.ide.editor.codemirror;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;

import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.marking.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:23:13 PM Mar 21, 2012 evgen $
 */
public class OverviewRuler extends Composite implements MouseDownHandler {
    private LayoutPanel panel;

    private LayoutPanel ruler;

    private Editor editor;

    private Mark bottomMark;

    private List<Marker> errors = new ArrayList<Marker>();

    private List<Marker> warnings = new ArrayList<Marker>();

    /**
     *
     */
    public OverviewRuler(Editor editor) {
        this.editor = editor;

        panel = new LayoutPanel();
        ruler = new LayoutPanel();
        initWidget(panel);
        panel.add(ruler);
        panel.setWidgetTopBottom(ruler, 0, Unit.PX, 13, Unit.PX);
        setWidth("100%");
        setHeight("100%");
        //setStyleName(CodeMirrorClientBundle.INSTANCE.css().overviewPanel());
        setStyleName(CodeMirrorStyles.OVERVIEW_PANEL);
        bottomMark = new Mark();
        panel.add(bottomMark);
        panel.setWidgetBottomHeight(bottomMark, 2, Unit.PX, 10, Unit.PX);
        panel.setWidgetLeftRight(bottomMark, 2, Unit.PX, 2, Unit.PX);
    }

    /**
     * @param problem
     * @param message
     */
    public void addProblem(Marker problem, String message) {
        if (!(problem.isError() || problem.isWarning()))
            return;

        int lastLineNumber = editor.getNumberOfLines();
        int problemY = (100 * problem.getLineNumber()) / lastLineNumber;

        Mark mark = new Mark(message, getStyleName(problem), problem.getLineNumber());
        mark.addDomHandler(this, MouseDownEvent.getType());
        ruler.add(mark);
        ruler.setWidgetTopHeight(mark, problemY, Unit.PCT, 5, Unit.PX);
        ruler.setWidgetLeftRight(mark, 2, Unit.PX, 2, Unit.PX);
        if (problem.isError()) {
            errors.add(problem);
        }

        if (problem.isWarning()) {
            warnings.add(problem);
        }

        if (!errors.isEmpty()) {
            bottomMark.setMessage("Errors: " + errors.size());
            //bottomMark.setStyleName(CodeMirrorClientBundle.INSTANCE.css().overviewBottomMarkError());
            bottomMark.setStyleName(CodeMirrorStyles.OVERVIEW_BOTTOM_MARK_ERROR);
        } else if (!warnings.isEmpty()) {
            bottomMark.setMessage("Warnings: " + warnings.size());
            //bottomMark.setStyleName(CodeMirrorClientBundle.INSTANCE.css().overviewBottomMarkWarning());
            bottomMark.setStyleName(CodeMirrorStyles.OVERVIEW_BOTTOM_MARK_WARNING);
        }
    }

    /** @see com.google.gwt.event.dom.client.MouseDownHandler#onMouseDown(com.google.gwt.event.dom.client.MouseDownEvent) */
    @Override
    public void onMouseDown(MouseDownEvent event) {
        editor.setCursorPosition(((Mark)event.getSource()).lineNumber, 1);
        editor.setFocus();
    }

    /**
     * @param problem
     * @return
     */
    private String getStyleName(Marker problem) {
        if (problem.isError()) {
            //return CodeMirrorClientBundle.INSTANCE.css().overviewMarkError();
            return CodeMirrorStyles.OVERVIEW_MARK_ERROR;
        }

        if (problem.isWarning()) {
            //return CodeMirrorClientBundle.INSTANCE.css().overviewMarkWarning();
            return CodeMirrorStyles.OVERVIEW_MARK_WARNING;
        }

        // default
        //return CodeMirrorClientBundle.INSTANCE.css().overviewMarkError();
        return CodeMirrorStyles.OVERVIEW_MARK_ERROR;
    }

    /**
     *
     */
    public void clearProblems() {
        ruler.clear();
        warnings.clear();
        errors.clear();
        bottomMark.getElement().removeAttribute("class");
        bottomMark.getElement().removeAttribute("title");
    }

    private static class Mark extends Composite implements MouseOutHandler, MouseOverHandler {
        private HTML widget;

        private NotificationWidget notification;

        private int lineNumber;

        /**
         *
         */
        public Mark() {
            widget = new HTML();
            initWidget(widget);
            addDomHandler(this, MouseOutEvent.getType());
            addDomHandler(this, MouseOverEvent.getType());
        }

        /**
         *
         */
        public Mark(String message, String style, int lineNumber) {
            this();
            this.lineNumber = lineNumber;
            setStyleName(style);
            setMessage(message);
        }

        /** @param message */
        public void setMessage(String message) {
            widget.getElement().setAttribute("title", message);
        }

        /** @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent) */
        @Override
        public void onMouseOver(MouseOverEvent event) {
            if (widget.getElement().hasAttribute("title") && widget.getElement().getAttribute("title").isEmpty()) {
                return;
            }

            if (notification == null) {
                notification = new NotificationWidget(getElement(), getAbsoluteLeft(), getAbsoluteTop());
            }
        }

        /** @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent) */
        @Override
        public void onMouseOut(MouseOutEvent event) {
            if (notification != null)
                notification.destroy();
            notification = null;
        }
    }

}
