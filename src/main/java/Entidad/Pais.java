/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidad;

/**
 *
 * @author Montero
 */
public class Pais {
    private int numHabitantes;
    private String nombrePais;
    private String continenteId;

    public Pais(int numHabitantes, String nombrePais, String continenteId) {
        this.numHabitantes = numHabitantes;
        this.nombrePais = nombrePais;
        this.continenteId = continenteId;
    }

    public int getNumHabitantes() {
        return numHabitantes;
    }

    public void setNumHabitantes(int numHabitantes) {
        this.numHabitantes = numHabitantes;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }

    public String getContinenteId() {
        return continenteId;
    }

    public void setContinenteId(String continenteId) {
        this.continenteId = continenteId;
    }
    
    
    
    
    
}
