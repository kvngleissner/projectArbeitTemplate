# 3FA071_Gruppe3
BSINFO Projektarbeit der Gruppe 3 (Dat , Alexander, Nico , Nicolas)

- Aktuellste Version ist im main-Branch zu finden

- Maven Surefire Report unter target/site/index.html

- Create System.properties File in src/main/resources/
  With the following:
  .db.url=jdbc:mariadb://localhost:3306/(DBNAME)
  .db.user=(USER)
  .db.pw=(PASSWORD)

# Setup Guide

- clone from git (via git clone with access token or zip)
- if necessary set folder src/main/java to source
- mvn clean install site
- mvn surefire-report:report
- setup db properties file: src/main/resources/System.properties
- .db.url=jdbc:mariadb://localhost:3306/projektarbeit
  .db.user=
  .db.pw=
- start database "mysql.server start"
- setup database via SetUpDbResourceTest
- render frontend: move into /frontend -> "ng serve"
- open frontend via localhost


