# JSONParser
JSON Parser


This is an extremely light-weight JSON parser. The parser omits messages to a builder and that 
builder can be set at compile or runtime. The purpose of this parser is not to go to a specific 
domain. Instead it allows the end user to make that implementation decision. I.E. We stop at 
the intermediate structure.   

Reasoning Behind stopping short:
1.	The rules for the destination domain are often too hard to encode uniformly
2.	Each destination domain can have different data structure requirements
