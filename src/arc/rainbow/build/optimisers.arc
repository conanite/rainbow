
(assign fn-types '(bound stack free other)
        arg-types '(bound stack free literal quote other))

(def optimiser-generator ()
  (make-directory package-dir)
  (each f fn-types
    (write-class faster-operate-method f))
  (each f fn-types
    (each a1 arg-types
      (write-class faster-operate-method f a1)))
  (each f fn-types
    (each a1 arg-types
      (each a2 arg-types
        (write-class faster-operate-method f a1 a2))))
  (each f fn-types
    (each a1 arg-types
      (each a2 arg-types
        (each a3 arg-types
          (write-class faster-operate-method f a1 a2 a3))))))

(def write-class (operator . types)
  (prn "generating #((classname types))")
  (w/stdout (outfile "#(package-dir)#((classname types)).java")
    (generate-class operator types)))

(def generate-class (operator types)
  (let args (map list (range 0 len.types) types)
    (prn "package " opt-package ";")
    (prn)
    (prn imports)
    (prn)
    (prn "public class " (classname types) " extends Instruction implements Invoke {")
    (fields args)
    (prn)
    (constructor args)
    (prn)
    (operator args)
    (prn)
    (invokee args)
    (prn)
    (simple-to-string args)
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

(def faster-operate-method (args)
  (prn "  public void operate(VM vm) {")
  (let (n fn-type) car.args
    (if (is fn-type 'other)
        (prn "    ArcObject fn = vm.popA();"))
    (each (n type) (rev cdr.args)
      (if (is type 'other)
          (prn "    ArcObject arg#(n) = vm.popA();")))

    (let arg-list (apply string
                         (map (fn ((n type)) ", arg#(n)#((type-getter type))") cdr.args))
      (prn "    fn" (type-getter fn-type) ".invokef(vm#(arg-list));")))
  (prn "  }"))

(def invokee (args)
  (prn "  public ArcObject getInvokee(VM vm) {")
  (let (n fn-type) car.args
    (if (is fn-type 'other)
        (prn "    return vm.peekA();")
        (is fn-type 'bound)
        (prn "    return fn.interpret(vm.lc());")
        (is fn-type 'stack)
        (prn "    return fn.get(vm);")
        (is fn-type 'free)
        (prn "    return fn.value();")))
  (prn "  }"))

(def simple-to-string (args)
  (prn "  public String toString() {")
  (let (n fn-type) car.args
    (prn "    return \"(invocation:\" + " (simple-to-stringify "fn" fn-type)))
  (each (n type) cdr.args
    (prn "        + " (simple-to-stringify "arg#(n)" type))
  )
  (prn "        + \")\";")
  (prn "  }")
)

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
    stack    "\"[stack:\" + #(name)                                  + \"]\""
    free     "\"[free:\"  + #(name) + \"->\" + #(name).value()       + \"]\""
    literal  "\"[lit:\"   + #(name)                                  + \"]\""
    quote    "\"[quote:\" + #(name)                                  + \"]\""
    other    "\"[other]\""))

(def simple-to-stringify (name type)
  (case type
    bound    "\"[bound:\" + #(name)                            + \"]\""
    stack    "\"[stack:\" + #(name)                            + \"]\""
    free     "\"[free:\"  + #(name) + \"->\" + #(name).value() + \"]\""
    literal  "\"[lit:\"   + #(name)                            + \"]\""
    quote    "\"[quote:\" + #(name)                            + \"]\""
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
    stack   "(StackSymbol)"
    free    "(Symbol)"
    literal ""
    quote   ""
            (err "no cast for #(type)")))

(def constructor-getter (type)
  (case type
    bound   "(BoundSymbol)args.car()"
    stack   "(StackSymbol)args.car()"
    free    "(Symbol)args.car()"
    literal "args.car()"
    quote   "((Quotation)args.car()).quoted()"
            (err "no cast for #(type)")))

(def java-type-for (type)
  (case type
    bound   "BoundSymbol"
    stack   "StackSymbol"
    free    "Symbol"
    literal "ArcObject"
    quote   "ArcObject"
            (err "no java-type for #(type)")))

(def type-getter (type)
  (case type
    bound   ".interpret(vm.lc())"
    stack   ".get(vm)"
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
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.StackSymbol;
import rainbow.vm.instructions.invoke.Invoke;"
)
