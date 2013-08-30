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
package org.eclipse.jdt.client.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FolderModel;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class PackageCreatedEvent extends GwtEvent<PackageCreatedHandler> {

    public static final Type<PackageCreatedHandler> TYPE = new Type<PackageCreatedHandler>();

    private final FolderModel parentFolder;

    private final String pack;

    /**
     * @param pack
     * @param parentFolder
     */
    public PackageCreatedEvent(String pack, FolderModel parentFolder) {
        this.pack = pack;
        this.parentFolder = parentFolder;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<PackageCreatedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(PackageCreatedHandler handler) {
        handler.onPackageCreated(this);
    }

    /** @return the parentFolder */
    public FolderModel getParentFolder() {
        return parentFolder;
    }

    /** @return the pack */
    public String getPack() {
        return pack;
    }

}
