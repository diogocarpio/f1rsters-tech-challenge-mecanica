package com.f1rsters.tech_challenge_mecanica.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class Veiculo {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Cliente cliente;
    @Column(unique=true) @NotBlank
    private String placa;
    private String marca;
    private String modelo;
    private int ano;
}