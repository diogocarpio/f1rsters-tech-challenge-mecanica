package com.f1rsters.tech_challenge_mecanica.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Cliente {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String nome;
    @Column(unique=true)
    private String cpfCnpj;
    @OneToMany(mappedBy = "cliente")
    private List<Veiculo> veiculos;
}