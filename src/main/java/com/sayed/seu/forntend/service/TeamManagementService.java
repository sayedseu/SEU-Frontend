package com.sayed.seu.forntend.service;

import com.sayed.seu.forntend.exception.ResourceNotFoundException;
import com.sayed.seu.forntend.model.Semester;
import com.sayed.seu.forntend.model.Team;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class TeamManagementService {

    @Value(value = "${baseURL}/team-management")
    private String baseURL;

    public Team insert(Team team) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Team> teamHttpEntity = new HttpEntity<>(team);
        try {
            return restTemplate.postForObject(baseURL + "/new", teamHttpEntity, Team.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("");
        }
    }

    public List<Team> retrieve() throws ResourceNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Team[]> responseEntity = restTemplate.getForEntity(baseURL + "/teams", Team[].class);
            Team[] teams = responseEntity.getBody();
            if (teams == null) return null;
            else return Arrays.asList(teams);
        } catch (Exception e) {
            throw new ResourceNotFoundException("");
        }
    }

    public List<Team> retrieve(Semester semester) throws ResourceNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Semester> semesterHttpEntity = new HttpEntity<>(semester);
        try {
            Team[] teams = restTemplate.postForObject(baseURL + "/teams/semester", semesterHttpEntity, Team[].class);
            System.out.println(teams);
            if (teams == null) return null;
            else return Arrays.asList(teams);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("");
        }
    }

    public Team update(Team team) throws ResourceNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Team> httpEntity = new HttpEntity<>(team);
        try {
            return restTemplate.exchange(baseURL + "/team/update", HttpMethod.PUT, httpEntity, Team.class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("");
        }
    }
}
