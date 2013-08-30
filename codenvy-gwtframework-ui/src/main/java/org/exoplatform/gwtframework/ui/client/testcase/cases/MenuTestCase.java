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

package org.exoplatform.gwtframework.ui.client.testcase.cases;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

import org.exoplatform.gwtframework.ui.client.menu.MenuBar;
import org.exoplatform.gwtframework.ui.client.menu.MenuItem;
import org.exoplatform.gwtframework.ui.client.testcase.ShowCaseImageBundle;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MenuTestCase extends TestCase {

    @Override
    public void draw() {
        //border: 1px solid rgb(204, 204, 204);
        DOM.setStyleAttribute(testCasePanel().getElement(), "border", "none");
        MenuBar menu = new MenuBar();
        testCasePanel().add(menu);

        MenuItem fileItem = menu.addItem("File");
        {

            MenuItem newItem = fileItem.addItem("New");
            newItem.setHotKey("Ctrl+N");

            newItem.addItem("XML File", new Command() {
                public void execute() {
                    Window.alert("File/New/XML File Item Selected");
                }
            });

            newItem.addItem("HTML File");
            newItem.addItem(null);
            newItem.addItem("File from template...");

            fileItem.addItem(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.search()), "Search");
            MenuItem saveAllItem = fileItem.addItem(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.remove()), "Delete");
            saveAllItem.setEnabled(false);

            fileItem.addItem(null);
            fileItem.addItem(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.ok()), "Refresh");
        }

        MenuItem helpItem = menu.addItem("Help");
        helpItem.addItem("About");
        helpItem.setCommand(new Command() {
            public void execute() {
                Window.alert("Help/About Item Selected");
            }
        });

        System.out.println("= MENU DUMP ============================");
        System.out.println("" + menu);
        System.out.println("========================================");
    }

}
