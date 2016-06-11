Autor:
    Maciej Poćwierz, nr albumu: 242736


Tytuł zadania:
    AAL-2-LS - znajdowanie najtańszego połączenia pomiędzy miastami


Tryby działania programu:

    - Tryb rozwiązywania pojedyńczej instancji problemu (domyślny):

        --input - nazwa pliku wejściowego

        --cityStart id miasta początkowego

        --cityEnd id miasta docelowego


    - Tryb wizualizacji (komenda visualize):

        --input - nazwa pliku wejściowego


    - Tryb generowania instancji problemu (komenda generate)

        --output - nazwa pliku wyjściowego

        --citiesCount - liczba miast (wymagany)

        --roadsCount - liczba dróg (wymagany)

        --alliancesCount - liczba grup miast partnerskich (wymagany)

        --cityPriceMin - minimalna cena za wjazd do miasta

        --cityPriceMax - maksymalna cena za wjazd do miasta

        --roadPriceMin - minimalna cena za wjazd na drogę

        --roadPriceMax - maksymalna cena za wjazd na drogę

        --roadDurationMin - minimalny czas przejazdu drogą

        --roadDurationMax - maksymalny czas przejazdu drogą

        --costPerMinute - stały koszt związany z czasem przejazdu

    - Tryb testu wydajności (komenda performance-test)

        --dimensionMin - minimalna liczba wierzchołków w grafie

        --dimensionMax - maksymalna liczba wierzchołków w grafie

        --dimensionStep - krok co jaki zwiększany jest rozmiar problemu

        --density - gęstość generowanych grafów

        UWAGA: tryb testu wydajności wygeneruje grafy o rozmiarach od dimensionMin
        do dimensionMax w krokach co dimensionStep, jednak pominie 10 pierwszych kroków
        przy analizie wyników, aby dać maszynie JVM czas na zoptymalizowanie kodu bajtowego.
        Należy zatem zadbać aby zawsze spełniony był warunek: (dimensionMax-dimensionMin)/dimensionStep > 10


Przykładowe wywołania:
UWAGA: Wymagane jest użycie Javy 8.

- Tryb rozwiązywania pojedyńczej instancji problemu:
    java -jar aal.jar --input test.json --cityStart city1 --cityEnd city4

- Tryb generowania instancji problemu:
    java -jar aal.jar generate --citiesCount 10 --roadsCount 30 --alliancesCount 3 --output out.json

-Tryb wizualizacji:
    java -jar aal.jar visualize --input test.json

- Test wydajnościowy:
    java -jar aal.jar performance-test --dimensionMin 100 --dimensionMax 600 --dimensionStep 10


Dane wejściowe / wyjściowe:
    Dane we/wy przechowywane są w formacie JSON.
    Pliki zawierają informacje na temat miast obecnych w instancji problemu, dróg pomiędzy nimi,
    grupach miast partnerskich a także stałym koszcie związanym z czasem przejazdu.


Metoda rozwiązania:
    Problem rozwiązywany jest za pomocą algorytmu Dijkstry.
    Ponieważ graf reprezentujący sieć dróg jest w praktyce prawie zawsze grafem rzadkim, zdecydowano się na
    implementację z użyciem kolejki priorytetowej (konkretnie kopca binarnego).


Podział na pakiety:
    - graph - pakiet zawiera pliki źródłowe związane z reprezentacją problemu w postaci grafu.
    - io - pakiet zawiera klasy służące do odczytu, zapisu, lub wizualizacji instancji problemu
    - util - klasy zawierające funkcje pomocnicze

