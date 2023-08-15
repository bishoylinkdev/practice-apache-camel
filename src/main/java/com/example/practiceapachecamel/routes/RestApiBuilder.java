package com.example.practiceapachecamel.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpConstants;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RestApiBuilder extends RouteBuilder {

    public static class RequestBody {
        public String name;
        public Integer age;
    }

    public static class ResponseBody {
        public String data;
        public String status;
        public Integer statusCode;
    }

    private String scheme = "http";

    @Value("${camel.component.netty-http.host}")
    private String host;

    @Value("${camel.component.netty-http.port}")
    private String port;

    @Override
    public void configure() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseBody response = new ResponseBody();
        response.data = "apache camel response";
        response.status = HttpStatus.OK.toString();
        response.statusCode = HttpStatus.OK.value();
        String url = String.format("%s://%s:%s/hello2?bridgeEndpoint=true", scheme, host, port);

        restConfiguration().component("servlet").bindingMode(RestBindingMode.auto);
        from("rest:post:hello1")
                .setHeader(Exchange.HTTP_METHOD, () -> HttpPost.METHOD_NAME)
                .setHeader(HttpConstants.CONTENT_TYPE, () -> ContentType.APPLICATION_JSON)
                .process(exchange -> {
                    String payload = exchange.getIn().getBody(String.class);
                    RequestBody requestBody = objectMapper.readValue(payload, RequestBody.class);
                    log.info("RequestBody data : {} {}", requestBody.name, requestBody.age);
                    requestBody.name += "interceptor";
                    requestBody.age = 28;
                    exchange.getIn().setBody(objectMapper.writeValueAsBytes(requestBody));
                })
                .log("calling rest endpoint")
                .to(url)
                .process(exchange -> {
                    String payload = exchange.getIn().getBody(String.class);
                    ResponseBody responseBody = objectMapper.readValue(payload, ResponseBody.class);
                    log.info("RequestBody data : {} {}", responseBody.status, responseBody.statusCode);
                });

        from("rest:post:hello2")
                .log("writing request to file")
                .transform().constant(objectMapper.writeValueAsString(response));
    }

}
