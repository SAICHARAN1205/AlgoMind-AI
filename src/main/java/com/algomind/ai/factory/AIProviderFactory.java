package com.algomind.ai.factory;

import com.algomind.ai.provider.AIProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AIProviderFactory {

    private final Map<String, AIProvider> providers;
    
    @Value("${active.ai.provider:gemini}")
    private String activeProviderName;

    public AIProviderFactory(List<AIProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(AIProvider::getProviderName, provider -> provider));
    }

    public AIProvider getActiveProvider() {
        return getProvider(activeProviderName);
    }

    public AIProvider getProvider(String providerName) {
        AIProvider provider = providers.get(providerName.toLowerCase());
        if (provider == null) {
            System.err.println("Requested provider '" + providerName + "' not found. Falling back to gemini.");
            return providers.get("gemini");
        }
        return provider;
    }
}
