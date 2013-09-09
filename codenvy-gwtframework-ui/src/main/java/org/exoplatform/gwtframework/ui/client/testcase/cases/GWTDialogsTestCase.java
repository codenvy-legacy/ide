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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.GWTDialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GWTDialogsTestCase extends TestCase {

    @Override
    public void draw() {
        new GWTDialogs();

        ImageButton askForValueButton = new ImageButton("Ask For Value");
        testCasePanel().add(askForValueButton);

        askForValueButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Dialogs.getInstance().askForValue("Ask-value-title", "Ask-value-message", "default value",
                                                  new StringValueReceivedHandler() {
                                                      public void stringValueReceived(String value) {
                                                          Window.alert("String value received: [" + value + "]");
                                                      }
                                                  });
            }
        });

        testCasePanel().add(new HTML("<br>"));

        ImageButton askButton = new ImageButton("Ask");
        testCasePanel().add(askButton);

        askButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Dialogs.getInstance().ask("Ask-title", "Ask-message", new BooleanValueReceivedHandler() {
                    public void booleanValueReceived(Boolean value) {
                        Window.alert("Boolean value received: [" + value + "]");
                    }
                });
            }
        });

        testCasePanel().add(new HTML("<br>"));

        ImageButton warningButton = new ImageButton("Error");
        testCasePanel().add(warningButton);

        warningButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

                Dialogs.getInstance().showError("Error-title", "Error-message", new BooleanValueReceivedHandler() {
                    public void booleanValueReceived(Boolean value) {
                        Window.alert("Boolean value received: [" + value + "]");
                    }
                });

            }
        });

        testCasePanel().add(new HTML("<br>"));

        ImageButton infoButton = new ImageButton("Info");
        testCasePanel().add(infoButton);

        infoButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Dialogs.getInstance().showInfo("Info-title", "Info-message", new BooleanValueReceivedHandler() {
                    public void booleanValueReceived(Boolean value) {
                        Window.alert("Boolean value received: [" + value + "]");
                    }
                });

            }
        });

    }

}
