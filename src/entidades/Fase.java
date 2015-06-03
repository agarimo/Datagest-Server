package entidades;

import main.DataGestServer;

/**
 *
 * @author Agárimo
 */
public class Fase {

    int idFase;
    String codigo;
    String origen;
    int tipo;
    String texto1;
    String texto2;
    String texto3;
    int dias;
    
    public Fase(){
        
    }

    public Fase(String codigo, String origen, int tipo, String texto1, String texto2, String texto3, int dias) {
        this.codigo = codigo;
        this.origen = origen;
        this.tipo = tipo;
        this.texto1 = texto1;
        this.texto2 = texto2;
        this.texto3 = texto3;
        this.dias = dias;
    }

    public Fase(int idFase, String codigo, String origen, int tipo, String texto1, String texto2, String texto3, int dias) {
        this(codigo, origen, tipo, texto1, texto2, texto3, dias);
        this.idFase = idFase;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public int getIdFase() {
        return idFase;
    }

    public void setIdFase(int idFase) {
        this.idFase = idFase;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getTexto1() {
        return texto1;
    }

    public void setTexto1(String texto1) {
        this.texto1 = texto1;
    }

    public String getTexto2() {
        return texto2;
    }

    public void setTexto2(String texto2) {
        this.texto2 = texto2;
    }

    public String getTexto3() {
        return texto3;
    }

    public void setTexto3(String texto3) {
        this.texto3 = texto3;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "(" + codigo + ")" + dias + tipoToString();
    }

    private String tipoToString() {
        
        if (tipo == 1) {
            return "ND";
        } else if (tipo == 2) {
            return "RS";
        } else if (tipo == 3) {
            return "RR";
        } else {
            return "Desconocido";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Fase other = (Fase) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if ((this.origen == null) ? (other.origen != null) : !this.origen.equals(other.origen)) {
            return false;
        }
        if (this.tipo != other.tipo) {
            return false;
        }
        if ((this.texto1 == null) ? (other.texto1 != null) : !this.texto1.equals(other.texto1)) {
            return false;
        }
        if ((this.texto2 == null) ? (other.texto2 != null) : !this.texto2.equals(other.texto2)) {
            return false;
        }
        if ((this.texto3 == null) ? (other.texto3 != null) : !this.texto3.equals(other.texto3)) {
            return false;
        }
        if (this.dias != other.dias) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 97 * hash + (this.origen != null ? this.origen.hashCode() : 0);
        hash = 97 * hash + this.tipo;
        hash = 97 * hash + (this.texto1 != null ? this.texto1.hashCode() : 0);
        hash = 97 * hash + (this.texto2 != null ? this.texto2.hashCode() : 0);
        hash = 97 * hash + (this.texto3 != null ? this.texto3.hashCode() : 0);
        hash = 97 * hash + this.dias;
        return hash;
    }
    
    public String creaFase(){
        String query="INSERT into datagest.fase (origen, codigo, tipo, texto1, texto2,texto3, dias) values("
                + DataGestServer.entrecomillar(getOrigen()) + ","
                + DataGestServer.entrecomillar(getCodigo()) + ","
                + getTipo() + ","
                + DataGestServer.entrecomillar(getTexto1()) + ","
                + DataGestServer.entrecomillar(getTexto2()) + ","
                + DataGestServer.entrecomillar(getTexto3()) + ","
                + getDias()
                + ")";
        return query;
    }
    
    public String editaFase(){
        String query="UPDATE datagest.fase SET "
                + "origen=" + DataGestServer.entrecomillar(getOrigen()) + ","
                + "codigo=" + DataGestServer.entrecomillar(getCodigo()) + ","
                + "tipo=" + getTipo() + ","
                + "dias=" + getDias() + ","
                + "texto1=" + DataGestServer.entrecomillar(getTexto1()) + ","
                + "texto2=" + DataGestServer.entrecomillar(getTexto2()) + ","
                + "texto3=" + DataGestServer.entrecomillar(getTexto3())
                + "WHERE idFase=" + getIdFase();
        return query;
    }
    
    public String borraFase(){
        String query="DELETE FROM datagest.fase WHERE idFase=" + getIdFase() + ";";
        return query;
    }
    
    public String buscaFase(){
        String query="SELECT * FROM datagest.fase WHERE texto1=" + DataGestServer.entrecomillar(getTexto1()) + " and texto2=" + DataGestServer.entrecomillar(getTexto2()) +
                "and texto3="+DataGestServer.entrecomillar(getTexto3())+" and origen="+DataGestServer.entrecomillar(getOrigen());
        return query;
    }
    
    
}
