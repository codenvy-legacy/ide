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
package org.exoplatform.ide.extension.heroku.client.stack;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.shared.Stack;

/**
 * Grid for displaying application's stacks.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 29, 2011 11:33:38 AM anya $
 */
public class StackGrid extends ListGrid<Stack> {
    private final String ID = "ideStackGrid";

    private final String STACK = HerokuExtension.LOCALIZATION_CONSTANT.changeStackViewStackField();

    private final String BETA = HerokuExtension.LOCALIZATION_CONSTANT.changeStackViewBetaField();

    public StackGrid() {
        super();

        setID(ID);

        Column<Stack, SafeHtml> nameColumn = new Column<Stack, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final Stack stack) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        return (stack.isCurrent()) ? "<b>" + stack.getName() + "</b>" : stack.getName();
                    }
                };
                return html;
            }
        };

        Column<Stack, SafeHtml> betaColumn = new Column<Stack, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(final Stack stack) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        if (stack.isBeta())
                            return (stack.isCurrent()) ? "<b>beta</b>" : "beta";
                        return "";
                    }
                };
                return html;
            }
        };

        getCellTable().addColumn(nameColumn, STACK);
        getCellTable().setColumnWidth(nameColumn, "70%");
        getCellTable().addColumn(betaColumn, BETA);
        getCellTable().setColumnWidth(betaColumn, "30%");
    }

}
