package dev.danielmesquita.miniauthorizer.projection;

public interface UserDetailsProjection {
  String getUsername();

  String getPassword();

  String getAuthority();

  Long getRoleId();
}
