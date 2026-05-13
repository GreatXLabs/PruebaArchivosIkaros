package pruebaarchivos;

import java.io.File;           
import java.io.FileWriter;     
import java.io.BufferedWriter;
import java.io.PrintWriter;   
import java.io.FileReader;   
import java.io.BufferedReader; 
import java.io.IOException;   
import java.util.ArrayList;    
import java.util.List;   
import java.util.Scanner;

public class PruebaArchivos {

    public static void main(String[] args) {
        String nombreArchivo = "Usuarios.txt";

        File archivo = new File(nombreArchivo);

        try {
            if (archivo.createNewFile()) {
                System.out.println("Archivo creado con exito");
            } else {
                System.out.println("El archivo ya existe");
            }
        } catch (IOException e) {
            System.out.println("Ocurrió un error al crear el archivo.");
            e.printStackTrace();
        }
        
        int opt = -1;
        int ID = 0; 
        Scanner scan = new Scanner(System.in);
        try (BufferedReader brID = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = brID.readLine()) != null) {
                String[] campos = linea.split("&");
                int idArchivo = Integer.parseInt(campos[0]);
                if (idArchivo >= ID) {
                    ID = idArchivo + 1; 
                }
            }
        } catch (IOException | NumberFormatException e) {
            
        }
        while(opt != 0){
            System.out.print("1.Alta Usuario\n2.Modificacion Usuario\n3.Lista usuarios\n0.Salir\nIngrese opcion: ");
            opt = scan.nextInt();
            
            if (opt == 1){
                scan.nextLine(); //para limpiarlo
                String NuevoUsuario = ID + "&";
                System.out.print("Ingrese el nombre: ");
                NuevoUsuario += scan.next();
                NuevoUsuario += "&";
                System.out.print("Ingrese el Apellido: ");
                NuevoUsuario += scan.next();
                
                try (FileWriter fw = new FileWriter(nombreArchivo, true);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter pw = new PrintWriter(bw)) {
            
                    pw.println(NuevoUsuario);
                    System.out.println("Usuario guardado con ID: " + ID);
                    ID++;    
                } catch (IOException e) {
                    System.out.println("Error al guardar usuario");
                }
            }
            else if (opt == 2){
                scan.nextLine(); //para limpiarlo
                
                ListarUsuarios(nombreArchivo);
                
                System.out.print("Que usuario desea modificar (ingrese ID): ");
                String Id = scan.next();
                System.out.print("Ingrese nuevo nombre (si no quiere modificarlo ponga 0): ");
                String Nombre = scan.next();
                System.out.print("Ingrese nuevo Apellido (si no quiere modificarlo ponga 0): ");
                String Apellido = scan.next();
                ArrayList<String> Usuario = new ArrayList<>();
                if(Nombre.equals("0") && Apellido.equals("0")){
                    System.out.println("No modifico nada");
                }else{
                    try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
                        String linea;
                        while ((linea = br.readLine()) != null) {
                            String[] campos = linea.split("&");
                
                            if (campos[0].equals(Id)) {
                                if (!Nombre.equals("0")){
                                    campos[1] = Nombre;
                                }
                                if (!Apellido.equals("0")){
                                    campos[2] = Apellido;
                                }
                                linea = campos[0] + "&" + campos[1] + "&" + campos[2];
                                System.out.println("Usuario con ID " + Id + " fue modificado.");
                            }
                            Usuario.add(linea);
                        }
                    } catch (IOException e) {
                        System.out.println("Error al leer para modificar.");
                    }
                    try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo, false))) {
                        for (String l : Usuario) {
                            pw.println(l);
                        }
                    } catch (IOException e) {
                        System.out.println("Error al actualizar el archivo.");
                    }
                }             
            }
            else if (opt == 3){
                ListarUsuarios(nombreArchivo);
            }
        }         
    }   
    
    public static void ListarUsuarios(String nombreArchivo) {
        System.out.println("\nUsuarios:");
        try (BufferedReader brLista = new BufferedReader(new FileReader(nombreArchivo))) {
            String l;
            while((l = brLista.readLine()) != null) {
                System.out.println(l.replace("&", " | "));
            }
            System.out.println("");
        } catch (IOException e) {
            System.out.println("Error al listar usuarios.");
        }
    }
}
