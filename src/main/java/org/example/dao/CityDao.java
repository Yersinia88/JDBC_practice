package org.example.dao;

import org.example.DatabaseException;
import org.example.dto.City;

public interface CityDao {

    int addCity(City city) throws DatabaseException;

    void updateCity(City city) throws DatabaseException;

    void deleteCity(int id) throws DatabaseException;

    void queryCityData(int id) throws DatabaseException;

}
