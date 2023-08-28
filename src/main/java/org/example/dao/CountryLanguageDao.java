package org.example.dao;

import org.example.DatabaseException;
import org.example.dto.CountryLanguage;

public interface CountryLanguageDao {
    int addCountryLanguage (CountryLanguage countryLanguage) throws DatabaseException;
    void updateCountryLanguage(CountryLanguage countryLanguage) throws DatabaseException;
    void deleteCountryLanguage (CountryLanguage countryLanguage) throws DatabaseException;
    void queryCityData(CountryLanguage countryLanguage) throws DatabaseException;
}
