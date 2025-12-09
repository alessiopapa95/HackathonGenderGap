<h1>Hackathon Gender Gap</h1>

Consultare il file Report.pdf per tutte le informazioni relative a questo progetto.

<h2>Guida all'utilizzo dell'applicazione</h2>

<h3>Importare database hackathon.sql</h3>

Prerequisito:
- Aver installato MySQL sul proprio terminale ed aver avviato il server

Passaggi da eseguire:
1.  Utilizzare da terminale il comando:
    "mysql -u [nome_utente] -p Hackathon < backup_database.sql"

2.  Accedere al contenuto del file Hackathongendergap/src/dashboard/DatabaseConnection.java
    e modificare il contenuto di USER e PASSWORD, inserendo quelli del proprio utente MySQL.

<h3>Avviare l'applicazione</h3>

1.  Importa il progetto in un IDE compatibile con Java (es. IntelliJ IDEA, Eclipse).

2.  Assicurati che la struttura dei pacchetti sia corretta ed eventuali dipendenze in /lib siano incluse nel classpath. (IntelliJ: Clic destro sulla cartella lib → Add as Library - Eclipse: Clic destro sul progetto → Build Path, Configure Build Path…, Tab Libraries → Add JARs… Seleziona i .jar dentro la cartella lib/.)

3.  Esegui la classe principale (main) per avviare l’applicazione — verifica i dettagli nel codice.

In alternativa, compilare l'applicativo tramite il terminale

1.  Aprire da terminale la cartella HackathonGenderGap

2.  Compilare eseguendo il comando: 
    javac --module-path lib/javafx-sdk-17.0.17/lib --add-modules javafx.controls,javafx.fxml -cp "lib/mysql-connector-j-9.5.0.jar" -d out $(find src -name "*.java")

3.  Avviare attraverso il comando:
    java --module-path lib/javafx-sdk-17.0.17/lib --add-modules javafx.controls,javafx.fxml -cp "out:lib/mysql-connector-j-9.5.0.jar" Main

