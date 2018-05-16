# Projekt z przedmiotu _Metody i Techniki Symulacji Komputerowej"

## Git Flow

1. Tworzymy _issue_ w którym opisujemy zagadnienie, którym będziemy się zajmować.
    * Tytuł powinien być możliwie krótki.
    * Opis powinien szczegółowo wyjaśniać zagadnienie.
    * Dla czytelności można dodać odpowiednie etykiety.
2. Tworzymy _branch_ o nazwie `issue/[numer utworzonego issue]`.
    * Odbijamy się tylko od _brancha_ `master`.
    * Przed utworzeniem nowego _brancha_ należy pobrać zmiany dla _brancha_ `master`.
3. Zmiany publikujemy wyłącznie na _branchu_ dotyczącym danego _issue_.
4. Po skończonej pracy w _issue_ piszemy podsumowanie i robimy _merge_ do _brancha_ `master`.
5. Zamykamy _issue_.

## Organizacja katalogów

* `foms/` - zawiera modele FOM.
* `src/` - zawiera pliki źródłowe aplikacji.
    * `federates/` - zawiera klasy federatórw.
        * `ambassadors/` - zawiera klasy ambasadorów federatów.
    * `interactions/` - zawiera pomocnicze klasy interakcji.
    * `objects/` - zawiera pomocnicze klasy obiektów.
