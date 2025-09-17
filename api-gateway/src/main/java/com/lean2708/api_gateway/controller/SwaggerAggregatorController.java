package com.lean2708.api_gateway.controller;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SwaggerAggregatorController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${gateway.url}")
    private String gatewayUrl;

    @Value("${app.api-prefix}")
    private String apiPrefix;

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
        // Lấy docs từ từng service
        JsonNode authDocs = fetchDocs(gatewayUrl + apiPrefix + "/auth", "auth-service");
        JsonNode profileDocs = fetchDocs(gatewayUrl + apiPrefix + "/profiles", "profile-service");
        JsonNode fileDocs = fetchDocs(gatewayUrl + apiPrefix + "/files", "file-service");

        // Root object OpenAPI
        ObjectNode root = mapper.createObjectNode();
        root.put("openapi", "3.0.1");

        // Info
        ObjectNode info = mapper.createObjectNode();
        info.put("title", apiTitle);
        info.put("version", apiVersion);
        info.put("description", apiDescription);
        root.set("info", info);

        // Servers
        ObjectNode serverNode = mapper.createObjectNode();
        serverNode.put("url", serverUrl);
        serverNode.put("description", serverName);
        root.putArray("servers").add(serverNode);

        // Paths (merge cả 3 service)
        ObjectNode paths = mapper.createObjectNode();
        mergePaths(paths, authDocs, "/auth");
        mergePaths(paths, profileDocs, "/profiles");
        mergePaths(paths, fileDocs, "/files");
        root.set("paths", paths);

        // Components (merge schema 3 service)
        ObjectNode components = mapper.createObjectNode();
        ObjectNode schemas = mapper.createObjectNode();

        if (authDocs.path("components").path("schemas").isObject()) {
            schemas.setAll((ObjectNode) authDocs.path("components").path("schemas"));
        }
        if (profileDocs.path("components").path("schemas").isObject()) {
            schemas.setAll((ObjectNode) profileDocs.path("components").path("schemas"));
        }
        if (fileDocs.path("components").path("schemas").isObject()) {
            schemas.setAll((ObjectNode) fileDocs.path("components").path("schemas"));
        }

        components.set("schemas", schemas);
        root.set("components", components);

        return ResponseEntity.ok(root);
    }

    private JsonNode fetchDocs(String baseUrl, String serviceName) throws Exception {
        try {
            String result = restTemplate.getForObject(baseUrl + "/v3/api-docs", String.class);
            return mapper.readTree(result);
        } catch (Exception e) {
            log.error("Không lấy được swagger docs từ {}: {}", serviceName, e.getMessage());
            return mapper.createObjectNode();
        }
    }

    private void mergePaths(ObjectNode paths, JsonNode docs, String servicePrefix) {
        if (docs == null || docs.get("paths") == null) {
            return;
        }
        docs.get("paths").fieldNames().forEachRemaining(path -> {
            if (path.startsWith("/internal")) {
                log.info("Bỏ qua internal API: {}", path);
                return;
            }

            // thêm prefix riêng cho service
            String newPath = "/ride-hub/v1" + servicePrefix + path;
            paths.set(newPath, docs.get("paths").get(path));
        });
    }

}
