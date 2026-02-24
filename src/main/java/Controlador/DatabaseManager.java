/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Entidad.Continente;
import Entidad.Pais;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/**
 *
 * @author Montero
 */
public class DatabaseManager {

    private static final String NOMBRE_BD = "local";
    private static final String URI = "mongodb://localhost:27017";
    private static final String TABLA_CONTINENTES = "continentes";
    private static final String TABLA_PAISES = "pais";

    MongoClient mongo;
    MongoDatabase database;

    public boolean runMongoDatabase() {
        try {
            mongo = MongoClients.create(URI);
            database = mongo.getDatabase(NOMBRE_BD);
            System.out.println("Database conectada con éxito");
            return true;
        } catch (Exception e) {
            System.out.println("Error al conectar con MongoDB: " + e.getMessage());
            return false;
        }
    }

    public void closeMongoDatabase() {
        if (mongo != null) {
            mongo.close();
        }
        System.out.println("Database cerrada con éxito");
    }

    public void añadirContinentes(Continente continente) {
        try {
            MongoCollection<Document> collection = database.getCollection(TABLA_CONTINENTES);
            Document document = new Document("name", limpiarTexto(continente.getName()));
            collection.insertOne(document);
        } catch (Exception e) {
            System.out.println("Error insertando continente: " + e.getMessage());
        } finally {
            this.closeMongoDatabase();
        }
    }

    public void añadirPais(Pais pais) {
        try {
            MongoCollection<Document> collection = database.getCollection(TABLA_PAISES);
            Document document = new Document("name", limpiarTexto(pais.getNombrePais()))
                    .append("numberPeople", pais.getNumHabitantes())
                    .append("continenteId", pais.getContinenteId());

            collection.insertOne(document);
        } catch (Exception e) {
            System.out.println("Error insertando país: " + e.getMessage());
        } finally {
            this.closeMongoDatabase();
        }
    }

    public List<Continente> getListaDeContinentes() {
        List<Continente> lista = new ArrayList<>();

        try {
            MongoCollection<Document> collection = database.getCollection(TABLA_CONTINENTES);
            FindIterable<Document> documentos = collection.find();

            for (Document doc : documentos) {
                Continente continente = new Continente(doc.getString("name"));
                lista.add(continente);
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo continentes: " + e.getMessage());
        }

        return lista;
    }

    public List<Pais> getListaPaises() {
        List<Pais> lista = new ArrayList<>();

        try {
            MongoCollection<Document> collection = database.getCollection(TABLA_PAISES);
            FindIterable<Document> documentos = collection.find();

            for (Document doc : documentos) {
                Pais pais = new Pais(0, "", "");
                pais.setNombrePais(doc.getString("name"));
                pais.setNumHabitantes(doc.getInteger("numberPeople"));
                pais.setContinenteId(doc.getString("continenteId"));
                lista.add(pais);
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo países: " + e.getMessage());
        }

        return lista;
    }

    public String getIdFromContinente(String nombreContinente) {
        String id = null;
        try {
            MongoCollection<Document> collection = database.getCollection(TABLA_CONTINENTES);
            Document continente = collection.find(Filters.eq("name", limpiarTexto(nombreContinente))).first();

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
            MongoCollection<Document> collection = database.getCollection(TABLA_CONTINENTES);
            Document continente = collection.find(Filters.eq("name", limpiarTexto(nombreContinente))).first();

            if (continente != null) {
                id = continente.getObjectId("_id").toHexString();
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo id del continente: " + e.getMessage());
        }
        return id;
    }

    public void deletePais(Pais pais) {
        try {
            MongoCollection<Document> collection = database.getCollection(TABLA_PAISES);
            collection.deleteOne(Filters.eq("name", limpiarTexto(pais.getNombrePais())));

        } catch (Exception e) {
            System.out.println("Error borrando país: " + e.getMessage());
        } finally {
            this.closeMongoDatabase();
        }
    }

    private String limpiarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim();
    }
}
