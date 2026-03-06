package usuarios.capaAccesoADatos.repositories;

import java.sql.*;
import java.util.*;
import org.springframework.stereotype.Repository;

import usuarios.capaAccesoADatos.models.RolEntity;
import usuarios.capaAccesoADatos.repositories.conexion.conexionBD;

@Repository
public class RolRepository {
    private final conexionBD conexionABaseDeDatos;

    public RolRepository() {
        conexionABaseDeDatos = new conexionBD();
    }

    public Optional<RolEntity> findById(Integer idRol) {
        System.out.println("Consultando rol por ID");
        RolEntity objRol = null;

        try {
            conexionABaseDeDatos.conectar();
            PreparedStatement sentencia = null;
            String consulta = "SELECT * FROM rol WHERE id = ?";
            sentencia = conexionABaseDeDatos.getConnection().prepareStatement(consulta);
            sentencia.setInt(1, idRol);
            ResultSet res = sentencia.executeQuery();

            while (res.next()) {
                System.out.println("Rol encontrado");
                objRol = new RolEntity();
                objRol.setId(res.getInt("id"));
                objRol.setNombre(res.getString("nombre"));
            }

            sentencia.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            System.out.println("Error al consultar rol: " + e.getMessage());
        }

        return objRol == null ? Optional.empty() : Optional.of(objRol);
    }

    public Optional<Collection<RolEntity>> findAll() {
        System.out.println("Listando roles desde base de datos");
        Collection<RolEntity> rol = new LinkedList<>();

        try {
            conexionABaseDeDatos.conectar();
            String consulta = "SELECT * FROM rol";
            PreparedStatement sentencia = conexionABaseDeDatos.getConnection().prepareStatement(consulta);
            ResultSet res = sentencia.executeQuery();

            while (res.next()) {
                RolEntity objRol = new RolEntity();
                objRol.setId(res.getInt("id"));
                objRol.setNombre(res.getString("nombre"));
                rol.add(objRol);
            }
            sentencia.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            System.out.println("Error al listar roles: " + e.getMessage());
        }

        return rol.isEmpty() ? Optional.empty() : Optional.of(rol);
    }

    public RolEntity save(RolEntity rol) {
        System.out.println("Guardando rol");
        RolEntity objRolAlmacenado = null;
        int resultado = -1;

        try {
            conexionABaseDeDatos.conectar();
            PreparedStatement sentencia = null;
            String consulta = "INSERT INTO rol (nombre) VALUES (?)";
            sentencia = conexionABaseDeDatos.getConnection().prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, rol.getNombre());
            resultado = sentencia.executeUpdate();

            ResultSet generatedKeys = sentencia.getGeneratedKeys();
            if (generatedKeys.next()) {
                int idGenerado = generatedKeys.getInt(1);
                rol.setId(idGenerado);
                System.out.println("ID generado: " + idGenerado);
                if (resultado == 1) {
                    objRolAlmacenado = this.findById(idGenerado).get();
                }
            }  else {
                System.out.println("No se pudo obtener el ID generado.");
            }

            sentencia.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            System.out.println("Error al guardar rol: " + e.getMessage());
        }

        return objRolAlmacenado;
    }

    public boolean delete(Integer idRol) {
        System.out.println("Eliminando rol");
        int resultado = -1;

        try {
            conexionABaseDeDatos.conectar();
            String consulta = "DELETE FROM rol WHERE id = ?";
            PreparedStatement sentencia = conexionABaseDeDatos.getConnection().prepareStatement(consulta);
            sentencia.setLong(1, idRol);
            resultado = sentencia.executeUpdate();

            sentencia.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            System.out.println("Error al eliminar rol: " + e.getMessage());
        }

        return resultado == 1;
    }

    public Optional<RolEntity> update(Integer idRol, RolEntity rol) {
        System.out.println("Actualizando rol");
        RolEntity objRolActualizado = null;
        int resultado = -1;

        try {
            conexionABaseDeDatos.conectar();
            String consulta = "UPDATE rol SET nombre = ? WHERE id = ?";
            PreparedStatement sentencia = conexionABaseDeDatos.getConnection().prepareStatement(consulta);
            sentencia.setString(1, rol.getNombre());
            sentencia.setLong(2, idRol);

            resultado = sentencia.executeUpdate();
            sentencia.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            System.out.println("Error al actualizar rol: " + e.getMessage());
        }

        if (resultado == 1) {
            objRolActualizado = findById(idRol).orElse(null);
        }

        return objRolActualizado==null ? Optional.empty() : Optional.of(objRolActualizado);
    }
}
