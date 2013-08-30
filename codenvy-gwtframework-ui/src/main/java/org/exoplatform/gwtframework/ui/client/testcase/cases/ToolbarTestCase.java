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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;

import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.testcase.ShowCaseImageBundle;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarTestCase extends TestCase {

    private Toolbar toolbar;

    @Override
    public void draw() {
        FlowPanel panel = new FlowPanel();
        DOM.setStyleAttribute(panel.getElement(), "position", "relative");
        DOM.setStyleAttribute(panel.getElement(), "left", "10px");
        DOM.setStyleAttribute(panel.getElement(), "top", "50px");
        DOM.setStyleAttribute(panel.getElement(), "width", "600px");
        DOM.setStyleAttribute(panel.getElement(), "height", "100px");
        //DOM.setStyleAttribute(panel.getElement(), "background", "#FFAAEE");
        DOM.setStyleAttribute(panel.getElement(), "borderWidth", "1px");
        DOM.setStyleAttribute(panel.getElement(), "borderStyle", "solid");
        DOM.setStyleAttribute(panel.getElement(), "borderColor", "#AAAAAA");
        testCasePanel().add(panel);

        toolbar = new Toolbar();
        panel.add(toolbar);

        addButtonHeader("Icon button");
        createButton("Add Left", addButtonLeftClickHandler);
        createButton("Add Right", addButtonRightClickHandler);
        addButtonDelimiter("Loader");
//      createButton("Add Left", addLoaderLeftClickHandler);
//      createButton("Add Right", addLoaderRightClickHandler);

        addButtonDelimiter("Toolbar Delimiter");
        createButton("Add Left", addDelimiterLeftClickHandler);
        createButton("Add Right", addDelimiterRightClickHandler);

        addButtonDelimiter("Actions");
        //createButtonGroup();
        createButton("Clear", true, clearToolbarClickHandler);
        createButton("Show All Items", true, showAllItemsClickHandler);
        createButton("Hide Duplicated Delimiters", true, removeDuplicatedDelimitersClickHandler);
    }

    private ClickHandler clearToolbarClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            toolbar.clear();
        }
    };

    private ClickHandler addButtonLeftClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            IconButton iconButton =
                    new IconButton(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.add()),
                                   ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.addDisabled()));

            toolbar.addItem(iconButton);
        }
    };

    private ClickHandler addButtonRightClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            IconButton iconButton =
                    new IconButton(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.add()),
                                   ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.addDisabled()));
            toolbar.addItem(iconButton, true);
        }
    };

//   private ClickHandler addLoaderLeftClickHandler = new ClickHandler()
//   {
//      public void onClick(ClickEvent event)
//      {
//         LoadingIndicator loader =
//            new LoadingIndicator(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.remove()));
//         loader.setCommand(new Command()
//         {
//            public void execute()
//            {
//               Window.alert("Click!");
//            }
//         });
//
//         toolbar.addItem(loader);
//      }
//   };
//
//   private ClickHandler addLoaderRightClickHandler = new ClickHandler()
//   {
//      public void onClick(ClickEvent event)
//      {
//         LoadingIndicator loader =
//            new LoadingIndicator(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.remove()));
//         loader.setCommand(new Command()
//         {
//            public void execute()
//            {
//               Window.alert("Click!");
//            }
//         });
//
//         toolbar.addItem(loader, true);
//      }
//   };

    private ClickHandler addDelimiterLeftClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            toolbar.addDelimiter();
        }
    };

    private ClickHandler addDelimiterRightClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            toolbar.addDelimiter(true);
        }
    };

    private ClickHandler showAllItemsClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            toolbar.showAllItems();
        }
    };

    private ClickHandler removeDuplicatedDelimitersClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            toolbar.hideDuplicatedDelimiters();
        }
    };

}
