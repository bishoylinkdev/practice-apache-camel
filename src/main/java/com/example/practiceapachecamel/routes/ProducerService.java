package com.example.practiceapachecamel.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.engine.DefaultProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    private final CamelContext camelContext;

    @Autowired
    public ProducerService(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @SneakyThrows
    public void process() {
        ProducerTemplate producerTemplate = new DefaultProducerTemplate(camelContext);
        producerTemplate.start();
        RequestBody requestBody = new RequestBody();
        requestBody.name = "producerTemplate";
        requestBody.age = 5;
        ObjectMapper objectMapper = new ObjectMapper();
        String response = producerTemplate.requestBody("rest:post:hello1",
                objectMapper.writeValueAsString(requestBody), String.class);
        ResponseBody responseBody = objectMapper.readValue(response, ResponseBody.class);
        producerTemplate.stop();
    }
}
