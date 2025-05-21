package com.example.bankcards.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public class EnumValidation {

    public  <E extends Enum<E>> E safeParseEnum(Class<E> enumClass, String value) {
        if (value == null) return null;
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
