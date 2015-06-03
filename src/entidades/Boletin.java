package entidades;

import java.util.Calendar;
import java.util.Date;
import main.DataGestServer;

/**
 *
 * @author Agárimo
 */
public class Boletin {

    private String idBoletin;
    private String origen;
    private Date fecha;
    private String ruta;
    
    public Boletin(){
        
    }
    
    public Boletin(String origen){
        this.origen=origen;
    }

    public Boletin(Date fecha, String origen) {
        this.origen = origen;
        this.fecha = fecha;
        this.ruta = fecha + "\\" + origen + "\\";
        idBoletin=generaId();
    }
    
    public Boletin(String idBoletin,Date fecha, String origen){
        this.idBoletin=idBoletin;
        this.fecha=fecha;
        this.origen=origen;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getIdBoletin() {
        return idBoletin;
    }

    public void setIdBoletin(String idBoletin) {
        this.idBoletin = idBoletin;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    private String generaId() {
        String cod;
        long codigo;
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.fecha);

        cod = cal.get(Calendar.DAY_OF_YEAR) + Integer.toString(cal.get(Calendar.YEAR) - 2010) + getOrigen();
        codigo = Long.parseLong(cod);
        cod = Long.toHexString(codigo);

        while (cod.length() < 8) {
            cod = "0" + cod;
        }
        return cod;
    }

    @Override
    public String toString() {
        return this.origen;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Boletin other = (Boletin) obj;
        if ((this.origen == null) ? (other.origen != null) : !this.origen.equals(other.origen)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.origen != null ? this.origen.hashCode() : 0);
        return hash;
    }
    
    
    
    public String creaBoletin(){
        String query="INSERT into datagest.boletin (idBoletin, fecha, origen) values("
                + DataGestServer.entrecomillar(getIdBoletin()) + ","
                + DataGestServer.entrecomillar(DataGestServer.imprimeFecha(getFecha())) + ","
                + DataGestServer.entrecomillar(getOrigen())
                + ")";
        return query;
    }
    
    public String editaBoletin(){
        String query="UPDATE datagest.boletin SET "
                + "idBoletin=" + DataGestServer.entrecomillar(getIdBoletin()) + ","
                + "fecha=" + DataGestServer.entrecomillar(DataGestServer.imprimeFecha(getFecha())) + ","
                + "origen=" + DataGestServer.entrecomillar(getOrigen()) + " "
                + "WHERE idBoletin=" + DataGestServer.entrecomillar(getIdBoletin());
        return query;
    }
    
    public String borraBoletin(){
        String query="DELETE FROM datagest.boletin WHERE idBoletin=" + DataGestServer.entrecomillar(getIdBoletin());
        return query;
    }
    
    public String buscaBoletin(){
        String query="SELECT count(*) FROM datagest.boletin WHERE idBoletin=" + DataGestServer.entrecomillar(getIdBoletin()) + ";";
        return query;
    }
    
}
