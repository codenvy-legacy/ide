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
package org.exoplatform.ide.extension.heroku.client.control;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.key.AddKeyEvent;

/**
 * Control for adding keys on Heroku.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 31, 2011 9:32:28 AM anya $
 */
@RolesAllowed({"workspace/developer"})
public class AddKeyControl extends AbstractHerokuControl {

    public AddKeyControl() {
        super(HerokuExtension.LOCALIZATION_CONSTANT.addKeyControlId());
        setTitle(HerokuExtension.LOCALIZATION_CONSTANT.addKeyControlTitle());
        setPrompt(HerokuExtension.LOCALIZATION_CONSTANT.addKeyControlPrompt());
        setEvent(new AddKeyEvent());
        setImages(HerokuClientBundle.INSTANCE.addKeys(), HerokuClientBundle.INSTANCE.addKeysDisabled());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);

        setVisible(true);
    }

    /**
     *
     */
    protected void refresh() {
        setEnabled(vfsInfo != null);
    }

}
