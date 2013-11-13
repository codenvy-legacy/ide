/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.dto.definitions;

import com.codenvy.dto.shared.DTO;
import com.codenvy.ide.dto.definitions.SimpleDto;

import java.util.List;
import java.util.Map;

/**
 * DTO for testing that the {@link com.codenvy.dto.generator.DtoGenerator}
 * correctly generates server implementations for object graphs (nested lists, and maps).
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
@DTO
public interface ComplicatedDto {

    public enum SimpleEnum {
        ONE, TWO, THREE
    }

    List<String> getStrings();

    ComplicatedDto withStrings(List<String> strings);

    SimpleEnum getSimpleEnum();

    ComplicatedDto withSimpleEnum(SimpleEnum simpleEnum);

    Map<String, SimpleDto> getMap();

    ComplicatedDto withMap(Map<String, SimpleDto> map);

    List<SimpleDto> getSimpleDtos();

    ComplicatedDto withSimpleDtos(List<SimpleDto> listDtos);

    List<List<SimpleEnum>> getArrayOfArrayOfEnum();

    ComplicatedDto withArrayOfArrayOfEnum(List<List<SimpleEnum>> arrayOfArrayOfEnum);
}
