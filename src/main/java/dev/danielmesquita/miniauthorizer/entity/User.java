package dev.danielmesquita.miniauthorizer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_user")
public class User{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String password;

  private String role;
}
