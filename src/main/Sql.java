package main;

import entidades.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Agárimo
 */
public class Sql {

    Connection con;
    Statement stmt;
    ResultSet rs;

    public Sql(Conexion conexion) throws SQLException {
        con = DriverManager.getConnection(conexion.getRuta(), conexion.getUsuario(), conexion.getPass());
    }

    public static boolean ejecutar(String query, Conexion conexion) {
        boolean bool = false;
        try {
            Connection con = DriverManager.getConnection(conexion.getRuta(), conexion.getUsuario(), conexion.getPass());
            Statement stmt = con.createStatement();

            bool = stmt.execute(query);

            con.close();

            return bool;
        } catch (SQLException ex) {
//            vista.Error.sql(Datagest.ventana, ex.getMessage());
            return bool;
        }
    }

    public static int comprobarFase(Fase fase, Conexion conexion) {
        int id;
        try {
            Connection con = DriverManager.getConnection(conexion.getRuta(), conexion.getUsuario(), conexion.getPass());
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(fase.buscaFase());
            System.out.println(fase.buscaFase());

            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                id = -1;
            }

            con.close();
            System.out.println(id);
            return id;

        } catch (SQLException ex) {
//            vista.Error.sql(Datagest.ventana, ex.getMessage());
            return -1;
        }
    }

    public boolean ejecutar(String query) throws SQLException {
        boolean bool;
        stmt = con.createStatement();
        bool = stmt.execute(query);

        return bool;
    }

    public ResultSet ejecutarQueryRs(String query) throws SQLException {
        ResultSet result;
        stmt = con.createStatement();
        result = stmt.executeQuery(query);

        return result;
    }

    public int buscar(String query) throws SQLException {
        int id;
        ResultSet result = ejecutarQueryRs(query);

        if (result.next()) {
            id = result.getInt(1);
        } else {
            id = -1;
        }
        return id;
    }

    public int ultimoRegistro() throws SQLException {
        int a = -1;
        String query = "SELECT last_insert_id()";

        rs = ejecutarQueryRs(query);
        if (rs.next()) {
            a = rs.getInt(1);
        } else {
            System.out.println("exception");
        }

        return a;
    }

    public int contadorRegistros(String query) throws SQLException {
        int a;

        rs = ejecutarQueryRs(query);
        rs.first();
        a = rs.getInt(1);

        return a;
    }

    public void close() throws SQLException {
        con.close();
    }

    //Listados
    public List<Boletin> listaBoletin(String query) throws SQLException {
        List<Boletin> aux = new ArrayList<Boletin>();
        Boletin boletin;
        rs = ejecutarQueryRs(query);

        while (rs.next()) {
            boletin = new Boletin(rs.getString("idBoletin"), rs.getDate("fecha"), rs.getString("nombre"));
            aux.add(boletin);
        }

        stmt.close();
        rs.close();

        return aux;
    }

    public List<Edicto> listaEdicto(String query) throws SQLException {
        List<Edicto> aux = new ArrayList<Edicto>();
        Edicto edicto;
        rs = ejecutarQueryRs(query);

        while (rs.next()) {
            edicto = new Edicto(rs.getString("idEdicto"),rs.getInt("idDescarga"), rs.getString("origen"), rs.getDate("fecha"), rs.getString("tipo"));
            aux.add(edicto);
        }

        stmt.close();
        rs.close();

        return aux;
    }

    public List<Descarga> listaDescarga(String query) throws SQLException {
        List<Descarga> aux = new ArrayList<Descarga>();
        Descarga descarga;
        rs = ejecutarQueryRs(query);

        while (rs.next()) {
            descarga = new Descarga(rs.getInt("idDescarga"), rs.getString("parametros"),rs.getString("csv"), rs.getDate("fecha"), rs.getString("datos"), rs.getInt("estado"));
            aux.add(descarga);
        }

        stmt.close();
        rs.close();

        return aux;
    }

    public List<Fase> listaFase(String query) throws SQLException {
        List<Fase> aux = new ArrayList<Fase>();
        Fase fase;
        rs = ejecutarQueryRs(query);

        while (rs.next()) {
            fase = new Fase(rs.getInt("idFase"), rs.getString("codigo"), rs.getString("origen"), rs.getInt("tipo"), rs.getString("texto1"),
                    rs.getString("texto2"), rs.getString("texto3"), rs.getInt("dias"));
            aux.add(fase);
        }

        stmt.close();
        rs.close();

        return aux;
    }

    public List<Origen> listaOrigen(String query) throws SQLException {
        List<Origen> aux = new ArrayList<Origen>();
        Origen origen;
        rs = ejecutarQueryRs(query);

        while (rs.next()) {
            origen = new Origen(rs.getString("idOrigen"), rs.getString("nombre"), rs.getString("codigo"), rs.getString("observaciones"));
            aux.add(origen);
        }

        stmt.close();
        rs.close();

        return aux;
    }

    public List<Tipo> listaTipo(String query) throws SQLException {
        List<Tipo> aux = new ArrayList<Tipo>();
        Tipo tipo;
        rs = ejecutarQueryRs(query);

        while (rs.next()) {
            tipo = new Tipo(rs.getString("idTipo"), rs.getString("nombre"));
            aux.add(tipo);
        }

        stmt.close();
        rs.close();

        return aux;
    }
    
    public Descarga getDescarga(Descarga aux) throws SQLException{
        Descarga descarga;
        rs=ejecutarQueryRs(aux.cargaDescarga());
        
        rs.first();
        descarga = new Descarga(rs.getInt("idDescarga"), rs.getString("parametros"),rs.getString("csv"), rs.getDate("fecha"), rs.getString("datos"), rs.getInt("estado"));
        
        stmt.close();
        rs.close();
        
        return descarga;
    }

    public Edicto getEdicto(Edicto aux) throws SQLException {
        Edicto edicto;
        rs = ejecutarQueryRs(aux.cargaEdicto());

        rs.first();
        edicto = new Edicto(rs.getString("idEdicto"),rs.getInt("idDescarga"), rs.getString("origen"), rs.getDate("fecha"), rs.getString("tipo"), rs.getString("datos"));

        stmt.close();
        rs.close();

        return edicto;
    }

    public Origen getOrigen(Origen aux) throws SQLException {
        Origen origen;
        rs = ejecutarQueryRs(aux.buscaOrigen());

        rs.first();
        origen = new Origen(rs.getString("idOrigen"), rs.getString("nombre"), rs.getString("codigo"), rs.getString("observaciones"));

        stmt.close();
        rs.close();

        return origen;
    }
}
