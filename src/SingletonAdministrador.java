import java.io.*;
import java.util.*;

class SingletonAdministrador extends Usuario {

    public static final String ARCHIVO_PRESTAMOS = "prestamos.txt";
    static List<Prestamo> listaPrestamos = new ArrayList<>();

    static class Prestamo {
        public enum Estado {PENDIENTE, ACEPTADO}

        private final Usuario usuario;
        private final Libro libro;
        private Estado estado;

        public Prestamo(Usuario usuario, Libro libro) {
            this.usuario = usuario;
            this.libro = libro;
            this.estado = Estado.PENDIENTE;
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public Libro getLibro() {
            return libro;
        }

        public Estado getEstado() {
            return estado;
        }

        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public String toString() {
            return "Usuario: " + usuario.getNombreUsuario() + ", Libro: " + libro.getTitulo() + ", Estado: " + estado;
        }
    }

    private static final String ARCHIVO_LIBROS = "libros.txt";
    private static List<Libro> listaLibros = new ArrayList<>(); // Centraliza la lista de libros

    // Instancia única del Administrador
    private static SingletonAdministrador instancia;

    // Método estático para obtener la instancia única del Administrador
    public static synchronized SingletonAdministrador obtenerInstancia(String nombreUsuario, String contrasena) {
        if (instancia == null) {
            instancia = new SingletonAdministrador(nombreUsuario, contrasena);
        }
        return instancia;
    }

    SingletonAdministrador(String nombreUsuario, String contrasena) {
        super(nombreUsuario, contrasena);
        cargarLibrosDesdeArchivo();
        // Agrega otras inicializaciones en caso de ser necesarias para el Administrador
    }
    public void cargarPrestamosYMostrarLista() {
        SingletonAdministrador.cargarPrestamosDesdeArchivo();

        // Mostrar libros prestados solo al cargar desde el archivo
        if (!listaPrestamos.isEmpty()) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    Lista de Libros Prestados                      ║");
            System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
            System.out.printf("║ %-20s %-30s%-15s║%n", "Usuario", "Libro", "Estado");
            System.out.println("╠═══════════════════════════════════════════════════════════════════╣");

            for (Prestamo prestamo : listaPrestamos) {
                if (prestamo.getEstado() == Prestamo.Estado.ACEPTADO) {
                    System.out.printf("║ %-20s %-30s%-15s║%n",
                            prestamo.getUsuario().getNombreUsuario(),
                            prestamo.getLibro().getTitulo(),
                            prestamo.getEstado());
                }
            }

            System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        }
    }

    public static List<Libro> getListaLibros() {
        return listaLibros;
    }

    public void guardarPrestamosEnArchivo(String archivoPrestamos) {
        try {
            // Verificar si el archivo ya existe
            File file = new File(archivoPrestamos);
            if (!file.exists()) {
                file.createNewFile();
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                for (Prestamo prestamo : listaPrestamos) {
                    bw.write(prestamo.getUsuario().getNombreUsuario() + ","
                            + prestamo.getLibro().getTitulo() + ","
                            + prestamo.getLibro().getIsbn() + ","
                            + prestamo.getEstado());
                    bw.newLine();
                }
                System.out.println("Préstamos guardados en el archivo correctamente.");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error al guardar préstamos en el archivo: " + e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al crear el archivo: " + e.getMessage());
        }
    }
    public void guardarLibrosEnArchivo() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SistemaGestionBiblioteca.ARCHIVO_LIBROS))) {
            for (Libro libro : listaLibros) {
                bw.write(libro.getTitulo() + "," + libro.getAutor() + "," + libro.getIsbn());
                bw.newLine();
            }
            System.out.println("Libros guardados en el archivo correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al guardar libros en el archivo: " + e.getMessage());
        }
    }

    private void cargarLibrosDesdeArchivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(SistemaGestionBiblioteca.ARCHIVO_LIBROS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    String titulo = partes[0];
                    String autor = partes[1];
                    String isbn = partes[2];
                    Libro libro = new Libro(titulo, autor, isbn);
                    listaLibros.add(libro);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Imprime detalles del error en la consola
            System.err.println("Error al cargar libros desde el archivo: " + e.getMessage());
        }
    }

    public static void cargarPrestamosDesdeArchivo() {
        Set<Prestamo> prestamosSet = new HashSet<>(); // Usar un conjunto para evitar duplicados
        try (BufferedReader br = new BufferedReader(new FileReader("prestamos.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 4) {
                    String nombreUsuario = partes[0].trim();
                    String tituloLibro = partes[1].trim();
                    String isbnLibro = partes[2].trim();
                    String estadoString = partes[3].trim();

                    Usuario usuario = SistemaGestionBiblioteca.usuariosRegistrados.get(nombreUsuario);
                    Libro libro = buscarLibroPorTituloYISBN(tituloLibro, isbnLibro);

                    if (usuario != null && libro != null) {
                        Prestamo prestamo = new Prestamo(usuario, libro);
                        prestamo.setEstado(Prestamo.Estado.valueOf(estadoString));
                        prestamosSet.add(prestamo); // Agregar al conjunto para evitar duplicados
                    }
                }
            }

            listaPrestamos.addAll(prestamosSet); // Agregar todos los préstamos del conjunto a la lista principal
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar préstamos desde el archivo: " + e.getMessage());
        }
    }

    private static Libro buscarLibroPorTituloYISBN(String tituloLibro, String isbnLibro) {
        for (Libro libro : listaLibros) {
            if (libro.getTitulo().equalsIgnoreCase(tituloLibro) && libro.getIsbn().equals(isbnLibro)) {
                return libro;
            }
        }
        return null;
    }

    public void agregarLibro() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese los detalles del libro:");

        System.out.print("Título: ");
        String titulo = scanner.nextLine();

        System.out.print("Autor: ");
        String autor = scanner.nextLine();

        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();

        Libro nuevoLibro = new Libro(titulo, autor, isbn);
        listaLibros.add(nuevoLibro);

        System.out.println("Libro agregado con éxito.");

        // Llamada al método para guardar la lista de libros en el archivo
        guardarLibrosEnArchivo();
    }

    public void eliminarLibro() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ISBN del libro que desea eliminar: ");
        String isbn = scanner.nextLine();

        Iterator<Libro> iterator = listaLibros.iterator();
        while (iterator.hasNext()) {
            Libro libro = iterator.next();
            if (libro.getIsbn().equals(isbn)) {
                iterator.remove();
                System.out.println("Libro eliminado con éxito.");
                SistemaGestionBiblioteca.guardarLibrosEnArchivo(); // Agregamos esta línea para guardar los cambios en el archivo
                return;
            }
        }

        System.out.println("No se encontró ningún libro con ese ISBN.");
    }


    public void editarLibro() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ISBN del libro que desea editar: ");
        String isbn = scanner.nextLine();

        for (Libro libro : listaLibros) {
            if (libro.getIsbn().equals(isbn)) {
                System.out.println("Ingrese los nuevos detalles del libro:");

                System.out.print("Nuevo Título: ");
                String nuevoTitulo = scanner.nextLine();
                libro.setTitulo(nuevoTitulo);

                System.out.print("Nuevo Autor: ");
                String nuevoAutor = scanner.nextLine();
                libro.setAutor(nuevoAutor);

                System.out.print("Nuevo ISBN: ");
                String nuevoISBN = scanner.nextLine();
                libro.setIsbn(nuevoISBN);

                // Puedes agregar más campos según sea necesario

                System.out.println("Libro editado con éxito.");
                SistemaGestionBiblioteca.guardarLibrosEnArchivo();
                return;
            }
        }

        System.out.println("No se encontró ningún libro con ese ISBN.");
    }


    public void listarLibrosPrestados() {
        System.out.println("\n=== Lista de Libros Prestados ===");

        // Encabezados de la tabla
        System.out.printf("%-20s %-20s %-20s%n", "Libro", "Estado", "Prestado a");
        System.out.println("------------------------------------");

        // Lógica para mostrar la lista de libros prestados
        // Mostrar libros prestados solo al cargar desde el archivo
        if (!listaPrestamos.isEmpty()) {
            System.out.println("\n=== Lista de Libros Prestados ===");
            System.out.printf("%-20s %-20s %-20s%n", "Usuario", "Libro", "Estado");
            System.out.println("------------------------------------");
            for (Prestamo prestamo : listaPrestamos) {
                if (prestamo.getEstado() == Prestamo.Estado.ACEPTADO) {
                    System.out.printf("%-20s %-20s %-20s%n",
                            prestamo.getUsuario().getNombreUsuario(),
                            prestamo.getLibro().getTitulo(),
                            prestamo.getEstado());
                }
            }
        }
    }

    public void verUsuariosRegistrados() {
        System.out.println("\n=== Lista de Usuarios Registrados ===");

        // Encabezados de la tabla
        System.out.printf("%-20s %-20s%n", "Nombre de usuario", "Contraseña");
        System.out.println("-------------------------------");

        // Lógica para mostrar la lista de usuarios registrados
        for (Usuario usuario : SistemaGestionBiblioteca.usuariosRegistrados.values()) {
            System.out.printf("%-20s %-20s%n", usuario.getNombreUsuario(), usuario.getContrasena());
        }
    }

    public void listarPrestamosPendientes() {
        System.out.println("\n=== Lista de Préstamos Pendientes ===");

        // Encabezados de la tabla
        System.out.printf("%-20s %-20s %-20s%n", "Usuario", "Libro", "Estado");
        System.out.println("------------------------------------");

        // Lógica para mostrar la lista de préstamos pendientes
        for (Prestamo prestamo : listaPrestamos) {
            if (prestamo.getEstado() == Prestamo.Estado.PENDIENTE) {
                System.out.printf("%-20s %-20s %-20s%n",
                        prestamo.getUsuario().getNombreUsuario(),
                        prestamo.getLibro().getTitulo(),
                        prestamo.getEstado());
            }
        }
    }

    public void aceptarPrestamo() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ISBN del libro para aceptar el préstamo: ");
        String isbn = scanner.nextLine();

        // Buscar el préstamo pendiente con el ISBN proporcionado
        Prestamo prestamoPendiente = null;
        for (Prestamo prestamo : listaPrestamos) {
            if (prestamo.getLibro().getIsbn().equals(isbn) && prestamo.getEstado() == Prestamo.Estado.PENDIENTE) {
                prestamoPendiente = prestamo;
                break;
            }
        }

        // Verificar si se encontró un préstamo pendiente
        if (prestamoPendiente != null) {
            // Aceptar el préstamo
            prestamoPendiente.setEstado(Prestamo.Estado.ACEPTADO);
            System.out.println("Préstamo aceptado con éxito.");
            SistemaGestionBiblioteca.guardarPrestamosEnArchivo();
            // Guardar préstamos en el archivo
        } else {
            System.out.println("No se encontró ningún préstamo pendiente con ese ISBN o el préstamo ya fue aceptado.");
        }
    }

    public void verPrestamos() {
        System.out.println("\n=== Lista de Préstamos ===");

        // Encabezados de la tabla
        System.out.printf("%-20s %-20s %-20s%n", "Usuario", "Libro", "Estado");
        System.out.println("------------------------------------");

        // Lógica para mostrar la lista de préstamos
        for (Prestamo prestamo : listaPrestamos) {
            System.out.printf("%-20s %-20s %-20s%n",
                    prestamo.getUsuario().getNombreUsuario(),
                    prestamo.getLibro().getTitulo(),
                    prestamo.getEstado());
        }
    }
}
