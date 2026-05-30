package com.aitravel.smartplanner.ai;

import com.aitravel.smartplanner.tripwatch.TravelWatch;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class McpTravelContextService {
    private static final Logger log = LoggerFactory.getLogger(McpTravelContextService.class);
    private final ObjectProvider<List<McpSyncClient>> clients;

    public McpTravelContextService(ObjectProvider<List<McpSyncClient>> clients) {
        this.clients = clients;
    }

    public Optional<String> destinationContext(TravelWatch watch) {
        for (McpSyncClient client : clients.getIfAvailable(List::of)) {
            try {
                Optional<McpSchema.Tool> tool = client.listTools().tools().stream()
                    .filter(this::isTravelContextTool)
                    .min(Comparator.comparing(McpSchema.Tool::name));
                if (tool.isEmpty()) {
                    continue;
                }
                McpSchema.CallToolResult result = client.callTool(new McpSchema.CallToolRequest(tool.get().name(), Map.of(
                    "destination", watch.getDestination(),
                    "departureLocation", watch.getDepartureLocation(),
                    "tripType", watch.getTripType(),
                    "startDate", watch.getStartDate().toString(),
                    "endDate", watch.getEndDate().toString(),
                    "travellers", watch.getTravellers()
                )));
                String text = resultToText(result);
                if (!text.isBlank()) {
                    return Optional.of("MCP " + tool.get().name() + ": " + text);
                }
            } catch (RuntimeException ex) {
                log.warn("MCP travel context lookup failed: {}", ex.getMessage());
            }
        }
        return Optional.empty();
    }

    private boolean isTravelContextTool(McpSchema.Tool tool) {
        String haystack = (tool.name() + " " + tool.title() + " " + tool.description()).toLowerCase();
        return haystack.contains("travel")
            || haystack.contains("destination")
            || haystack.contains("place")
            || haystack.contains("poi")
            || haystack.contains("attraction")
            || haystack.contains("hotel");
    }

    private String resultToText(McpSchema.CallToolResult result) {
        if (Boolean.TRUE.equals(result.isError())) {
            return "";
        }
        if (result.structuredContent() != null) {
            return result.structuredContent().toString();
        }
        return result.content().stream()
            .map(content -> content instanceof McpSchema.TextContent text ? text.text() : content.toString())
            .filter(text -> text != null && !text.isBlank())
            .findFirst()
            .orElse("");
    }
}
