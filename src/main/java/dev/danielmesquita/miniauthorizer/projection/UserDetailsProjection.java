package dev.danielmesquita.miniauthorizer.projection;

public interface UserDetailsProjection {
  String getUsername();

  String getPassword();

  Long getRoleId();

  String getAuthority();
}
