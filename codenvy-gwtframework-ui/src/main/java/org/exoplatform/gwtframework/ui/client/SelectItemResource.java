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
package org.exoplatform.gwtframework.ui.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;


/**
 * The resources for the {@link SelectItemOld} component.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 1, 2011 5:21:24 PM anya $
 */
public interface SelectItemResource extends ClientBundle {
    @Source("eXoStyle/skin/default/SelectItem.css")
    Style css();

    @Source("eXoStyle/skin/default/images/select/arrow-down.png")
    ImageResource arrow();

    /** CSS style resources. */
    public interface Style extends CssResource {
        String selectItem();

        String selectItemDisabled();

        String selectItemTitle();

        String selectItemTitleHidden();

        String labelOrientationTop();

        String labelOrientationLeft();

        String labelOrientationRight();

        String labelAlignCenter();

        String labelAlignLeft();

        String labelAlignRight();

        String editableSelect();

        String comboboxSelectPanel();

        String comboBox();

        String comboBoxInput();

        @ClassName("combobox-grid")
        String comboBoxGrid();

        String comboBoxImageButton();

        String comboBoxDocPanel();

    }
}
