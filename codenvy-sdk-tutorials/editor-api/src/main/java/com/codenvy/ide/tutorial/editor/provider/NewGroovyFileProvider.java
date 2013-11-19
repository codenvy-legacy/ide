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
package com.codenvy.ide.tutorial.editor.provider;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceProvider;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import static com.codenvy.ide.tutorial.editor.EditorTutorialExtension.GROOVY_MIME_TYPE;

/**
 * Provides creating of a new Groovy file.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NewGroovyFileProvider extends NewResourceProvider {

    @Inject
    public NewGroovyFileProvider() {
        super("Groovy file", "Groovy file", null, "groovy");
    }

    /** {@inheritDoc} */
    @Override
    public void create(@NotNull String name, @NotNull final Folder parent, @NotNull final Project project,
                       @NotNull final AsyncCallback<Resource> callback) {
        String content = "//Pattern for groovy script\n" +
                         "def p = ~/.*\\.groovy/\n" +
                         "new File( 'd:\\\\scripts' ).eachFileMatch(p) {f ->\n" +
                         "  // imports list\n" +
                         "  def imports = []\n" +
                         "  f.eachLine {\n" +
                         "    // condition to detect an import instruction\n" +
                         "    ln -> if ( ln =~ '^import .*' ) {\n" +
                         "      imports << \"${ln - 'import '}\"\n" +
                         "    }\n" +
                         "  }\n" +
                         "  // print thmen\n" +
                         "  if ( ! imports.empty ) {\n" +
                         "    println f\n" +
                         "    imports.each{ println \"   $it\" }\n" +
                         "  }\n" +
                         "}\n" +
                         "\n" +
                         "/* Coin changer demo code from http://groovy.codehaus.org */\n" +
                         "\n" +
                         "enum UsCoin {\n" +
                         "  quarter(25), dime(10), nickel(5), penny(1)\n" +
                         "  UsCoin(v) { value = v }\n" +
                         "  final value\n" +
                         "}\n" +
                         "\n" +
                         "enum OzzieCoin {\n" +
                         "  fifty(50), twenty(20), ten(10), five(5)\n" +
                         "  OzzieCoin(v) { value = v }\n" +
                         "  final value\n" +
                         "}\n" +
                         "\n" +
                         "def plural(word, count) {\n" +
                         "  if (count == 1) return word\n" +
                         "  word[-1] == 'y' ? word[0..-2] + \"ies\" : word + \"s\"\n" +
                         "}\n" +
                         "\n" +
                         "def change(currency, amount) {\n" +
                         "  currency.values().inject([]){ list, coin ->\n" +
                         "     int count = amount / coin.value\n" +
                         "     amount = amount % coin.value\n" +
                         "     list += \"$count ${plural(coin.toString(), count)}\"\n" +
                         "  }\n" +
                         "}\n";

        project.createFile(parent, name + '.' + getExtension(), content, GROOVY_MIME_TYPE, new AsyncCallback<File>() {
            @Override
            public void onSuccess(File file) {
                callback.onSuccess(file);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }
}