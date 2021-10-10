
import { CompoundExp, ForExp, AppExp, Exp, Program, isForExp, parseL21Exp, parseL21Program, IfExp, makeDefineExp, parseL21CExp, makeForExp, isCExp } from "./L21-ast";
import { Result, makeOk, mapResult, isOk, bind, safe2, makeFailure, safe3 } from "../imp/result";
import { makeProgram, makeIfExp,makeAppExp, makeNumExp, CExp, isAtomicExp, makeVarDecl, makeVarRef, makeProcExp, isExp, parseAppExp, isProgram, ProcExp, isAppExp, isProcExp, isIfExp, isNumExp } from "./L21-ast";
import {  isCompoundExp, isDefineExp, AtomicExp, DefineExp } from "../imp/L2-ast";
import { map, and, any, is } from "ramda";
import { isIdentifier } from "../imp/type-predicates";


/*
Purpose: Applies a syntactic transformation from a ForExp to an equivalent AppExp
Signature: for2app(exp)
Type: [ForExp->AppExp]
*/
export const for2app = (exp: ForExp): AppExp =>
{
    let arr:Array<AppExp> = [];
    if(isNumExp(exp.start) && isNumExp(exp.end))
    {
        for(let i=exp.start.val; i<=exp.end.val; i++)
        {
            if(isForExp(exp.body))
            {
                const p =  makeProcExp([exp.loop],[for2app(exp.body)]);
                const n = makeNumExp(i);
                const toAdd = makeAppExp(p,[n]);
                arr.push(toAdd);    //change
            }
            else
            {
                const p =  makeProcExp([exp.loop],[exp.body]);
                const n = makeNumExp(i);
                const toAdd = makeAppExp(p,[n]);
                arr.push(toAdd);    //change 
            }
            
            //need to add to the array!!
        }
    }
    return makeAppExp(makeVarRef("lambda()"),arr);
}


/*
Purpose: Gets an L21 AST and returns an equivalent L2 AST
Signature: L21ToL2(exp)
Type: [Exp|Program -> Result<Exp|Program>]
*/

export const L21ToL2 = (exp: Exp | Program): Result<Exp | Program> =>
{
    return isExp(exp) ? ConvertExp(exp) : ConvertProgram(exp);

    /*
    return isCompoundExp(exp) ? ConvertCompound(exp) :
    isAtomicExp(exp) ? ConvertAtomic(exp):
    isDefineExp(exp) ? ConvertDefine(exp):
    isProgram(exp) ? ConvertProgram(exp):
    makeFailure("Error!");
    */

}

const ConvertExp = (exp:Exp):Result<Exp> =>
{
    return isCExp(exp) ? ConvertExp(exp) :
    isDefineExp(exp) ? ConvertDefine(exp):
    makeFailure("Error!");
}

const ConvertCExp = (exp:CExp): Result<Exp> =>
{
    return isCompoundExp(exp) ? ConvertCompound(exp) :
    isAtomicExp(exp) ? ConvertAtomic(exp):
    makeFailure("Error!");
}

const ConvertProgram=(exp:Program):Result<Program> =>
{
    return bind(mapResult(ConvertExp, exp.exps),(exps: Exp[]) => makeOk(makeProgram(exps)));
}

const ConvertCompound=(exp:CompoundExp):Result<Exp> =>
{
    return isAppExp(exp)? ConvertAppExp(exp) :
    isForExp(exp)? ConvertForExp(exp) :
    isIfExp(exp)? ConvertIfExp(exp) :
    ConvertProcExp(exp);
    
}

const ConvertAtomic=(exp:AtomicExp):Result<Exp>=>
{
    return makeOk(exp);
}

const ConvertDefine=(exp:DefineExp):Result<Exp> =>
{
    return !isIdentifier(exp.var) ? makeFailure("First arg of define must be an identifier") :
    bind(ConvertCExp(exp.val),
         (value: CExp) => makeOk(makeDefineExp(makeVarDecl(exp.var.var), value)));
}

const ConvertAppExp=(exp:AppExp):Result<Exp> =>
{
    const safe=((rands: CExp[]) => makeOk(makeAppExp(exp.rator,rands)));
    return bind(mapResult(ConvertExp, exp.rands), safe);
}

const ConvertForExp=(exp:ForExp):Result<Exp> =>
{ 
    return makeOk(for2app(exp));
}

const ConvertIfExp=(exp:IfExp):Result<Exp> =>
{
    let params = [exp.test,exp.then,exp.alt];
    return bind(mapResult(ConvertExp, params),
         (cexps: CExp[]) => makeOk(makeIfExp(cexps[0], cexps[1], cexps[2])));
}

const ConvertProcExp=(exp:ProcExp):Result<Exp> =>
{
    return bind(mapResult(ConvertExp, exp.body),
        (cexps: CExp[]) => makeOk(makeProcExp(exp.args, cexps)));
}