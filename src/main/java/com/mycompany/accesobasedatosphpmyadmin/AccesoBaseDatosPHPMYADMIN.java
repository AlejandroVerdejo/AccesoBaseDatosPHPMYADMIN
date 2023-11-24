package com.mycompany.accesobasedatosphpmyadmin;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AccesoBaseDatosPHPMYADMIN {

    static final String DB_URL = "jdbc:mysql://localhost/JCVD";
    static final String USER = "admin";
    static final String PASS = "admin";
    
    private static String SELECT;
    private static String INSERT;
    private static String UPDATE;
    private static String DELETE;
    private static String[] COLUMN = {"ID","NOMBRE","GENERO","FECHA","COMPAÑIA","PRECIO"};
    private static String[] VALUES = new String[5];
    
    private static Connection conn = null;
    private static Statement stmt = null;

    public static void main(String[] args) throws UnsupportedEncodingException {
        Scanner sc = new Scanner(System.in);
        try 
        {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
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
                                boolean check = comprobar(SELECT);
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
                                SELECT = (String) SELECT;
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
                        INSERT = "INSERT INTO `videojuegos` (`ID`, `NOMBRE`, `GENERO`, `FECHA`, `COMPAÑIA`, `PRECIO`) VALUES (NULL, '" + VALUES[0] + "', '" + VALUES[1] + "', '" + VALUES[2] + "', '" + VALUES[3] + "', '" + VALUES[4] + "')";
                        insert(INSERT);
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
                        break;
                    case 4:
                            System.out.print("Escribe el nombre del juego que quieres borrar: ");
                            sc.nextLine();
                            cad = sc.nextLine();
                            DELETE = "DELETE FROM VIDEOJUEGOS WHERE NOMBRE = '" + cad + "'";
                            delete(DELETE);
                        break;
                }
            } while (opc != 0);

        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    public static void select(String select)
    {
        if (select.contains("COMPA?IA"))
        {
            System.out.println("true");
            System.out.println(select);
            select = select.replace("COMPA?IA", "COMPAÑIA");
        }
        try
        {
            ResultSet rs = stmt.executeQuery(select);
            while (rs.next()) 
            {
                try { System.out.println(" ID: " + rs.getInt("ID")); }
                catch (SQLException e) {}
                try { System.out.println(" · Nombre: " + rs.getString("NOMBRE")); }
                catch (SQLException e) {}
                try { System.out.println(" · Genero: " + rs.getString("GENERO")); }
                catch (SQLException e) {}
                try { System.out.println(" · Fecha de publicacion: " + rs.getString("FECHA")); }
                catch (SQLException e) {}
                try { System.out.println(" · Compañia: " + rs.getString("COMPAÑIA")); }
                catch (SQLException e) {}
                try { System.out.println(" · Precio: " + rs.getString("PRECIO")); }
                catch (SQLException e) {}                  
            }
        }
        catch (SQLException e)
        {
            System.out.println("No se ha encontrado el juego indicado");
        }
    }
    public static boolean insert(String insert)
    {
        try
        {
            stmt.executeUpdate(insert);
            System.out.println("Juego añadido correctamente");
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("No se ha podido añadir el juego correctamente");
            return false;
        }        
    }
    public static boolean update(String update)
    {
        try
        {
            stmt.executeUpdate(update);
            System.out.println("Juego actualizado correctamente");
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("No se ha podido modificar el juego indicado");
            return false;
        }        
    }
    public static boolean delete(String delete)
    {
        try
        {
            stmt.executeUpdate(delete);
            System.out.println("Juego eliminado correctamente");
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("No se ha podido eliminar el juego indicado");
            return false;
        }
    }
    public static boolean comprobar(String select)
    {
        try
        {
            ResultSet rs = stmt.executeQuery(select);
            while (rs.next())
            {
                return true;
            }
            return false;
        }
        catch (SQLException e)
        {
            return false;
        }
    }
}
