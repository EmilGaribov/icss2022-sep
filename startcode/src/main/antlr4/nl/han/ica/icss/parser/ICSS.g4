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

//Extra emil werk
GREATER_THAN : '>';
LESS_THAN : '<';
GREATER_THAN_EQUAL : '>=';
LESS_THAN_EQUAL_TO : '<=';
EQUAL_TO  : '==';
NOT_EQUAL_TO : '!=';

AND : '&&';
OR  : '||';
NOT : '!';

//--- PARSER: ---
stylesheet:statements* EOF;

statements: cssrules
         | llcsrules;

cssrules: selecty OPEN_BRACE blockofcontent* CLOSE_BRACE;
llcsrules: CAPITAL_IDENT ASSIGNMENT_OPERATOR value SEMICOLON;

// --- everthing below me is a helper for a rule above.

declare: prop COLON value SEMICOLON;

blockofcontent: declare
              | ifexpr;

ifexpr: IF BOX_BRACKET_OPEN expr BOX_BRACKET_CLOSE
OPEN_BRACE value CLOSE_BRACE
(ELSE OPEN_BRACE value CLOSE_BRACE)?;

expr
  : comparison
  | expr AND expr
  | expr OR expr
  | NOT expr
  | atom;

 atom
  : CAPITAL_IDENT
  | TRUE
  | FALSE
  | calcus
  | '(' expr ')';

comparison: calcus compOp calcus
          | atom compOp atom;

 compOp
   : GREATER_THAN
   | LESS_THAN
   | GREATER_THAN_EQUAL
   | LESS_THAN_EQUAL_TO
   | EQUAL_TO
   | NOT_EQUAL_TO;

calcus: termius ((PLUS | MIN) termius)*;

termius: facto ((MUL)facto)* ;

facto: lite
        | CAPITAL_IDENT
        | '(' calcus ')';

lite: COLOR
       | PIXELSIZE
       | PERCENTAGE
       | SCALAR;

selecty: LOWER_IDENT
        | ID_IDENT
        | CLASS_IDENT;

prop: LOWER_IDENT;

value: COLOR
     | PIXELSIZE
     | PERCENTAGE
     | SCALAR
     | CAPITAL_IDENT
     | TRUE
     | FALSE
     | calcus;
// TODO: zorg ervoor dat dit voor vervolg levels wel wordt meegenomen, maar misschien ergens anders.

//     | ifexpr;


