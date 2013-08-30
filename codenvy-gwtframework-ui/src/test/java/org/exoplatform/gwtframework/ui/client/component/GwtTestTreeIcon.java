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
package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.RootPanel;

import org.exoplatform.gwtframework.ui.client.testcase.ShowCaseImageBundle;
import org.exoplatform.gwtframework.ui.client.tree.TreeRecord;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: $
 */
public class GwtTestTreeIcon extends GwtComponentTest {

    //call method for add icon in panel
    public void testCheckAddIcon() {

        //create folder icon
        TreeIcon folderIcon = new TreeIcon(TreeRecord.Images.FOLDER_CLOSED, null, null);
        //set id for icon
        folderIcon.getElement().setId("foldericon");

        //set on rootpanel folder icon
        RootPanel.get().add(folderIcon);

        //create element for iconfolder
        Element elementFolderIcon = Document.get().getElementById("foldericon");

        //add in List all elements with img tag
        NodeList<Element> elementsByTagName = elementFolderIcon.getElementsByTagName("img");

        //string variable for name url icon
        String nameFolderIcon = TreeRecord.Images.FOLDER_CLOSED;

        //move in list and search folder icon
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            Element element = elementsByTagName.getItem(i);
            if (element instanceof ImageElement)
                ;
            //chek create icon in root panel
            assertEquals(nameFolderIcon, ImageElement.as(element).getSrc());
        }

    }

    //method for validation in DOM add and remove TopRightIcon icon
    public void testTopRight() {
        //create
        TreeIcon topRightIcon = new TreeIcon(ShowCaseImageBundle.INSTANCE.itemInRepository().getURL());

        //add new icon
        topRightIcon.addIcon(TreeIconPosition.TOPLEFT, ShowCaseImageBundle.INSTANCE.itemInRepository());

        //set id for fold
        topRightIcon.getElement().setId("toprighticon");

        //set on rootpanel icon
        RootPanel.get().add(topRightIcon);

        //create element for iconfolder
        Element elementTopRightIcon = Document.get().getElementById("toprighticon");

        //add in List  elements with img tag
        NodeList<Element> elementsByTagName = elementTopRightIcon.getElementsByTagName("img");

        //string variable for name url icon
        String nameTopRightIcon = ShowCaseImageBundle.INSTANCE.itemInRepository().getURL();

        //move in list and search folder icon
        for (int i = 0; i < elementsByTagName.getLength(); i++) {

            Element element = elementsByTagName.getItem(i);
            if (element instanceof ImageElement)
                ;
            //chek create icon in root panel
            assertEquals(nameTopRightIcon, ImageElement.as(element).getSrc());
        }

        //remove icon
        topRightIcon.removeIcon(TreeIconPosition.TOPLEFT);

        //chek remove in root panel
        //add in List elements with img tag
        NodeList<Element> elementsByRemoveTagName = elementTopRightIcon.getElementsByTagName("img");

        //chek remove the icon
        for (int i = 0; i < elementsByRemoveTagName.getLength(); i++) {
            Element element = elementsByRemoveTagName.getItem(i);
            if (element instanceof ImageElement)
                if (nameTopRightIcon == ImageElement.as(element).getSrc()) {
                    assertTrue(false);
                    break;
                }
        }
    }

    //method for validation in DOM add and remove bottomright icon
    public void testBottomRightIcon() {
        //create
        TreeIcon bottomRightIcon = new TreeIcon(ShowCaseImageBundle.INSTANCE.itemAddRepository().getURL());

        //add new icon
        bottomRightIcon.addIcon(TreeIconPosition.BOTTOMRIGHT, ShowCaseImageBundle.INSTANCE.itemAddRepository());

        //set id for fold
        bottomRightIcon.getElement().setId("bottomRightIcon");

        //set on rootpanel icon
        RootPanel.get().add(bottomRightIcon);

        //create element for iconfolder
        Element elementBottomRightIcon = Document.get().getElementById("bottomRightIcon");

        //add in List  elements with img tag
        NodeList<Element> elementsByTagName = elementBottomRightIcon.getElementsByTagName("img");

        //string variable for name url icon
        String nameBottomRightIcon = ShowCaseImageBundle.INSTANCE.itemAddRepository().getURL();

        //variable for result search in loop

        //move in list and search folder icon
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            Element element = elementsByTagName.getItem(i);
            if (element instanceof ImageElement)
                ;
            //chek create icon in root panel
            assertEquals(nameBottomRightIcon, ImageElement.as(element).getSrc());
        }

        //remove icon
        bottomRightIcon.removeIcon(TreeIconPosition.TOPLEFT);

        //chek remove in root panel
        //add in List elements with img tag
        NodeList<Element> elementsByRemoveTagName = elementBottomRightIcon.getElementsByTagName("img");

        //chek remove the icon
        for (int i = 0; i < elementsByRemoveTagName.getLength(); i++) {
            Element element = elementsByRemoveTagName.getItem(i);
            if (element instanceof ImageElement) {
                if (nameBottomRightIcon == ImageElement.as(element).getSrc())
                    assertTrue(false);
                break;

            }
        }
    }
}
