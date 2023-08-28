package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor

public class City {
    private Integer id;
    private String cityName;
    private String countryCode;
    private String district;
    private Integer population;

    public City(Integer id, String district, Integer population) {
        this.id = id;
        this.district = district;
        this.population = population;
    }

    public City(String cityName, String countryCode, String district, Integer population) {
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.district = district;
        this.population = population;
    }
}
