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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis; // Usiamo CategoryAxis per gli anni (Stringhe)
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.BarChart;
import javafx.scene.layout.Priority;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller {
    private BorderPane root;
    private TableView<Item> databaseTable;
    private Pane storicoView; // Contenitore per il futuro grafico
    private Pane filterTable; // Contenitore per il futuro filtro per la tabella
    private Pane nullTable; // Vuoto da mettere a dx della tabella
    private Pane storicoPane; // Vuoto da mettere a dx della tabella

    private ObservableList<Item> allItems = FXCollections.observableArrayList(); // Lista di appoggio per memorizzare i dati del database

    public Controller() {
        
        root = new BorderPane();

        Label welcomeLabel = new Label("Benvenuto! Seleziona 'Database' o 'Storico' per iniziare.");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333;");
        StackPane.setMargin(welcomeLabel, new Insets(0, 0, 50, 0));
        
        // Usiamo StackPane per centrare perfettamente la Label
        StackPane welcomePane = new StackPane(welcomeLabel);
        
        // 1.0 Inizializzazione della TableView e dei filtri laterali (Database)
        databaseTable = new TableView<>();
        filterTable = new VBox();
        nullTable = new VBox();
        filterTable.setPadding(new Insets(5,50,10,10));
        nullTable.setPadding(new Insets(10));

        // 1.1 Aggiunta filtro area geografica
        Label lblFiltroArea = new Label("Filtro Area Geografica");

        ComboBox<String> cmbArea = new ComboBox<>();
        cmbArea.getItems().add("Nessun filtro");
        cmbArea.getItems().addAll("NORD", "CENTRO", "SUD E ISOLE", "TOTALE ATENEI"); 
        cmbArea.setValue("Nessun filtro");
        cmbArea.setPrefWidth(150.0);
        cmbArea.setPrefHeight(30.0);

        // 1.2 Aggiunta di filtro regione
        Label lblFiltroRegioni = new Label("Filtro Regione");

        ComboBox<String> cmbRegioni = new ComboBox<>();
        cmbRegioni.getItems().add("Nessun filtro");
        cmbRegioni.getItems().addAll(
            "Abruzzo",
            "Basilicata",
            "Calabria",
            "Campania",
            "Emilia Romagna",
            "Friuli Venezia Giulia",
            "Lazio",
            "Liguria",
            "Lombardia",
            "Marche",
            "Molise",
            "Piemonte",
            "Puglia",
            "Sardegna",
            "Sicilia",
            "Toscana",
            "Trentino Alto Adige",
            "Umbria",
            "Valle d'Aosta",
            "Veneto"
        );
        cmbRegioni.setValue("Nessun filtro");
        cmbRegioni.setPrefWidth(150.0);
        cmbRegioni.setPrefHeight(30.0);

        // 1.3 Aggiunta filtro anno
        Label lblFiltroAnno = new Label("Filtro Anno");

        ComboBox<String> cmbAnno = new ComboBox<>();
        cmbAnno.getItems().add("Nessun filtro");
        cmbAnno.getItems().addAll("2013/2014", "2014/2015", "2015/2016", "2016/2017", "2017/2018", "2018/2019", "2019/2020", "2020/2021", "2021/2022", "2022/2023", "2023/2024"); // metti tutti gli anni disponibili
        cmbAnno.setValue("Nessun filtro");
        cmbAnno.setPrefWidth(150.0);
        cmbAnno.setPrefHeight(30.0);

        // 1.4 Aggiunta filtro corso
        Label lblFiltroCorso = new Label("Filtro Corso");

        ComboBox<String> cmbCorso = new ComboBox<>();
        cmbCorso.getItems().add("Nessun filtro");
        cmbCorso.getItems().addAll("Business, administration and law", "Arts and humanities", "Education", "Health and welfare", "Information and Communication Technologies (ICTs)", "Natural sciences, mathematics and statistics", "Services", "Engineering, manufacturing and construction", "Agriculture, forestry, fisheries and veterinary", "Social sciences, journalism and information"); 
        cmbCorso.setValue("Nessun filtro");
        cmbCorso.setPrefWidth(150.0);
        cmbCorso.setPrefHeight(30.0);

        // 1.5 applica filtro
        cmbArea.setOnAction(e -> applicaFiltri(cmbArea.getValue(), cmbRegioni.getValue(), cmbAnno.getValue(), cmbCorso.getValue()));
        cmbRegioni.setOnAction(e -> applicaFiltri(cmbArea.getValue(), cmbRegioni.getValue(), cmbAnno.getValue(), cmbCorso.getValue()));
        cmbAnno.setOnAction(e -> applicaFiltri(cmbArea.getValue(), cmbRegioni.getValue(), cmbAnno.getValue(), cmbCorso.getValue()));
        cmbCorso.setOnAction(e -> applicaFiltri(cmbArea.getValue(), cmbRegioni.getValue(), cmbAnno.getValue(), cmbCorso.getValue()));

        VBox root1 = new VBox(10, lblFiltroArea, cmbArea, lblFiltroRegioni, cmbRegioni, lblFiltroAnno, cmbAnno, lblFiltroCorso, cmbCorso);
        root1.setPadding(new Insets(20));

        filterTable.getChildren().add(root1);
        
        
        // 1.6 Inizializzazione della tabella
        initializeDatabaseTable();

                
        // 2.0 Inizializzazione della vista "Storico" (Inizialmente vuota)
        storicoView = new VBox(); // VBox o StackPane, a seconda delle esigenze
        storicoView.setPadding(new Insets(10));
        Label storicoLabel = new Label("WORK IN PROGRESS: Antonio pensaci tu");
        storicoLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: #911;"); 
        StackPane.setMargin(storicoLabel, new Insets(0, 0, 50, 0));
        
        // Usiamo StackPane per centrare perfettamente la Label
        storicoPane = new StackPane(storicoLabel);
        
        // 3.0 Creazione e Configurazione dei Pulsanti
        Button databaseButton = new Button("Database");
        Button storicoButton = new Button("Storico");
        
        databaseButton.setOnAction(e -> showDatabaseView());
        storicoButton.setOnAction(e -> showStoricoView());
        
        HBox topButtons = new HBox(10, databaseButton, storicoButton); // Spaziatura 10
        topButtons.setPadding(new Insets(10)); // Margine attorno ai pulsanti
        
        root.setTop(topButtons);
        
        // 4.0 Settaggio della vista iniziale
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
        root.setLeft(filterTable); // Mostra i filtri
        root.setCenter(databaseTable); // Mostra la TableView
        root.setRight(nullTable); // Spazio a dx della tabella
    }
    
    @FXML
    private void showStoricoView() {
        // 1. Genera i due grafici
        LineChart<String, Number> lineChartStorico = createStoricoChart();
        BarChart<String, Number> barChartDistribuzione = createGenderDistributionChart();
        
        // Rimuoviamo il setPrefWidth, lasciamo che HBox gestisca la larghezza
        // lineChartStorico.setPrefWidth(400); 
        // barChartDistribuzione.setPrefWidth(400); 
        
        // 2. Affiancali in un HBox
        HBox chartsContainer = new HBox(20); // Spaziatura di 20 pixel tra i grafici
        chartsContainer.setPadding(new Insets(20));
        chartsContainer.getChildren().addAll(lineChartStorico, barChartDistribuzione);
        
        // 3. APPLICA L'ESPANSIONE ORRIZONTALE (HGrow)
        // Questo dice al layout HBox di dare ai nodi la priorità di espandersi
        HBox.setHgrow(lineChartStorico, Priority.ALWAYS);
        HBox.setHgrow(barChartDistribuzione, Priority.ALWAYS);
        
        // 4. Imposta il centro del BorderPane
        root.setLeft(null); 
        storicoPane.getChildren().clear(); 
        storicoPane.getChildren().add(chartsContainer);
        
        root.setCenter(storicoPane);
        root.setRight(null);
    }



    // Metodo di Caricamento Dati
    
    @FXML
    private void aggiornaDati() {
        allItems.clear();

        try (Connection conn = DatabaseConnection.getConnection();
            Statement instruction = conn.createStatement();
            ResultSet rs = instruction.executeQuery(
                "SELECT id, Anno AS anno, Nome_Ateneo AS nomeAteneo, Regione AS regione, Area_Geografica AS areaGeografica, Tipologia_Laurea AS tipologiaLaurea, Corso AS corso, F AS f, M AS m, Totale AS totale FROM imm_final"
            )) {

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

                allItems.add(new Item(id, anno, nome_ateneo, regione, area_geografica, tipologia_laurea, corso, femmine, maschi, totale));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // assegna tutti i dati alla TableView
        databaseTable.setItems(allItems);
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

    private void applicaFiltri(String areaSelezionata, String regioneSelezionata, String annoSelezionato, String corsoSelezionato) {

        ObservableList<Item> filtrati = FXCollections.observableArrayList();

        for (Item it : allItems) {

            boolean matchArea;
            if (areaSelezionata.equals("Nessun filtro")) {
                matchArea = true; // Nessun filtro applicato
            } else if (areaSelezionata.equals("NORD")) {
                // Se selezionato "NORD", accetta sia NORD-EST che NORD-OVEST
                matchArea = it.getAreaGeografica().equals("NORD-EST") || it.getAreaGeografica().equals("NORD-OVEST");
            } else if (areaSelezionata.equals("SUD E ISOLE")) {
                // Se selezionato "SUD E ISOLE", accetta sia SUD che ISOLE
                matchArea = it.getAreaGeografica().equals("SUD") || it.getAreaGeografica().equals("ISOLE");
            } else if (areaSelezionata.equals("TOTALE ATENEI")) {
                // Se selezionato "TOTALE", allora accetta ITALIA
                matchArea = it.getAreaGeografica().equals("ITALIA");
            } else {
                // Per tutti gli altri filtri (e.g., "CENTRO", o le aree specifiche), usa il matching esatto
                matchArea = it.getAreaGeografica().equals(areaSelezionata);
            }
            boolean matchRegione = regioneSelezionata.equals("Nessun filtro") || it.getRegione().equals(regioneSelezionata);
            boolean matchAnno = annoSelezionato.equals("Nessun filtro") || it.getAnno().equals(annoSelezionato);
            boolean matchCorso = corsoSelezionato.equals("Nessun filtro") || it.getCorso().equals(corsoSelezionato);


            if (matchArea && matchRegione && matchAnno && matchCorso) {
                filtrati.add(it);
            }
        }

        databaseTable.setItems(filtrati);
    }

    private LineChart<String, Number> createStoricoChart() {
        
        // Assi
        final CategoryAxis xAxis = new CategoryAxis(); // Anni sono stringhe (Categorie)
        final NumberAxis yAxis = new NumberAxis(10, 20, 0.5);
        
        xAxis.setLabel("Anno Accademico");
        yAxis.setLabel("Rapporto Femmine / Totale");
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, null, "%")); // Mostra come percentuale

        // Grafico
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Distribuzione di Genere in ICTs per Anno");
        lineChart.setLegendVisible(false); // Una sola serie, non serve la legenda
        lineChart.setCreateSymbols(true);
        
        // Serie di Dati
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Rapporto F/Totale ICTs");

        // Mappa per aggregare i dati: Anno -> [Totale Femmine, Totale Generale]
        java.util.Map<String, double[]> datiAggregati = new java.util.TreeMap<>();

        final String CORSO_TARGET = "Information and Communication Technologies (ICTs)";

        // 1. Filtraggio e Aggregazione dei Dati
        for (Item item : allItems) {
            if (item.getCorso().equals(CORSO_TARGET)) {
                String anno = item.getAnno();
                int femmine = item.getF();
                int totale = item.getTotale();

                // Aggiorna la mappa: [F, Totale]
                datiAggregati.computeIfAbsent(anno, k -> new double[2]);
                datiAggregati.get(anno)[0] += femmine;
                datiAggregati.get(anno)[1] += totale;
            }
        }

        // 2. Calcolo del Rapporto e Aggiunta alla Serie
        for (java.util.Map.Entry<String, double[]> entry : datiAggregati.entrySet()) {
        String anno = entry.getKey();
        double totaleFemmine = entry.getValue()[0];
        double totaleGenerale = entry.getValue()[1];
        
        double rapporto = (totaleGenerale > 0) ? (totaleFemmine / totaleGenerale) : 0.0;
        
        // **!!! MODIFICA CHIAVE QUI: Moltiplica il rapporto per 100 !!!**
        double rapportoPercentuale = rapporto * 100.0; 
        
        // Aggiungi il punto (Anno, Rapporto in %) alla serie
        series.getData().add(new XYChart.Data<>(anno, rapportoPercentuale));
    }

        lineChart.getData().add(series);
        
        return lineChart;
    }

    private BarChart<String, Number> createGenderDistributionChart() {

        // --- Assi ---
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis(0, 100, 10);

        xAxis.setLabel("Area Geografica");
        yAxis.setLabel("Percentuale sul Totale Immatricolazioni Area (%)");
        yAxis.setAutoRanging(false);
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, null, "%"));

        // --- Grafico ---
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Distribuzione di Genere in ICTs per Area");
        barChart.setCategoryGap(20);

        // --- Serie F / M ---
        XYChart.Series<String, Number> seriesF = new XYChart.Series<>();
        seriesF.setName("Donne (F)");

        XYChart.Series<String, Number> seriesM = new XYChart.Series<>();
        seriesM.setName("Uomini (M)");

        // --- Aggregazione dati ---
        Map<String, double[]> datiAggregati = new LinkedHashMap<>();
        datiAggregati.put("NORD", new double[]{0, 0, 0});
        datiAggregati.put("CENTRO", new double[]{0, 0, 0});
        datiAggregati.put("SUD E ISOLE", new double[]{0, 0, 0});

        final String CORSO_TARGET = "Information and Communication Technologies (ICTs)";

        for (Item item : allItems) {
            if (!item.getCorso().equals(CORSO_TARGET)) continue;

            String area = item.getAreaGeografica();
            String macroArea;

            if (area.equals("NORD-EST") || area.equals("NORD-OVEST")) {
                macroArea = "NORD";
            } else if (area.equals("CENTRO")) {
                macroArea = "CENTRO";
            } else if (area.equals("SUD") || area.equals("ISOLE")) {
                macroArea = "SUD E ISOLE";
            } else {
                continue;
            }

            double[] dati = datiAggregati.get(macroArea);
            dati[0] += item.getF();
            dati[1] += item.getM();
            dati[2] += item.getTotale();
        }

        // --- Percentuali e popolamento serie ---
        for (Map.Entry<String, double[]> entry : datiAggregati.entrySet()) {
            String area = entry.getKey();
            double femmine = entry.getValue()[0];
            double maschi = entry.getValue()[1];
            double totale = entry.getValue()[2];

            if (totale > 0) {
                double pF = (femmine / totale) * 100.0;
                double pM = (maschi / totale) * 100.0;

                seriesF.getData().add(new XYChart.Data<>(area, pF));
                seriesM.getData().add(new XYChart.Data<>(area, pM));
            }
        }

        barChart.getData().addAll(seriesF, seriesM);

        // --- Colorazione barre e testo legenda ---
        Platform.runLater(() -> {
            
            // 1. Colorazione barre
            for (XYChart.Data<String, Number> d : seriesF.getData()) {
                if (d.getNode() != null)
                    d.getNode().setStyle("-fx-bar-fill: #ff4da6;"); // Rosa
            }
            for (XYChart.Data<String, Number> d : seriesM.getData()) {
                if (d.getNode() != null)
                    d.getNode().setStyle("-fx-bar-fill: #4da6ff;"); // Blu
            }

            // 2. Colorazione dei SIMBOLI della legenda
            // I simboli sono i nodi con la classe .chart-legend-item-symbol.seriesN
            
            Node s0 = barChart.lookup(".chart-legend-item-symbol.series0"); // Simbolo per Serie F (Donne)
            Node s1 = barChart.lookup(".chart-legend-item-symbol.series1"); // Simbolo per Serie M (Uomini)

            // Imposta il colore di background per il simbolo della serie F
            if (s0 != null) {
                // Rimuovi style precedente, se presente
                s0.setStyle("");
                // Imposta il colore di background desiderato
                s0.setStyle("-fx-background-color: #ff4da6;"); 
                // Assicurati che sia visibile
                s0.setVisible(true); 
            }
            
            // Imposta il colore di background per il simbolo della serie M
            if (s1 != null) {
                 // Rimuovi style precedente, se presente
                 s1.setStyle("");
                // Imposta il colore di background desiderato
                s1.setStyle("-fx-background-color: #4da6ff;");
                // Assicurati che sia visibile
                s1.setVisible(true);
            }
            
            // Rimuovi la logica di colorazione/rimozione manuale del testo/simbolo dalla legenda
            // La riga successiva NON deve esserci per colorare i simboli, a meno che tu non voglia solo il testo colorato!
            // Rimuovi: 'if (barChart.lookup(".chart-legend") instanceof Pane legendPane) { ... }'
        });

        return barChart;
    }


    


}