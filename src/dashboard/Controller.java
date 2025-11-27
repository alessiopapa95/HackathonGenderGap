package dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane; // Per il grafico (inizialmente vuoto)
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets; // Spaziatura

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class Controller {
    private BorderPane root;
    private TableView<Item> databaseTable;
    private Pane storicoView; // Contenitore per il futuro grafico
    private Pane filterTable; // Contenitore per il futuro filtro per la tabella
    private Pane nullTable; // Vuoto da mettere a sx della tabella

    public Controller() {
        root = new BorderPane();

        Label welcomeLabel = new Label("Benvenuto! Seleziona 'Database' o 'Storico' per iniziare.");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333;");
        StackPane.setMargin(welcomeLabel, new Insets(0, 0, 50, 0));
        
        // Usiamo StackPane per centrare perfettamente la Label
        StackPane welcomePane = new StackPane(welcomeLabel);
        
        // 1. Inizializzazione della TableView e dei filtri laterali (Database)
        databaseTable = new TableView<>();
        filterTable = new VBox();
        nullTable = new VBox();
        filterTable.setPadding(new Insets(5,50,10,10));
        nullTable.setPadding(new Insets(10));

        // 1.1 Crea l'oggetto Label
        Label filterTitle = new Label("Filtra per:");
        filterTable.getChildren().add(filterTitle);

        // 1.2 Inizializzazione della tabella
        initializeDatabaseTable();

        
        // 2. Inizializzazione della vista "Storico" (Inizialmente vuota)
        storicoView = new VBox(); // VBox o StackPane, a seconda delle esigenze
        storicoView.setPadding(new Insets(10));
        // Aggiungi un messaggio placeholder per ora
        // ((VBox) storicoView).getChildren().add(new javafx.scene.control.Label("Area per il Grafico Storico"));
        
        // 3. Creazione e Configurazione dei Pulsanti (Top)
        Button databaseButton = new Button("Database");
        Button storicoButton = new Button("Storico");
        
        databaseButton.setOnAction(e -> showDatabaseView());
        storicoButton.setOnAction(e -> showStoricoView());
        
        HBox topButtons = new HBox(10, databaseButton, storicoButton); // Spaziatura 10
        topButtons.setPadding(new Insets(10)); // Margine attorno ai pulsanti
        
        root.setTop(topButtons);
        
        // 4. Settaggio della vista iniziale
        aggiornaDati();   // Carica i dati
        root.setCenter(welcomePane);
    }
    
    // Metodo per inizializzare la struttura della TableView
    private void initializeDatabaseTable() {
        TableColumn<Item, Integer> colId = new TableColumn<>("Id");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Item, String> colAnno = new TableColumn<>("Anno");
        colAnno.setCellValueFactory(new PropertyValueFactory<>("anno"));

        TableColumn<Item, String> colNomeAteneo = new TableColumn<>("Nome_Ateneo");
        colNomeAteneo.setCellValueFactory(new PropertyValueFactory<>("nomeAteneo"));

        TableColumn<Item, String> colRegione = new TableColumn<>("Regione");
        colRegione.setCellValueFactory(new PropertyValueFactory<>("regione"));

        TableColumn<Item, String> colAreaGeografica = new TableColumn<>("Area_Geografica");
        colAreaGeografica.setCellValueFactory(new PropertyValueFactory<>("areaGeografica"));

        TableColumn<Item, String> colTipoLaurea = new TableColumn<>("Tipologia_Laurea");
        colTipoLaurea.setCellValueFactory(new PropertyValueFactory<>("tipologiaLaurea"));

        TableColumn<Item, String> colCorso = new TableColumn<>("Corso");
        colCorso.setCellValueFactory(new PropertyValueFactory<>("corso"));

        TableColumn<Item, Integer> colFemmine= new TableColumn<>("F");
        colFemmine.setCellValueFactory(new PropertyValueFactory<>("f"));

        TableColumn<Item, Integer> colMaschi = new TableColumn<>("M");
        colMaschi.setCellValueFactory(new PropertyValueFactory<>("m"));

        TableColumn<Item, Integer> colTotale = new TableColumn<>("Totale");
        colTotale.setCellValueFactory(new PropertyValueFactory<>("totale"));

        databaseTable.getColumns().addAll(colId, colAnno, colNomeAteneo, colRegione, colAreaGeografica, colTipoLaurea, colCorso, colFemmine, colMaschi, colTotale);
    }

    public BorderPane getRoot() { return root; }
    
    // Metodi di Visualizzazione
    
    @FXML
    private void showDatabaseView() {
        // Mostra la TableView
        root.setLeft(filterTable); //METTERE I FILTRI
        root.setCenter(databaseTable);
        root.setRight(nullTable);
    }
    
    @FXML
    private void showStoricoView() {
        root.setLeft(null);
        root.setRight(null);
        // mostra il contenitore per il Grafico Storico
        // quando avrai il BarChart, lo aggiungerai qui.
        // esempio: root.setCenter(myBarChart); 
        // per ora, mostra il Pane vuoto
        root.setCenter(storicoView); 
    }

    // --- Metodo di Caricamento Dati (come prima, ma usa databaseTable) ---
    
    @FXML
    private void aggiornaDati() {
        ObservableList<Item> items = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection();
            //creo un oggetto di classe Statement che mi permette di inviare query SQL al database tramite la connessione conn
            Statement instruction = conn.createStatement();
            //eseguo un executeQuery (il Select) e lo salvo in un ResultSet)
            ResultSet rs = instruction.executeQuery(
                "SELECT id, Anno AS anno, Nome_Ateneo AS nomeAteneo, Regione AS regione, Area_Geografica AS areaGeografica, Tipologia_Laurea AS tipologiaLaurea, Corso AS corso, F AS f, M AS m, Totale AS totale FROM imm_final"
            )) {

            // legge i valori riga per riga finché ci sono righe e li mette nella lista observable
            while (rs.next()) {
                int id = rs.getInt("id");
                String anno = rs.getString("anno");
                String nome_ateneo = rs.getString("nomeAteneo");
                String regione = rs.getString("regione");
                String area_geografica = rs.getString("areaGeografica");
                String tipologia_laurea = rs.getString("tipologiaLaurea");
                String corso = rs.getString("corso");
                int femmine = rs.getInt("f");
                int maschi = rs.getInt("m");
                int totale = rs.getInt("totale");
                
                items.add(new Item(id, anno, nome_ateneo, regione, area_geografica, tipologia_laurea, corso, femmine, maschi, totale));
            }

        } catch (SQLException e) {
            System.err.println("Errore durante il caricamento dei dati del database:");
            e.printStackTrace();
        }

        //lega la lista alla tabella e tramite i getter aggiorna i dati in ogni riga
        databaseTable.setItems(items);
    }

    // Classe Item 

    public static class Item {
        private int id;
        private String anno;
        private String nomeAteneo;
        private String regione;
        private String areaGeografica;
        private String tipologiaLaurea;
        private String corso;
        private int femmine;
        private int maschi;
        private int totale;


        public Item(int id, String anno, String nomeAteneo, String regione, String areaGeografica, String tipologiaLaurea, String corso, int femmine, int maschi, int totale) {
            this.id = id;
            this.anno = anno;
            this.nomeAteneo = nomeAteneo;
            this.regione = regione;
            this.areaGeografica = areaGeografica;
            this.tipologiaLaurea = tipologiaLaurea;
            this.corso = corso;
            this.femmine = femmine;
            this.maschi = maschi;
            this.totale = totale;
        }

        public int getId() { return id; }
        public String getAnno() { return anno; }
        public String getNomeAteneo() { return nomeAteneo; }
        public String getRegione() { return regione; }
        public String getAreaGeografica() { return areaGeografica; }
        public String getTipologiaLaurea() { return tipologiaLaurea; }
        public String getCorso() { return corso; }
        public int getF() { return femmine; }
        public int getM() { return maschi; }
        public int getTotale() { return totale; }



    }
}