package com.work.work.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DifyUtilsTest {

    @Test
    void sanitizeJson_ValidJsonWithExtraChars_ShouldReturnCleanJson() {
        String dirtyJson = "Prefix{\"key\":\"value\"}Suffix";
        String expected = "{\"key\":\"value\"}";
        String result = DifyUtils.sanitizeJson(dirtyJson);
        assertEquals(expected, result);
    }

    @Test
    void sanitizeJson_InvalidJson_ShouldThrowException() {
        String invalidJson = "{invalid}";
        assertThrows(IllegalArgumentException.class, () -> DifyUtils.sanitizeJson(invalidJson));
    }

    @Test
    void sanitizeHtml_ValidHtml_ShouldReturnCleanHtml() {
        String dirtyHtml = "\\n<html><body>test</body></html>\\n";
        String expected = "<html><body>test</body></html>";
        String result = DifyUtils.sanitizeHtml(dirtyHtml);
        assertEquals(expected, result);
    }

    @Test
    void sanitizeHtml_NoHtmlTags_ShouldThrowException() {
        String noHtml = "just text";
        assertThrows(IllegalArgumentException.class, () -> DifyUtils.sanitizeHtml(noHtml));
    }

    @Test
    void extractTextConcat_ValidJson_ShouldReturnConcatenatedText() {
        String json = "{\"Sentences\":[{\"Text\":\"Hello\"},{\"Text\":\"World\"}]}";
        String expected = "HelloWorld";
        String result = DifyUtils.extractTextConcat(json);
        assertEquals(expected, result);
    }

    @Test
    void extractTextConcat_NoSentences_ShouldThrowException() {
        String json = "{\"noSentences\":true}";
        assertThrows(IllegalArgumentException.class, () -> DifyUtils.extractTextConcat(json));
    }

    @Test
    void extractTextConcat_NullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> DifyUtils.extractTextConcat(null));
    }
}
