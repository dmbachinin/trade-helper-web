package ru.trade.helper.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TokenConnectionData {
    @JsonProperty("observer-buyers")
    private Map<String, List<String>> observerBuyersTokens;
}
