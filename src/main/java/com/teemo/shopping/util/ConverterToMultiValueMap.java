package com.teemo.shopping.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class ConverterToMultiValueMap {

    public static MultiValueMap<String, String> convertToFormData(Object source) {
        return convertToFormData(source, new HashMap<>());
    }

    public static MultiValueMap<String, String> convertToFormData(Object source,
        Map<String, CustomConverter> customConverters)
        throws RuntimeException {   //customConvertor<Map<propertyName, Converter>>
        MultiValueMap<String, String> ret = new LinkedMultiValueMap<>();
        Field[] fields = source.getClass().getDeclaredFields();
        for (var field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            if (name == "this$0") {
                continue;  // this 는 생략
            }
            String snakeCaseName = name.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            Object obj = null;
            try {
                obj = field.get(source);
            } catch (Exception e) {
                continue;
            }
            if (obj == null) {
                continue;
            }
            String typeName = field.getType().getName();

            if (customConverters.containsKey(name)) {       // 커스텀 String Converter
                ret.add(snakeCaseName, customConverters.get(name).apply(obj));
            } else if (typeName.equals(String.class.getName())) {
                ret.add(snakeCaseName, (String) obj);
            } else if (typeName.equals(int.class.getName())) {
                ret.add(snakeCaseName, String.valueOf((int) obj));
            } else if (typeName.equals(Integer.class.getName())) {
                ret.add(snakeCaseName, ((Integer) obj).toString());
            } else {
                throw new RuntimeException("지원하지 않는 타입");
            }
        }
        return ret;
    }
}