package com.esun.seating.common.util;

import org.springframework.stereotype.Component;

/**
 * XSS 防護工具：對輸出字串進行 HTML Escape，
 * 防止惡意腳本注入 (Cross-Site Scripting)。
 */
@Component
public class XssUtil {

    private XssUtil() {}

    /**
     * 將輸入字串中的 HTML 特殊字元轉為 HTML entities。
     */
    public static String escape(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            switch (c) {
                case '&'  -> sb.append("&amp;");
                case '<'  -> sb.append("&lt;");
                case '>'  -> sb.append("&gt;");
                case '"'  -> sb.append("&quot;");
                case '\'' -> sb.append("&#x27;");
                case '/'  -> sb.append("&#x2F;");
                default   -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
