package com.libraryapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;

    // Constructor vacío requerido por JPA.
    public User() {}

    // Crea un usuario con nombre de usuario y email.
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Devuelve el identificador del usuario.
    public Integer getId() {
        return id;
    }

    // Asigna el identificador del usuario.
    public void setId(Integer id) {
        this.id = id;
    }

    // Devuelve el nombre de usuario.
    public String getUsername() {
        return username;
    }

    // Asigna el nombre de usuario.
    public void setUsername(String username) {
        this.username = username;
    }

    // Devuelve el email del usuario.
    public String getEmail() {
        return email;
    }

    // Asigna el email del usuario.
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof User)) return false;
        User user = (User) object;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
