public class Libro {
    private String titulo;
    private String autor;
    private String isbn;

    public Libro(String titulo, String autor, String isbn) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getIsbn() {
        return isbn;
    }

    // Setter para el título
    public void setTitulo(String nuevoTitulo) {
        this.titulo = nuevoTitulo;
    }

    // Setter para el autor
    public void setAutor(String nuevoAutor) {
        this.autor = nuevoAutor;
    }

    // Setter para el ISBN
    public void setIsbn(String nuevoISBN) {
        this.isbn = nuevoISBN;
    }
}
