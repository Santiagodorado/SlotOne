package usuarios.capaAccesoADatos.repositories;

import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import usuarios.capaAccesoADatos.models.RolEntity;
import usuarios.capaAccesoADatos.models.UsuarioEntity;

@Repository
public class UsuarioRepository {

    private static final String SELECT_JOIN =
            "SELECT u.id, u.nombres, u.apellidos, u.correo, u.telefono, u.clave, "
                    + "u.tipo_identificacion, u.num_identificacion, u.id_rol, r.nombre AS rol_nombre "
                    + "FROM usuario u LEFT JOIN rol r ON u.id_rol = r.id";

    private final DataSource dataSource;

    public UsuarioRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<UsuarioEntity> findById(Integer idUsuario) {
        System.out.println("Consultar usuario por id");
        String sql = SELECT_JOIN + " WHERE u.id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement sentencia = conn.prepareStatement(sql)) {
            sentencia.setInt(1, idUsuario);
            try (ResultSet res = sentencia.executeQuery()) {
                if (res.next()) {
                    System.out.println("Usuario encontrado");
                    return Optional.of(mapUsuario(res));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Collection<UsuarioEntity>> findAll() {
        System.out.println("Consultar todos los usuarios");
        Collection<UsuarioEntity> usuario = new LinkedList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement sentencia = conn.prepareStatement(SELECT_JOIN);
                ResultSet res = sentencia.executeQuery()) {
            while (res.next()) {
                usuario.add(mapUsuario(res));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        return usuario.isEmpty() ? Optional.empty() : Optional.of(usuario);
    }

    public UsuarioEntity save(UsuarioEntity objUsuario) {
        System.out.println("Guardar usuario");
        UsuarioEntity objUsuarioAlmacenado = null;
        String consulta =
                "INSERT INTO usuario (nombres, apellidos, correo, telefono, clave, tipo_identificacion, num_identificacion, id_rol) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement sentencia =
                        conn.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, objUsuario.getNombres());
            sentencia.setString(2, objUsuario.getApellidos());
            sentencia.setString(3, objUsuario.getCorreo());
            sentencia.setString(4, objUsuario.getTelefono());
            sentencia.setString(5, objUsuario.getClave());
            sentencia.setString(6, objUsuario.getTipoIdentificacion().toString());
            sentencia.setString(7, String.valueOf(objUsuario.getNumIdentificacion()));
            sentencia.setInt(8, objUsuario.getRol().getId());
            int resultado = sentencia.executeUpdate();

            ResultSet generatedKeys = sentencia.getGeneratedKeys();
            if (generatedKeys.next()) {
                int idGenerado = generatedKeys.getInt(1);
                objUsuario.setId(idGenerado);
                System.out.println("ID generado: " + idGenerado);
                if (resultado == 1) {
                    objUsuarioAlmacenado = this.findById(idGenerado).orElse(null);
                }
            } else {
                System.out.println("No se pudo obtener el ID generado.");
            }
        } catch (SQLException e) {
            System.out.println("Error al guardar usuario: " + e.getMessage());
        }

        return objUsuarioAlmacenado;
    }

    public boolean delete(Integer idUsuario) {
        System.out.println("Eliminar usuario");
        String consulta = "DELETE FROM usuario WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement sentencia = conn.prepareStatement(consulta)) {
            sentencia.setLong(1, idUsuario);
            int resultado = sentencia.executeUpdate();
            return resultado == 1;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        }
        return false;
    }

    public Optional<UsuarioEntity> update(Integer idUsuario, UsuarioEntity usuarioActualizado) {
        System.out.println("Actualizar usuario");
        UsuarioEntity objUsuarioActualizado = null;
        int resultado = -1;
        String consulta =
                "UPDATE usuario SET nombres = ?, apellidos = ?, correo = ?, telefono = ?, clave = ?, tipo_identificacion = ?, num_identificacion = ?, id_rol = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement sentencia = conn.prepareStatement(consulta)) {
            sentencia.setString(1, usuarioActualizado.getNombres());
            sentencia.setString(2, usuarioActualizado.getApellidos());
            sentencia.setString(3, usuarioActualizado.getCorreo());
            sentencia.setString(4, usuarioActualizado.getTelefono());
            sentencia.setString(5, usuarioActualizado.getClave());
            sentencia.setString(6, usuarioActualizado.getTipoIdentificacion().toString());
            sentencia.setString(7, String.valueOf(usuarioActualizado.getNumIdentificacion()));
            sentencia.setLong(8, usuarioActualizado.getRol().getId());
            sentencia.setLong(9, idUsuario);

            resultado = sentencia.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
        }

        if (resultado == 1) {
            objUsuarioActualizado = this.findById(idUsuario).orElse(null);
        } else {
            System.out.println("No se pudo actualizar la reserva.");
        }

        return objUsuarioActualizado == null ? Optional.empty() : Optional.of(objUsuarioActualizado);
    }

    public Optional<UsuarioEntity> findByEmailAndPassword(String email, String password) {
        System.out.println("Consultar usuario por email y password");
        String sql = SELECT_JOIN + " WHERE u.correo = ? AND u.clave = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement sentencia = conn.prepareStatement(sql)) {
            sentencia.setString(1, email);
            sentencia.setString(2, password);
            try (ResultSet res = sentencia.executeQuery()) {
                if (res.next()) {
                    System.out.println("Usuario encontrado");
                    return Optional.ofNullable(mapUsuario(res));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<UsuarioEntity> findByEmail(String email) {
        System.out.println("Buscando usuario por email");
        String sql = SELECT_JOIN + " WHERE u.correo = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement sentencia = conn.prepareStatement(sql)) {
            sentencia.setString(1, email);
            try (ResultSet rs = sentencia.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Usuario encontrado");
                    return Optional.of(mapUsuario(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario por email: " + e.getMessage());
        }
        return Optional.empty();
    }

    private UsuarioEntity mapUsuario(ResultSet res) throws SQLException {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(res.getInt("id"));
        usuario.setNombres(res.getString("nombres"));
        usuario.setApellidos(res.getString("apellidos"));
        usuario.setCorreo(res.getString("correo"));
        usuario.setTelefono(res.getString("telefono"));
        usuario.setClave(res.getString("clave"));
        usuario.setTipoIdentificacion(UsuarioEntity.TipoIdentificacion.valueOf(res.getString("tipo_identificacion")));
        usuario.setNumIdentificacion(Integer.valueOf(res.getString("num_identificacion")));
        usuario.setRol(new RolEntity(res.getInt("id_rol"), res.getString("rol_nombre")));
        return usuario;
    }
}
