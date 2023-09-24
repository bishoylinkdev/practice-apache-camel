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
import org.springframework.stereotype.Service;

@Component
@Service
public class RestApiBuilder extends RouteBuilder {

    @Value("${camel.component.servlet.scheme}")
    private String scheme = "http";

    @Value("${camel.component.servlet.host}")
    private String host;

    @Value("${camel.component.servlet.port}")
    private String port;

    @Value("${camel.component.servlet.sourceEndpointPath}")
    private String sourceEndpointPath;

    @Value("${camel.component.servlet.destinationEndpointPath}")
    private String destinationEndpointPath;

    @Override
    public void configure() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseBody response = new ResponseBody();
        response.data = "Apache Camel Response";
        response.status = HttpStatus.OK.toString();
        response.statusCode = HttpStatus.OK.value();
        String sourceURI = String.format("rest:post:%s", sourceEndpointPath);
        String destinationUrl = String.format("%s://%s:%s/camel/%s?bridgeEndpoint=true", scheme, host, port, destinationEndpointPath);

        restConfiguration().component("servlet").bindingMode(RestBindingMode.auto);
        from(sourceURI)
                .setHeader(Exchange.HTTP_METHOD, () -> HttpPost.METHOD_NAME)
                .setHeader(HttpConstants.CONTENT_TYPE, () -> ContentType.APPLICATION_JSON)
                .process(exchange -> {
                    String payload = exchange.getIn().getBody(String.class);
                    RequestBody requestBody = objectMapper.readValue(payload, RequestBody.class);
                    log.info("RequestBody Data : {} {}", requestBody.logLevel, requestBody.message);
                    requestBody.message = "Apache Camel Interceptor";
                    exchange.getIn().setBody(objectMapper.writeValueAsBytes(requestBody));
                })
                .log(String.format("Calling Rest Endpoint: %s", destinationUrl))
                .to(destinationUrl)
                .process(exchange -> {
                    String payload = exchange.getIn().getBody(String.class);
                    log.info("ResponseBody Data : {}", payload);
                    exchange.getIn().setBody(objectMapper.writeValueAsBytes(response));
                });

        String source2URI = String.format("rest:post:%s", destinationEndpointPath);
        from(source2URI)
                .log("sending apache camel response back")
                .transform().constant(objectMapper.writeValueAsString(response));
    }

}
