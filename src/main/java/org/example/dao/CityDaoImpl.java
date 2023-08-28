package org.example.dao;

import org.example.DatabaseException;
import org.example.dto.City;

import java.sql.*;

public class CityDaoImpl implements CityDao {

    private final PreparedStatement queryPstmt;
    private final PreparedStatement addCityPstmt;
    private final PreparedStatement updateCityPstmt;
    private final PreparedStatement deleteCityPstmt;


    public CityDaoImpl(Connection conn) throws DatabaseException {
        try {
            try (Statement statement = conn.createStatement()) {
                String sql = "ALTER TABLE city DROP FOREIGN KEY IF EXISTS city_ibfk_1;";
                statement.executeUpdate(sql);
            }
            queryPstmt = conn.prepareStatement("" +
                    "SELECT * FROM city\n" +
                    "WHERE ID = ?");

            addCityPstmt = conn.prepareStatement("" +
                    "INSERT INTO city (Name, CountryCode, District, Population)\n" +
                    "VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);

            updateCityPstmt = conn.prepareStatement("" +
                    "UPDATE city\n" +
                    "SET District = ?, Population = ?\n" +
                    "WHERE ID = ?;", Statement.KEEP_CURRENT_RESULT);

            deleteCityPstmt = conn.prepareStatement("" +
                    "DELETE FROM city\n" +
                    "WHERE ID = ?;");

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public int addCity(City city) throws DatabaseException {
        if (city.getCityName() == null || city.getCityName().isEmpty()) {
            throw new IllegalArgumentException("A város nevének megadása kötelező.");
        }
        if (city.getCountryCode() == null || city.getCountryCode().isEmpty()) {
            throw new IllegalArgumentException("A város kódjának megadása kötelező.");
        }
        if (city.getDistrict() == null || city.getDistrict().isEmpty()) {
            throw new IllegalArgumentException("A város megyéjének megadása kötelező.");
        }
        if (city.getPopulation() == null || city.getPopulation().toString().isEmpty()) {
            throw new IllegalArgumentException("A város lakosságának a számának a megadása kötelező.");
        }

        try {
            addCityPstmt.setString(1, city.getCityName());
            addCityPstmt.setString(2, city.getCountryCode());
            addCityPstmt.setString(3, city.getDistrict());
            addCityPstmt.setInt(4, city.getPopulation());

            int result = addCityPstmt.executeUpdate();
            if (result != 1) {
                throw new DatabaseException("Nem sikerült létrehozni a rekordot" + city);
            } else {
                System.out.println("A hozzáadás sikeres volt!");
            }

            ResultSet generatedKeys = addCityPstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new DatabaseException("Nem sikerült létrehozni a rekordot: " + city);
            }

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void updateCity(City city) throws DatabaseException {
        if (city.getDistrict() == null) {
            throw new DatabaseException("A megye nevének megadása köteléző.");
        }

        if (city.getPopulation() == null) {
            throw new DatabaseException("A város népességének megadása köteléző.");
        }


        try {
            updateCityPstmt.setString(1, city.getDistrict());
            updateCityPstmt.setInt(2, city.getPopulation());
            updateCityPstmt.setInt(3, city.getId());

            int isUpdated = updateCityPstmt.executeUpdate();

            if (isUpdated == 0) {
                throw new DatabaseException("Nincs ilyen ID. Nem sikerült a rekord módosítása.");
            } else {
                System.out.println("A módosítás megtörtént!");
            }

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }

    @Override
    public void deleteCity(int id) throws DatabaseException {
        if (id == 0) {
            throw new DatabaseException("Az ID mező kitöltése kötelező");
        }

        try {
            deleteCityPstmt.setInt(1, id);

            int isDeleted = deleteCityPstmt.executeUpdate();

            if (isDeleted == 0) {
                System.out.println("Nincs ilyen ID, nem tudjuk törölni a rekordot.");
            } else {
                System.out.println("A törlés megtörtént!");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void queryCityData(int id) throws DatabaseException {
        if (id == 0) {
            throw new DatabaseException("Az ID mező kitöltése kötelező");
        }
        try {
            queryPstmt.setInt(1, id);
            ResultSet result = queryPstmt.executeQuery();
            while (result.next()) {
                int id2 = result.getInt(1);
                String cityName = result.getString(2);
                String countryCode = result.getString(3);
                String district = result.getString(4);
                int population = result.getInt(5);
                System.out.print("Valóban ezt a rekordot szerentné módosítani? ");
                System.out.printf("%d, %s, %s, %s, %d%n", id2, cityName, countryCode, district, population);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }
}
