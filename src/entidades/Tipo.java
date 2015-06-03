package entidades;

import main.DataGestServer;

/**
 *
 * @author Agárimo
 */
public class Tipo {

    private String idTipo;
    private String nombre;
    
    public Tipo(String idTipo){
        this.idTipo=idTipo;
    }

    public Tipo(String idTipo, String nombre) {
        this.idTipo = idTipo;
        this.nombre = nombre;
    }

    public String getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(String idTipo) {
        this.idTipo = idTipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tipo other = (Tipo) obj;
        if ((this.idTipo == null) ? (other.idTipo != null) : !this.idTipo.equals(other.idTipo)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.idTipo != null ? this.idTipo.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.idTipo;
    }

    public String creaTipo() {
        String query = "INSERT into datagest.tipo (idTipo, nombre) values("
                + DataGestServer.entrecomillar(getIdTipo()) + ","
                + DataGestServer.entrecomillar(getNombre())
                + ")";
        return query;
    }

    public String editaTipo() {
        String query ="UPDATE datagest.tipo SET "
                + "nombre=" + DataGestServer.entrecomillar(getNombre())
                + "WHERE idTipo=" + DataGestServer.entrecomillar(getIdTipo());
        return query;
    }

    public String borraTipo() {
        String query = "DELETE FROM datagest.tipo WHERE idTipo=" + DataGestServer.entrecomillar(getIdTipo());
        return query;
    }

    public String buscaTipo() {
        String query = "SELECT * FROM datagest.tipo WHERE idTipo=" + DataGestServer.entrecomillar(getIdTipo());
        return query;
    }
}
