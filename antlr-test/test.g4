grammar UT2L;

ut2l: (function | comment | variable)* main comment*;

comment:  (SINGLECOMM | MULTICOMM);

variable: (global_var_type | regular_var_type) IDENTIFIER;

global_var_type: (STATIC | SHARED) regular_var_type;

regular_var_type:   INT | FLOAT | STRING | DOUBLE | STATIC |
                    SHARED | BOOL | VOID |
                    ORDER | CANDLE | EXCEPTION;

function: func_dec exep? func_body;

func_dec:   regular_var_type 
            LPAR (variable)* RPAR;

exep: THROW EXCEPTION;

func_body: (variable | comment | )*;

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
ORDER: 'Order';
CANDLE: 'Candle';
EXCEPTION: 'Exception';
VOID: 'void';

// parameters
TRADE: 'Trade';
TEXT: 'Text';

// type values
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
