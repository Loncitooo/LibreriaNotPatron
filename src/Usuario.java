import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Usuario{
    private String nombreUsuario;
    private String contrasena;
    static List<SingletonAdministrador.Prestamo> listaPrestamos = new ArrayList<>();

    public Usuario(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.listaPrestamos = SingletonAdministrador.listaPrestamos;
    }
    private Libro[] listaLibros;

    public Usuario() {

    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    @Override
    public String toString() {
        return "Nombre de usuario: " + nombreUsuario + " Contraseña: ****";
    }

    public void buscarLibro() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ISBN del libro que desea buscar: ");
        String isbn = scanner.nextLine();

        for (Libro libro : SingletonAdministrador.getListaLibros()) {
            if (libro.getIsbn().equals(isbn)) {
                System.out.println("Detalles del libro encontrado:");
                System.out.println(libro);
                return;
            }
        }

        System.out.println("No se encontró ningún libro con ese ISBN.");
    }

    public void solicitarPrestamo() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el título del libro que desea solicitar en préstamo: ");
        String titulo = scanner.nextLine();

        for (Libro libro : SingletonAdministrador.getListaLibros()) {
            if (libro.getTitulo().equalsIgnoreCase(titulo)) {
                // Crear un nuevo préstamo
                SingletonAdministrador.Prestamo nuevoPrestamo = new SingletonAdministrador.Prestamo(this, libro);
                this.listaPrestamos.add(nuevoPrestamo); // Agregar el préstamo a la lista local del usuario
                System.out.println("Solicitud de préstamo realizada.");
                return;
            }
        }

        System.out.println("No se encontró ningún libro con ese título.");
    }

    public void verPrestamos() {
        System.out.println("\n=== Lista de Préstamos ===");

        // Encabezados de la tabla
        System.out.printf("%-20s %-20s %-20s%n", "Usuario", "Libro", "Estado");
        System.out.println("------------------------------------");

        // Lógica para mostrar la lista de préstamos
        for (SingletonAdministrador.Prestamo prestamo : listaPrestamos) {
            System.out.printf("%-20s %-20s %-20s%n",
                    prestamo.getUsuario().getNombreUsuario(),
                    prestamo.getLibro().getTitulo(),
                    prestamo.getEstado());
        }
    }

    public static void cargarPrestamosDesdeArchivo() {
    }

    }

