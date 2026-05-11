package usuarios.capaAccesoADatos.repositories;

import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import usuarios.capaAccesoADatos.models.RolEntity;

@Repository
public class RolRepository {

    private final DataSource dataSource;

    public RolRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<RolEntity> findById(Integer idRol) {
        System.out.println("Consultando rol por ID");
        String consulta = "SELECT * FROM rol WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement sentencia = conn.prepareStatement(consulta)) {
            sentencia.setInt(1, idRol);
            try (ResultSet res = sentencia.executeQuery()) {
                if (res.next()) {
                    System.out.println("Rol encontrado");
                    RolEntity objRol = new RolEntity();
                    objRol.setId(res.getInt("id"));
                    objRol.setNombre(res.getString("nombre"));
                    return Optional.of(objRol);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar rol: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Collection<RolEntity>> findAll() {
        System.out.println("Listando roles desde base de datos");
        Collection<RolEntity> rol = new LinkedList<>();
        String consulta = "SELECT * FROM rol";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement sentencia = conn.prepareStatement(consulta);
                ResultSet res = sentencia.executeQuery()) {
            while (res.next()) {
                RolEntity objRol = new RolEntity();
                objRol.setId(res.getInt("id"));
                objRol.setNombre(res.getString("nombre"));
                rol.add(objRol);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar roles: " + e.getMessage());
        }
        return rol.isEmpty() ? Optional.empty() : Optional.of(rol);
    }

    public RolEntity save(RolEntity rol) {
        System.out.println("Guardando rol");
        RolEntity objRolAlmacenado = null;
        String consulta = "INSERT INTO rol (nombre) VALUES (?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement sentencia =
                        conn.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, rol.getNombre());
            int resultado = sentencia.executeUpdate();

            ResultSet generatedKeys = sentencia.getGeneratedKeys();
            if (generatedKeys.next()) {
                int idGenerado = generatedKeys.getInt(1);
                rol.setId(idGenerado);
                System.out.println("ID generado: " + idGenerado);
                if (resultado == 1) {
                    objRolAlmacenado = this.findById(idGenerado).orElse(null);
                }
            } else {
                System.out.println("No se pudo obtener el ID generado.");
            }
        } catch (SQLException e) {
            System.out.println("Error al guardar rol: " + e.getMessage());
        }

        return objRolAlmacenado;
    }

    public boolean delete(Integer idRol) {
        System.out.println("Eliminando rol");
        String consulta = "DELETE FROM rol WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement sentencia = conn.prepareStatement(consulta)) {
            sentencia.setLong(1, idRol);
            int resultado = sentencia.executeUpdate();
            return resultado == 1;
        } catch (SQLException e) {
            System.out.println("Error al eliminar rol: " + e.getMessage());
        }
        return false;
    }

    public Optional<RolEntity> update(Integer idRol, RolEntity rol) {
        System.out.println("Actualizando rol");
        int resultado = -1;
        String consulta = "UPDATE rol SET nombre = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement sentencia = conn.prepareStatement(consulta)) {
            sentencia.setString(1, rol.getNombre());
            sentencia.setLong(2, idRol);
            resultado = sentencia.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar rol: " + e.getMessage());
        }

        if (resultado != 1) {
            return Optional.empty();
        }
        RolEntity objRolActualizado = findById(idRol).orElse(null);
        return objRolActualizado == null ? Optional.empty() : Optional.of(objRolActualizado);
    }
}
