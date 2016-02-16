package io.github.netbrain.nicrofw;

import org.junit.Test;

import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.Method.GET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UriMapTest {

    @Test
    public void testUrlMatchingWithParameters(){
        String url = "/film/:id";
        UriMap uriMap = new UriMap(url);
        Map<String, String> result = uriMap.matches(GET, "/film/1");
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals("1",result.get("id"));
    }
}