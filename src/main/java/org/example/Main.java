package org.example;

import org.example.models.Catalog;
import org.example.models.Plant;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/XMLPlantCatalogs";
    private static final String LOGIN = "postgres";
    private static final String PASSWORD = "root";

    private static Connection connection;

    public static void main(String[] args) {

        //Запрос файла у пользователя

        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter a path to the file:");
        Document doc;
        try {
            doc = SetDocument(scan.nextLine());
        } catch (Exception e) {
            System.out.println("An error of file opening: " + e);
            return;
        }

        //Получение каталога

        Catalog catalog = null;
        try {
            catalog = GetCatalog(doc);
        } catch (Exception e) {
            System.out.println("An error getting the catalog: " + e);
            return;
        }

        //Вывод информации о каталоге в консоль

        PrintCatalogInfo(catalog);

        //Подключение к БД

        try {
            ConnectToDB();
        } catch (SQLException e) {
            System.out.println("An error connection to DB: " + e);
            return;
        }

        //Передача данных в БД

        try {
            SendDataToDB(catalog);
        } catch (SQLException e) {
            System.out.println("An error sending data: " + e);
        }
    }

    private static String GetCatalogID(Catalog catalog) throws SQLException {

        //Проверка наличия каталога

        String query;
        query = String.format("select id " +
                "from d_cat_catalog " +
                "where uuid = '%s' and delivery_date = '%s'", catalog.getUuid(), new Date(catalog.getDate().getTime()));
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        String id;
        if (rs.next()) {
            id = rs.getString("id");
        } else id = "";

        ps.close();
        Objects.requireNonNull(rs).close();

        return id;
    }

    private static void SendDataToDB(Catalog catalog) throws SQLException {

        //Передача данных в БД

        String query;

        String id = GetCatalogID(catalog);

        Statement statement = connection.createStatement();
        if (id.equals("")) {
            query = String.format("insert into d_cat_catalog (uuid, delivery_date, company) " +
                    "values ('%s', '%s', '%s')", catalog.getUuid(), new Date(catalog.getDate().getTime()), catalog.getCompany());
            statement.executeUpdate(query);
            id = GetCatalogID(catalog);
        }

        for (Plant plant : catalog.getPlants()) {
            query = String.format("insert into f_cat_plants (common, botanical, zone, light, price, availability, catalog_id) " +
                    "values ('%s', '%s', '%s', '%s', %s, %s, %s)",
                    plant.getCommon(), plant.getBotanical(), plant.getZone(), plant.getLight(), plant.getPrice(), plant.getAvailability(), id);
            statement.executeUpdate(query);
        }
        statement.close();
    }

    private static void ConnectToDB() throws SQLException {

        //Подключение к БД

        connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
    }

    private static void PrintCatalogInfo(Catalog catalog) {

        //Вывод информации о каталоге в консоль

        int i = 0;
        System.out.println(catalog.getUuid() + " " + catalog.getDate() + " " + catalog.getCompany());
        for (Plant item : catalog.getPlants()) {
            System.out.println("--------" + ++i + "--------");
            System.out.println(item.getCommon());
            System.out.println(item.getBotanical());
            System.out.println(item.getZone());
            System.out.println(item.getLight());
            System.out.println(item.getPrice());
            System.out.println(item.getAvailability());
        }
    }

    private static Catalog GetCatalog(Document doc) throws Exception {
        Catalog catalog = new Catalog();
        Node nodeCatalog = doc.getFirstChild();

        //Парсинг аттрибутов каталога

        NamedNodeMap attributes = nodeCatalog.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.item(i).getNodeType() != Node.ATTRIBUTE_NODE) {
                continue;
            }
            switch (attributes.item(i).getNodeName()) {
                case "uuid": {
                    catalog.setUuid(attributes.item(i).getTextContent());
                    break;
                }
                case "date": {
                    DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                    catalog.setDate(format.parse(attributes.item(i).getTextContent()));
                    break;
                }
                case "company": {
                    catalog.setCompany(attributes.item(i).getTextContent());
                    break;
                }
            }
        }

        //Парсинг списка растений в каталоге

        NodeList nodePlants = nodeCatalog.getChildNodes();
        AddPlantsToCatalog(nodePlants, catalog);

        return catalog;
    }

    private static void AddPlantsToCatalog(NodeList nodePlants, Catalog catalog) {

        //Заполнение каталога растениями

        for (int i = 0; i < nodePlants.getLength(); i++) {
            if (nodePlants.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            NodeList plantInstance = nodePlants.item(i).getChildNodes();
            Plant plant = GetPlant(plantInstance);

            //Добавление в лист экземпляра растения

            catalog.getPlants().add(plant);
        }
    }

    private static Plant GetPlant(NodeList plantInstance) {

        //Парсинг элементов растения

        Plant plant = new Plant();
        for (int j = 0; j < plantInstance.getLength(); j++) {
            if (plantInstance.item(j).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (plantInstance.item(j).getNodeName()) {
                case "COMMON": {
                    plant.setCommon(plantInstance.item(j).getTextContent());
                    break;
                }
                case "BOTANICAL": {
                    plant.setBotanical(plantInstance.item(j).getTextContent());
                    break;
                }
                case "ZONE": {
                    plant.setZone(plantInstance.item(j).getTextContent());
                    break;
                }
                case "LIGHT": {
                    plant.setLight(plantInstance.item(j).getTextContent());
                    break;
                }
                case "PRICE": {
                    plant.setPrice(Double.parseDouble(plantInstance.item(j).getTextContent().substring(1)));
                    break;
                }
                case "AVAILABILITY": {
                    plant.setAvailability(Integer.parseInt(plantInstance.item(j).getTextContent()));
                    break;
                }
            }
        }
        return plant;
    }

    private static Document SetDocument(String path) throws Exception {

        //Загрузка файла, запрошенного пользователем

        File file = new File(path);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        return dbf.newDocumentBuilder().parse(file);
    }
}