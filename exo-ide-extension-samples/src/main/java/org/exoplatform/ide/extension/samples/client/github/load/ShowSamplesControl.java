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
package org.exoplatform.ide.extension.samples.client.github.load;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * Control to call Try Out Samples form.
 * <p/>
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubControl.java Nov 18, 2011 5:06:02 PM vereshchaka $
 */
@RolesAllowed({"developer"})
public class ShowSamplesControl extends SimpleControl implements IDEControl, VfsChangedHandler {

    private static final String ID = SamplesExtension.LOCALIZATION_CONSTANT.loadSamplesControlId();

    private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.loadSamplesControlTitle();

    private static final String PROMPT = SamplesExtension.LOCALIZATION_CONSTANT.loadSamplesControlPrompt();

    private VirtualFileSystemInfo vfsInfo;

    /** @param id */
    public ShowSamplesControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(SamplesClientBundle.INSTANCE.importSamplesControl(),
                  SamplesClientBundle.INSTANCE.importSamplesDisabledControl());
        setEvent(new ShowSamplesEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(false);

        IDE.addHandler(VfsChangedEvent.TYPE, this);

        updateEnabling();
    }

    private void updateEnabling() {
        setEnabled(vfsInfo != null);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        updateEnabling();
    }

}
