package com.didichuxing.datachannel.agent.integration.test.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @description: 格式化
 * @author: huangjw
 * @Date: 19/2/12 18:33
 */
public class LogEventFormat implements Format {
    @Override
    public String format(String original) {
        return original;
    }

    @Override
    public Object unFormat(String source) {
        if (StringUtils.isNotBlank(source)) {
            List<String> result = new ArrayList<>();
            if (source.startsWith("[") && source.endsWith("]")) {
                // list
                JSONArray array = JSON.parseArray(source);
                for (int i = 0; i < array.size(); i++) {
                    result.add((String) array.getJSONObject(i).get("content"));
                }
                return result;
            } else {
                // 单条
                if (source.startsWith("{") && source.endsWith("}")) {
                    JSONObject object = JSON.parseObject(source);
                    result.add((String) object.get("content"));
                } else {
                    result.add(source);
                }
                return result;
            }
        }
        return null;
    }
}
