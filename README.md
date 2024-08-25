<h1> Strona do zarzadzania dokumentami w języku polskim. </h1>
Cała aplikacja polega na umożliwieniu grupie osób szybszego sposobu zarządzania dokumentami. Osoby te mogą w każdej chwili dodawać nowe dokumenty do systemu, grupować je po typie, dodawać datę ważność oraz najważniejsze przeszukiwać i sortować całą bazę dokumentów na podstawie ich zawartości i każdej przypisanej im wartości.
Osoba, która dodała dokument do systemu może go w każdej chwili edytować, lecz wszystkie inne osoby mające dostęp na tej stronie mogą go jedynie wyszukiwać oraz pobierać.
By ta aplikacja była bezpieczna jedyną osobą, która może dodawać użytkowników do tej aplikacji jest administrator, którego dane ustawia się przy pierwszym uruchomieniu aplikacji. Jak to zrobić w dalszej części implementacji.

Główne pole zarządzania dokumentami dodanymi przez siebie:
![image](https://github.com/user-attachments/assets/616bbc50-d69c-4443-a211-95c4c77c7c76)

Kalendarz z rozmieszczonymi dokumentami ze względu na przypisaną im datę:
![image](https://github.com/user-attachments/assets/47194d06-2204-411d-8413-745276f9a91f)


<h2> Implementacja </h2>

Aby to zrobić, należy wykonać następujące kroki:
1. Zainstalować Javę 17, wybraną środowisko programistyczną (IDE) do obsługi Javy oraz menadżer
  pakietów NPM.

2. Zainstalować bazę danych PostgreSQL lub inną wybraną bazę danych. Ten krok wymaga pewnych
  zmian w pliku "application.yml" znajdującym się w folderze "src/main/resources" na linii 8:
  1.  url: jdbc:postgresql://localhost:5432/kancelaria
  2.  username: ${DBUSERNAME}
  3.  password: ${DBPASSWORD}
  4.  driverClassName: org.postgresql.Driver
  Aby odpowiednio dostosować aplikację do zainstalowanej bazy danych, konieczne jest ustawienie
  zmiennych w pliku "application.yml". Te zmienne powinny być dostosowane do konkretnego
  środowiska i bazy danych. Wystarczy stworzyć pustą bazę danych z dodanym schematem
  o nazwie "first". Jeśli używana jest inna baza danych niż PostgreSQL, należy również
  zmienić klasę zapisaną na linii 4, na odpowiednią oraz dodać zależność do sterownika, do pliku
  "pom.xml", obsługującego tworzenie połączenia. Zmienne środowiskowe "DBUSERNAME"
  i "DBPASSWORD", które będą reprezentować nazwę użytkownika i hasło do bazy danych, powinny
  być zapisane w IDE przy parametrach uruchomienia aplikacji. Można je także zapisać
  w zmiennych systemowych lub zapisać jawni w pliku, lecz nie są to zalecane rozwiązania.
  Jeśli klient chce, aby testy były uruchamiane na jego środowisku, należy utworzyć nową bazę
  danych. Ta nowa baza danych powinna być identyczna do pierwszej, ale z inną nazwą. Należy
  również wprowadzić odpowiednie zmiany w pliku "application.yml" znajdującym się w folderze
  "test/resources", by adres prowadził na bazę testową.

3. Dodaj dane początkowe do bazy danych. Wstaw role "User" i "Manager" do tabeli "first.role".
  Dodaj typ dokumentu o wartości pola "id" równej "0" do tabeli "first.type". Możesz nadać
  mu dowolną nazwę, np. "brak typu".

4. Aplikacja do poprawnego działania potrzebuje jeszcze jednej zmiennej środowiskowej o nazwie
  „SECRETKEY” o zawartości klucza prywatnego o długości co najmniej 256 bitów, zaleca
  się nie używać kluczy większych niż 64 bajty. Można wygenerować taki klucz samodzielnie
  za pomocą narzędzi do szyfrowania. Przykładowym narzędziem do generowania kluczy może
  być „KeyGenerator” od IBM. Alternatywnie, są również dostępne online generatory kluczy,
  takie jak: https://www.mobilefish.com/services/pseudorandom_number_generator/
  pseudorandom_number_generator.php#big_random_number_output.
  Aplikacja także potrzebuje by w folderze "/src/main/frontend" stworzyć plik ".env" z
  zawartością "SECURE_LOCAL_STORAGE_HASH_KEY="TWÓJ WYGENEROWANY KLUCZ"".

5. Aby obsługiwać różne formaty dokumentów, aplikacja wymaga usługi Gotenberg działającej
  w środowisku Docker na porcie 3000.47 Gotenberg jest wykorzystywany do konwersji dokumentów,
  które nie są w formacie "pdf" lub "docx" na format "pdf", który jest obsługiwany
  przez twoją aplikację. Należy pamiętać, że usługę Gotenberg powinno się włączać tylko
  do użytku lokalnego i zabezpieczyć ją przed dostępem zdalnym ze względów bezpieczeństwa.

6. Należy upewnić się, że żadna usługa w środowisku klienckim nie zajmuje portów 3000, 8091
  oraz 8092. Oto kroki, które można podjąć, jeśli któryś z tych portów jest zajęty:
◦ Port 3000 (Gotenberg):
▪ Włącz usługę Gotenberg na innym porcie niż 3000.
▪ W pliku "FileToPdf" znajdującym się w projekcie na ścieżce "src/main/java/com/
  asledz/kancelaria_prawnicza/utilis/", na linii 31 zmień numer portu na ten, na którym
  Gotenberg został uruchomiony.
◦ Port 8091 (Spring):
▪ W pliku "application.yml" na ścieżce "src/main/resources/", zmień numer portu na linii 5 na inny.
▪ Wraz z tym w aplikacji frontend’owej należy zmienić wartość do której będzie się
  ten serwer odnosił. Jest ta dana zapisana w pliku „config” na ścieżce „src/main/frontend/
  src/components/redux/” na linii 7.
◦ Port 8092 (aplikacja React):
▪ W pliku „package.json” znajdującym się na ścieżce „src/main/frontend/”, zmień numer
  portu na którym aplikacja React jest uruchamiana (linia 37).
▪ Wraz z tą zmianą trzeba zaktualizować politykę CORS aplikacji backend’owej, która
  znajduje się w pliku „SecurityConfig” na ścieżce „src/main/java/com/asledz/kancelaria_
  prawnicza/security/”. Na linii 70 znajduje się adres oraz port serwera frontend’owego,
  z którego zapytania są akceptowane.

7. Usunięcie komentarza z adnotacji „@Component” w klasie „Reindexer” na ścieżce „src/main/
  java/com/asledz/kancelaria_prawnicza/search”. Służy to wstępnej indeksacji danych znajdujących
  się w bazie danych. Przy każdym następnym resecie aplikacji można ją z powrotem zakomentować,
  aby zmniejszyć czas, w jakim aplikacja się uruchamia. Wtedy aplikacja działa
  do czasu, gdy baza danych nie jest napełniana danymi spoza aplikacji.

8. Przy pierwszym uruchomieniu zalecane jest włączenie programu przez dowolne IDE, a dopiero następnie
   spakowanie całego kodu w .jar i włączanie go przez konsolę.

9. By włączyć serwer React odpowiedzialny za wygląd strony należy użyć komend w folderze "/src/main/frontend":
  - npm install
  - npm build
  Po zainstalowaniu i zbudowaniu aplikacji można ją w każdym momęcie włączać komendą, w tym samym folderze, "npm start".

Po wykonaniu tych czynności aplikacja jest gotowa do działania. By ją włączyć należy:
- włączyć serwer Spring jak w punkcie 8.
- włączyć serwer za pomocą użycia w terminalu komendy "npm start" w folderze "/src/main/frontend"
   
<h2>Implementacja HTTPS w sieciach otwartych na internet lub nie zabezpieczonych</h2>

By aplikacja była bezpieczniejsza zaleca się zmianę protokołu HTTP na HTTPS, lecz jeśli sieć, w której jest ona implementowana jest bez dostępu na Internet i jest bezpieczna to nie jest to pilne.
- By tego dokonać polecam zapoznać się z tym artykułem: https://www.baeldung.com/spring-boot-https-self-signed-certificate
- Jeśli tego dokonacie, to trzeba zmienić w folderze /src/main/frontend/src/components/redux/config.js na lini 6 z localhost na odpowiedni adres IP komputera na którym ta aplikacja jest włączona oraz na linii 7 odpowiedni port, który został najprawdopodobniej zmieniony podczas włączania protokołu SSL w celu uzyskania komunikacji przez HTTPS.
- Na koniec w pliku /src/main/com/asledz/kancelaria_prawnicza/security/securityConfig.java na linii 70 również zmienić napis localhost i liczbę po dwukropku na odpowiednią tak jak w kroku wyżej.
Jeśli dokonacie zmian pamiętajcie by skompilować zmiany do nowego pliku wykonywalnego .jar.
