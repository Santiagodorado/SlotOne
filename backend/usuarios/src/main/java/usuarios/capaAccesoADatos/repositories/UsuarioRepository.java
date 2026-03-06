package usuarios.capaAccesoADatos.repositories;
import java.sql.*;
import java.util.*;

import org.springframework.stereotype.Repository;

import usuarios.capaAccesoADatos.models.RolEntity;
import usuarios.capaAccesoADatos.models.UsuarioEntity;
import usuarios.capaAccesoADatos.repositories.conexion.conexionBD;

@Repository
public class UsuarioRepository {

    private final conexionBD conexionABaseDeDatos;

    public UsuarioRepository() { this.conexionABaseDeDatos = new conexionBD();}

    public Optional<UsuarioEntity> findById(Integer idUsuario) {
        System.out.println("Consultar usuario por id");
        UsuarioEntity objUsuario = null;
        
        try {
            conexionABaseDeDatos.conectar();
            PreparedStatement sentencia = null;
            String consulta = """
                SELECT u.*, r.nombre
                FROM usuario u 
                LEFT JOIN rol r ON u.idRol = r.id 
                WHERE u.id = ?  
            """;
            sentencia = conexionABaseDeDatos.getConnection().prepareStatement(consulta);
            sentencia.setLong(1, idUsuario);
            ResultSet res = sentencia.executeQuery();

            while (res.next()) {
                System.out.println("Usuario encontrado");
                objUsuario = mapUsuario(res);
            }

            sentencia.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }

        return objUsuario==null ? Optional.empty() : Optional.of(objUsuario);
    }

    public Optional<Collection<UsuarioEntity>> findAll() {
        System.out.println("Consultar todos los usuarios");
        Collection<UsuarioEntity> usuario = new LinkedList<UsuarioEntity>();
        
        try {
            conexionABaseDeDatos.conectar();
            PreparedStatement sentencia = null;
            String consulta = """
                SELECT u.*, r.nombre
                FROM usuario u 
                LEFT JOIN rol r ON u.idRol = r.id
            """;
            sentencia = conexionABaseDeDatos.getConnection().prepareStatement(consulta);
            ResultSet res = sentencia.executeQuery();
            while (res.next()) {
                usuario.add(mapUsuario(res));
            }

            sentencia.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }

        return usuario.isEmpty() ? Optional.empty() : Optional.of(usuario);
    }

    public UsuarioEntity save(UsuarioEntity objUsuario) {
        System.out.println("Guardar usuario");
        UsuarioEntity objUsuarioAlmacenado = null;
        int resultado = -1;

        try {
            conexionABaseDeDatos.conectar();
            PreparedStatement sentencia = null;
            String consulta = "INSERT INTO usuario (nombres, apellidos, correo, clave, tipoIdentificacion, numIdentificacion, idRol) VALUES (?, ?, ?, ?, ?, ?, ?)";
            sentencia = conexionABaseDeDatos.getConnection().prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, objUsuario.getNombres());
            sentencia.setString(2, objUsuario.getApellidos());
            sentencia.setString(3, objUsuario.getCorreo());
            sentencia.setString(4, objUsuario.getClave());
            sentencia.setString(5, objUsuario.getTipoIdentificacion().toString());
            sentencia.setString(6, String.valueOf(objUsuario.getNumIdentificacion()));
            sentencia.setInt(7, objUsuario.getRol().getId());
            resultado = sentencia.executeUpdate();

            ResultSet generatedKeys = sentencia.getGeneratedKeys();
            if (generatedKeys.next()) {
                int idGenerado = generatedKeys.getInt(1);
                objUsuario.setId(idGenerado);
                System.out.println("ID generado: " + idGenerado);
                if (resultado == 1) {
                    objUsuarioAlmacenado = this.findById(idGenerado).get();
                }
            }  else {
                System.out.println("No se pudo obtener el ID generado.");
            }
            sentencia.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            System.out.println("Error al guardar usuario: " + e.getMessage());
        }

        return objUsuarioAlmacenado;
    }

    public boolean delete(Integer idUsuario) { 
        System.out.println("Eliminar usuario");
        int resultado = -1;

        try {
            conexionABaseDeDatos.conectar();
            PreparedStatement sentencia = null;
            String consulta = "DELETE FROM usuario WHERE id = ?";
            sentencia = conexionABaseDeDatos.getConnection().prepareStatement(consulta);
            sentencia.setLong(1, idUsuario);
            resultado = sentencia.executeUpdate();

            sentencia.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        }

        return resultado == 1;
    }

    public Optional<UsuarioEntity> update(Integer idUsuario, UsuarioEntity usuarioActualizado) {
        System.out.println("Actualizar usuario");
        UsuarioEntity objUsuarioActualizado = null;
        int resultado = -1;

        try {
            conexionABaseDeDatos.conectar();
            PreparedStatement sentencia = null;
            String consulta = "UPDATE usuario SET nombres = ?, apellidos = ?, correo = ?, clave = ?, tipoIdentificacion = ?, numIdentificacion = ?, idRol = ? WHERE id = ?";
            sentencia = conexionABaseDeDatos.getConnection().prepareStatement(consulta);
            sentencia.setString(1, usuarioActualizado.getNombres());
            sentencia.setString(2, usuarioActualizado.getApellidos());
            sentencia.setString(3, usuarioActualizado.getCorreo());
            sentencia.setString(4, usuarioActualizado.getClave());
            sentencia.setString(5, usuarioActualizado.getTipoIdentificacion().toString());
            sentencia.setString(6, String.valueOf(usuarioActualizado.getNumIdentificacion()));
            sentencia.setLong(7, usuarioActualizado.getRol().getId());
            sentencia.setLong(8, idUsuario);

            resultado = sentencia.executeUpdate();
            sentencia.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
        }

        if (resultado == 1) {
            objUsuarioActualizado = this.findById(idUsuario).get();
        } else {
            System.out.println("No se pudo actualizar la reserva.");
        }

        return objUsuarioActualizado==null ? Optional.empty() : Optional.of(objUsuarioActualizado);
    }

    public Optional<UsuarioEntity> findByEmailAndPassword(String email, String password) {
        System.out.println("Consultar usuario por email y password");
        UsuarioEntity objUsuario = null;
        
        try {
            conexionABaseDeDatos.conectar();
            PreparedStatement sentencia = null;
            String consulta = """
                SELECT u.*, r.nombre
                FROM usuario u 
                LEFT JOIN rol r ON u.idRol = r.id 
                WHERE u.correo = ? AND u.clave = ?
            """;
            sentencia = conexionABaseDeDatos.getConnection().prepareStatement(consulta);
            sentencia.setString(1, email);
            sentencia.setString(2, password);
            ResultSet res = sentencia.executeQuery();

            if (res.next()) {
                System.out.println("Usuario encontrado");
                objUsuario = mapUsuario(res);
            }

            sentencia.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }

        return Optional.ofNullable(objUsuario);
    }

    public Optional<UsuarioEntity> findByEmail(String email) {
        System.out.println("Buscando usuario por email");
        UsuarioEntity usuario = null;
        try {
            conexionABaseDeDatos.conectar();
            PreparedStatement sentencia = null;
            String sql = """
                SELECT u.*, r.nombre as rol_nombre
                FROM usuario u 
                LEFT JOIN rol r ON u.idRol = r.id 
                WHERE u.correo = ?
            """;
            sentencia = conexionABaseDeDatos.getConnection().prepareStatement(sql);
            sentencia.setString(1, email);
            ResultSet rs = sentencia.executeQuery();
            while (rs.next()) {
                System.out.println("Usuario encontrado");
                usuario = new UsuarioEntity();
                usuario.setId(rs.getInt("id"));
                usuario.setNombres(rs.getString("nombres"));
                usuario.setApellidos(rs.getString("apellidos"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setClave(rs.getString("clave"));
                usuario.setTipoIdentificacion(UsuarioEntity.TipoIdentificacion.valueOf(rs.getString("tipoIdentificacion")));
                usuario.setNumIdentificacion(Integer.parseInt(rs.getString("numIdentificacion")));
                usuario.setRol(new RolEntity(rs.getInt("idRol"), rs.getString("rol_nombre")));
            }
            sentencia.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario por email: " + e.getMessage());
        }
        return usuario != null ? Optional.of(usuario) : Optional.empty();
    }

    private UsuarioEntity mapUsuario(ResultSet res) throws SQLException {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(res.getInt("id"));
        usuario.setNombres(res.getString("nombres"));
        usuario.setApellidos(res.getString("apellidos"));
        usuario.setCorreo(res.getString("correo"));
        usuario.setClave(res.getString("clave"));
        usuario.setTipoIdentificacion(UsuarioEntity.TipoIdentificacion.valueOf(res.getString("tipoIdentificacion")));
        usuario.setNumIdentificacion(Integer.valueOf(res.getString("numIdentificacion")));
        usuario.setRol(new RolEntity(res.getInt("idRol"), res.getString("nombre")));
        return usuario;
    }
}
