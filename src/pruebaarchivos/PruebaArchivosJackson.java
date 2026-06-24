package pruebaarchivos;

// ─────────────────────────────────────────────────────────────────────────────
// LIBRERÍA: Jackson (com.fasterxml.jackson)
//
// Es la librería JSON más usada en la industria Java. La diferencia clave
// con Gson es que Jackson trabaja con objetos Java DIRECTAMENTE mediante
// su clase central: ObjectMapper.
//
// Con ObjectMapper podés:
//   → Leer un archivo JSON y convertirlo a una List<Usuario> en una sola línea
//   → Guardar una List<Usuario> como JSON en una sola línea
//
// No necesitás manipular JsonArray, JsonObject ni JsonElement nunca.
//
// CÓMO AGREGAR JACKSON AL PROYECTO:
//   Maven (pom.xml):
//     <dependency>
//       <groupId>com.fasterxml.jackson.core</groupId>
//       <artifactId>jackson-databind</artifactId>
//       <version>2.17.1</version>
//     </dependency>
//
//   JAR manual (NetBeans/Eclipse/IntelliJ sin Maven):
//     Descargá jackson-databind-2.17.1.jar desde:
//     https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind/2.17.1
//     También necesitás:
//       jackson-core-2.17.1.jar
//       jackson-annotations-2.17.1.jar
//     Todos se agregan en Libraries → Add JAR/Folder.
//
// IMPORTS NECESARIOS:
//   ObjectMapper      → el motor principal: convierte objetos Java ↔ JSON
//   TypeReference     → le indica a ObjectMapper el tipo exacto al leer
//                       (necesario para List<T> porque Java borra los genéricos)
//   File              → para referenciar el archivo en disco
//   IOException       → para manejar errores de lectura/escritura
//   ArrayList/List    → colecciones estándar de Java
//   Scanner           → para leer input del usuario
// ─────────────────────────────────────────────────────────────────────────────
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PruebaArchivosJackson {

    // El archivo JSON donde se guardan los usuarios
    static final String NOMBRE_ARCHIVO = "Usuarios.json";

    // ─────────────────────────────────────────────────────────────────────────
    // ObjectMapper: el corazón de Jackson.
    //
    // Es el objeto que hace toda la magia de conversión. Se crea una sola
    // vez como variable estática para reutilizarlo en todo el programa.
    //
    // enable(INDENT_OUTPUT) → genera JSON con indentación (pretty print):
    // [
    //   {
    //     "id" : 0,
    //     "nombre" : "Juan",
    //     "apellido" : "Perez",
    //     "clave" : "1234"
    //   }
    // ]
    // ─────────────────────────────────────────────────────────────────────────
    static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    // ─────────────────────────────────────────────────────────────────────────
    // Clase Usuario: POJO (Plain Old Java Object)
    //
    // Jackson lee los campos de esta clase por reflexión y los convierte
    // automáticamente a JSON y viceversa. El nombre del campo en Java
    // se convierte en la clave del JSON:
    //
    //   id       → "id"
    //   nombre   → "nombre"
    //   apellido → "apellido"
    //   clave    → "clave"      ← NUEVO: contraseña del usuario
    //
    // REQUISITO de Jackson: la clase necesita un constructor vacío (sin
    // parámetros) para poder instanciarla al leer desde JSON.
    // El constructor con parámetros es opcional pero útil para crear
    // usuarios nuevos cómodamente desde el código.
    // ─────────────────────────────────────────────────────────────────────────
    static class Usuario {
        public int id;
        public String nombre;
        public String apellido;
        public String clave;       // ← NUEVO campo agregado al JSON

        // Constructor vacío — OBLIGATORIO para Jackson
        public Usuario() {}

        // Constructor con parámetros — para crear usuarios fácilmente
        public Usuario(int id, String nombre, String apellido, String clave) {
            this.id       = id;
            this.nombre   = nombre;
            this.apellido = apellido;
            this.clave    = clave;
        }
    }

    public static void main(String[] args) {

        // Creación del archivo si no existe
        File archivo = new File(NOMBRE_ARCHIVO);
        try {
            if (archivo.createNewFile()) {
                System.out.println("Archivo creado con exito");
                // ─────────────────────────────────────────────────────────────
                // Inicialización: al crear el archivo por primera vez,
                // escribimos una lista vacía. Usamos mapper.writeValue()
                // para guardar una ArrayList vacía como "[]" en el archivo.
                // Esto evita errores al intentar leer un archivo vacío.
                // ─────────────────────────────────────────────────────────────
                mapper.writeValue(archivo, new ArrayList<>());
            } else {
                System.out.println("El archivo ya existe");
            }
        } catch (IOException e) {
            System.out.println("Ocurrió un error al crear el archivo.");
            e.printStackTrace();
        }

        // ─────────────────────────────────────────────────────────────────────
        // Cálculo del próximo ID
        //
        // leerUsuarios() devuelve una List<Usuario> normal de Java.
        // Se recorre con un for-each estándar, accediendo a usuario.id
        // como cualquier campo de objeto. Sin .get(), sin getAsInt(),
        // sin parseos: es Java puro.
        // ─────────────────────────────────────────────────────────────────────
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
                "4.Login (verificar nombre y clave)\n" +   // ← NUEVA opción
                "0.Salir\n" +
                "Ingrese opcion: "
            );
            opt = scan.nextInt();

            // ─────────────────────────────────────────────────────────────────
            // OPCIÓN 1: ALTA DE USUARIO
            //
            // Se crea un objeto Usuario con el constructor,
            // se agrega a la lista y se guarda. Ahora también pide la clave.
            // ─────────────────────────────────────────────────────────────────
            if (opt == 1) {
                scan.nextLine();

                System.out.print("Ingrese el nombre: ");
                String nombre = scan.next();
                System.out.print("Ingrese el Apellido: ");
                String apellido = scan.next();
                System.out.print("Ingrese la clave: ");   // ← NUEVO
                String clave = scan.next();

                List<Usuario> lista = leerUsuarios();
                lista.add(new Usuario(ID, nombre, apellido, clave));
                guardarUsuarios(lista);

                System.out.println("Usuario guardado con ID: " + ID);
                ID++;

            // ─────────────────────────────────────────────────────────────────
            // OPCIÓN 2: MODIFICACIÓN DE USUARIO
            //
            // Se recorre la lista de objetos Java. Cuando se encuentra
            // el usuario con el ID buscado, se modifican sus campos
            // directamente. Ahora también permite modificar la clave.
            // Ingresar "0" en cualquier campo significa "no modificar ese campo".
            // ─────────────────────────────────────────────────────────────────
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
                System.out.print("Ingrese nueva clave (si no quiere modificarla ponga 0): ");  // ← NUEVO
                String nuevaClave = scan.next();

                if (nuevoNombre.equals("0") && nuevoApellido.equals("0") && nuevaClave.equals("0")) {
                    System.out.println("No modifico nada");
                } else {
                    List<Usuario> lista = leerUsuarios();
                    boolean encontrado = false;

                    for (Usuario u : lista) {
                        if (u.id == idBuscado) {
                            // Modificación directa del campo del objeto Java
                            if (!nuevoNombre.equals("0"))   u.nombre   = nuevoNombre;
                            if (!nuevoApellido.equals("0")) u.apellido = nuevoApellido;
                            if (!nuevaClave.equals("0"))    u.clave    = nuevaClave;  // ← NUEVO
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

            // ─────────────────────────────────────────────────────────────────
            // OPCIÓN 4: LOGIN — verificar nombre y clave
            //
            // Se recorre la lista buscando un usuario cuyo nombre Y clave
            // coincidan exactamente con los ingresados.
            //
            //   → Si se encuentra: imprime 1 (credenciales correctas)
            //   → Si no se encuentra: imprime 0 (credenciales incorrectas)
            //
            // La comparación usa .equals() porque nombre y clave son String.
            // ─────────────────────────────────────────────────────────────────
            } else if (opt == 4) {
                scan.nextLine();

                System.out.print("Ingrese nombre: ");
                String nombreLogin = scan.next();
                System.out.print("Ingrese clave: ");
                String claveLogin = scan.next();

                List<Usuario> lista = leerUsuarios();
                boolean loginOk = false;

                for (Usuario u : lista) {
                    // Ambos campos deben coincidir al mismo tiempo
                    if (u.nombre.equals(nombreLogin) && u.clave.equals(claveLogin)) {
                        loginOk = true;
                        break;   // No hace falta seguir buscando
                    }
                }

                // 1 = credenciales correctas, 0 = incorrectas
                System.out.println(loginOk ? 1 : 0);
            }
        }

        scan.close();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // leerUsuarios(): lee el archivo JSON y devuelve List<Usuario>
    //
    // mapper.readValue(archivo, new TypeReference<List<Usuario>>(){})
    //
    //   → archivo               : el File del que se lee
    //   → TypeReference<...>{}  : le dice a Jackson que el JSON es una lista
    //                             de objetos Usuario. Necesitamos TypeReference
    //                             porque Java borra el tipo genérico en tiempo
    //                             de ejecución (type erasure), y así Jackson
    //                             sabe exactamente a qué clase mapear cada objeto.
    //
    // El resultado es una List<Usuario> normal de Java, lista para usar.
    // ─────────────────────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────────────────────
    // guardarUsuarios(): guarda List<Usuario> en el archivo JSON
    //
    // mapper.writeValue(archivo, lista)
    //
    //   → archivo : el File donde se escribe (sobreescribe el contenido)
    //   → lista   : la List<Usuario> que se convierte automáticamente a JSON
    //
    // Jackson recorre cada Usuario de la lista, lee sus campos (id, nombre,
    // apellido, clave) y genera el JSON correspondiente. Todo en una línea.
    // ─────────────────────────────────────────────────────────────────────────
    public static void guardarUsuarios(List<Usuario> lista) {
        try {
            mapper.writeValue(new File(NOMBRE_ARCHIVO), lista);
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo JSON.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ListarUsuarios(): muestra todos los usuarios en consola
    //
    // Se accede a los campos del objeto directamente (u.id, u.nombre, u.apellido)
    // como cualquier objeto Java. Sin parsing, sin conversiones.
    // La clave NO se muestra por pantalla intencionalmente.
    // ─────────────────────────────────────────────────────────────────────────
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
