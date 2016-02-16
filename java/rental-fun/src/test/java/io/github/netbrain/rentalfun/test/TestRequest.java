package io.github.netbrain.rentalfun.test;

import sun.misc.IOUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TestRequest {

    private final static Logger log = Logger.getLogger(TestRequest.class.getName());

    private String method;
    private String path;
    private Map<String,String> headers = new HashMap<>();
    private byte[] data = new byte[0];


    private TestRequest(String method, String path) {
        this.method = method.toUpperCase();
        this.path = path;
        headers.put("User-Agent","RentalFun-TestClient/0.1");
        headers.put("Accept","*/*");
    }

    public static TestRequest create(String method, String path){
        return new TestRequest(method,path);
    }

    public TestRequest withData(byte[] data) {
        this.data = data;
        return this;
    }

    public TestRequest withHeader(String key, String value){
        headers.put(key,value);
        return this;
    }

    public TestRequest withHeader(Map<String,String> headers){
        headers.putAll(headers);
        return this;
    }

    public byte[] execute(){
        HttpURLConnection httpURLConnection = null;
        byte[] response = new byte[0];
        try {
            httpURLConnection = (HttpURLConnection) new URL(String.format("%s://%s:%d%s","http","127.0.0.1",8080,path)).openConnection();
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setInstanceFollowRedirects(true);
            headers.forEach(httpURLConnection::setRequestProperty);
            if(data.length  > 0){
                httpURLConnection.setDoOutput(true);
                httpURLConnection.getOutputStream().write(data);
                headers.put("Content-Length", String.valueOf(data.length));
            }
            response = IOUtils.readFully(httpURLConnection.getInputStream(), -1, true);
            return response;
        } catch (IOException e) {
            if(httpURLConnection != null) {
                try {
                    byte[] errResponse = IOUtils.readFully(httpURLConnection.getErrorStream(), -1, true);
                    throw new RuntimeException(String.format("Remote returned with error payload:\n\n %s",new String(errResponse, "UTF-8")), e);
                } catch (IOException e1) {
                    //NOOP
                }
            }
            throw new RuntimeException(e);
        }finally {
            try {
                log.info(
                        String.format(">>>Request>>>>>>: [%s] %s\n%s\n\n<<<Response<<<<<<:\n%s\n",
                                method,
                                path,
                                requestToString(headers,data),
                                responseToString(httpURLConnection.getHeaderFields(),response)
                        )
                );
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String requestToString(Map<String,String> headers, byte[] data) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        headers.forEach((k,v)->stringBuilder.append(k+": "+v+"\n"));
        stringBuilder.append('\n');
        stringBuilder.append(new String(data,"UTF-8"));
        return stringBuilder.toString();
    }

    private String responseToString(Map<String, List<String>> headers, byte[] data) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        headers.forEach((k,v)->stringBuilder.append(k == null ? "" : k+": "+String.join(", ",v)+"\n"));
        stringBuilder.append('\n');
        stringBuilder.append(new String(data,"UTF-8"));
        return stringBuilder.toString();
    }
}
