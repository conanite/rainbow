;(constructor '((0 bound) (1 free) (2 literal) (3 quote) (4 other)))
;(operate-method '((0 bound) (1 free) (2 literal) (3 quote) (4 other)))
;(to-string '((0 bound) (1 free) (2 literal) (3 quote) (4 other)))
;(classname (map cadr '((0 bound) (1 free) (2 literal) (3 quote) (4 other))))
;(generate-class '(bound free literal quote other))

(assign fn-types '(bound free other)
        arg-types '(bound free literal quote other))

(def optimiser-generator ()
  (make-directory package-dir)
  (each f fn-types
    (write-class f))
  (each f fn-types
    (each a1 arg-types
      (write-class f a1)))
  (each f fn-types
    (each a1 arg-types
      (each a2 arg-types
        (write-class f a1 a2))))
  (each f fn-types
    (each a1 arg-types
      (each a2 arg-types
        (each a3 arg-types
          (write-class f a1 a2 a3))))))

(def write-class types
  (prn "generating #((classname types))")
  (w/stdout (outfile "#(package-dir)#((classname types)).java")
    (generate-class types)))

(def generate-class (types)
  (let args (map list (range 0 len.types) types)
    (prn "package " opt-package ";")
    (prn)
    (prn imports)
    (prn)
    (prn "public class " (classname types) " extends Instruction {")
    (fields args)
    (prn)
    (constructor args)
    (prn)
    (operate-method args)
    (prn)
    (to-string args)
    (prn "}")))

(mac not-other (var . body)
  `(unless (is ,var 'other) ,@body))

(def fields (args)
  (let (n fn-type) car.args
    (not-other fn-type (declare-field "fn" fn-type))
    (each (n type) cdr.args
      (not-other type (declare-field "arg#(n)" type)))))

(def constructor (args)
  (prn "  public " (classname (map cadr args)) "(Pair args) {")
  (let (n fn-type) car.args
    (field-initialiser "fn" fn-type))
  (each (n type) cdr.args
    (prn "    args = (Pair)args.cdr();")
    (field-initialiser "arg#(n)" type)
  )
  (prn "  }"))

(def operate-method (args)
  (prn "  public void operate(VM vm) {")
  (let (n fn-type) car.args
    (if (is fn-type 'other)
        (prn "    ArcObject fn = vm.popA();"))
    (each (n type) (rev cdr.args)
      (if (is type 'other)
          (prn "    ArcObject arg#(n) = vm.popA();")))

    (let arg-list (apply string
                         (map (fn ((n type))
                                  "new Pair(arg#(n)#((type-getter type)), ")
                              cdr.args))
      (prn "    fn" (type-getter fn-type) ".invoke(vm, " arg-list "NIL" (newstring (- len.args 1) #\)) ");")))
  (prn "  }"))

(def to-string (args)
  (prn "  public String toString(LexicalClosure lc) {")
  (let (n fn-type) car.args
    (prn "    return \"(invocation:\" + " (to-stringify "fn" fn-type)))
  (each (n type) cdr.args
    (prn "        + " (to-stringify "arg#(n)" type))
  )
  (prn "        + \")\";")
  (prn "  }")
)

(def to-stringify (name type)
  (case type
    bound    "\"[bound:\" + #(name) + \"->\" + #(name).interpret(lc) + \"]\""
    free     "\"[free:\"  + #(name) + \"->\" + #(name).value()       + \"]\""
    literal  "\"[lit:\"   + #(name)                                  + \"]\""
    quote    "\"[quote:\" + #(name)                                  + \"]\""
    other    "\"[other]\""))

(def classname (types)
  "Invoke_#((apply string (intersperse "_" types)))")

(def field-initialiser (name type)
  (not-other type
    (prn "    this.#(name) = #((constructor-getter type));")))

(def declare-field (name type)
  (prn "  #((java-type-for type)) #(name);"))

(def cast (type)
  (case type
    bound   "(BoundSymbol)"
    free    "(Symbol)"
    literal ""
    quote   ""
            (err "no cast for #(type)")))

(def constructor-getter (type)
  (case type
    bound   "(BoundSymbol)args.car()"
    free    "(Symbol)args.car()"
    literal "args.car()"
    quote   "((Quotation)args.car()).quoted()"
            (err "no cast for #(type)")))

(def java-type-for (type)
  (case type
    bound   "BoundSymbol"
    free    "Symbol"
    literal "ArcObject"
    quote   "ArcObject"
            (err "no java-type for #(type)")))

(def type-getter (type)
  (case type
    bound   ".interpret(vm.lc())"
    free    ".value()"
    literal ""
    quote   ""
    other   ""))

(= opt-package "rainbow.vm.instructions.invoke.optimise"
   package-dir "../java/rainbow/vm/instructions/invoke/optimise/"
   imports
   "import rainbow.types.ArcObject;
import rainbow.LexicalClosure;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.Quotation;
import rainbow.vm.interpreter.BoundSymbol;"
)
