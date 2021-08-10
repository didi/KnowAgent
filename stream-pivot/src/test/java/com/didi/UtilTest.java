package com.didi;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UtilTest {

    @Test
    public void base64Test() {
        Base64.Encoder encoder = Base64.getEncoder();
        String auth = "411:eO7rVMA5PYhaK9u";
        String auth1 = "11:R8XJRm0Wrv7Zwvq";
        byte[] bytes = auth.getBytes(StandardCharsets.UTF_8);
        byte[] bytes1 = auth1.getBytes(StandardCharsets.UTF_8);
        String result = encoder.encodeToString(bytes);
        String result1 = encoder.encodeToString(bytes1);
        System.out.println(result);
        System.out.println(result1);
    }

}
