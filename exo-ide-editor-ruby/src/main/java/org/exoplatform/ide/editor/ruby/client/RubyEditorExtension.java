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
package org.exoplatform.ide.editor.ruby.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.editor.AddCommentsModifierEvent;
import org.exoplatform.ide.client.framework.module.EditorCreator;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.client.api.Editor;

/**
 * Provides a text editing area along with UI for executing text commands on the.<br>
 * Support syntax coloration for Ruby language (http://www.ruby-lang.org/en/)
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Revision$
 */
public class RubyEditorExtension extends Extension implements InitializeServicesHandler {

    interface DefaultContent extends ClientBundle {
        @Source("hello.rb")
        TextResource getSource();
    }

    public final static DefaultContent DEFAULT_CONTENT = GWT.create(DefaultContent.class);

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        IDE.getInstance().addControl(new NewItemControl("File/New/New Ruby File", "Ruby File", "Create Ruby File",
                                                        RubyClientBundle.INSTANCE.ruby(), RubyClientBundle.INSTANCE.rubyDisabled(),
                                                        MimeType.APPLICATION_RUBY).setGroupName(GroupNames.NEW_SCRIPT));

        RubyClientBundle.INSTANCE.css().ensureInjected();
    }

    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        IDE.getInstance().getFileTypeRegistry()
           .addFileType(
                        new FileType(MimeType.APPLICATION_RUBY, "rb", RubyClientBundle.INSTANCE.ruby()),
                        new EditorCreator() {
                            @Override
                            public Editor createEditor() {
                                return new RubyEditor(MimeType.APPLICATION_RUBY);
                                // return new CodeMirror(MimeType.APPLICATION_RUBY, new CodeMirrorConfiguration()
                                // .setGenericParsers("['parseruby.js', 'tokenizeruby.js']")
                                // .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/rubycolors.css']")
                                // .setParser(new RubyParser())
                                // .setCanBeOutlined(true)
                                // .setAutocompleteHelper(new RubyAutocompleteHelper())
                                // .setCodeAssistant(new RubyCodeAssistant()));
                            }
                        });

        IDE.getInstance().getFileTypeRegistry()
           .addFileType(
                        new FileType("MimeType.APPLICATION_RUBY_HTML", "rb", RubyClientBundle.INSTANCE.ruby()),
                        new EditorCreator() {
                            @Override
                            public Editor createEditor() {
                                return new RubyEditor(MimeType.APPLICATION_RUBY_HTML);
                                // return new CodeMirror(MimeType.APPLICATION_RUBY_HTML, new CodeMirrorConfiguration()
                                // .setGenericParsers("['parseruby.js', 'tokenizeruby.js']")
                                // .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/rubycolors.css']")
                                // .setParser(new RubyParser())
                                // .setCanBeOutlined(true)
                                // .setAutocompleteHelper(new RubyAutocompleteHelper())
                                // .setCodeAssistant(new RubyCodeAssistant()));
                            }
                        });

        // IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_RUBY, new RubyOutlineItemCreator());
        // IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_RUBY_HTML, new RubyOutlineItemCreator());

        IDE.fireEvent(new AddCommentsModifierEvent(MimeType.APPLICATION_RUBY, new RubyCommentModifier()));
    }

}
