package com.sayed.seu.forntend.service;


import com.sayed.seu.forntend.exception.ResourceNotFoundException;
import com.sayed.seu.forntend.model.Notifications;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class NotificationsService {

    @Value(value = "${baseURL}/notification")
    private String baseURL;

    public Notifications insert(Notifications notifications) throws ResourceNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Notifications> httpEntity = new HttpEntity<>(notifications);
        try {
            return restTemplate.postForObject(baseURL + "/new", httpEntity, Notifications.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException("");
        }
    }

    public List<Notifications> retrieve(String facultyName) throws ResourceNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(baseURL + "/list")
                .queryParam("facultyName", facultyName);
        try {
            return restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Notifications>>() {
                    })
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("");
        }
    }

    public boolean delete(long id) throws ResourceNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseURL + "/delete")
                .queryParam("id", id);

        try {
            return restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, null, Boolean.class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("");
        }
    }
}
