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
package org.exoplatform.ide.extension.samples.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.startpage.OpenStartPageEvent;
import org.exoplatform.ide.extension.samples.client.startpage.StartPagePresenter.Display;
import org.exoplatform.ide.extension.samples.client.startpage.WelcomePageOpenedEvent;
import org.exoplatform.ide.extension.samples.client.startpage.WelcomePageOpenedHandler;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * Control to show welcome page.
 * <p/>
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubControl.java Nov 18, 2011 5:06:02 PM vereshchaka $
 */
public class WelcomeControl extends SimpleControl implements IDEControl, VfsChangedHandler, ViewClosedHandler,
                                                             WelcomePageOpenedHandler {

    private static final String ID = SamplesExtension.LOCALIZATION_CONSTANT.welcomeControlId();

    private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.welcomeControlTitle();

    private static final String PROMPT = SamplesExtension.LOCALIZATION_CONSTANT.welcomeControlPrompt();

    private VirtualFileSystemInfo vfsInfo;

    private boolean opened;

    /** @param id */
    public WelcomeControl() {
        super(ID);
        setGroupName(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(SamplesClientBundle.INSTANCE.welcome(), SamplesClientBundle.INSTANCE.welcomeDisabled());
        setEvent(new OpenStartPageEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);

        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(WelcomePageOpenedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);

        updateEnabling();
    }

    private void updateEnabling() {
        if (vfsInfo == null) {
            setEnabled(false);
            return;
        }
        setEnabled(!opened);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        updateEnabling();
    }

    /** @see org.exoplatform.ide.extension.samples.client.startpage.WelcomePageOpenedHandler#onWelcomePageOpened(org.exoplatform.ide
     * .extension.samples.client.startpage.WelcomePageOpenedEvent) */
    @Override
    public void onWelcomePageOpened(WelcomePageOpenedEvent event) {
        opened = true;
        updateEnabling();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            opened = false;
            updateEnabling();
        }
    }

}
