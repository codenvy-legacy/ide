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
package com.codenvy.ide.ext.java.jdt.core.search;

import com.codenvy.ide.ext.java.jdt.core.IPackageFragment;

import java.util.ArrayList;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class SearchEngine {

    public interface SearchCallback {
        void searchFinished(ArrayList<TypeNameMatch> typesFound);
    }

    private final String projectId;

    private SearchCallback callback;

    private ArrayList<TypeNameMatch> typesFound;

    private int index = 0;

    private char[][] allTypes;

    private final IPackageFragment currentPackage;

    /** @param projectId */
    public SearchEngine(String projectId, IPackageFragment currentPackage) {
        this.projectId = projectId;
        this.currentPackage = currentPackage;
    }

    /**
     * @param allTypes
     * @param typesFound
     */
    public void searchAllTypeNames(char[][] allTypes, ArrayList<TypeNameMatch> typesFound, SearchCallback callback) {
        this.allTypes = allTypes;
        this.typesFound = typesFound;
        this.callback = callback;
        if (allTypes.length == 0) {
            callback.searchFinished(typesFound);
            return;
        }
        getTypes(allTypes[index]);
    }

    /** @param fqn */
    private void getTypes(char[] fqn) {
        //TODO
//      JavaCodeAssistantService.get().findClassesByPrefix(
//         String.valueOf(fqn),
//         projectId,
//         new AsyncRequestCallback<TypesList>(new AutoBeanUnmarshaller<TypesList>(JavaEditorExtension.AUTO_BEAN_FACTORY
//            .types()))
//         {
//
//            @Override
//            protected void onSuccess(TypesList result)
//            {
//               typeListReceived(result);
//            }
//
//            @Override
//            protected void onFailure(Throwable exception)
//            {
//               exception.printStackTrace();
//            }
//         });
    }

//    /** @param result */
//    private void typeListReceived(TypesList result) {
//        index++;
//        for (ShortTypeInfo typeInfo : result.getTypes()) {
//            Type type = new Type(typeInfo);
//            if (!Modifier.isPublic(typeInfo.getModifiers())) {
//                if (!currentPackage.getElementName().equals(type.getPackageFragment().getElementName()))
//                    continue;
//            }
//            typesFound.add(new JavaSearchTypeNameMatch(type, typeInfo.getModifiers()));
//        }
//        if (index < allTypes.length)
//            getTypes(allTypes[index]);
//        else
//            callback.searchFinished(typesFound);
//    }

}
