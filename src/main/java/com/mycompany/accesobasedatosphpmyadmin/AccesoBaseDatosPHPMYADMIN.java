package com.mycompany.accesobasedatosphpmyadmin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

public class AccesoBaseDatosPHPMYADMIN {

    //Los datos para entrar a la base de datos
    static final String DB_URL = "jdbc:mysql://localhost/JCVD";
    static final String USER = "admin";
    static final String PASS = "admin";
    
    static String SELECT;
    //Columnas de la base de datos
    static String[] COLUMN = {"ID","NOMBRE","GENERO","FECHA","COMPAÑIA","PRECIO"};
    //Array para introducir los datos para crear nuevas entradas en la base de datos
    static String[] VALUES = new String[5];
    
    //Conexion a la base de datos para poder utilizarla en cualquier parte del codigo
    static Connection conn = null;
    //Statement para poder utilizarlo en cualquier parte del codigo
    static Statement stmt = null;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try 
        {
            //Creamos el PoolDataSource
            PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();
            
            //Introducimos los datos necesarios para conectar a la base de datos
            pds.setConnectionFactoryClassName("com.mysql.cj.jdbc.Driver");
            pds.setURL(DB_URL);
            pds.setUser(USER);
            pds.setPassword(PASS);
            pds.setInitialPoolSize(3);
            
            //Iniciamos la conexion a la base de datos
            conn = pds.getConnection();
            //Iniciamos el statement
            stmt = conn.createStatement();
            int opc;
            String cad;
            do
            {
                System.out.println("Que accion quieres realizar?");
                System.out.println("· 1 Ver juegos");
                System.out.println("· 2 Añadir juegos");
                System.out.println("· 3 Actualizar juego");
                System.out.println("· 4 Eliminar juego");
                System.out.println("· 0 Cerrar");  
                opc = sc.nextInt();
                switch (opc)
                {
                    case 1:
                        System.out.println("· 1 Ver todos");
                        System.out.println("· 2 Buscar juego");
                        System.out.println("· 3 Comprobar nombre");
                        System.out.println("· 4 Consulta personalizada");
                        opc = sc.nextInt();
                        switch (opc)
                        {
                            case 1:
                                SELECT = "SELECT * FROM VIDEOJUEGOS";
                                select(SELECT);                                
                                break;
                            case 2:
                                System.out.println("Por que quieres buscar?");
                                System.out.println("· 1 ID");
                                System.out.println("· 2 Nombre");
                                System.out.println("· 3 Genero");
                                System.out.println("· 4 Fecha");
                                System.out.println("· 5 Compañia");
                                System.out.println("· 6 Precio");
                                opc = sc.nextInt();
                                if (opc != 0)
                                {
                                    System.out.println("Que " + COLUMN[opc-1].toLowerCase() + " quieres buscar?");
                                    sc.nextLine();
                                    cad = sc.nextLine();
                                    SELECT = "SELECT * FROM VIDEOJUEGOS WHERE " + COLUMN[opc-1] + " = '" + cad + "'";
                                    select(SELECT);
                                }
                                else
                                {
                                    opc = -1;
                                }
                                break;
                            case 3:
                                sc.nextLine();
                                System.out.print("Escribe el nombre que quieres comprobar: ");
                                cad = sc.nextLine();
                                SELECT = "SELECT NOMBRE FROM VIDEOJUEGOS WHERE NOMBRE = '" + cad + "'";
                                boolean check = comprobar(cad);
                                if (check)
                                {
                                    System.out.println("Existe un videojuego con ese nombre");
                                }
                                else
                                {
                                    System.out.println("No existe un videojuego con ese nombre");
                                }
                                break;
                            case 4:
                                System.out.println("Escribe la consulta que quieras realizar:");
                                sc.nextLine();
                                SELECT = sc.nextLine();
                                select(SELECT);
                                break;
                        }
                        
                        break;
                    case 2:
                        
                        sc.nextLine();
                        System.out.print(" Nombre: ");
                        VALUES[0] = sc.nextLine();
                        System.out.print(" Genero: ");
                        VALUES[1] = sc.nextLine();
                        System.out.print(" Fecha: [YYYY/MM/DD] ");
                        VALUES[2] = sc.nextLine();
                        System.out.print(" Compañia: ");
                        VALUES[3] = sc.nextLine();
                        System.out.print(" Precio: [00.00] ");
                        VALUES[4] = sc.nextLine();  
                        insert(VALUES[0],VALUES[1],VALUES[2],VALUES[3],VALUES[4]);
                        break;
                    case 3:
                        System.out.println("Escribe el ID del juego que quieres modificar");
                        sc.nextLine();
                        String id = sc.nextLine();
                        System.out.println("Que quieres modificar?");
                        System.out.println("· 1 Nombre");
                        System.out.println("· 2 Genero");
                        System.out.println("· 3 Fecha");
                        System.out.println("· 4 Compañia");
                        System.out.println("· 5 Precio");
                        opc = sc.nextInt();
                        System.out.print("Escribe el nuevo valor de " + COLUMN[opc].toLowerCase() + ": ");
                        sc.nextLine();
                        cad = sc.nextLine();
                        update(opc,cad,id);
                        break;
                    case 4:
                            System.out.print("Escribe el nombre del juego que quieres borrar: ");
                            sc.nextLine();
                            cad = sc.nextLine();
                            delete(cad);
                        break;
                }
            } while (opc != 0);

        } 
        catch (SQLException e) 
        {
            System.out.println("Error: \n" + e);
        }
    }
    public static void select(String select)
    {
        //En caso de que la consulta contenga la palabra 'COMPAÑIA', dara un error al contener una 'Ñ', por lo que se sustituira aqui automaticamentre para evitar errores
        if (select.contains("COMPA?IA"))
        {
            select = select.replace("COMPA?IA", "COMPAÑIA");
        }
        try
        {
            //Ejecutara la consulta indicada
            ResultSet rs = stmt.executeQuery(select);
            //Mientras que la consulta de resultados
            while (rs.next()) 
            {
                //Se utiliza un try-catch con cada uno para en caso que la consulta no pida todos los datos, estos no se muestren y no de error
                //Muestra la columna de 'ID'
                try { System.out.println(" ID: " + rs.getString("ID")); }
                catch (SQLException e) {}
                //Muestra la columna de 'NOMBRE'
                try { System.out.println(" · Nombre: " + rs.getString("NOMBRE")); }
                catch (SQLException e) {}
                //Muestra la columna de 'GENERO'
                try { System.out.println(" · Genero: " + rs.getString("GENERO")); }
                catch (SQLException e) {}
                //Muestra la columna de 'FECHA'
                try { System.out.println(" · Fecha de publicacion: " + rs.getString("FECHA")); }
                catch (SQLException e) {}
                //Muestra la columna de 'COMPAÑIA'
                try { System.out.println(" · Compañia: " + rs.getString("COMPAÑIA")); }
                catch (SQLException e) {}
                //Muestra la columna de 'PRECIO'
                try { System.out.println(" · Precio: " + rs.getString("PRECIO")); }
                catch (SQLException e) {}                  
            }
        }
        catch (SQLException e)
        {
            System.out.println("No se ha encontrado el juego indicado");
        }
    }
    public static boolean insert(String nombre, String genero, String fecha, String compañia, String precio)
    {
        try
        {
            //Crea un consulta preparada para introducir datos a la base de datos
            String consulta = "INSERT INTO `videojuegos`(`ID`, `NOMBRE`, `GENERO`, `FECHA`, `COMPAÑIA`, `PRECIO`) VALUES (NULL,?,?,?,?,?)";
            PreparedStatement sentencia = conn.prepareStatement(consulta);
            //Sustituye las ? en la sentencia preparada por los valores enviados
            sentencia.setString(1,nombre);
            sentencia.setString(2,genero);
            sentencia.setString(3, fecha);
            sentencia.setString(4, compañia);
            sentencia.setString(5, precio);
            //Ejecuta la sentencia de INSERT
            sentencia.executeUpdate(); 
            System.out.println("Juego añadido correctamente");
            //Envia que se ha ejecutado la sentencia sin errores
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("No se ha podido añadir el juego correctamente");
            //Envia que ha ocurrido un error
            return false;
        }        
    }
    public static boolean update(int column, String valor,String id)
    {
        try
        {
            //Crea la consulta preparada para actualizar los datos de la base de datos con la columna indicada
            String consulta = "UPDATE VIDEOJUEGOS SET " + COLUMN[column] + " = ? WHERE ID = ?";
            PreparedStatement sentencia = conn.prepareStatement(consulta);
            //Sustituye la ? con los valores enviados
            sentencia.setString(1,valor);
            sentencia.setString(2, id);
            //Ejecuta la sentencia de UPDATE
            sentencia.executeUpdate();            
            System.out.println("Juego actualizado correctamente");
            //Envia que se ha ejecutado la sentencia sin errores
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("No se ha podido modificar el juego indicado");
            //Envia que ha ocurrido un error
            return false;
        }        
    }
    public static boolean delete(String nombre)
    {
        try
        {
            //Crea la consulta preparada para eliminar datos de la base de datos
            String consulta = "DELETE FROM VIDEOJUEGOS WHERE NOMBRE = ? ";
            PreparedStatement sentencia = conn.prepareStatement(consulta);
            //Sustituye la ? por el valor enviado
            sentencia.setString(1,nombre);
            //Ejecuta la sentencia de DELETE
            sentencia.executeUpdate();
            System.out.println("Juego eliminado correctamente");
            //Envia que se ha ejecutado la sentencia sin errores
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("No se ha podido eliminar el juego indicado");
            //Envia que ha ocurrido un error
            return false;
        }
    }
    public static boolean comprobar(String nombre)
    {
        try
        {
            //Crea la consulta preparada para buscar el nombre
            String consulta = "SELECT NOMBRE FROM VIDEOJUEGOS WHERE NOMBRE = ? ";
            PreparedStatement sentencia = conn.prepareStatement(consulta);
            //Sustituye la ? por el valor enviado
            sentencia.setString(1,nombre);
            //Ejecuta la consulta
            ResultSet rs = sentencia.executeQuery();
            //Si hay resultados
            while (rs.next())
            {
                //Envia que ha encontrado un juego con ese nombre
                return true;
            }
            //Envia que no se han encontrado juegos con ese nombre
            return false;
        }
        catch (SQLException e)
        {
            return false;
        }
    }
}
