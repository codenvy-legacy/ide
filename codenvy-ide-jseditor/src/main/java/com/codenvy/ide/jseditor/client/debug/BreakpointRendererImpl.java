/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.jseditor.client.debug;

import static com.codenvy.ide.jseditor.client.gutter.Gutters.BREAKPOINTS_GUTTER;

import javax.annotation.Nonnull;

import com.codenvy.ide.debug.BreakpointRenderer;
import com.codenvy.ide.jseditor.client.document.DocumentHandle;
import com.codenvy.ide.jseditor.client.document.UseDocumentHandle;
import com.codenvy.ide.jseditor.client.texteditor.EditorHandle;
import com.codenvy.ide.jseditor.client.texteditor.EditorResources;
import com.codenvy.ide.jseditor.client.texteditor.HasGutter;
import com.codenvy.ide.jseditor.client.texteditor.HasGutter.LineNumberingChangeCallback;
import com.codenvy.ide.jseditor.client.texteditor.LineStyler;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import elemental.css.CSSStyleDeclaration;
import elemental.dom.Element;

/**
 * Renderer for breakpoint marks in gutter (on the left margin of the text).
 */
public class BreakpointRendererImpl implements BreakpointRenderer, UseDocumentHandle {

    private static final String BREAKPOINT_ACTIVE_PROPERTY_NAME = "bpActive";

    /** The resources for breakpoint display. */
    private final BreakpointResources breakpointResources;

    /** The resources for editor display. */
    private final EditorResources editorResources;

    /** The component responsible for gutter handling. */
    private final HasGutter hasGutter;

    /** The component responsible for line style handling. */
    private final LineStyler lineStyler ;

    /** The handle on the component. */
    private DocumentHandle documentHandle;

    @AssistedInject
    public BreakpointRendererImpl(final BreakpointResources breakpointResources,
                                  final EditorResources editorResources,
                                  @Assisted final EditorHandle editorHandle) {
        this.breakpointResources = breakpointResources;
        this.editorResources = editorResources;
        this.hasGutter = editorHandle.getEditor().getHasGutter();
        this.lineStyler = editorHandle.getEditor().getLineStyler();
        this.documentHandle = editorHandle.getEditor().getEmbeddedDocument().getDocumentHandle();
    }

    @Override
    public void setDocumentHandle(final DocumentHandle handle) {
        this.documentHandle = handle;
    }

    @Override
    public DocumentHandle getDocumentHandle() {
        return this.documentHandle;
    }

    @Override
    public void addBreakpointMark(final int lineNumber) {
        if (hasGutter != null) {
            this.hasGutter.addGutterItem(lineNumber, BREAKPOINTS_GUTTER, createBreakpointMark());
        }
    }

    @Override
    public void addBreakpointMark(final int lineNumber, final LineChangeAction action) {
        if (hasGutter != null) {
            this.hasGutter.addGutterItem(lineNumber, BREAKPOINTS_GUTTER, createBreakpointMark(), new LineNumberingChangeCallback() {
                @Override
                public void onLineNumberingChange(final int fromLine, final int linesRemoved, final int linesAdded) {
                    action.onLineChange(documentHandle.getDocument().getFile(), fromLine, linesAdded, linesRemoved);
                }
            });
        }
    }

    @Override
    public void removeBreakpointMark(final int lineNumber) {
        if (hasGutter != null) {
            this.hasGutter.removeGutterItem(lineNumber, BREAKPOINTS_GUTTER);
        }
    }

    @Override
    public void clearBreakpointMarks() {
        if (hasGutter != null) {
            this.hasGutter.clearGutter(BREAKPOINTS_GUTTER);
        }
    }

    @Override
    public void setBreakpointActive(final int lineNumber, final boolean active) {
        if (hasGutter == null) {
            return;
        }
        final Element mark = this.hasGutter.getGutterItem(lineNumber, BREAKPOINTS_GUTTER);
        if (mark != null) {
            setActiveMark(mark, active);
        }
    }

    @Override
    public void setLineActive(final int lineNumber, final boolean active) {
        if (active && this.lineStyler != null) {
            this.lineStyler.addLineStyles(lineNumber, this.editorResources.editorCss().debugLine());
        } else {
            this.lineStyler.removeLineStyles(lineNumber, this.editorResources.editorCss().debugLine());
        }
    }

    /**
     * Creates the element for the breakpoint mark.
     * 
     * @return the breakpoint mark element
     */
    @Nonnull
    private Element createBreakpointMark() {
        final Element mainElement = Elements.createDivElement();

        final Element imageElement = getMarkElement(false);
        mainElement.appendChild(imageElement);

        mainElement.getStyle().setCursor(CSSStyleDeclaration.Cursor.POINTER);
        mainElement.getDataset().setAt(BREAKPOINT_ACTIVE_PROPERTY_NAME, Boolean.FALSE);

        return mainElement;
    }

    /**
     * Changes (if needed) the appearance of the breakpoint mark to match the given state.
     * 
     * @param markElement the element to change
     * @param active the new state
     */
    private void setActiveMark(final @Nonnull Element markElement, final boolean active) {
        if (active == isActive(markElement)) {
            return;
        }

        final Element newImageElement = getMarkElement(active);

        final Element oldImageElement = markElement.getFirstElementChild();
        markElement.replaceChild(newImageElement, oldImageElement);
        markElement.getDataset().setAt(BREAKPOINT_ACTIVE_PROPERTY_NAME, Boolean.toString(active));
    }

    /**
     * Creates a mark element for the given state.
     * 
     * @param active the state
     * @return the mark element
     */
    @Nonnull
    private Element getMarkElement(final boolean active) {
        if (active) {
            final Image i = new Image(breakpointResources.currentBreakpoint());
            return (Element)i.getElement();
        } else {
            final Image i = new Image(breakpointResources.breakpoint());
            return (Element)i.getElement();
        }
    }

    /**
     * Tells which is the mark element appearance: active/not active.
     * 
     * @param mark the element
     * @return true iff the appearance of the element is 'active'
     */
    private boolean isActive(final @Nonnull Element mark) {
        final Object activeProp = mark.getDataset().at(BREAKPOINT_ACTIVE_PROPERTY_NAME);
        if (activeProp == null) {
            return false;
        }
        return Boolean.valueOf(activeProp.toString());
    }

    @Override
    public boolean isReady() {
        return this.hasGutter != null && this.lineStyler != null;
    }
}