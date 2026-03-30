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

stylesheet: (cssrules | llcsrules)* EOF;

ruleBody: (declare | ifexpr | llcsrules)*;

cssrules: selecty OPEN_BRACE ruleBody CLOSE_BRACE;

llcsrules: CAPITAL_IDENT ASSIGNMENT_OPERATOR expression SEMICOLON;

declare: prop COLON expression SEMICOLON;

ifexpr: ifblock elseblock?;
ifblock: IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE OPEN_BRACE ruleBody CLOSE_BRACE;
elseblock: ELSE OPEN_BRACE ruleBody CLOSE_BRACE;

expression
  : literal
  | variable
  | '(' expression ')'
  | NOT expression
  | expression MUL expression
  | expression (PLUS | MIN) expression
  | expression (GREATER_THAN | LESS_THAN | EQUAL_TO | NOT_EQUAL_TO | GREATER_THAN_EQUAL | LESS_THAN_EQUAL_TO) expression
  | expression (AND | OR) expression
  ;

literal
  : COLOR
  | PIXELSIZE
  | PERCENTAGE
  | SCALAR
  | TRUE
  | FALSE
  ;

variable: CAPITAL_IDENT;

selecty: LOWER_IDENT | ID_IDENT | CLASS_IDENT;

prop: LOWER_IDENT;

