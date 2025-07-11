package com.work.work.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;

public class DifyUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 清理并验证JSON字符串
     * @param dirtyJson 可能包含前后垃圾字符的原始字符串
     * @return 合法的压缩JSON字符串
     * @throws IllegalArgumentException 当不是合法JSON时抛出
     */
    public static String sanitizeJson(String dirtyJson) {
        // 1. 删除前后非JSON内容（匹配第一个 { 和最后一个 } 之间的内容）
        String trimmed = dirtyJson.replaceAll("^[^\\{]*", "")  // 删除第一个 { 之前的所有字符
                .replaceAll("[^\\}]*$", ""); // 删除最后一个 } 之后的所有字符

        // 2. 移除所有换行和多余空格（保留字符串内部空格）
        String compressed = trimmed.replaceAll("\\s+", "");

        // 3. 用Jackson验证并标准化
        try {
            Object parsed = mapper.readValue(compressed, Object.class);
            return mapper.writeValueAsString(parsed);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON after sanitizing: " + compressed, e);
        }
    }

    public static String sanitizeHtml(String dirtyHtml) {
        // 1. 去除所有 \n
        String noNewLine = dirtyHtml.replace("\\n", "");

        // 2. 提取<html>...</html>之间内容
        int start = noNewLine.indexOf("<html");
        int end = noNewLine.lastIndexOf("</html>");
        if (start == -1 || end == -1) {
            throw new IllegalArgumentException("未找到<html>标签");
        }
        String htmlContent = noNewLine.substring(start, end + 7);

        // 3. 用Jsoup解析判断合法性
        try {
            Jsoup.parse(htmlContent);
            return htmlContent;
        } catch (Exception e) {
            throw new IllegalArgumentException("HTML不合法: " + e.getMessage(), e);
        }
    }

    public static String extractTextConcat(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode sentences = root.get("Sentences");
            if (sentences == null || !sentences.isArray()) {
                throw new IllegalArgumentException("未找到Sentences数组");
            }
            StringBuilder sb = new StringBuilder();
            for (JsonNode node : sentences) {
                JsonNode textNode = node.get("Text");
                if (textNode != null) {
                    sb.append(textNode.asText());
                }
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("解析JSON失败: " + e.getMessage(), e);
        }
    }

}
