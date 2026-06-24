package pruebaarchivos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PruebaArchivosJackson {

    static final String NOMBRE_ARCHIVO = "Usuarios.json";

    static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    static class Usuario {
        public int id;
        public String nombre;
        public String apellido;
        public String clave;

        public Usuario() {}

        public Usuario(int id, String nombre, String apellido, String clave) {
            this.id       = id;
            this.nombre   = nombre;
            this.apellido = apellido;
            this.clave    = clave;
        }
    }

    public static void main(String[] args) {

        File archivo = new File(NOMBRE_ARCHIVO);
        try {
            if (archivo.createNewFile()) {
                System.out.println("Archivo creado con exito");
                mapper.writeValue(archivo, new ArrayList<>());
            } else {
                System.out.println("El archivo ya existe");
            }
        } catch (IOException e) {
            System.out.println("Ocurrió un error al crear el archivo.");
            e.printStackTrace();
        }

        int ID = 0;
        List<Usuario> usuarios = leerUsuarios();
        for (Usuario u : usuarios) {
            if (u.id >= ID) {
                ID = u.id + 1;
            }
        }

        int opt = -1;
        Scanner scan = new Scanner(System.in);

        while (opt != 0) {
            System.out.print(
                "1.Alta Usuario\n" +
                "2.Modificacion Usuario\n" +
                "3.Lista usuarios\n" +
                "4.Login (verificar nombre y clave)\n" +
                "0.Salir\n" +
                "Ingrese opcion: "
            );
            opt = scan.nextInt();

            if (opt == 1) {
                scan.nextLine();

                System.out.print("Ingrese el nombre: ");
                String nombre = scan.next();
                System.out.print("Ingrese el Apellido: ");
                String apellido = scan.next();
                System.out.print("Ingrese la clave: ");
                String clave = scan.next();

                List<Usuario> lista = leerUsuarios();
                lista.add(new Usuario(ID, nombre, apellido, clave));
                guardarUsuarios(lista);

                System.out.println("Usuario guardado con ID: " + ID);
                ID++;

            } else if (opt == 2) {
                scan.nextLine();

                ListarUsuarios();

                System.out.print("Que usuario desea modificar (ingrese ID): ");
                int idBuscado = scan.nextInt();
                scan.nextLine();

                System.out.print("Ingrese nuevo nombre (si no quiere modificarlo ponga 0): ");
                String nuevoNombre = scan.next();
                System.out.print("Ingrese nuevo Apellido (si no quiere modificarlo ponga 0): ");
                String nuevoApellido = scan.next();
                System.out.print("Ingrese nueva clave (si no quiere modificarla ponga 0): ");
                String nuevaClave = scan.next();

                if (nuevoNombre.equals("0") && nuevoApellido.equals("0") && nuevaClave.equals("0")) {
                    System.out.println("No modifico nada");
                } else {
                    List<Usuario> lista = leerUsuarios();
                    boolean encontrado = false;

                    for (Usuario u : lista) {
                        if (u.id == idBuscado) {
                            if (!nuevoNombre.equals("0"))   u.nombre   = nuevoNombre;
                            if (!nuevoApellido.equals("0")) u.apellido = nuevoApellido;
                            if (!nuevaClave.equals("0"))    u.clave    = nuevaClave;
                            System.out.println("Usuario con ID " + idBuscado + " fue modificado.");
                            encontrado = true;
                        }
                    }

                    if (!encontrado) {
                        System.out.println("No se encontro un usuario con ID " + idBuscado);
                    } else {
                        guardarUsuarios(lista);
                    }
                }

            } else if (opt == 3) {
                ListarUsuarios();

            } else if (opt == 4) {
                scan.nextLine();

                System.out.print("Ingrese nombre: ");
                String nombreLogin = scan.next();
                System.out.print("Ingrese clave: ");
                String claveLogin = scan.next();

                List<Usuario> lista = leerUsuarios();
                boolean loginOk = false;

                for (Usuario u : lista) {
                    if (u.nombre.equals(nombreLogin) && u.clave.equals(claveLogin)) {
                        loginOk = true;
                        break;
                    }
                }

                System.out.println(loginOk ? 1 : 0);
            }
        }

        scan.close();
    }

    public static List<Usuario> leerUsuarios() {
        try {
            return mapper.readValue(
                new File(NOMBRE_ARCHIVO),
                new TypeReference<List<Usuario>>() {}
            );
        } catch (IOException e) {
            System.out.println("Error al leer el archivo JSON.");
            return new ArrayList<>();
        }
    }

    public static void guardarUsuarios(List<Usuario> lista) {
        try {
            mapper.writeValue(new File(NOMBRE_ARCHIVO), lista);
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo JSON.");
        }
    }

    public static void ListarUsuarios() {
        System.out.println("\nUsuarios:");
        List<Usuario> lista = leerUsuarios();

        if (lista.isEmpty()) {
            System.out.println("(No hay usuarios registrados)");
        }

        for (Usuario u : lista) {
            System.out.println(u.id + " | " + u.nombre + " | " + u.apellido);
        }
        System.out.println();
    }
}