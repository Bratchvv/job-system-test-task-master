package com.github.bratchvv.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bratchvv.BaseSpringBootTest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@WebAppConfiguration
public abstract class BaseRestTest extends BaseSpringBootTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @SneakyThrows
    protected <T> T toObject(MvcResult result, Class<T> clazz) {
        String json = result.getResponse().getContentAsString();
        return new ObjectMapper().readValue(json, clazz);
    }

    @SneakyThrows
    public <T> T fromJSON(TypeReference<T> type, String jsonPacket) {
        return objectMapper.readValue(jsonPacket, type);
    }

    @SneakyThrows
    public <T> T fromJSON(TypeReference<T> type, MvcResult result) {
        return objectMapper.readValue(result.getResponse().getContentAsString(), type);
    }

}
