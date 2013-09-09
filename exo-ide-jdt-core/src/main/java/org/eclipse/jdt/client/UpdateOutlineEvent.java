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
package org.eclipse.jdt.client;

import com.google.gwt.event.shared.GwtEvent;

import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Event occurs to update Java Outline. To handle it implement {@link UpdateOutlineHandler}.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 5:25:54 PM anya $
 */
public class UpdateOutlineEvent extends GwtEvent<UpdateOutlineHandler> {

    /** Type used to register event. */
    public static final GwtEvent.Type<UpdateOutlineHandler> TYPE = new GwtEvent.Type<UpdateOutlineHandler>();

    /** Compilation unit with info for updating Outline. */
    private CompilationUnit compilationUnit;

    /** File, that was parsed. */
    private FileModel file;

    /**
     * @param compilationUnit
     *         compilation unit with info for updating Outline
     * @param file
     *         file, that was parsed
     */
    public UpdateOutlineEvent(CompilationUnit compilationUnit, FileModel file) {
        this.compilationUnit = compilationUnit;
        this.file = file;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UpdateOutlineHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(UpdateOutlineHandler handler) {
        handler.onUpdateOutline(this);
    }

    /** @return {@link CompilationUnit} compilation unit with info for updating Outline */
    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    /** @return the file */
    public FileModel getFile() {
        return file;
    }
}
