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
package org.exoplatform.ide.client.properties;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class ShowPropertiesControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
                                                                    ViewOpenedHandler, ViewClosedHandler {

    public static final String ID = "View/Properties";

    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.showPropertiesTitleControl();

    public static final String PROMPT_SHOW = IDE.IDE_LOCALIZATION_CONSTANT.showPropertiesShowPromptControl();

    public static final String PROMPT_HIDE = IDE.IDE_LOCALIZATION_CONSTANT.showPropertiesHidePromptControl();

    /**
     *
     */
    public ShowPropertiesControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT_SHOW);
        setImages(IDEImageBundle.INSTANCE.properties(), IDEImageBundle.INSTANCE.propertiesDisabled());
        setEvent(new ShowPropertiesEvent(true));
        setGroupName(GroupNames.PROPERTIES);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(ViewOpenedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getFile() == null) {
            setVisible(false);
            setEnabled(false);
            return;
        }

        setVisible(true);

        if (!event.getFile().isPersisted()) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof PropertiesPresenter.Display) {
            setSelected(false);
            setPrompt(PROMPT_SHOW);
            setEvent(new ShowPropertiesEvent(true));
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewOpenedEvent) */
    @Override
    public void onViewOpened(ViewOpenedEvent event) {
        if (event.getView() instanceof PropertiesPresenter.Display) {
            setSelected(true);
            setPrompt(PROMPT_HIDE);
            setEvent(new ShowPropertiesEvent(false));
        }
    }

}
