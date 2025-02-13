package com.springboot.mapapitest.map.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestService {

    private final RestTemplate restTemplate;

    @Value("${naver.client-id}")
    private String id;

    @Value("${naver.client-secret}")
    private String secret;

    @Value("${naver.url.search.local}")
    private String localUrl;

    public TestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<String, String>> search (String query) {

        List<Map<String, String>> restaurants = new ArrayList<>();

        try {
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(query);
            String encode = StandardCharsets.UTF_8.decode(buffer).toString();

            URI uri = UriComponentsBuilder.fromUriString(localUrl)
                    .queryParam("query", encode)
                    .queryParam("display", 5)
                    .queryParam("start", 1)
                    .queryParam("sort", "random")
                    .encode()
                    .build()
                    .toUri();

            RequestEntity<Void> rel = RequestEntity.get(uri)
                    .header("X-Naver-Client-Id", id)
                    .header("X-Naver-Client-Secret", secret)
                    .build();

            ResponseEntity<String> response = restTemplate.exchange(rel, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            System.out.println(response.getBody());

            JsonNode itemsNode = rootNode.path("items");
            for (JsonNode itemNode : itemsNode) {
                Map<String, String> restaurant = new HashMap<>();
                restaurant.put("title", itemNode.path("title").asText()); // 장소 이름
                restaurant.put("address", itemNode.path("address").asText());
                restaurant.put("roadAddress", itemNode.path("roadAddress").asText());// 장소 주소

                double latitude = Double.parseDouble(itemNode.path("mapy").asText()) / 1e7;
                double longitude = Double.parseDouble(itemNode.path("mapx").asText()) / 1e7;
                restaurant.put("latitude", String.valueOf(latitude));
                restaurant.put("longitude", String.valueOf(longitude));

                restaurants.add(restaurant); // 리스트에 추가
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

            return  restaurants;

    }
}
