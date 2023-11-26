import java.io.*;
import java.util.*;


public class SistemaGestionBiblioteca {
    public static final String ARCHIVO_PRESTAMOS ="prestamos.txt";
    public static final String ARCHIVO_LIBROS = "libros.txt";
    static Map<String, Usuario> usuariosRegistrados = new HashMap<>();
    static Usuario usuarioActual = null;

    private static final String ARCHIVO_USUARIOS = "usuarios.txt";
    public static void main(String[] args) {
        cargarUsuariosDesdeArchivo();
        cargarLibrosDesdeArchivo();
        cargarPrestamosDesdeArchivo();

        // Asegurar que los préstamos se carguen solo si hay un usuario autenticado
        String archivoPrestamos = "";
        if (usuarioActual instanceof SingletonAdministrador) {
            ((SingletonAdministrador) usuarioActual).guardarPrestamosEnArchivo(archivoPrestamos);
        }
        Scanner scanner = new Scanner(System.in);

        int opcion;
        do {
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println("║        Sistema de Gestión de Biblioteca        ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            if (usuarioActual == null) {
                // Opciones para usuarios no autenticados
                System.out.println("1. Iniciar Sesión");
                System.out.println("2. Registrarse");
            } else {
                // Opciones comunes para usuarios autenticados
                System.out.println("1. Cerrar Sesión");

                // Opciones específicas para el Administrador
                if (usuarioActual instanceof SingletonAdministrador) {
                    System.out.println("2. Agregar Libros");
                    System.out.println("3. Eliminar Libro");
                    System.out.println("4. Editar Libros");
                    System.out.println("5. Buscar Libros");
                    System.out.println("6. Lista de libros Prestados");
                    System.out.println("7. Ver Usuarios Registrados");
                    System.out.println("8. Aceptar Préstamo");
                } else {
                    // Opciones específicas para el Usuario Normal
                    System.out.println("2. Buscar Libro");
                    System.out.println("3. Solicitar Préstamo");
                }
            }

            System.out.println("10. Salir");
            System.out.print("\nSeleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                switch (opcion) {
                    case 1:
                        if (usuarioActual == null) {
                            // Iniciar Sesión
                            System.out.print("Ingrese su nombre de usuario: ");
                            String nombreUsuario = scanner.next();
                            System.out.print("Ingrese su contraseña: ");
                            String contrasena = scanner.next();

                            // Validar credenciales
                            Usuario usuario = usuariosRegistrados.get(nombreUsuario);
                            if (usuario != null && usuario.getContrasena().equals(contrasena)) {
                                usuarioActual = usuario;
                                System.out.println("╔═════════════════════════════════════════════════════════════╗");
                                System.out.println("║      Inicio de sesión exitoso. ¡Bienvenido!" + nombreUsuario + "          ║");
                                System.out.println("╚═════════════════════════════════════════════════════════════╝");
                                if (usuarioActual != null) {
                                    // Asegurar que los préstamos se carguen solo si hay un usuario autenticado
                                    usuarioActual.cargarPrestamosDesdeArchivo();
                                }
                            } else {
                                System.out.println("Nombre de usuario o contraseña incorrectos. Inicio de sesión fallido.");
                            }

                        } else {
                            // Cerrar Sesión
                            usuarioActual = null;
                            System.out.println("Sesión cerrada. ¡Hasta luego!");
                        }
                        break;
                    case 2:
                        if (usuarioActual == null) {
                            // Menú de registro para nuevos usuarios
                            System.out.println("╔══════════════════════════════════════════╗");
                            System.out.println("║      Bienvenido al Sistema de Registro   ║");
                            System.out.println("╚══════════════════════════════════════════╝");
                            System.out.print("Ingrese un nombre de usuario: ");
                            String nuevoNombreUsuario = scanner.next();
                            System.out.print("Ingrese una contraseña: ");
                            String nuevaContrasena = scanner.next();
                            if (usuarioActual instanceof SingletonAdministrador) {
                                // Cargar los préstamos para el administrador
                                ((SingletonAdministrador) usuarioActual).cargarPrestamosDesdeArchivo();
                            }
                            boolean esAdmin = false; // Por defecto, el nuevo usuario no es administrador
                            Usuario nuevoUsuario = esAdmin ? new SingletonAdministrador(nuevoNombreUsuario, nuevaContrasena) :
                                    new Usuario(nuevoNombreUsuario, nuevaContrasena);
                            usuariosRegistrados.put(nuevoNombreUsuario, nuevoUsuario);
                            guardarUsuariosEnArchivo();
                            System.out.println("════════════════════════════════════════════");
                            System.out.println("¡Registro exitoso!");
                            System.out.println("Gracias por unirte al sistema, " + nuevoNombreUsuario + "!");
                        } else if (usuarioActual instanceof SingletonAdministrador) {
                            // Menú de agregar libro para administradores
                            ((SingletonAdministrador) usuarioActual).agregarLibro();
                        } else {
                            // Menú de buscar libro para usuarios normales
                            ((Usuario) usuarioActual).buscarLibro();
                        }
                        break;

                    case 3:
                        if (usuarioActual == null) {
                            System.out.println("Debe iniciar sesión para solicitar un préstamo.");
                        } else if (usuarioActual instanceof SingletonAdministrador) {
                            ((SingletonAdministrador) usuarioActual).eliminarLibro();
                        } else {
                            // Menú de solicitar préstamo para usuarios normales
                            ((Usuario) usuarioActual).solicitarPrestamo();
                        }
                        break;

                    case 4:
                        if (usuarioActual instanceof SingletonAdministrador) {
                            ((SingletonAdministrador) usuarioActual).editarLibro();
                        } else {
                            System.out.println("Debe iniciar sesión como administrador para acceder a esta opción.");
                        }
                        break;
                    case 5:
                        if (usuarioActual != null) {
                            if (usuarioActual instanceof SingletonAdministrador) {
                                ((SingletonAdministrador) usuarioActual).buscarLibro();
                            } else {
                                usuarioActual.buscarLibro();
                            }
                        } else {
                            System.out.println("Debe iniciar sesión para acceder a esta opción.");
                        }
                        break;
                    case 6:
                        if (usuarioActual instanceof SingletonAdministrador) {
                            ((SingletonAdministrador) usuarioActual).cargarPrestamosYMostrarLista();
                        } else {
                            System.out.println("Debe iniciar sesión como administrador para acceder a esta opción.");
                        }
                        break;
                    case 7:
                        if (usuarioActual instanceof SingletonAdministrador) {
                            ((SingletonAdministrador) usuarioActual).verUsuariosRegistrados();
                        } else {
                            System.out.println("Debe iniciar sesión como administrador para acceder a esta opción.");
                        }
                        break;
                    case 8:
                        if (usuarioActual instanceof SingletonAdministrador) {
                            ((SingletonAdministrador) usuarioActual).aceptarPrestamo();
                        } else {
                            System.out.println("Debe iniciar sesión como administrador para acceder a esta opción.");
                        }
                        break;

                    default:
                        System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
                        System.out.println("┃   Cerrando el sistema.   ┃");
                        System.out.println("┃      ¡Hasta luego!       ┃");
                        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
                }
            } catch (InputMismatchException e) {
                // Manejar la excepción cuando el usuario no ingresa un entero
                System.out.println("Por favor, ingrese un número válido.");
                scanner.next(); // Consumir la entrada incorrecta para evitar un bucle infinito
                opcion = -1; // Establecer una opción inválida para continuar el bucle
            }
        } while (opcion != 10);
        // Guardar información al finalizar
        guardarUsuariosEnArchivo();
        guardarUsuariosEnArchivo();
        guardarLibrosEnArchivo();
        // Llamada para guardar préstamos
        if (usuarioActual instanceof SingletonAdministrador) {
            ((SingletonAdministrador) usuarioActual).guardarPrestamosEnArchivo(SingletonAdministrador.ARCHIVO_PRESTAMOS);
        }
        scanner.close();
        // Asegurar que los préstamos se guarden solo si hay un usuario autenticado
        if (usuarioActual instanceof SingletonAdministrador) {
            ((SingletonAdministrador) usuarioActual).guardarPrestamosEnArchivo(archivoPrestamos);
        }

        scanner.close();
    }


    private static void cargarPrestamosDesdeArchivo() {
    }

    static void guardarPrestamosEnArchivo() {
    }

    static void guardarLibrosEnArchivo() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_LIBROS))) {
            for (Libro libro : SingletonAdministrador.getListaLibros()) {
                bw.write(libro.getTitulo() + "," + libro.getAutor() + "," + libro.getIsbn());
                bw.newLine();
            }
            System.out.println("Libros guardados en el archivo correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al guardar libros en el archivo: " + e.getMessage());
        }
    }


    private static void cargarLibrosDesdeArchivo() {
    }

    static void cargarUsuariosDesdeArchivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    String nombreUsuario = partes[0];
                    String contrasena = partes[1];
                    boolean esAdmin = Boolean.parseBoolean(partes[2]);
                    if (esAdmin) {
                        SingletonAdministrador admin = new SingletonAdministrador(nombreUsuario, contrasena);
                        usuariosRegistrados.put(nombreUsuario, admin);
                    } else {
                        Usuario usuario = new Usuario(nombreUsuario, contrasena);
                        usuariosRegistrados.put(nombreUsuario, usuario);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar usuarios desde el archivo: " + e.getMessage());
        }
    }
    public static void guardarPrestamosEnArchivo(String archivoPrestamos) {
        if (usuarioActual instanceof SingletonAdministrador) {
            ((SingletonAdministrador) usuarioActual).guardarPrestamosEnArchivo(archivoPrestamos);
        }
    }

    public static void guardarUsuariosEnArchivo() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            for (Usuario usuario : usuariosRegistrados.values()) {
                bw.write(usuario.getNombreUsuario() + ","
                        + usuario.getContrasena() + ","
                        + (usuario instanceof SingletonAdministrador));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios en el archivo: " + e.getMessage());
        }
    }
}

