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
package org.exoplatform.ide.editor.java.hover;

import com.google.gwt.event.shared.HandlerManager;

import org.eclipse.jdt.client.UpdateOutlineEvent;
import org.eclipse.jdt.client.UpdateOutlineHandler;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.internal.text.JavaWordFinder;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.hover.TextHover;
import org.exoplatform.ide.editor.shared.text.IRegion;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class AbstractJavaHover implements TextHover, UpdateOutlineHandler {

    protected CompilationUnit cUnit;

    /**
     *
     */
    public AbstractJavaHover(HandlerManager eventBus) {
        eventBus.addHandler(UpdateOutlineEvent.TYPE, this);
    }

    /** @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent) */
    @Override
    public void onUpdateOutline(UpdateOutlineEvent event) {
        cUnit = event.getCompilationUnit();
    }

    /** @see org.exoplatform.ide.editor.client.hover.TextHover#getHoverRegion(org.exoplatform.ide.editor.client.api.Editor, int) */
    @Override
    public IRegion getHoverRegion(Editor editor, int offset) {
        return JavaWordFinder.findWord(editor.getDocument(), offset);
    }

}
