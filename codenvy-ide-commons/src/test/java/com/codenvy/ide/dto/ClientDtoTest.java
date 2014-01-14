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
package com.codenvy.ide.dto;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.definitions.ComplicatedDto;
import com.codenvy.ide.dto.definitions.SimpleDto;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests that the interfaces specified in com.codenvy.ide.dto.definitions have
 * corresponding generated client implementations.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
@GwtModule("com.codenvy.ide.Commons")
public class ClientDtoTest extends GwtTest {

    protected static DtoFactory dtoFactory;

    @BeforeClass
    public static void setUp() throws Exception {
        dtoFactory = new DtoFactory();
        new DtoClientImpls().accept(dtoFactory);
    }

    @Test
    public void testCreateSimpleDto() throws Exception {
        final String fooString = "Something";
        final int fooId = 1;

        SimpleDto dto = dtoFactory.createDto(SimpleDto.class).withName(fooString).withId(fooId);

        // Check to make sure things are in a sane state.
        checkSimpleDto(dto, fooString, fooId);
    }

    @Test
    public void testSimpleDtoSerializer() throws Exception {
        final String fooString = "Something";
        final int fooId = 1;

        SimpleDto dto = dtoFactory.createDto(SimpleDto.class).withName(fooString).withId(fooId);
        final String json = dtoFactory.toJson(dto);

        JSONObject jsonObject = JSONParser.parseStrict(json).isObject();
        Assert.assertEquals(jsonObject.get("name").isString().stringValue(), fooString);
        Assert.assertTrue(jsonObject.get("id").isNumber().doubleValue() == fooId);
    }

    @Test
    public void testSimpleDtoDeserializer() throws Exception {
        final String fooString = "Something";
        final int fooId = 1;

        JSONObject json = new JSONObject();
        json.put("name", new JSONString(fooString));
        json.put("id", new JSONNumber(fooId));

        SimpleDto dto = dtoFactory.createDtoFromJson(json.toString(), SimpleDto.class);

        // Check to make sure things are in a sane state.
        checkSimpleDto(dto, fooString, fooId);
    }

    @Test
    @Ignore
    public void testListSimpleDtoDeserializer() throws Exception {
        final String fooString_1 = "Something 1";
        final int fooId_1 = 1;
        final String fooString_2 = "Something 2";
        final int fooId_2 = 2;

        JSONObject json1 = new JSONObject();
        json1.put("name", new JSONString(fooString_1));
        json1.put("id", new JSONNumber(fooId_1));

        JSONObject json2 = new JSONObject();
        json2.put("name", new JSONString(fooString_2));
        json2.put("id", new JSONNumber(fooId_2));

        JSONArray jsonArray = new JSONArray();
        jsonArray.set(0, json1);
        jsonArray.set(1, json2);

        // TODO JSONParserPatcher doesn't handle JSON array
        Array<SimpleDto> listDtoFromJson = dtoFactory.createListDtoFromJson(jsonArray.toString(), SimpleDto.class);

        Assert.assertEquals(listDtoFromJson.get(0).getName(), fooString_1);
        Assert.assertEquals(listDtoFromJson.get(0).getId(), fooId_1);
        Assert.assertEquals(listDtoFromJson.get(1).getName(), fooString_2);
        Assert.assertEquals(listDtoFromJson.get(1).getId(), fooId_2);
    }

    @Test
    public void testComplicatedDtoSerializer() throws Exception {
        final String fooString = "Something";
        final int fooId = 1;

        List<String> listStrings = new ArrayList<>(2);
        listStrings.add("Something 1");
        listStrings.add("Something 2");

        ComplicatedDto.SimpleEnum simpleEnum = ComplicatedDto.SimpleEnum.ONE;

        // Assume that SimpleDto works. Use it to test nested objects
        SimpleDto simpleDto = dtoFactory.createDto(SimpleDto.class).withName(fooString).withId(fooId);

        Map<String, SimpleDto> mapDtos = new HashMap<>(1);
        mapDtos.put(fooString, simpleDto);

        List<SimpleDto> listDtos = new ArrayList<>(1);
        listDtos.add(simpleDto);

        List<List<ComplicatedDto.SimpleEnum>> listOfListOfEnum = new ArrayList<>(1);
        List<ComplicatedDto.SimpleEnum> listOfEnum = new ArrayList<>(3);
        listOfEnum.add(ComplicatedDto.SimpleEnum.ONE);
        listOfEnum.add(ComplicatedDto.SimpleEnum.TWO);
        listOfEnum.add(ComplicatedDto.SimpleEnum.THREE);
        listOfListOfEnum.add(listOfEnum);

        ComplicatedDto dto = dtoFactory.createDto(ComplicatedDto.class).withStrings(listStrings).
                withSimpleEnum(simpleEnum).withMap(mapDtos).withSimpleDtos(listDtos).
                                               withArrayOfArrayOfEnum(listOfListOfEnum);


        final String json = dtoFactory.toJson(dto);
        JSONObject jsonObject = JSONParser.parseStrict(json).isObject();

        Assert.assertTrue(jsonObject.containsKey("strings"));
        JSONArray jsonArray = jsonObject.get("strings").isArray();
        Assert.assertEquals(jsonArray.get(0).isString().stringValue(), listStrings.get(0));
        Assert.assertEquals(jsonArray.get(1).isString().stringValue(), listStrings.get(1));

        Assert.assertTrue(jsonObject.containsKey("simpleEnum"));
        Assert.assertEquals(jsonObject.get("simpleEnum").isString().stringValue(), simpleEnum.name());

        Assert.assertTrue(jsonObject.containsKey("map"));
        JSONObject jsonMap = jsonObject.get("map").isObject();
        JSONObject value = jsonMap.get(fooString).isObject();
        Assert.assertEquals(value.get("name").isString().stringValue(), fooString);
        Assert.assertTrue(value.get("id").isNumber().doubleValue() == fooId);

        Assert.assertTrue(jsonObject.containsKey("simpleDtos"));
        JSONArray simpleDtos = jsonObject.get("simpleDtos").isArray();
        JSONObject simpleDtoJsonObject = simpleDtos.get(0).isObject();
        Assert.assertEquals(simpleDtoJsonObject.get("name").isString().stringValue(), fooString);
        Assert.assertTrue(simpleDtoJsonObject.get("id").isNumber().doubleValue() == fooId);

        Assert.assertTrue(jsonObject.containsKey("arrayOfArrayOfEnum"));
        JSONArray arrayOfArrayOfEnum = jsonObject.get("arrayOfArrayOfEnum").isArray().get(0).isArray();
        Assert.assertEquals(arrayOfArrayOfEnum.get(0).isString().stringValue(), ComplicatedDto.SimpleEnum.ONE.name());
        Assert.assertEquals(arrayOfArrayOfEnum.get(1).isString().stringValue(), ComplicatedDto.SimpleEnum.TWO.name());
        Assert.assertEquals(arrayOfArrayOfEnum.get(2).isString().stringValue(), ComplicatedDto.SimpleEnum.THREE.name());
    }

    @Test
    public void testComplicatedDtoDeserializer() throws Exception {
        final String fooString = "Something";
        final int fooId = 1;

        JSONArray jsonArray = new JSONArray();
        jsonArray.set(0, new JSONString(fooString));

        JSONObject simpleDtoJsonObject = new JSONObject();
        simpleDtoJsonObject.put("name", new JSONString(fooString));
        simpleDtoJsonObject.put("id", new JSONNumber(fooId));

        JSONObject jsonMap = new JSONObject();
        jsonMap.put(fooString, simpleDtoJsonObject);

        JSONArray simpleDtosArray = new JSONArray();
        simpleDtosArray.set(0, simpleDtoJsonObject);

        JSONArray arrayOfEnum = new JSONArray();
        arrayOfEnum.set(0, new JSONString(ComplicatedDto.SimpleEnum.ONE.name()));
        arrayOfEnum.set(1, new JSONString(ComplicatedDto.SimpleEnum.TWO.name()));
        arrayOfEnum.set(2, new JSONString(ComplicatedDto.SimpleEnum.THREE.name()));
        JSONArray arrayOfArrayEnum = new JSONArray();
        arrayOfArrayEnum.set(0, arrayOfEnum);

        JSONObject complicatedDtoJsonObject = new JSONObject();
        complicatedDtoJsonObject.put("strings", jsonArray);
        complicatedDtoJsonObject.put("simpleEnum", new JSONString(ComplicatedDto.SimpleEnum.ONE.name()));
        complicatedDtoJsonObject.put("map", jsonMap);
        complicatedDtoJsonObject.put("simpleDtos", simpleDtosArray);
        complicatedDtoJsonObject.put("arrayOfArrayOfEnum", arrayOfArrayEnum);

        ComplicatedDto complicatedDto =
                dtoFactory.createDtoFromJson(complicatedDtoJsonObject.toString(), ComplicatedDto.class);

        Assert.assertEquals(complicatedDto.getStrings().get(0), fooString);
        Assert.assertEquals(complicatedDto.getSimpleEnum(), ComplicatedDto.SimpleEnum.ONE);
        checkSimpleDto(complicatedDto.getMap().get(fooString), fooString, fooId);
        checkSimpleDto(complicatedDto.getSimpleDtos().get(0), fooString, fooId);
        Assert.assertEquals(complicatedDto.getArrayOfArrayOfEnum().get(0).get(0), ComplicatedDto.SimpleEnum.ONE);
        Assert.assertEquals(complicatedDto.getArrayOfArrayOfEnum().get(0).get(1), ComplicatedDto.SimpleEnum.TWO);
        Assert.assertEquals(complicatedDto.getArrayOfArrayOfEnum().get(0).get(2), ComplicatedDto.SimpleEnum.THREE);
    }

    private void checkSimpleDto(SimpleDto dto, String expectedName, int expectedId) {
        Assert.assertEquals(dto.getName(), expectedName);
        Assert.assertEquals(dto.getId(), expectedId);
    }
}
