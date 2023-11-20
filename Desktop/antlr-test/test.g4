grammar UTL;


// tokens
// keywords
FOR: 'for';
WHILE: 'while';
ELSE: 'else';
IF: 'if';
CONTINUE: 'continue';
RETURN: 'return';
BREAK: 'break';
CASE: 'case';
SWITCH: 'switch';
SCHEDULE: 'schedule';
PARALLEL: 'parallel';
DEFAULT: 'default';
THROW: 'throw';
TRY: 'try';
CATCH: 'catch';

// special functions
MAIN: 'Main';
REFRESHRATE: 'RefreshRate';
GET_CANDLE: 'GetCandle';
CONNECT: 'Connect';
PRINT: 'Print';
PREORDER: 'Preorder';
OBSERVE: 'Observe';
ONINIT: 'OnInit';
ONSTART: 'OnStart';
TERMINATE: 'Terminate';

// defined variables
BID: 'Bid';
ASK: 'Ask';
VOLUME: 'Volume';
LOW: 'Low';
HIGH: 'High';
CLOSE: 'Close';
OPEN: 'Open';
TIME: 'Time';
DIGITS: 'Digits';

// data types
INT: 'int';
FLOAT: 'float';
STRING: 'string';
DOUBLE: 'double';
STATIC: 'static';
SHARED: 'shared';
BOOL: 'bool';
EXCEPTION: 'Exception';
TRADE: 'Trade';
TEXT: 'Text';
ORDER: 'Order';
CANDLE: 'Candle';

// type values
VOID_VAL: 'void';
INT_VAL: [1-9][0-9]*;
ZERO: [0];
FLOAT_VAL: INT_VAL '.' [0-9]+ | ZERO '.' [0-9]*;
BOOLEAN_VAL: 'true' | 'false';
NULL_VAL: 'null';
BUY: 'BUY';
SELL: 'SELL';

// operators
LPAR: '(';
RPAR: ')';

METHOD_ID: '.';

LBRCK: '[';
RBRCK: ']';

INC: '++';
DEC: '--';
NOT: '!';
BNOT: '~';

MULT: '*';
DIV: '/';
MOD: '%';

SUM: '+';
MINUS: '-';

SL: '<<';
SR: '>>';

GTEQ: '>=';
LTEQ: '=<';
GT: '>';
LT: '<';
EQ: '==';
NOTEQ: '!=';

AND: '&&';
OR: '||';

BXOR: '^';
BOR: '|';
BAND: '&';

ASSIGN: '=';
COMMA: ',';

// symbols
LBRACE: '{';
RBRACE: '}';
COLON: ':';
SEMICOLON: ';';
QUESTION: '?';

// names
IDENTIFIER: [a-z_][a-zA-Z0-9_]*;
SINGLECOMM: '#' ~[\r\n]*;
MULTICOMM: '/*' .*? '*/';
WHITESPACE: [ \n\t\r]+;
