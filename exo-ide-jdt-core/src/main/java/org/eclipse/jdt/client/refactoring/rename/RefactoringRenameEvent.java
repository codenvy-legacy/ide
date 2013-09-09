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
package org.eclipse.jdt.client.refactoring.rename;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RefactoringRenameEvent.java Jan 17, 2013 4:51:38 PM azatsarynnyy $
 */
public class RefactoringRenameEvent extends GwtEvent<RefactoringRenameHandler> {

    private FileModel file;

    public static final GwtEvent.Type<RefactoringRenameHandler> TYPE = new Type<RefactoringRenameHandler>();

    public RefactoringRenameEvent() {
    }

    /** @param file */
    public RefactoringRenameEvent(FileModel file) {
        this.file = file;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RefactoringRenameHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(RefactoringRenameHandler handler) {
        handler.onRename(this);
    }

    /**
     * Returns file to rename.
     *
     * @return a file to rename
     */
    public FileModel getFile() {
        return file;
    }

}
