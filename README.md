# AutoReroll

Klientowy mod Fabric dla **Minecraft 1.21.4**, który automatycznie rolluje zaczarowanie miecza
(np. w menu zaczarowania na anarchia.gg) i zatrzymuje się, gdy wypadnie procent z ustawionego zakresu,
np. od 47% do 50%.

## Wymagania

- Minecraft 1.21.4
- Fabric Loader 0.16.10+
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.119.3+1.21.4
- Java 21

## Instalacja

1. Pobierz jar z zakladki **Actions** (artefakt `autoreroll`) albo zbuduj go sam (patrz nizej).
2. Wrzuc jar do folderu `mods/` razem z Fabric API.

## Uzycie

1. W grze wcisnij **O**, otworzy sie okno ustawien:
   - zatrzymaj od % / do % (zakres),
   - opoznienie miedzy rollami (w tickach, 20 = 1 s),
   - limit rolli (0 = bez limitu),
   - slot rerolla i slot miecza.
2. Kliknij **Wybierz slot rerolla**, otworz GUI zaklinania i kliknij przycisk rolowania.
   Klikniecie tylko zapisuje ID slotu, nic nie jest wysylane na serwer.
3. Opcjonalnie to samo dla **Wybierz slot miecza** (slot, w ktorym widac procent).
   Przy wartosci `-1` mod przeszukuje wszystkie sloty GUI serwera.
4. W GUI zaklinania wcisnij **R**, zeby wystartowac. Ten sam klawisz zatrzymuje rolowanie.

Mod zatrzymuje sie sam po trafieniu w zakres, po zamknieciu GUI, po osiagnieciu limitu rolli
albo gdy przez 10 prob nie znajdzie procentu.

## Konfiguracja

Plik `config/autoreroll.json`:

| Pole          | Opis                                                                  |
|---------------|-----------------------------------------------------------------------|
| `min`, `max`  | zakres procentu, w ktorym mod ma sie zatrzymac                        |
| `delayTicks`  | opoznienie miedzy rollami w tickach                                   |
| `maxRolls`    | maksymalna liczba rolli, `0` = bez limitu                             |
| `rerollSlot`  | ID slotu z przyciskiem rolowania                                      |
| `previewSlot` | ID slotu z mieczem, `-1` = skanuj wszystkie sloty GUI                 |
| `regex`       | regex wyciagajacy procent z nazwy i opisu przedmiotu (grupa 1 = liczba) |

## Budowanie

```
gradle build
```

Gotowy jar znajdziesz w `build/libs/`. Mozesz tez wygenerowac Gradle Wrapper poleceniem
`gradle wrapper` i dalej budowac przez `./gradlew build`.

## Uwaga

Mod automatyzuje klikanie w GUI. Uzywaj go tylko tam, gdzie regulamin serwera na to pozwala.

## Licencja

MIT
