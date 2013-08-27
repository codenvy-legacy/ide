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
package org.exoplatform.ide.editor.java.client;

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.editor.AddCommentsModifierEvent;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.java.client.codeassistant.services.JavaCodeAssistantService;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyEditorExtension Mar 10, 2011 3:48:59 PM evgen $
 */
public class JavaEditorExtension extends Extension implements InitializeServicesHandler, JavaCodeAssistantErrorHandler {

    public static final JavaConstants MESSAGES = GWT.create(JavaConstants.class);

    public static final JavaCodeAssistantAutoBeanFactory AUTO_BEAN_FACTORY = GWT
            .create(JavaCodeAssistantAutoBeanFactory.class);

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
        JavaClientBundle.INSTANCE.css().ensureInjected();
    }

    /** @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     * .client.framework.application.event.InitializeServicesEvent) */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {

        if (JavaCodeAssistantService.get() == null)
            new JavaCodeAssistantService(Utils.getRestContext(), Utils.getWorkspaceName(), event.getLoader());

        IDE.fireEvent(new AddCommentsModifierEvent(MimeType.APPLICATION_JAVA, new JavaCommentsModifier()));
    }

    /** @see org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistantErrorHandler#handleError(java.lang.Throwable) */
    @Override
    public void handleError(Throwable exc) {
        if (exc instanceof ServerException) {
            ServerException exception = (ServerException)exc;
            String outputContent =
                    "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
            if (!exception.getMessage().equals("")) {
                outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on
                // "<br />"
            }

            IDE.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
        } else {
            IDE.fireEvent(new ExceptionThrownEvent(exc.getMessage()));
        }
    }

}
