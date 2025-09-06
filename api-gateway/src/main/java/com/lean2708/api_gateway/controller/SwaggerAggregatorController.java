package com.lean2708.api_gateway.controller;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@RestController
public class SwaggerAggregatorController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${services.auth.url}")
    private String authServiceUrl;

    @Value("${open.api.title}")
    private String apiTitle;

    @Value("${open.api.version}")
    private String apiVersion;

    @Value("${open.api.description}")
    private String apiDescription;

    @Value("${open.api.serverUrl}")
    private String serverUrl;

    @Value("${open.api.serverName}")
    private String serverName;

    @GetMapping("/ride-hub/v1/swagger/docs")
    public ResponseEntity<JsonNode> aggregateDocs() throws Exception {
        // get json from auth-service
        JsonNode authDocs = mapper.readTree(
                restTemplate.getForObject(authServiceUrl + "/v3/api-docs", String.class));

        // Create OpenAPI
        ObjectNode root = mapper.createObjectNode();
        root.put("openapi", "3.0.1");

        // lấy info từ config thay vì hardcode
        ObjectNode info = mapper.createObjectNode();
        info.put("title", apiTitle);
        info.put("version", apiVersion);
        info.put("description", apiDescription);
        root.set("info", info);

        ObjectNode serverNode = mapper.createObjectNode();
        serverNode.put("url", serverUrl);
        serverNode.put("description", serverName);
        root.putArray("servers").add(serverNode);

        ObjectNode paths = mapper.createObjectNode();

        // Convert to API Gateway
        authDocs.get("paths").fieldNames().forEachRemaining(path -> {
            String newPath = "/ride-hub/v1" + path;
            paths.set(newPath, authDocs.get("paths").get(path));
        });
        root.set("paths", paths);

        ObjectNode components = mapper.createObjectNode();
        ObjectNode schemas = mapper.createObjectNode();
        schemas.setAll((ObjectNode) authDocs.path("components").path("schemas"));
        components.set("schemas", schemas);
        root.set("components", components);

        return ResponseEntity.ok(root);
    }
}
