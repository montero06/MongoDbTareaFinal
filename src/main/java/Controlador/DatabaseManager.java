/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import com.joseluu.proyectofinalmongojavi.entidad.Continente;
import com.joseluu.proyectofinalmongojavi.entidad.Pais;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Montero
 */
public class DatabaseManager {

    String bd = "local";
    String uri = "mongodb://localhost:27017";
    MongoClient mongo;
    MongoDatabase database;

    public boolean runMongoDatabase() {
        boolean status = false;

        try {
            mongo = MongoClients.create(uri);
            database = mongo.getDatabase(bd);

            System.out.println("Database conectada con éxito");

            status = true;
        } catch (Exception e) {
            System.out.println("Error al conectar con MongoDB:" + e.getMessage());
            status = false;
        }

        return status;
    }

    public void closeMongoDatabase() {
        mongo.close();

        System.out.println("Database cerrada con exitos");
    }

    public void añadirContinentes(Continente continente) {
        try {
            MongoCollection<Document> collection
                    = database.getCollection("continentes");
            Document document = new Document("name", continente.getName());

            collection.insertOne(document);

        } catch (Exception e) {
            System.out.println("Error insertando: " + e.getMessage());
        } finally {
            this.closeMongoDatabase();
        }
    }

    public void añadirPais(Pais pais) {
        try {
            MongoCollection<Document> collection
                    = database.getCollection("pais");
            Document document = new Document("name", pais.getNombrePais())
                    .append("numberPeople", pais.getNumHabitantes())
                    .append("continenteId", pais.getContinenteId());

            collection.insertOne(document);

        } catch (Exception e) {
            System.out.println("Error insertando: " + e.getMessage());
        } finally {
            this.closeMongoDatabase();
        }
    }

    public List<Continente> getListaDeContinentes() {

        List<Continente> lista = new ArrayList<>();

        try {
            MongoCollection<Document> collection
                    = database.getCollection("continentes");

            FindIterable<Document> documentos = collection.find();

            for (Document doc : documentos) {

                Continente continente = new Continente("");

                continente.setName(doc.getString("name"));

                lista.add(continente);
            }

        } catch (Exception e) {
            System.out.println("Error obteniendo continentes:" + e.getMessage());
        }

        return lista;
    }

    public List<Pais> getListaPaises() {

        List<Pais> lista = new ArrayList<>();

        try {
            MongoCollection<Document> collection
                    = database.getCollection("pais");

            FindIterable<Document> documentos = collection.find();

            for (Document doc : documentos) {

                Pais pais = new Pais(0, "", "");

                pais.setNombrePais(doc.getString("name"));
                pais.setNumHabitantes(doc.getInteger("numberPeople"));
                pais.setContinenteId(doc.getString("continenteId"));

                lista.add(pais);
            }

        } catch (Exception e) {
            System.out.println("Error obteniendo paises:" + e.getMessage());
        }

        return lista;
    }

    public String getIdFromContinente(String nombreContinente) {

        String id = null;

        try {

            MongoCollection<Document> collection
                    = database.getCollection("continentes");

            Document continente = collection.find(
                    Filters.eq("name", nombreContinente)
            ).first();

            if (continente != null) {
                id = continente.getObjectId("_id").toString();
            }

        } catch (Exception e) {
            System.out.println("Error obteniendo id del continente: " + e.getMessage());
        }

        return id;
    }

    public String getIdContinentePuro(String nombreContinente) {
        String id = null;
        try {
            MongoCollection<Document> collection = database.getCollection("continentes");
            Document continente = collection.find(Filters.eq("name", nombreContinente)).first();

            if (continente != null) {
                // toHexString() extrae solo los 24 caracteres (ej: 698e1de2...)
                id = continente.getObjectId("_id").toHexString();
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo id del continente: " + e.getMessage());
        }
        return id;
    }

    public void deletePais(Pais pais) {
        try {
            MongoCollection<Document> collection
                    = database.getCollection("pais");
            collection.deleteOne(
                    Filters.eq("name", pais.getNombrePais())
            );

        } catch (Exception e) {
            System.out.println("Error borrando: " + e.getMessage());
        } finally {
            this.closeMongoDatabase();
        }
    }

}
