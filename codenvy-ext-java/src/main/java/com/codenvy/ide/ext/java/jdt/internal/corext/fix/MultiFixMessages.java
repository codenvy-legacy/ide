/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.corext.fix;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface MultiFixMessages extends Messages {
    MultiFixMessages INSTANCE = GWT.create(MultiFixMessages.class);

    String CodeStyleMultiFix_ConvertSingleStatementInControlBodeyToBlock_description();

    String ControlStatementsCleanUp_RemoveUnnecessaryBlocks_description();

    String ControlStatementsCleanUp_RemoveUnnecessaryBlocksWithReturnOrThrow_description();

    String ExpressionsCleanUp_addParanoiac_description();

    String ExpressionsCleanUp_removeUnnecessary_description();

    String UnimplementedCodeCleanUp_AddUnimplementedMethods_description();

    String UnimplementedCodeCleanUp_MakeAbstract_description();

    String UnusedCodeMultiFix_RemoveUnusedImport_description();

    String UnusedCodeMultiFix_RemoveUnusedMethod_description();

    /** @return  */
    String UnusedCodeMultiFix_RemoveUnusedConstructor_description();

    /** @return  */
    String UnusedCodeMultiFix_RemoveUnusedType_description();

    /** @return  */
    String UnusedCodeMultiFix_RemoveUnusedField_description();

    /** @return  */
    String UnusedCodeMultiFix_RemoveUnusedVariable_description();

    /** @return  */
    String CodeStyleMultiFix_AddThisQualifier_description();

    /** @return  */
    String CodeStyleCleanUp_QualifyNonStaticMethod_description();

    /** @return  */
    String CodeStyleCleanUp_removeFieldThis_description();

    /** @return  */
    String CodeStyleCleanUp_removeMethodThis_description();

    /** @return  */
    String CodeStyleMultiFix_QualifyAccessToStaticField();

    /** @return  */
    String CodeStyleCleanUp_QualifyStaticMethod_description();

    /** @return  */
    String CodeStyleMultiFix_ChangeNonStaticAccess_description();

    /** @return  */
    String CodeStyleMultiFix_ChangeIndirectAccessToStaticToDirect();

    /** @return  */
    String UnusedCodeCleanUp_RemoveUnusedCasts_description();

}
