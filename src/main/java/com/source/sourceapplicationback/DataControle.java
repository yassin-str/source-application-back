package com.source.sourceapplicationback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.source.sourceapplicationback.dto.ApplicationDTO;
import com.source.sourceapplicationback.dto.ApplicationDecomissionee;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

@RestController("source-application/api")
public class HelloControle {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public String token;

    @GetMapping(value = "/")
    public ResponseEntity<?>  authe() throws JsonProcessingException {


        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 30)).build();
        WebClient client = WebClient.builder().exchangeStrategies(exchangeStrategies).build();

        JSONObject credentials = new JSONObject();
        credentials.put("Authorization", token);
        String response;
        try {
            response = client.get()
                    .uri(new URI("http://localhost:8080/ebx-dataservices/rest/data/v1/BBrancheSourceApplications/InstanceSourceApplications/root/T_APPLICATION"))
                    .header("Authorization", token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONArray recs = null;
        JSONObject jsonObject;

       /* JSONArray recs = response.getJSONArray("rows");


        for (int i = 0; i < recs.length(); ++i) {
            JSONObject rec = recs.getJSONObject(i);
           String str= rec.getString("label");
           System.out.println(str);
            // ...
        }

        */

        //System.out.println(response);
        jsonObject = new JSONObject(response);
        recs = jsonObject.getJSONArray("rows");
        // recs.toList()
        //        .stream().
        //        filter((JSONObject js) -> js.getJSONObject("content").getJSONObject("s_ETAT").get("label").equals("Décommissionné"))
        final ObjectMapper objectMapper = new ObjectMapper();


        List<ApplicationDTO> applicationDTOS = objectMapper.readValue(recs.toString(), new TypeReference<List<ApplicationDTO>>() {
        });
        System.out.println(applicationDTOS.size());


        return ResponseEntity.status(HttpStatus.OK).body(applicationDTOS);

    }
    @GetMapping(value = "/app")
    public ResponseEntity<?>  authe2() throws JsonProcessingException {


        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 30)).build();
        WebClient client = WebClient.builder().exchangeStrategies(exchangeStrategies).build();

        JSONObject credentials = new JSONObject();
        credentials.put("Authorization", token);
        String response;
        try {
            response = client
                    .get()
                    .uri(new URI("http://localhost:8080/ebx-dataservices/rest/data/v1/BBrancheSourceApplications/InstanceSourceApplications/root/T_APPLICATION?filter=s_ETAT=\'D\'"))
                    .header("Authorization", token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONArray recs = null;
        JSONObject jsonObject;

       /* JSONArray recs = response.getJSONArray("rows");


        for (int i = 0; i < recs.length(); ++i) {
            JSONObject rec = recs.getJSONObject(i);
           String str= rec.getString("label");
           System.out.println(str);
            // ...
        }

        */

        //System.out.println(response);
        jsonObject = new JSONObject(response);
        recs = jsonObject.getJSONArray("rows");
        // recs.toList()
        //        .stream().
        //        filter((JSONObject js) -> js.getJSONObject("content").getJSONObject("s_ETAT").get("label").equals("Décommissionné"))
        final ObjectMapper objectMapper = new ObjectMapper();


        List<ApplicationDecomissionee> applicationDecomissionees=objectMapper.readValue(recs.toString(),new TypeReference<List<ApplicationDecomissionee>>(){});
        System.out.println(applicationDecomissionees.size());



        return ResponseEntity.status(HttpStatus.OK).body(applicationDecomissionees);

    }

    @GetMapping(value = "/auth")
    public String auth() throws  JsonMappingException, JsonProcessingException {

        String createTokenUrl = "http://localhost:8080/ebx-dataservices/rest/auth/v1/token:create";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject credentials = new JSONObject();
        credentials.put("login", "ebx_admin");
        credentials.put("password", "ebx_admin");
        HttpEntity<String> request = new HttpEntity<String>(credentials.toString(), headers);
        String tokenAsJsonStr = restTemplate.postForObject(createTokenUrl, request, String.class);

        JsonNode root = objectMapper.readTree(tokenAsJsonStr);

       System.out.println(root.path("accessToken").asText());

       token="EBX "+root.path("accessToken").asText();
       System.out.println(token);
        return "hello world ";
    }

}
