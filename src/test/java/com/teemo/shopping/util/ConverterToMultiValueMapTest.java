package com.teemo.shopping.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class ConverterToMultiValueMapTest {

    class TestClass1 {

        int mailChicken = 105;
        String orientalChicken = "StingSAJ";
        Integer goodChicken = 123;
        String optionalChicken = null;
        List<String> pastChickens = List.of(new String[]{"ABC치킨", "BBC치킨"});
    }

    @Test
    void convertToFormDataTest() {
        TestClass1 testClass1 = new TestClass1();
        MultiValueMap<String, String> compareTarget = new LinkedMultiValueMap<>();
        compareTarget.add("mail_chicken", "105");
        compareTarget.add("oriental_chicken", "StingSAJ");
        compareTarget.add("good_chicken", "123");
        compareTarget.add("past_chickens", "[ABC치킨,BBC치킨]");
        Map<String, CustomConverter> customConvertersMap = new HashMap<>();
        customConvertersMap.put("pastChickens", (obj) -> {
            List<String> strs = (List<String>) obj;
            return "[" + String.join(",", strs) + "]";
        });
        MultiValueMap<String, String> formData = ConverterToMultiValueMap.convertToFormData(
            testClass1, customConvertersMap);

        StringBuffer compareStrBuffer1 = new StringBuffer();
        StringBuffer compareStrBuffer2 = new StringBuffer();

        for (var key : compareTarget.keySet()) {
            compareStrBuffer1.append(key);
            compareStrBuffer1.append(compareTarget.get(key));
        }
        for (var key : formData.keySet()) {
            compareStrBuffer2.append(key);
            compareStrBuffer2.append(formData.get(key));
        }
        assertEquals(compareStrBuffer1.toString(), compareStrBuffer2.toString());
    }
}