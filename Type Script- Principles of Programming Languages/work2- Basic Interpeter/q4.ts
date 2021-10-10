import { Exp, Program, isProgram, isExp, isProcExp, isCompoundExp, isAtomicExp, isAppExp, AppExp, ProcExp, IfExp, CompoundExp, AtomicExp, isNumExp, isBoolExp, isPrimOp, NumExp, BoolExp, PrimOp, VarRef, isDefineExp, DefineExp, isVarRef} from '../imp/L2-ast';
import { Result, makeOk, bind, mapResult, isOk, safe3, safe2, makeFailure } from '../imp/result';
import { unparseL2 } from '../imp/L2-unparse';
import { concat, map, reduce } from 'ramda';
import { isPrimitiveOp } from '../imp/L2-ast';

/*
Purpose: Gets an L2 AST and returns a string of the equivalent JavaScript program
Signature: l2ToJS(exp)
Type: [Exp|Program -> Result<string>]
*/
export const l2ToJS = (exp: Exp | Program): Result<string> => 
{
    return isCompoundExp(exp) ? UnparseCompound(exp) :
    isAtomicExp(exp) ? UnparseAtomic(exp):
    isDefineExp(exp) ? UnparseDefine(exp):
    UnparseProgram(exp);
}

const UnparseProgram=(exp:Exp |Program):Result<string> =>
{
    const safe = (exps: string[])=>
    {
        let str1 = concat("console.log(", exps[exps.length-1]);
        exps[exps.length-1] = concat(str1,");");
        return makeOk(exps.join(";\n"));
    }
    return isProgram(exp) ? bind(mapResult(l2ToJS, exp.exps), safe):
    makeFailure("The expression is not a program");
}

const UnparseCompound=(exp:CompoundExp):Result<string> =>
{
    return isAppExp(exp) ? UnparseAppExp(exp) :
    isProcExp(exp) ? UnparseProcExp(exp) : UnparseIfExp(exp);
}

const UnparseAtomic=(exp:AtomicExp):Result<string>=>
{
    return isNumExp(exp) ? UnparseNumExp(exp) :
    isBoolExp(exp) ? UnparseBoolExp(exp) : 
    isPrimOp(exp) ? UnparsePrimOpExp(exp) : UnparseVarRef(exp);
}

const UnparseDefine=(exp:DefineExp):Result<string> =>
{
    return bind(l2ToJS(exp.val), (val: string) => makeOk(`const ${exp.var.var} = ${val}`));
}

const UnparseAppExp=(exp:AppExp):Result<string> =>
{
    if(isPrimOp(exp.rator) && exp.rator.op==="=")
    {
        return safe2((rator: string, rands: string[]) => makeOk(`(${rands.join(" "+rator+rator+rator+" ")})`))
            (l2ToJS(exp.rator), mapResult(l2ToJS, exp.rands));
    }
    else if((isProcExp(exp.rator)) || (isVarRef(exp.rator)))
        {
            return safe2((rator: string, rands: string[]) => makeOk(`${rator}(${rands.join(",")})`))
                (l2ToJS(exp.rator), mapResult(l2ToJS, exp.rands));
        }
    else if(isPrimOp(exp.rator) && exp.rator.op==="not")
    {
        return safe2((rator: string, rands: string[]) => makeOk(`(!${rands.join(",")})`))
            (l2ToJS(exp.rator), mapResult(l2ToJS, exp.rands));
    }
    
    else if(isPrimOp(exp.rator) && exp.rator.op==="and")
    {
        return safe2((rator: string, rands: string[]) => makeOk(`(&${rands.join(",")})`))
            (l2ToJS(exp.rator), mapResult(l2ToJS, exp.rands));
    }
    else if(isPrimOp(exp.rator) && exp.rator.op==="or")
    {
        return safe2((rator: string, rands: string[]) => makeOk(`(|${rands.join(",")})`))
            (l2ToJS(exp.rator), mapResult(l2ToJS, exp.rands));
    }
    
    else return safe2((rator: string, rands: string[]) => makeOk(`(${rands.join(" "+rator+" ")})`))
            (l2ToJS(exp.rator), mapResult(l2ToJS, exp.rands));
}

const UnparseProcExp=(exp:ProcExp):Result<string> =>
{
    if(exp.body.length>1)
    {
        let safe=(body: string[]):Result<string> =>
        {
            let str1 = concat("return ", body[body.length-1]);
            body[body.length-1] = concat(str1,";");
            return makeOk(`((${map(v => v.var, exp.args).join(",")}) => {${body.join("; ")}})`);
        }
        return bind(mapResult(l2ToJS, exp.body), safe);
    }
    return bind(mapResult(l2ToJS, exp.body), (body: string[]) => makeOk(`((${map(v => v.var, exp.args).join(",")}) => ${body.join("; ")})`));
}

const UnparseIfExp=(exp:IfExp):Result<string> =>
{
    return safe3((test: string, then: string, alt: string) => makeOk(`(${test} ? ${then} : ${alt})`))
        (l2ToJS(exp.test), l2ToJS(exp.then), l2ToJS(exp.alt));
}

const UnparseNumExp = (exp:NumExp):Result<string> => 
{
    return makeOk(exp.val.toString());
}

const UnparseBoolExp = (exp:BoolExp):Result<string> => 
{
    return makeOk(exp.val ? "#t" : "#f");
}

const UnparsePrimOpExp = (exp:PrimOp):Result<string> => 
{
    return makeOk(exp.op);
}

const UnparseVarRef = (exp:VarRef):Result<string> => 
{
    return makeOk(exp.var);
}
