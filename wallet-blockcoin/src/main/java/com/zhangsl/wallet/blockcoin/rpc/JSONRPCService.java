package com.zhangsl.wallet.blockcoin.rpc;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zhangsl.wallet.common.exception.WalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by zhang.shaolong on 2018/3/13.
 */
public class JSONRPCService {

    private static final Logger logger = LoggerFactory.getLogger(JSONRPCService.class);

    private static final String JSONRPC_CONTENT_TYPE = "application/json";

    private static final String PARAMS = "params";

    private static final String METHOD = "method";

    private static final String JSONRPC = "jsonrpc";

    private static final String ID = "id";

    private static final String VERSION = "2.0";

    private final AtomicReference<URL> serviceUrl = new AtomicReference<>();

    private final ObjectMapper mapper;

    private RestTemplate restTemplate;

    private final Random random;

    private Map<String, String> headers = new HashMap<>();


    public JSONRPCService(String url,RestTemplate restTemplate) {
        this(url,restTemplate,null);
    }


    public JSONRPCService(String url,RestTemplate restTemplate,Map<String, String> headers)  {
        if (headers != null) {
            this.headers = headers;
        }
        this.mapper = new ObjectMapper();
        this.random = new Random(System.currentTimeMillis());
        this.restTemplate = restTemplate;
        setServiceUrl(url);
        setDefaultHeader();
    }


    /**
     * 设置url信息
     * @param url
     */
    private void setServiceUrl(String url) {
        URL servcieUrl = null;
        try {
            servcieUrl = new URL(url);
        } catch (MalformedURLException e) {
        }
        this.serviceUrl.set(servcieUrl);
    }

    /**
     * 设置头信息
     */
    private void setDefaultHeader() {
        headers.put(HttpHeaders.CONTENT_TYPE,JSONRPC_CONTENT_TYPE);
    }


    /**
     * 通过请求信息获取数据
     *
     * @param methodName
     * @param argument
     * @param clazz
     * @param extraHeaders
     * @param <T>
     * @return
     * @throws Throwable
     */
    public <T> T callMethod(String methodName, Object argument, Class<T> clazz, Map<String, String> extraHeaders) throws Throwable {
        final ObjectNode request = createRequest(methodName,argument);
        final MultiValueMap<String, String> httpHeaders = new LinkedMultiValueMap<>();
        //添加默认请求头信息
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            httpHeaders.add(entry.getKey(), entry.getValue());
        }
        //添加请求头信息
        if (extraHeaders != null) {
            for (Map.Entry<String, String> entry : extraHeaders.entrySet()) {
                httpHeaders.add(entry.getKey(), entry.getValue());
            }
        }
        //后取返回值信息
        final HttpEntity<ObjectNode> requestHttpEntity = new HttpEntity<>(request, httpHeaders);
        T result;
        try {
            result = this.restTemplate.postForObject(serviceUrl.get().toExternalForm(), requestHttpEntity, clazz);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            logger.error("HTTP Error code={} status={}\nresponse={}"
                    , httpStatusCodeException.getStatusCode().value()
                    , httpStatusCodeException.getStatusText()
                    , httpStatusCodeException.getResponseBodyAsString()
            );

            if (clazz.getTypeName().equals(JSONObject.class.getTypeName())) {
                return JSONObject.parseObject(httpStatusCodeException.getResponseBodyAsString(),clazz);
            }
            throw new WalletException(httpStatusCodeException.getStatusText(),httpStatusCodeException,httpStatusCodeException.getStatusCode().value());
        } catch (HttpMessageConversionException httpMessageConversionException) {
            logger.error("Can not convert (request/response)", httpMessageConversionException);
            throw new WalletException( "Invalid JSON-RPC response", httpMessageConversionException,0);
        }
        return result;
    }



    /**
     * 直接获取信息
     *
     * @param methodName
     * @param argument
     * @param clazz
     * @param <T>
     * @return
     * @throws Throwable
     */
    public <T> T callMethod(String methodName, Object argument, Class<T> clazz) throws Throwable {
        return callMethod(methodName,argument,clazz,null);
    }



    /**
     * 构造请求的rpc 信息 http://wiki.geekdream.com/Specification/json-rpc_2.0.html
     *
     * @param methodName
     * @param argument
     * @return
     */
    private ObjectNode createRequest(String methodName, Object argument) {
        final ObjectNode request = mapper.createObjectNode();
        //标准协议需要传递id 和版本号信息
        request.put(ID, generateRandomId());
        request.put(JSONRPC, VERSION);
        //方法信息
        request.put(METHOD, methodName);
        //添加参数信息
        addParameters(argument, request);
        return request;
    }

    private void addParameters(Object arguments, ObjectNode request) {
        if (isArrayArguments(arguments)) {
            addArrayArguments(arguments, request);
        } else if (isCollectionArguments(arguments)) {
            addCollectionArguments(arguments, request);
        } else if (isMapArguments(arguments)) {
            addMapArguments(arguments, request);
        } else if (arguments != null) {
            request.set(PARAMS, mapper.valueToTree(arguments));
        }
    }


    private boolean isArrayArguments(Object arguments) {
        return arguments != null && arguments.getClass().isArray();
    }

    private void addArrayArguments(Object arguments, ObjectNode request) {
        Object[] args = Object[].class.cast(arguments);
        if (args.length > 0) {
            ArrayNode paramsNode = new ArrayNode(mapper.getNodeFactory());
            for (Object arg : args) {
                JsonNode argNode = mapper.valueToTree(arg);
                paramsNode.add(argNode);
            }
            request.set(PARAMS, paramsNode);
        }
    }

    private boolean isCollectionArguments(Object arguments) {
        return arguments != null && Collection.class.isInstance(arguments);
    }

    private void addCollectionArguments(Object arguments, ObjectNode request) {
        Collection<?> args = Collection.class.cast(arguments);
        if (!args.isEmpty()) {
            ArrayNode paramsNode = new ArrayNode(mapper.getNodeFactory());
            for (Object arg : args) {
                JsonNode argNode = mapper.valueToTree(arg);
                paramsNode.add(argNode);
            }
            request.set(PARAMS, paramsNode);
        }
    }

    private boolean isMapArguments(Object arguments) {
        return arguments != null && Map.class.isInstance(arguments);
    }

    private void addMapArguments(Object arguments, ObjectNode request) {
        if (!Map.class.cast(arguments).isEmpty()) {
            request.set(PARAMS, mapper.valueToTree(arguments));
        }
    }

    /**
     * 获取随机的id 信息
     * @return
     */
    private String generateRandomId() {
        return Integer.toString(random.nextInt(Integer.MAX_VALUE));
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
