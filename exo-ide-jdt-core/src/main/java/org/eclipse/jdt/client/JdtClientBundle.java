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
package org.eclipse.jdt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Apr 3, 2012 3:08:46 PM anya $
 */
public interface JdtClientBundle extends ClientBundle {
    JdtClientBundle INSTANCE = GWT.<JdtClientBundle>create(JdtClientBundle.class);

    @Source("org/eclipse/jdt/client/core/formatter/exo-jboss-codestyle.xml")
    ExternalTextResource eXoProfile();

    @Source("org/eclipse/jdt/client/core/formatter/formatter-sample.txt")
    TextResource formatterSample();

    @Source("org/eclipse/jdt/images/controls/clean.png")
    ImageResource clean();

    @Source("org/eclipse/jdt/images/controls/clean_Disabled.png")
    ImageResource cleanDisabled();

    @Source("org/eclipse/jdt/images/controls/format.png")
    ImageResource formatterProfiles();

    @Source("org/eclipse/jdt/images/controls/format_Disabled.png")
    ImageResource formatterProfilesDisabled();

    @Source("org/eclipse/jdt/images/controls/imports.png")
    ImageResource organizeImports();

    @Source("org/eclipse/jdt/images/controls/imports_Disabled.png")
    ImageResource organizeImportsDisabled();

    @Source("org/eclipse/jdt/images/controls/quick_fix.png")
    ImageResource quickFix();

    @Source("org/eclipse/jdt/images/controls/quick_fix_Disabled.png")
    ImageResource quickFixDisabled();

    @Source("org/eclipse/jdt/images/controls/quick_outline.png")
    ImageResource quickOutline();

    @Source("org/eclipse/jdt/images/controls/quick_outline_Disabled.png")
    ImageResource quickOutlineDisabled();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/correction_change.gif")
    ImageResource correction_change();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/field_public_obj.gif")
    ImageResource field_public();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/local.png")
    ImageResource local_var();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/private-field.png")
    ImageResource privateField();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/correction_cast.gif")
    ImageResource correction_cast();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/public-method.png")
    ImageResource publicMethod();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/packd_obj.gif")
    ImageResource packd_obj();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/delete_obj.gif")
    ImageResource delete_obj();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/correction_delete_import.gif")
    ImageResource correction_delete_import();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/private-method.png")
    ImageResource privateMethod();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/remove_correction.gif")
    ImageResource remove_correction();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/jexception_obj.gif")
    ImageResource exceptionProp();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/javadoc.gif")
    ImageResource javadoc();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/add_obj.gif")
    ImageResource add_obj();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/imp_obj.gif")
    ImageResource imp_obj();

    @Source("org/eclipse/jdt/client/internal/text/correction/proposals/protected-method.png")
    ImageResource protectedMethod();

    @Source("org/eclipse/jdt/images/controls/newJavaclass_wiz.gif")
    ImageResource newClassWizz();

    @Source("org/eclipse/jdt/images/controls/newJavaclass_wiz_Disabled.gif")
    ImageResource newClassWizzDisabled();

    /*
     * Package Explorer
     */
    @Source("org/eclipse/jdt/images/package-explorer/package-explorer.png")
    ImageResource packageExplorer();

    @Source("org/eclipse/jdt/images/package-explorer/package-explorer-disabled.png")
    ImageResource packageExplorerDisabled();

    @Source("org/eclipse/jdt/images/package-explorer/expand-all.png")
    ImageResource expandAll();

    @Source("org/eclipse/jdt/images/package-explorer/expand-all-disabled.png")
    ImageResource expandAllDisabled();

    @Source("org/eclipse/jdt/images/package-explorer/collapse-all.png")
    ImageResource collapseAll();

    @Source("org/eclipse/jdt/images/package-explorer/collapse-all-disabled.png")
    ImageResource collapseAllDisabled();

    @Source("org/eclipse/jdt/images/package-explorer/package.png")
    ImageResource packageFolder();

    @Source("org/eclipse/jdt/images/package-explorer/package-disabled.png")
    ImageResource packageFolderDisabled();

    @Source("org/eclipse/jdt/images/package-explorer/empty-package.png")
    ImageResource packageEmptyFolder();

    @Source("org/eclipse/jdt/images/package-explorer/empty-package-disabled.png")
    ImageResource packageEmptyFolderDisabled();

    @Source("org/eclipse/jdt/images/package-explorer/jar-references.png")
    ImageResource jarReferences();

    @Source("org/eclipse/jdt/images/package-explorer/jar-references-disabled.png")
    ImageResource jarReferencesDisabled();

    @Source("org/eclipse/jdt/images/package-explorer/single-jar-reference.png")
    ImageResource jarReference();

    @Source("org/eclipse/jdt/images/package-explorer/single-jar-reference-disabled.png")
    ImageResource jarReferenceDisabled();

    @Source("org/eclipse/jdt/images/package-explorer/resource-directory.png")
    ImageResource resourceDirectory();

    @Source("org/eclipse/jdt/images/package-explorer/resource-directory-disabled.png")
    ImageResource resourceDirectoryDisabled();

}
