; borrowed from anarki. use (defcall (type params) blah blah) to create
; a handler for type in functional position
(assign call* (table))
(sref call* ref 'cons)
(sref call* ref 'string)
(sref call* ref 'table)
