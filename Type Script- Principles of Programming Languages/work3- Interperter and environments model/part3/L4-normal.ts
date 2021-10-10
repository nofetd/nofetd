// ========================================================
// L4 normal eval
import { Sexp } from "s-expression";
import { map } from "ramda";
import { CExp, Exp, IfExp, Program, parseL4Exp, LetExp } from "./L4-ast";
import { isAppExp, isBoolExp, isCExp, isDefineExp, isIfExp, isLitExp, isNumExp,
         isPrimOp, isProcExp, isStrExp, isVarRef } from "./L4-ast";
import { applyEnv, makeEmptyEnv, Env } from './L4-env-normal';
import { applyPrimitive } from "./evalPrimitive";
import { isClosure, makeClosure, Value } from "./L4-value";
import { first, rest, isEmpty } from '../shared/list';
import { Result, makeOk, makeFailure, bind, mapResult } from "../shared/result";
import { parse as p } from "../shared/parser";
import { makeExtEnv } from "../part3/L4-env-normal";
import { ExtEnv, isExtEnv } from "./L4-env-normal";


export const L4normalEval = (exp: CExp, env: Env): Result<Value> =>
    isBoolExp(exp) ? makeOk(exp.val) :
    isNumExp(exp) ? makeOk(exp.val) :
    isStrExp(exp) ? makeOk(exp.val) :
    isPrimOp(exp) ? makeOk(exp) :
    isLitExp(exp) ? makeOk(exp.val) :
    isVarRef(exp) ? bind(applyEnv(env, exp.var), (exp:CExp)=> L4normalEval(exp,env)) :
    isIfExp(exp) ? evalIf(exp, env) :
    isProcExp(exp) ? makeOk(makeClosure(exp.args,exp.body,env)) :
    isAppExp(exp) ? bind(L4normalEval(exp.rator, env), proc => L4normalApplyProc(proc, exp.rands, env)) :
    makeFailure(`Bad ast: ${exp}`);

export const evalNormalProgram = (program: Program): Result<Value> =>
    evalExps(program.exps, makeEmptyEnv());

export const evalNormalParse = (s: string): Result<Value> =>
    bind(p(s),
         (parsed: Sexp) => bind(parseL4Exp(parsed),
                                (exp: Exp) => evalExps([exp], makeEmptyEnv())));


export const evalIf = (exp: IfExp, env: Env): Result<Value> =>
    bind(L4normalEval(exp.test, env),
         test => isTrueValue(test) ? L4normalEval(exp.then, env) : L4normalEval(exp.alt, env));

export const isTrueValue = (x: Value): boolean =>
    ! (x === false);

export const evalExps = (exps: Exp[], env: Env): Result<Value> =>
    isEmpty(exps) ? makeFailure("Empty!!"):
    isDefineExp(first(exps)) ? evalDefineExps(first(exps), rest(exps), env) :
    evalCExps(first(exps), rest(exps), env);

export const evalCExps = (exp1: Exp, exps: Exp[], env: Env): Result<Value> =>
    isCExp(exp1) && isEmpty(exps) ? L4normalEval(exp1, env) :
    isCExp(exp1) ? bind(L4normalEval(exp1, env), _ => evalExps(exps, env)) :
    makeFailure("Never");


export const evalDefineExps = (def: Exp, exps: Exp[], env: Env): Result<Value> =>
    isDefineExp(def) ? evalExps(exps, makeExtEnv([def.var.var], [def.val], env)) :
    makeFailure("Unexpected " + def);

export const L4normalApplyProc = (proc: Value, args: CExp[], env: Env): Result<Value> => {
    if (isPrimOp(proc)) {
        const argVals: Result<Value[]> = mapResult((arg) => L4normalEval(arg, env), args);
        return bind(argVals, (args: Value[]) => applyPrimitive(proc, args));
    } else if (isClosure(proc)) {

        const vars = map((p) => p.var, proc.params);
        const body = proc.body;
        return evalExps(body, makeExtEnv(vars, args, proc.env));
    } else {
        return makeFailure(`Bad proc applied ${proc}`);
    }
};


                               
