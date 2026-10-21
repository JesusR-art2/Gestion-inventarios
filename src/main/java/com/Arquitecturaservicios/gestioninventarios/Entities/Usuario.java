package com.Arquitecturaservicios.gestioninventarios.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Entity
@Table(name="usuario")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Usuario{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime lastLogin;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<UsuarioRol> roles = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreate;

    @UpdateTimestamp
    private LocalDateTime dateUpdate;

}

