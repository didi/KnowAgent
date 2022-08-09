package com.didichuxing.datachannel.agent.source.log.type;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.didichuxing.datachannel.agent.common.api.StandardLogType;
import com.didichuxing.datachannel.agent.source.log.config.MatchConfig;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

/**
 * @description: public日志适配类
 * @author: huangjw
 * @Date: 18/8/9 16:18
 */
public class PublicType extends AbstractLogType {

    static final int        LOG_KEY_LEN_LIMIT = 80;
    static String           log_pattern       = "^[a-z_]+(\\w|_)+(\\w)+$";
    static SimpleDateFormat format            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PublicType() {
        super(StandardLogType.Public.getType());
    }

    @Override
    public boolean check(File file, MatchConfig matchConfig) {
        List<String> contents = FileUtils.readFileContent(file, 10);
        if (contents != null && contents.size() != 0) {
            for (String content : contents) {
                if (!checkContent(content)) {
                    return false;
                }
            }
            return true;
        } else {
            // 若文件为空，直接跳过
            return false;
        }
    }

    private boolean checkContent(String log_cts) {
        String log_key_str = "";
        // 记录一行记录key
        Set<String> set = new HashSet<>();
        boolean date_field_flag = false;
        boolean data_format_flag = false;
        // kv字段、k、v
        String kv_pairs_str, kv_pairs_key, kv_pairs_value;

        // 日志包含"||"
        if (!log_cts.contains("||")) {
            return false;
        }
        // 记录第一个"||"
        int first_index = log_cts.indexOf("||");
        String log_key_fields = log_cts.substring(0, first_index);
        String log_param_fields = log_cts.substring(first_index + 2, log_cts.length());
        if (log_key_fields.contains(" ")) {
            log_key_str = log_key_fields.substring(log_key_fields.lastIndexOf(" ") + 1, log_key_fields.length());
        } else {
            log_key_str = log_key_fields;
        }
        // 判断key的长度
        if (log_key_str.length() >= LOG_KEY_LEN_LIMIT || log_key_str.length() == 0) {
            return false;
        }
        if (!Pattern.matches(log_pattern, log_key_str)) {
            return false;
        }

        // 日志内容校验
        while (log_param_fields.contains("||")) {
            // 记录"||"位置
            int kv_pairs_index = log_param_fields.indexOf("||");
            kv_pairs_str = log_param_fields.substring(0, log_param_fields.indexOf("||"));
            kv_pairs_key = kv_pairs_str.substring(0, kv_pairs_str.indexOf("="));
            kv_pairs_value = kv_pairs_str.substring(kv_pairs_str.indexOf("=") + 1, kv_pairs_str.length());

            // 时间戳校验
            if ("timestamp".equals(kv_pairs_key)) {
                date_field_flag = true;
                try {
                    format.parse(kv_pairs_value);
                    data_format_flag = true;
                } catch (ParseException e) {
                    set.clear();
                    return data_format_flag;
                }
            }
            // 重复字段校验
            if (set.contains(kv_pairs_key)) {
                return false;
            }
            set.add(kv_pairs_key);
            log_param_fields = log_param_fields.substring(kv_pairs_index + 2, log_param_fields.length());
        }
        if (!date_field_flag) {
            return false;
        }
        set.clear();
        return true;

    }
}
