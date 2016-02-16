package io.github.netbrain.nicrofw;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import io.github.netbrain.nicrofw.nanohttpd.HTTPSessionBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.Method.POST;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RouterTest {

    private Router router;

    @Before
    public void setUp() throws Exception {
        router = new Router();
        router.scanAnnotations("io.github.netbrain.nicrofw.handler.test");
    }

    @Test
    public void testThatScanAnnotationsGivesOneOrMoreRoutes() throws Exception {
        assertNotEquals(0,router.getRoutes().size());
    }

    @Test
    public void testThatItsPossibleToInvokeTheHandlerMethodFromAHTTPRequest() throws Exception {
        IHTTPSession session = new HTTPSessionBuilder("/test/annotation").build();
        Object result = router.handle(session);

        assertNotNull(result);
        assertEquals(String.class,result.getClass());
        assertEquals("Hello from simple!",String.valueOf(result));
    }

    @Test
    public void testThatItsPossibleToPassPostDataToAnArgumentOfAHandler() throws Exception {
        HashMap<String,String> testdata = new HashMap<>();
        testdata.put("testkey","testvalue");

        IHTTPSession session = new HTTPSessionBuilder(POST,"/test/postdata")
                .withBody(testdata)
                .build();

        Object result = router.handle(session);

        assertNotNull(result);
        assertTrue(Map.class.isAssignableFrom(result.getClass()));
        assertEquals("testvalue",((Map<String,String>)result).get("testkey"));
    }

    @Test
    public void testThatItsPossibleToPassRequestHeadersToAHandler() throws Exception {
        HashMap<String,String> testdata = new HashMap<>();
        testdata.put("testkey","testvalue");

        IHTTPSession session = new HTTPSessionBuilder("/test/headers")
                .withHeader("Some-Header","test")
                .build();

        Object result = router.handle(session);

        assertNotNull(result);
        assertTrue(Map.class.isAssignableFrom(result.getClass()));
        assertEquals("test",((Map<String,String>)result).get("Some-Header"));
    }
}