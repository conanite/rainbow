package rainbow.util;

import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.interpreter.Assignment;
import rainbow.vm.interpreter.IfClause;
import rainbow.vm.interpreter.Invocation;
import rainbow.vm.interpreter.LastAssignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Decompiler {
  private static final Symbol LET = Symbol.mkSym("let");
  private static final Symbol SELF = Symbol.mkSym("self");
  private static final Symbol IT = Symbol.mkSym("it");
  private static final Symbol AFN = Symbol.mkSym("afn");
  private static final Symbol DO = Symbol.mkSym("do");
  private static final Symbol WITH = Symbol.mkSym("with");
  private static final Symbol AIF = Symbol.mkSym("aif");

  public static ArcObject decompile(Invocation o) {
    if (o.parts.car() instanceof InterpretedFunction) {
      return decompile(withLetDo(o.parts));
    } else {
      return o.parts;
    }
  }

  private static ArcObject decompile(Pair pair) {
    if (pair.car() == LET) {
      return decompileLet(pair.cdr());
    } else {
      return pair;
    }
  }

  private static ArcObject decompileLet(ArcObject args) {
    if (args.car() == IT) {
      ArcObject arg = args.cdr().car();
      ArcObject rest = args.cdr().cdr();
      if (rest.cdr() instanceof Nil && rest.car() instanceof IfClause) {
        IfClause aif = (IfClause) rest.car();
        if (aif.isAifIf()) {
          return makeAif(aif, arg);
        }
      }
    } else if (args.car() == SELF && args.cdr().car() instanceof Nil) {
      ArcObject rest = args.cdr().cdr();
      if (rest.car() instanceof Assignment && rest.cdr() instanceof Nil) {
        Assignment a = (Assignment) rest.car();
        if (a.assignment.assignsTo("self") && a.assignment.expression instanceof InterpretedFunction && a.assignment instanceof LastAssignment) {
          return makeAfn((InterpretedFunction)a.assignment.expression);
        }
      }
    }

    return new Pair(LET, args);
  }

  private static Pair makeAfn(InterpretedFunction ifn) {
    List afn = new ArrayList();
    afn.add(AFN);
    afn.add(ifn.parameterList());
    afn.addAll(Arrays.asList(ifn.body));
    return Pair.buildFrom(afn);
  }

  private static Pair withLetDo(Pair parts) {
    InterpretedFunction ifn = (InterpretedFunction) parts.car();
    ArcObject args = parts.cdr();
    ArcObject params = ifn.parameterList();
    if (args.len() == params.len()) {
      if (args.len() == 0L) {
        return makeDo(ifn);
      } else if (args.len() == 1L) {
        return makeLet(ifn, args, params);
      } else {
        return makeWith(ifn, args, params);
      }
    } else {
      return parts;
    }
  }

  private static Pair makeDo(InterpretedFunction ifn) {
    List doform = new ArrayList();
    doform.add(DO);
    doform.addAll(Arrays.asList(ifn.body));
    return Pair.buildFrom(doform);
  }

  private static Pair makeWith(InterpretedFunction ifn, ArcObject args, ArcObject params) {
    List with = new ArrayList();
    with.add(WITH);
    List w = new ArrayList();
    while (!(params instanceof Nil)) {
      w.add(params.car());
      w.add(args.car());
      params = params.cdr();
      args = args.cdr();
    }
    with.add(Pair.buildFrom(w));
    with.addAll(Arrays.asList(ifn.body));
    return Pair.buildFrom(with);
  }

  private static Pair makeLet(InterpretedFunction ifn, ArcObject args, ArcObject params) {
    List let = new ArrayList();
    let.add(LET);
    let.add(params.car());
    let.add(args.car());
    let.addAll(Arrays.asList(ifn.body));
    return Pair.buildFrom(let);
  }

  private static Pair makeAif(IfClause ifClause, ArcObject arg) {
    List aif = new ArrayList();
    aif.add(AIF);
    aif.add(arg);
    aif.add(ifClause.thenExpression());
    aif.add(ifClause.elseExpression());
    return Pair.buildFrom(aif);
  }
}
