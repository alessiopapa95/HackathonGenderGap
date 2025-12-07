IMPORTARE DATABASE HACKATON.SQL 

Prerequisito:
- Aver installato MySQL sul proprio terminale ed aver avviato il server

Passaggi da eseguire:
1.  Utilizzare da terminale il comando:
    "mysql -u [nome_utente] -p Hackaton < backup_database.sql"

2.  Accedere al contenuto del file Hackatongendergap/src/dashboard/DatabaseConnection.java
    e modificare il contenuto di USER e PASSWORD, inserendo quelli del proprio utente MySQL.