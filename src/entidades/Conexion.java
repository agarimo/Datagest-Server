package entidades;

import java.io.Serializable;

/**
 *
 * @author Agárimo
 */
public class Conexion implements Serializable, Comparable<Conexion>{
    
    String nombre;
    String direccion;
    String puerto;
    String usuario;
    String pass;
    String ruta="jdbc:mysql://";
    
    public Conexion(String nombre, String direccion,String puerto, String usuario, String pass){
        this.nombre=nombre;
        this.direccion=direccion;
        this.puerto=puerto;
        this.usuario=usuario;
        this.pass=pass;
        this.ruta=this.ruta+direccion+":"+puerto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRuta() {
        return this.ruta;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    @Override
    public String toString(){
        return this.nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Conexion other = (Conexion) obj;
        if ((this.nombre == null) ? (other.nombre != null) : !this.nombre.equals(other.nombre)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.nombre != null ? this.nombre.hashCode() : 0);
        hash = 37 * hash + (this.direccion != null ? this.direccion.hashCode() : 0);
        hash = 37 * hash + (this.puerto != null ? this.puerto.hashCode() : 0);
        hash = 37 * hash + (this.usuario != null ? this.usuario.hashCode() : 0);
        hash = 37 * hash + (this.pass != null ? this.pass.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Conexion o) {
        String a=this.nombre;
        String b=o.getNombre();
        
        return a.compareTo(b);
    }
}
