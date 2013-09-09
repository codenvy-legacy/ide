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
package org.exoplatform.ide.editor.gadget.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.ui.client.util.UIHelper;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Images Mar 22, 2011 9:43:48 AM evgen $
 */
public interface Images extends ClientBundle {
    Images INSTANCE = GWT.create(Images.class);
    public static final String IMAGE_URL = UIHelper.getGadgetImagesURL();

    public static final String GOOGLE_GADGET = IMAGE_URL + "images-gadget/gadget.png";

    @Source("org/exoplatform/ide/editor/gadget/public/images/images-gadget/gadget.png")
    ImageResource gadgetImage();
}
