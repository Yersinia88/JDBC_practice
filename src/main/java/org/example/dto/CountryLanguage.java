package org.example.dto;


import com.google.protobuf.Enum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class CountryLanguage {
    private String countryCode;
    private String language;
    private String isOfficial;
    private Double percentage;

    public CountryLanguage(String countryCode, String language, String isOfficial, Double percentage) {
        this.countryCode = countryCode;
        this.language = language;
        this.isOfficial = isOfficial;
        this.percentage = percentage;
    }

    public CountryLanguage(String countryCode, String language) {
        this.countryCode = countryCode;
        this.language = language;
    }
}
