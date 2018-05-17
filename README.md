# Projekt z przedmiotu _"Metody i Techniki Symulacji Komputerowej"_

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
    * _Merge_ robimy z opcją `--no-ff`.
5. Zamykamy _issue_.

## Organizacja katalogów

* `foms/` - zawiera modele FOM.
* `src/` - zawiera pliki źródłowe aplikacji.
    * `federates/` - zawiera klasy federatórw.
        * `ambassadors/` - zawiera klasy ambasadorów federatów.
    * `interactions/` - zawiera pomocnicze klasy interakcji.
    * `objects/` - zawiera pomocnicze klasy obiektów.

## Uwagi
_Commity_ powinny być opatrzone etykietą `GH-[numer utworzonego issue]`. Odwołania do użytkownika robimy za pomocą `@[NAZWA UŻYTKOWNIKA]`.

Przykładowy _commit_:

> GH-1 Poprawki do klasy X według uwag @Y

Więcej w pomocy GitHuba: [Autolinked references and URLs](https://help.github.com/articles/autolinked-references-and-urls/), [Mentioning people and teams](https://help.github.com/articles/basic-writing-and-formatting-syntax/#mentioning-people-and-teams).
