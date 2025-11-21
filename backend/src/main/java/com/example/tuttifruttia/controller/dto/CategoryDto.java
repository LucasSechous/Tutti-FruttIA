package com.example.tuttifruttia.controller.dto;

public class CategoryDto {

    private Long id;
    private String name;
    private boolean activado;

    public CategoryDto() {}

    public CategoryDto(Long id, String name, boolean activado) {
        this.activado = activado;
        this.name = name;
        this.id = id;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isActivado() { return activado; }
    public void setActivado(boolean activado) { this.activado = activado; }


}
