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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.event.ShowAstEvent;
import org.eclipse.jdt.client.event.ShowAstHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 20, 2012 1:28:01 PM evgen $
 */
public class AstPresenter implements ShowAstHandler, ViewClosedHandler, UpdateOutlineHandler {

    public interface Display extends IsView {
        String id = "AstView";

        void drawAst(CompilationUnit cUnit);
    }

    private Display display;

    private CompilationUnit unit;

    /**
     *
     */
    public AstPresenter(HandlerManager eventBus) {
        eventBus.addHandler(ShowAstEvent.TYPE, this);
        eventBus.addHandler(ViewClosedEvent.TYPE, this);
        eventBus.addHandler(UpdateOutlineEvent.TYPE, this);

    }

    /** @see org.eclipse.jdt.client.event.ShowAstHandler#onShowAst(org.eclipse.jdt.client.event.ShowAstEvent) */
    @Override
    public void onShowAst(ShowAstEvent event) {

        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
        }
        if (unit != null)
            display.drawAst(unit);
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display)
            display = null;
    }

    /** @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent) */
    @Override
    public void onUpdateOutline(UpdateOutlineEvent event) {
        unit = event.getCompilationUnit();
    }

}
