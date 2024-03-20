package StudioMedico;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // Creazione del server HTTP sulla porta 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Gestione della richiesta POST per la vendita dei biglietti
        server.createContext("/inserimentoPazienti", new InserimentoPazientiHandler());

        // Gestione della richiesta GET per la visualizzazione del riepilogo dei biglietti
        server.createContext("/visualizzaPazienti", new VisualizzaPazientiHandler());

        // Gestione della richiesta GET per la pagina home
        server.createContext("/", new HomeHandler());

        server.createContext("/eliminaPaziente", new EliminaPazienteHandler());

        // Avvio del server
        server.start();

        System.out.println("Server in esecuzione sulla porta 8080");
    }

    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String htmlResponse = "<html>" +
                    "<head>" +
                    "<title>Home</title>" +
                    "<style>" +
                    "body {" +
                    "   background-image: url('https://www.facileristrutturare.it/wp-content/uploads/2021/08/napoli-centroestetico-web-4.jpg');" +
                    "   background-size: cover;" +
                    "}" +
                    "</style>" +
                    "<link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css'>" +
                    "</head>" +
                    "<body>" +
                    "<nav class='navbar navbar-expand-lg navbar-light bg-light'>" +
                    "<a class='navbar-brand' href='/'>Home</a>" +
                    "</nav>" +
                    "<div class='container'>" +
                    "<h1>Benvenuti nell'area riservata dello Studio Medico bitCamp!</h1>" +
                    "<p>Lo Studio Medico bitCamp e' lieto di darvi il benvenuto nella nostra piattaforma di gestione medica dedicata. Qui potete accedere a tutte le funzionalita' necessarie per gestire al meglio lo studio medico.</p>" +
                    "<p>Attraverso il nostro sistema, potete tenere traccia delle informazioni dei nostri pazienti e consultare le cartelle cliniche, e molto altro ancora, il tutto in modo efficiente e intuitivo.</p>" +
                    "<div class='card-deck'>" +
                    "<div class='card'>" +
                    "<div class='card-body'>" +
                    "<h5 class='card-title'>Inserimento Pazienti</h5>" +
                    "<p class='card-text'>Clicca sul pulsante per inserire un nuovo paziente.</p>" +
                    "<a href='/inserimentoPazienti' class='btn btn-primary'>Inserisci Paziente</a>" +
                    "</div>" +
                    "</div>" +
                    "<div class='card'>" +
                    "<div class='card-body'>" +
                    "<h5 class='card-title'>Visualizza Pazienti</h5>" +
                    "<p class='card-text'>Clicca sul pulsante per visualizzare i pazienti esistenti.</p>" +
                    "<a href='/visualizzaPazienti' class='btn btn-primary'>Visualizza Pazienti</a>" +
                    "</div>" +
                    "</div>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";


            // Imposta l'intestazione della risposta
            exchange.getResponseHeaders().set("Content-Type", "text/html");

            int lunghezza = htmlResponse.getBytes("UTF-8").length;

            // Imposta lo status code e la lunghezza della risposta
            exchange.sendResponseHeaders(200, lunghezza);

            // Scrive la risposta al client
            OutputStream os = exchange.getResponseBody();
            os.write(htmlResponse.getBytes());
            os.close();
        }
    }

    static class InserimentoPazientiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                // Form HTML per la vendita del biglietto
                String htmlResponse = "<html>" +
                        "<head>" +
                        "<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css'>" +
                        "<style>" +
                        "body {" +
                        "    background-image: url('https://www.facileristrutturare.it/wp-content/uploads/2021/08/napoli-centroestetico-web-4.jpg');" +
                        "    background-size: cover;" +
                        "    background-position: center;" +
                        "    height: 100vh;" +
                        "    color: white;" +
                        "}" +
                        ".container {" +
                        "    padding-top: 50px;" +
                        "}" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<nav class='navbar navbar-expand-lg navbar-light bg-light'>" +
                        "<a class='navbar-brand' href='/'>Home</a>" +
                        "</nav>" +
                        "<div class='container'>" +
                        "<h1 class='mt-5'>Inserimento Paziente</h1>" +
                        "<form id='pazienteForm' class='mt-3' method='post' action='/inserimentoPazienti'>" +
                        "<div class='form-group'>" +
                        "<label for='nome'>Nome:</label>" +
                        "<input type='text' class='form-control' id='nome' name='nome'>" +
                        "</div>" +
                        "<div class='form-group'>" +
                        "<label for='cognome'>Cognome:</label>" +
                        "<input type='text' class='form-control' id='cognome' name='cognome'>" +
                        "</div>" +
                        "<div class='form-group'>" +
                        "<label for='telefono'>Telefono:</label>" +
                        "<input type='text' class='form-control' id='telefono' name='telefono'>" +
                        "</div>" +
                        "<div class='form-group'>" +
                        "<label for='indirizzo'>Indirizzo:</label>" +
                        "<input type='text' class='form-control' id='indirizzo' name='indirizzo'>" +
                        "</div>" +
                        "<div class='form-group'>" +
                        "<label for='email'>Email:</label>" +
                        "<input type='text' class='form-control' id='email' name='email'>" +
                        "</div>" +
                        "<div class='form-group'>" +
                        "<label for='dataNascita'>Data di Nascita:</label>" +
                        "<input type='date' class='form-control' id='dataNascita' name='dataNascita'>" +
                        "</div>" +
                        "<div class='form-group'>" +
                        "<label for='eta'>Eta':</label>" +
                        "<input type='number' class='form-control' id='eta' name='eta'>" +
                        "</div>" +
                        "<div class='form-group'>" +
                        "<label for='sesso'>Sesso:</label>" +
                        "<input type='text' class='form-control' id='sesso' name='sesso'>" +
                        "</div>" +
                        "<div class='form-group'>" +
                        "<label for='codiceFiscale'>Codice Fiscale:</label>" +
                        "<input type='text' class='form-control' id='codiceFiscale' name='codiceFiscale'>" +
                        "</div>" +
                        "<div class='form-group'>" +
                        "<label for='patologia'>Patologia:</label>" +
                        "<input type='text' class='form-control' id='patologia' name='patologia'>" +
                        "</div>" +
                        "<div class='form-group'>" +
                        "<label for='allergia'>Allergia:</label>" +
                        "<input type='text' class='form-control' id='allergia' name='allergia'>" +
                        "</div>" +
                        "<div class='form-group'>" +
                        "<label for='note'>Note:</label>" +
                        "<textarea class='form-control' id='note' name='note'></textarea>" +
                        "</div>" +
                        "<button type='submit' class='btn btn-primary'>Inserisci</button>" +
                        "</form>" +
                        "</div>" +
                        "</body>" +
                        "</html>";


                // Imposta l'intestazione della risposta
                exchange.getResponseHeaders().set("Content-Type", "text/html");

                int lunghezza1 = htmlResponse.getBytes("UTF-8").length;

                // Imposta lo status code e la lunghezza della risposta
                exchange.sendResponseHeaders(200, lunghezza1);
                // Scrive la risposta al client
                OutputStream os = exchange.getResponseBody();
                os.write(htmlResponse.getBytes());
                os.close();
            } else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                // Recupera i dati inviati dal form
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();

                // Split dei dati inviati dal form
                String[] formDataArray = formData.split("&");

                // Estrazione dei valori dei campi dal formDataArray
                String nome = formDataArray[0].split("=")[1];
                String cognome = formDataArray[1].split("=")[1];
                String telefono = formDataArray[2].split("=")[1];
                String indirizzo = formDataArray[3].split("=")[1];
                String email = formDataArray[4].split("=")[1];
                String dataDiNascita = formDataArray[5].split("=")[1];
                int eta = Integer.parseInt(formDataArray[6].split("=")[1]);
                String sesso = formDataArray[7].split("=")[1];
                String codiceFiscale = formDataArray[8].split("=")[1];
                String patologia = formDataArray[9].split("=")[1];
                String allergia = formDataArray[10].split("=")[1];
                String note = formDataArray[11].split("=")[1];

                // Connessione al database e inserimento dei dati del paziente
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/studiomedico", "root", "Root")) {
                    String query = "INSERT INTO paziente (Nome, Cognome, Telefono, Indirizzo, Email, DataDiNascita, Eta, Sesso, Codice_Fiscale, Patologia, Allergia, Note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setString(1, nome);
                        pstmt.setString(2, cognome);
                        pstmt.setString(3, telefono);
                        pstmt.setString(4, indirizzo);
                        pstmt.setString(5, email);
                        pstmt.setString(6, dataDiNascita);
                        pstmt.setInt(7, eta);
                        pstmt.setString(8, sesso);
                        pstmt.setString(9, codiceFiscale);
                        pstmt.setString(10, patologia);
                        pstmt.setString(11, allergia);
                        pstmt.setString(12, note);
                        pstmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Dopo l'inserimento dei dati del paziente nel database, reindirizza l'utente alla pagina home
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(302, -1);
            } else {
                // Se la richiesta non è di tipo GET o POST, restituisci errore 405 Method Not Allowed
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    
    
    
    
    
    
    
    static class VisualizzaPazientiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Connessione al database e recupero dei pazienti
            StringBuilder response = new StringBuilder();
            response.append("<html>");
            response.append("<head>");
            response.append("<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css'>");
            response.append("<style>");
            response.append(".table-responsive {");
            response.append("    height: auto;"); // Imposta un'altezza massima al 100% del contenitore
            response.append("}");
            response.append("</style>");
            response.append("</head>");
            response.append("<body style='background-image: url(\"https://www.facileristrutturare.it/wp-content/uploads/2021/08/napoli-centroestetico-web-4.jpg\"); background-size: cover;'>");
            response.append("<nav class='navbar navbar-expand-lg navbar-light bg-light'>");
            response.append("<a class='navbar-brand' href='/'>Home</a>");
            response.append("</nav>");
            response.append("<div class='container'>");
            response.append("<h1 class='mt-5'>Elenco dei pazienti:</h1>");
            response.append("<div class='table-responsive'>");
            response.append("<table class='table table-striped table-bordered'>");
            response.append("<thead class='thead-dark'>");
            response.append("<tr>");
            response.append("<th scope='col'>ID</th>");
            response.append("<th scope='col'>Nome</th>");
            response.append("<th scope='col'>Cognome</th>");
            response.append("<th scope='col'>Telefono</th>");
            response.append("<th scope='col'>Indirizzo</th>");
            response.append("<th scope='col'>Email</th>");
            response.append("<th scope='col'>Data di Nascita</th>");
            response.append("<th scope='col'>Eta'</th>");
            response.append("<th scope='col'>Sesso</th>");
            response.append("<th scope='col'>Codice Fiscale</th>");
            response.append("<th scope='col'>Patologia</th>");
            response.append("<th scope='col'>Allergia</th>");
            response.append("<th scope='col'>Note</th>");
            response.append("<th scope='col'>Elimina</th>"); // Aggiunta della nuova colonna per il pulsante Elimina
            response.append("</tr>");
            response.append("</thead>");
            response.append("<tbody>");

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/studiomedico", "root", "Root")) {
                String query = "SELECT * FROM paziente";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        response.append("<tr>");
                        response.append("<td>").append(rs.getInt("id")).append("</td>");
                        response.append("<td>").append(rs.getString("Nome")).append("</td>");
                        response.append("<td>").append(rs.getString("Cognome")).append("</td>");
                        response.append("<td>").append(rs.getString("Telefono")).append("</td>");
                        response.append("<td>").append(rs.getString("Indirizzo")).append("</td>");
                        response.append("<td>").append(rs.getString("Email")).append("</td>");
                        response.append("<td>").append(rs.getString("DataDiNascita")).append("</td>");
                        response.append("<td>").append(rs.getInt("Eta")).append("</td>");
                        response.append("<td>").append(rs.getString("Sesso")).append("</td>");
                        response.append("<td>").append(rs.getString("Codice_Fiscale")).append("</td>");
                        response.append("<td>").append(rs.getString("Patologia")).append("</td>");
                        response.append("<td>").append(rs.getString("Allergia")).append("</td>");
                        response.append("<td>").append(rs.getString("Note")).append("</td>");
                        // Aggiunta del pulsante Elimina con il parametro id del paziente come value del pulsante
                        response.append("<td><form method='post' action='/eliminaPaziente'><input type='hidden' name='id' value='").append(rs.getInt("id")).append("'><button type='submit' class='btn btn-danger'>Elimina</button></form></td>");
                        response.append("</tr>");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            response.append("</tbody>");
            response.append("</table>");
            response.append("</div>");
            response.append("</div>");
            response.append("</body></html>");

            // Imposta l'intestazione della risposta
            exchange.getResponseHeaders().set("Content-Type", "text/html");

            int lunghezza2 = response.toString().getBytes("UTF-8").length;

            // Imposta lo status code e la lunghezza della risposta
            exchange.sendResponseHeaders(200, lunghezza2);
            // Scrive la risposta al client
            OutputStream os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        }
    }


    
    static class EliminaPazienteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                // Recupera l'ID del paziente dall'input del form
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                String[] formDataArray = formData.split("=");
                int pazienteId = Integer.parseInt(formDataArray[1]);

                // Connessione al database e eliminazione del paziente
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/studiomedico", "root", "Root")) {
                    String query = "DELETE FROM paziente WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setInt(1, pazienteId);
                        pstmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Reindirizza l'utente alla pagina di visualizzazione dei pazienti dopo l'eliminazione
                exchange.getResponseHeaders().set("Location", "/visualizzaPazienti");
                exchange.sendResponseHeaders(302, -1);
            } else {
                // Se la richiesta non è di tipo POST, restituisci errore 405 Method Not Allowed
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    
}

