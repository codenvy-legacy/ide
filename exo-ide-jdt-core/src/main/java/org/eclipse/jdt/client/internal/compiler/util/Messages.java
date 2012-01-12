/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.client.internal.compiler.util;

import com.google.gwt.core.client.GWT;

public interface Messages extends com.google.gwt.i18n.client.Messages
{
   Messages instance = GWT.create(Messages.class);

   @Key("compilation_unresolvedProblem")
   String compilation_unresolvedProblem();

   @Key("compilation_unresolvedProblems")
   String compilation_unresolvedProblems();

   @Key("compilation_request")
   String compilation_request(String a1, String a2, String a3);

   @Key("compilation_loadBinary")
   String compilation_loadBinary(String clazz);

   @Key("compilation_process")
   String compilation_process(String a1, String a2, String a3);

   @Key("compilation_write")
   String compilation_write(String a1, String a2);

   @Key("compilation_done")
   String compilation_done(String a1, String a2, String a3);

   @Key("compilation_units")
   String compilation_units(String a1);

   @Key("compilation_unit")
   String compilation_unit(String a1);

   @Key("compilation_internalError")
   String compilation_internalError(String a1);

   @Key("compilation_beginningToCompile")
   String compilation_beginningToCompile();

   @Key("compilation_processing")
   String compilation_processing(String a1);

   @Key("output_isFile")
   String output_isFile(String a1);

   @Key("output_notValidAll")
   String output_notValidAll(String a1);

   @Key("output_notValid")
   String output_notValid(String a1, String a2);

   @Key("problem_noSourceInformation")
   String problem_noSourceInformation();

   @Key("problem_atLine")
   String problem_atLine(String line);

   @Key("abort_invalidAttribute")
   String abort_invalidAttribute(String a1);

   @Key("abort_invalidExceptionAttribute")
   String abort_invalidExceptionAttribute(String a1);

   @Key("abort_invalidOpcode")
   String abort_invalidOpcode(String a1, String a2, String a3);

   @Key("abort_missingCode")
   String abort_missingCode();

   @Key("abort_againstSourceModel")
   String abort_againstSourceModel(String a1, String a2);

   @Key("accept_cannot")
   String accept_cannot();

   @Key("parser_incorrectPath")
   String parser_incorrectPath();

   @Key("parser_moveFiles")
   String parser_moveFiles();

   @Key("parser_syntaxRecovery")
   String parser_syntaxRecovery();

   @Key("parser_regularParse")
   String parser_regularParse();

   @Key("parser_missingFile")
   String parser_missingFile(String a1);

   @Key("parser_corruptedFile")
   String parser_corruptedFile(String a1);

   @Key("parser_endOfFile")
   String parser_endOfFile();

   @Key("parser_endOfConstructor")
   String parser_endOfConstructor();

   @Key("parser_endOfMethod")
   String parser_endOfMethod();

   @Key("parser_endOfInitializer")
   String parser_endOfInitializer();

   @Key("ast_missingCode")
   String ast_missingCode();

   @Key("constant_cannotCastedInto")
   String constant_cannotCastedInto(String a1, String a2);

   @Key("constant_cannotConvertedTo")
   String constant_cannotConvertedTo(String a1, String a2);

}
