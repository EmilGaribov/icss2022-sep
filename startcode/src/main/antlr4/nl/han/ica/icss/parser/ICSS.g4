grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';

//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: ---

// een groote regel met die alle kleinere regels aanroept
// je maakt de method maar dan met de mogelijke combies inplaats van hardcode
// zoals a {14} is het zelfde als de rule: selector ... close_brace; bijvoorbeeld
// dingen die altijd vast zijn zoals haakjes kan je direct (Paars) erin doen maar dingen die
// meerdere opties kunnen hebben plaats je in een regel (Geel) en dan roep je deze aan.
// dit kan dus ook een regel (method) achting iets aanroepen zoals declaration die roep je aan met een * aan het einde

stylesheet:statement* EOF; // soort van main zonder dat het hier word geroepen gebeurt het niet.

statement: cssrule
         | llcsrule;

cssrule: selector OPEN_BRACE declaration* CLOSE_BRACE;
llcsrule: CAPITAL_IDENT ASSIGNMENT_OPERATOR value SEMICOLON;


calculate: termius ((PLUS | MIN) termius)*;

termius: facto ((MUL)facto)* ;

facto: literal
        | CAPITAL_IDENT
        | '(' calculate ')';

literal: COLOR
       | PIXELSIZE
       | PERCENTAGE
       | SCALAR;

selector: LOWER_IDENT
        | ID_IDENT
        | CLASS_IDENT;

declaration: property COLON value SEMICOLON;

property: LOWER_IDENT;

value: COLOR
     | PIXELSIZE
     | PERCENTAGE
     | SCALAR
     | CAPITAL_IDENT
     | TRUE
     | FALSE
     | calculate;

