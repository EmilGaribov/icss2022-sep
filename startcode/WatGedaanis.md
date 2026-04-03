# ICSS-Compiler Opdracht

In dit md bestand geef ik een overzicht van de behaalde eisen voor de ICSS-compiler opdracht. 
Naast de verplichte onderdelen heb ik verschillende extra functionaliteiten geïmplementeerd om de taal uit te breiden en omdat dit mjj zining leek om te doen.

## 1. Parser (Grammar en Listener)
Ik heb een volledig functionerende grammar opgesteld (`ICSS.g4`) en 
een `ASTListener.java` geïmplementeerd die de parse-tree omzet in een Abstract Syntax Tree (AST).

Hiervoor heb ik een mix van postorder en preorder gebruikt. (enter en exit).

* **Gegenereerde broncode:** De door ANTLR gegenereerde klassen bevinden zich in: `startcode/target/generated-sources/antlr4`.
* **PA00 (Stack):** Ik heb zinvol gebruikgemaakt van mijn eigen generieke stack-implementatie (`HANStack<ASTNode>`) voor het opbouwen van de AST.

| ID   | Omschrijving                         | Prio | Punten | Behaald? |
|------|--------------------------------------|------|--------|----------|
| PA01 | Eenvoudige opmaak parseren (level 0) | Must | 10     | Ja       |
| PA02 | Assignments en variabelen (level 1)  | Must | 10     | Ja       |
| PA03 | Rekenregels (+, -, *) (level 2)      | Must | 10     | Ja       |
| PA04 | If/else-statements (level 3)         | Must | 10     | Ja       |

---

## 2. Checker (Semantische check)
Ik heb alle **Must** en **Should** eisen geïmplementeerd. 
Om de code overzichtelijk en onderhoudbaar te houden, heb ik de logica 
van de checker opgesplitst in specifieke klasses (Delegation).
Ook heb ik hiervoor de benodigde datastructuren zelf geïmplementeerd 
om aan de eisen van het vak Algoritmen & Datastructuren te voldoen. (Stack & Linkedlist)

**Projectstructuur Checker:**
* `Checker` (De core-logica en aansturing)
* `CheckExpression` (Validatie van expressies)
* `CheckOperation` (Type-checking voor rekenkundige en logische operatoren)
* `CheckVariable` (Validatie van variabele-definities en type-vastheid)
* `CheckIfClause` (Scope- en conditiecontrole)

**Zelfgeproduceerde Datastructuren:**
* `HANStack` & `HANLinkedList` (Generieke implementaties)
* `SymbolTable` (Voor het beheren van scopes en variabele-types per scope)

| ID   | Omschrijving                                       | Prio   | Punten | Behaald? |
|------|----------------------------------------------------|--------|--------|----------|
| CH01 | Gebruik van ongedefinieerde variabelen             | Should | 5      | Ja       |
| CH02 | Types bij operaties (gelijke types / scalar bij *) | Should | 5      | Ja       |
| CH03 | Geen kleuren in rekenkundige operaties             | Should | 5      | Ja       |
| CH04 | Type-check bij declaraties (width vs color)        | Should | 5      | Ja       |
| CH05 | If-conditie moet Boolean zijn                      | Should | 5      | Ja       |
| CH06 | Variabelen enkel binnen eigen scope gebruiken      | Must   | 5      | Ja       |

---

## 3. Transformatie (Evaluator)
De `Evaluator.java` is uitgebreid om expressies en conditionele logica te verwerken.
De AST wordt ge**transformeerd** naar een vereenvoudigde vorm waarbij
alle variabelen zijn ingevuld en if-statements zijn verminderd tot hun inhoud.

| ID   | Omschrijving                               | Prio | Punten | Behaald? |
|------|--------------------------------------------|------|--------|----------|
| TR01 | Evalueer expressies naar Literals          | Must | 10     | Ja       |
| TR02 | Evalueer if/else (verwijderen van clauses) | Must | 10     | Ja       |

---

## 4. Generator
De generator zet de getransformeerde AST om naar valide CSS2-broncode.
* **GE01:** De output is volledig CSS2-compliant en direct bruikbaar.
* **GE02:** De CSS wordt gegenereerd met de vereiste inspringing van **twee spaties** per scopeniveau voor optimale leesbaarheid.

| ID   | Omschrijving                              | Prio | Punten | Behaald? |
|------|-------------------------------------------|------|--------|----------|
| GE01 | AST naar CSS2-compliant string            | Must | 5      | Ja       |
| GE02 | Twee spaties inspringing per scope niveau | Must | 5      | Ja       |

---

## 5. Eigen uitbreiding (extra)
Voor de vrije uitbreiding heb ik gekozen voor het implementeren van
**Booleaanse expressies en logische operatoren**. 
Dit stelt de gebruiker in staat om sterke logica te gebruiken binnen de opmaakregels.

**Toegevoegde functionaliteiten:**
* **Vergelijkingsoperatoren:** Ondersteuning voor `>` (Greater Than), `<` (Less Than) en `==` (Equal To).
* **Logische operatoren:** Volledige ondersteuning voor `&&` (AND) en `||` (OR).
* **Strict Typing:** Ik heb de checker zo ingesteld dat variabelen een vast type behouden; (JAVA).

**Testen:**
De extra functionaliteiten zijn uitvoerig getest met het bestand `src/main/resources/extra.icss`. 
Hierin worden combinaties van vergelijkingen en logische poorten gebruikt om de robuustheid van de Evaluator aan te tonen.

---

## 6. Eigen mening enzo (klein)

Ik heb het meeste moeite gehad met LV3 bij de Parser stap (Listern) 
om dit op te lossen heb ik mijn g4 meerderen keren moeten aanpassen de checker stap vond ik veel leuker
en de transformatie fase was minder moeilijk de generatie fase was een eitje
als ik meer tijd had, zou ik comments toevoegen dat lijkt mij fijn om te hebben.