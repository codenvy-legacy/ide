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
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

import org.eclipse.jdt.client.AstPresenter.Display;
import org.eclipse.jdt.client.astview.ASTTreeViewModel;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 20, 2012 1:33:45 PM evgen $
 */
public class AstView extends ViewImpl implements Display {

    private ScrollPanel scrollPanel;

    /**
     *
     */
    public AstView() {
        super(id, ViewType.INFORMATION, "AST");
        scrollPanel = new ScrollPanel(new Label("Parsing File..."));
        add(scrollPanel);
    }

    /** @see org.eclipse.jdt.client.AstPresenter.Display#drawAst(org.eclipse.jdt.client.core.dom.CompilationUnit) */
    @Override
    public void drawAst(CompilationUnit cUnit) {
        CellTree.Resources res = GWT.create(CellTree.BasicResources.class);
        CellTree cellTree = new CellTree(new ASTTreeViewModel(cUnit), null, res);
        scrollPanel.clear();
        scrollPanel.add(cellTree);
    }

}
