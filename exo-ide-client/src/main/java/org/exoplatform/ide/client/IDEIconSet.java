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
package org.exoplatform.ide.client;

import org.exoplatform.gwtframework.ui.client.util.ImageFactory;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEIconSet {

    public static void init() {
        ImageFactory.addImage("ok", IDEImageBundle.INSTANCE.ok(), IDEImageBundle.INSTANCE.okDisabled());
        ImageFactory.addImage("cancel", IDEImageBundle.INSTANCE.cancel(), IDEImageBundle.INSTANCE.cancelDisabled());
        ImageFactory.addImage("yes", IDEImageBundle.INSTANCE.ok(), IDEImageBundle.INSTANCE.okDisabled());
        ImageFactory.addImage("no", IDEImageBundle.INSTANCE.cancel(), IDEImageBundle.INSTANCE.cancelDisabled());
        ImageFactory.addImage("search", IDEImageBundle.INSTANCE.search(), IDEImageBundle.INSTANCE.searchDisabled());
        ImageFactory.addImage("delete", IDEImageBundle.INSTANCE.delete(), IDEImageBundle.INSTANCE.deleteDisabled());
        ImageFactory.addImage("properties", IDEImageBundle.INSTANCE.properties(),
                              IDEImageBundle.INSTANCE.propertiesDisabled());

        ImageFactory.addImage("add", IDEImageBundle.INSTANCE.add(), IDEImageBundle.INSTANCE.addDisabled());
        ImageFactory.addImage("remove", IDEImageBundle.INSTANCE.remove(), IDEImageBundle.INSTANCE.removeDisabled());
        ImageFactory.addImage("up", IDEImageBundle.INSTANCE.up(), IDEImageBundle.INSTANCE.upDisabled());
        ImageFactory.addImage("down", IDEImageBundle.INSTANCE.down(), IDEImageBundle.INSTANCE.downDisabled());
        ImageFactory.addImage("defaults", IDEImageBundle.INSTANCE.defaults(), IDEImageBundle.INSTANCE.defaultsDisabled());
        ImageFactory.addImage("hide", IDEImageBundle.INSTANCE.hide(), IDEImageBundle.INSTANCE.hideDisabled());
        ImageFactory.addImage("next", IDEImageBundle.INSTANCE.next(), IDEImageBundle.INSTANCE.nextDisabled());
        ImageFactory.addImage("back", IDEImageBundle.INSTANCE.back(), IDEImageBundle.INSTANCE.backDisabled());

        ImageFactory.addImage("upload", IDEImageBundle.INSTANCE.upload(), IDEImageBundle.INSTANCE.uploadDisabled());
        ImageFactory.addImage("edit", IDEImageBundle.INSTANCE.edit(), IDEImageBundle.INSTANCE.editDisabled());

        ImageFactory.addImage("link-with-editor", IDEImageBundle.INSTANCE.linkWithEditor(),
                              IDEImageBundle.INSTANCE.linkWithEditorDisabled());
    }

}
