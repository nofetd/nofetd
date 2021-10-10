// ===========================================================
// AST type models
import { map, zipWith, type, indexOf, zip, filter, contains, KeyValuePair, concat, is } from "ramda";
import { isString, isNumber, isBoolean } from "../shared/type-predicates";
import { parse as p, isSexpString, isToken } from "../shared/parser";
import { Result, makeOk, makeFailure, bind, mapResult, safe2, isOk } from "../shared/result";
import { isSymbolSExp, isEmptySExp, isCompoundSExp, isSExp } from './L4-value';
import { makeEmptySExp, makeSymbolSExp, SExpValue, makeCompoundSExp, valueToString } from './L4-value'
import { CompoundSExp } from "../part2/L4-value";
import { Exp, isNumExp, isBoolExp, isStrExp, isPrimOp, isVarRef, isVarDecl, isProgram, isDefineExp, isAppExp, isIfExp, isProcExp, isLetExp, isLitExp, isExp, Program, isCExp, isBinding, CExp, isCompoundExp, CompoundExp, AtomicExp, DefineExp, AppExp, IfExp, ProcExp, LetExp, Binding, LitExp, VarRef, VarDecl, Parsed, isLetrecExp, isSetExp, SetExp, LetrecExp, parseL4Exp, parseL4Program } from "../part2/L4-ast";
import { Value, EmptySExp } from "../part2/L4-value"
import { Make_Gen, _Gen,Node, NodeDecl, makeNodeDecl, isNodeDecl, NodeRef, makeNodeRef, Graph, makeGraph, makeHeader, makeTD, makeCompoundGraph, Edge, makeEdge, makeEdgeLabel, isAtomicGraph, isCompoundGraph, isNodeRef } from "./mermaid-ast";





const ToNode=(exp:Exp | SExpValue,Gen:_Gen) : NodeDecl=>
{
    return isNumExp(exp) ? makeNodeDecl(Gen.NumExp_Gen("NumExp"),"[NumExp("+exp.val+")]"):
    isNumber(exp) ? makeNodeDecl(Gen.Number_Gen("Number"),"[Number("+exp+")]"):
    isBoolean(exp) ? makeNodeDecl(Gen.Boolean_Gen("Boolean"),"[Boolean("+exp+")]"):
    isBoolExp(exp) ? makeNodeDecl(Gen.BoolExp_Gen("BoolExp"),"[BoolExp("+exp.val+")]"):
    isStrExp (exp) ? makeNodeDecl(Gen.StrExp_Gen("StrExp"),"[StrExp("+exp.val+")]"):
    isString(exp)  ? makeNodeDecl(Gen.String_Gen("String"),"[String("+exp+")]"):
    isPrimOp(exp) ? makeNodeDecl(Gen.PrimOp_Gen("PrimOpExp"),"[PrimOp("+exp.op+")]"):
    isVarRef(exp) ?  makeNodeDecl(Gen.VarRef_Gen("VarRef"),"[VarRef("+exp.var+")]"):
    isVarDecl(exp) ?  makeNodeDecl(Gen.VarDecl_Gen("VarDecl"),"[VarDecl("+exp+")]"):
    isProgram(exp) ? makeNodeDecl(Gen.Exseps_Gen("Exps"),"[:]"): 
    isDefineExp(exp) ? makeNodeDecl(Gen.DefineExp_Gen("DefineExp"),"["+exp.tag+"]"):
    isAppExp(exp) ? makeNodeDecl(Gen.AppExp_Gen("AppExp"),"["+exp.tag+"]"):
    isIfExp(exp)? makeNodeDecl(Gen.IfExp_Gen("IfExp"),"["+exp.tag+"]"):
    isProcExp(exp)? makeNodeDecl(Gen.ProcExp_Gen("ProcExp"),"["+exp.tag+"]"):
    isLetExp(exp)? makeNodeDecl(Gen.LetExp_Gen("LetExp"),"["+exp.tag+"]"):
    isLitExp(exp)? makeNodeDecl(Gen.LitExp_Gen("LitExp"),"["+exp.tag+"]"):
    isNodeDecl(exp) ? makeNodeDecl(Gen.SetExp_Gen("SetExp"),"["+exp+"]"):
    isEmptySExp(exp) ? makeNodeDecl(Gen.EmptySExp_Gen("EmptySExp"),"["+exp.tag+"]"):
    isCompoundSExp(exp) ? makeNodeDecl(Gen.CompoundSExp_Gen("CompoundSExp"),"["+exp.tag+"]"):
    makeNodeDecl(Gen.Symbol_Gen("SymbolSExp"),"["+exp.tag+"]");
}




const toRefNode = (node: Node):NodeRef =>{
    return isNodeDecl(node) ? makeNodeRef(node.id) : node;
}

export const mapL4toMermaid = (exp: Parsed): Result<Graph> =>{
    const Gen = Make_Gen();
    const e = FirstEdges(exp,Gen);
    const edge =  isExp(exp) ? split(e,exp,Gen) :
    ToMermaidProgram(e,exp,Gen);
    return makeOk(makeGraph(makeHeader(makeTD("TD")),makeCompoundGraph(edge)));
}
const ToMermaidProgram=(edges:Edge[],exp:Program,Gen:_Gen):Edge[] =>
{
    const e1=exp.exps.map(function(x){ return ToMermaidExp(edges[0].to,x,Gen); });
    const edge = e1.reduce(concat);
    return edge;
    
}

const ToMermaidExp=(n:Node,exp:Exp,Gen:_Gen):Edge[] =>
{
    const edge = isCExp(exp) ? ToMermaidCExp(n,exp,Gen):
    ToMermaidDefine(n,exp,Gen);
    return edge;
}

const split = (edges:Edge[], exp:Exp,Gen:_Gen):Edge[] =>
{
    if(isNumExp(exp) || isBoolExp(exp) || isStrExp(exp) || isPrimOp(exp) || isVarRef(exp) || isVarDecl(exp) ){
        return edges;
  }
    if(isDefineExp(exp)){
        const e2= ToMermaidCExp(edges[1].to,exp.val,Gen);
        return edges.concat(e2);
    }

    if(isAppExp(exp)){ 
        const e1=ToMermaidCExp(edges[0].to,exp.rator,Gen);
        const e2=exp.rands.map(function(x){ return ToMermaidCExp(edges[1].to,x,Gen); });
        const e3 = e2.reduce(concat);
        const e4=  edges.concat(e1,e3);
        return e4;
    }
    if(isIfExp(exp)){
        const e1=ToMermaidCExp(edges[0].to,exp.test,Gen);
        const e2=ToMermaidCExp(edges[1].to,exp.then,Gen);
        const e3=ToMermaidCExp(edges[2].to,exp.alt,Gen);
        const e=edges.concat(e1,e2,e3);
        return e;
    }
    if(isProcExp(exp)){
        const e1 = exp.args.map(function(x){ return ToMermadiVarDecl(edges[0].to,x,Gen); });
        const e2 = e1.reduce(concat);
        const e3 = exp.body.map(function(x){ return ToMermaidCExp(edges[1].to,x,Gen); });
        const e4 = e3.reduce(concat);
        const e=edges.concat(e2,e4);
        return e;        
    }
    if(isBinding(exp)){
        const e1=ToMermaidCExp(edges[1].to,exp,Gen);
        const e=edges.concat(e1);
        return e;
    }
    if(isLetExp(exp)){
        const e1 =ToMermaidBind(edges[0].to,exp.bindings,Gen);
        const e2 =  exp.body.map(function(x){ return ToMermaidCExp(edges[1].to,x,Gen); });
        const e3 = e2.reduce(concat);
        const e=edges.concat(e1,e3);
         return e; 
    }

    if(isLitExp(exp)){
        const e1=ToSExpValue(edges[0].to,exp.val,Gen);
       return edges.concat(e1);
    }

    if(isLetrecExp(exp)){
        const e1 = ToMermaidBind(edges[0].to,exp.bindings,Gen);
        const e2 = exp.body.map(function(x){ return ToMermaidCExp(edges[1].to,x,Gen); });
        const e3 = e2.reduce(concat);
        const e=edges.concat(e1,e3);
         return e; 
    }

    if(isSetExp(exp)){
        const e1= ToMermaidCExp(edges[1].to,exp.val,Gen);
        return edges.concat(e1);               
    }
    return [];

}

const FirstEdges = (exp:Exp | Program, Gen:_Gen): Edge[] =>
{
    if(isProgram(exp)){
        const gen1 = Gen.Progrom_Gen("Program");
        const gen2 = Gen.Exseps_Gen("Exps");
        const e = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|args|"),makeNodeDecl(gen2,"[:]"));
        return [e];
    }

    if(isDefineExp(exp)){
        const gen1 = Gen.DefineExp_Gen("DefineExp");
        const gen2 = Gen.VarDecl_Gen("Var");
        const e = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|var|"),makeNodeDecl(gen2,"[VarDecl("+exp.var.var+")]"));
        const n1 = ToNode(exp.val,Gen);
        const e1 = makeEdge(makeNodeRef(gen1),makeEdgeLabel("--->|val|"),n1);
        return [e].concat(e1);
    }
    if(isNumExp(exp)){
        const gen1 = Gen.NumExp_Gen("NumExp");
        const gen2 = Gen.Number_Gen("Number"); 
        const e = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|val|"),makeNodeDecl(gen2,"[Number("+exp.val+")]"));
        return [e];
    }
    if(isBoolExp(exp)){
        const gen1 = Gen.BoolExp_Gen("BoolExp");
        const gen2 = Gen.Boolean_Gen("Boolean");
        const e = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|val|"),makeNodeDecl(gen2,"[Boolean("+exp.val+")]"));
        return [e];
    }
    if(isStrExp(exp)){
        const gen1 = Gen.StrExp_Gen("StrExp");
        const gen2 = Gen.String_Gen("String");
        const e = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|val|"),makeNodeDecl(gen2,"[String("+exp.val+")]"));
        return [e];
    }
    if(isPrimOp(exp)){
        const gen1 = Gen.PrimOp_Gen("PrimOp");
        const e = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|op|"),makeNodeDecl(gen1,"[PrimOp("+exp.op+")]"));
        return [e];
    }
    if(isVarRef(exp)){
        const gen1 = Gen.VarRef_Gen("VarRef");
        const e = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|var|"),makeNodeDecl(gen1,"[VarRef("+exp.var+")]"));
        return [e];
    }
    if(isVarDecl(exp)){
        const gen1 = Gen.VarDecl_Gen("VarDecl");
        const e = makeEdge(makeNodeDecl(gen1,"["+exp+"]"),makeEdgeLabel("--->|var|"),makeNodeDecl(gen1,"[VarDecl("+exp+")]"));
        return [e];
    }
    
    if(isAppExp(exp)){ 
        const gen1 = Gen.AppExp_Gen("AppExp");
        const n = ToNode(exp.rator,Gen);
        const e = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|rator|"),n);
        const gen2 = Gen.Rands_Gen("Rands");
        const e1 = makeEdge(makeNodeRef(gen1),makeEdgeLabel("--->|rands|"),makeNodeDecl(gen2,"[:]"));
        return [e,e1];
    }
    if(isIfExp(exp)){
        const gen1 = Gen.IfExp_Gen("IfExp");
        const n1 = ToNode(exp.test,Gen);
        const n2 = ToNode(exp.then,Gen);
        const n3 = ToNode(exp.alt,Gen);
        const e1 = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|test|"),n1);
        const e2 = makeEdge(makeNodeRef(gen1),makeEdgeLabel("--->|then|"),n2);
        const e3 = makeEdge(makeNodeRef(gen1),makeEdgeLabel("--->|alt|"),n3);;
        return [e1,e2,e3];
    }
    if(isProcExp(exp)){
        const gen1 = Gen.ProcExp_Gen("ProcExp");
        const gen2 = Gen.Args_Gen("Args");
        const e1 = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|args|"),makeNodeDecl(gen2,"[:]"));
        const gen3 = Gen.Body_Gen("Body");
        const e2 = makeEdge(makeNodeRef(gen1),makeEdgeLabel("--->|body|"),makeNodeDecl(gen3,"[:]"));
        return [e1,e2];        
    }
    if(isBinding(exp)){
        const gen1 = Gen.Binding_Gen("Binding");
        const gen2 = Gen.VarDecl_Gen("VarDecl");
        const e1 = makeEdge(makeNodeDecl(gen1,"["+exp+"]"),makeEdgeLabel("--->|var|"),makeNodeDecl(gen2,"[VarDecl("+exp+")]"));
        const n1 = ToNode(exp,Gen);
        const e2 =  makeEdge(makeNodeRef(gen1),makeEdgeLabel("--->|val|"),n1);
        return [e1,e2];
    }
    if(isLetExp(exp)){
        const gen1 = Gen.LetExp_Gen("LetExp");
        const gen2 = Gen.Binding_Gen("Binding");
        const e1 = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|bindings|"),makeNodeDecl(gen2,"[:]"));
        const gen3 = Gen.Body_Gen("Body");
        const e2 = makeEdge(makeNodeRef(gen1),makeEdgeLabel("--->|body|"),makeNodeDecl(gen3,"[:]"));
        return [e1,e2]; 
    }
    if(isLitExp(exp)){
        const gen1 = Gen.LitExp_Gen("LitExp");
        const n1 = ToNode(exp.val,Gen);
        const e1 =  makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|val|"),n1);
        return [e1];
    }
    if(isLetrecExp(exp)){
        const gen1 = Gen.LetExp_Gen("LetExp");       
        const gen2 = Gen.Binding_Gen("Binding");
        const e1 = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|bindings|"),makeNodeDecl(gen2,"[:]"));
        const gen3 = Gen.Body_Gen("Body");
        const e2 = makeEdge(makeNodeRef(gen1),makeEdgeLabel("--->|body|"),makeNodeDecl(gen3,"[:]"));
        return [e1,e2];
    }
    if(isSetExp(exp)){
        const gen1 = Gen.SetExp_Gen("SetExp");
        const gen2 = Gen.VarRef_Gen("VarRef");
        const e1 = makeEdge(makeNodeDecl(gen1,"["+exp.tag+"]"),makeEdgeLabel("--->|var|"),makeNodeDecl(gen2,"[var("+exp.var+")]"));
        const n1 = ToNode(exp.val,Gen);
        const e2 = makeEdge(makeNodeRef(gen1),makeEdgeLabel("--->|val|"),n1);
        return [e1,e2];               
    }
    return [];


}

const ToMermaidCExp=(n:Node,exp:CExp,Gen:_Gen):Edge[]=>{
    return isCompoundExp(exp) ? ToMermaidCompound(n,exp,Gen) :
    ToMermaidAtomic(n,exp,Gen);
}

const ToMermaidCompound=(n:Node,exp:CompoundExp,Gen:_Gen):Edge[] =>
{
    return isAppExp(exp) ? ToMermaidAppExp(n,exp,Gen) :
    isProcExp(exp) ? ToMermaidProcExp(n,exp,Gen) :
    isLitExp(exp)?  ToMermaidLitExp(n,exp,Gen): 
    isLetExp(exp)? ToMermaidLetExp(n,exp,Gen):
    isSetExp(exp)? ToMermaidSetExp(n,exp,Gen):
    isLetrecExp(exp) ? ToMermaidLetrecExp(n,exp,Gen):
    ToMermaidIfExp(n,exp,Gen);
}

const ToMermaidAtomic=(n:Node,exp:AtomicExp,Gen:_Gen):Edge[]=>
{
    const node = ToNode(exp,Gen);
    const e1=makeEdge(toRefNode(n) ,makeEdgeLabel("--->"),node);
    return [e1];
}

const ToMermaidDefine=(n:Node,exp:DefineExp,Gen:_Gen):Edge[] =>
{
    const v=Gen.DefineExp_Gen("DefineExp");
    const e=Gen.VarDecl_Gen("VarDecl");
    const edge=makeEdge(makeNodeRef(v),makeEdgeLabel("-->|var|"),makeNodeDecl(e,"[VarDecl("+exp.var.var+")]"));
    const e2=ToMermaidCExp(n, exp.val,Gen);
    return [edge].concat(e2);
}

const ToMermaidAppExp=(n:Node,exp:AppExp,Gen:_Gen):Edge[]=>{
  const i1=Gen.AppExp_Gen("AppExp");
  const n1=makeNodeDecl(i1,"[AppExp]");
  const e2=makeEdge(toRefNode(n),makeEdgeLabel("--->"),n1);

  const i=Gen.Rands_Gen("Rands");
  const e3=makeEdge(toRefNode(n1) ,makeEdgeLabel("--->|Rands|"),makeNodeDecl(i,"[:]"));

  const n2=ToNode(exp.rator,Gen);
  const e4=makeEdge(toRefNode(n1) ,makeEdgeLabel("--->|Rator|"),n2);

  const e5=exp.rands.map(function(x){ return ToMermaidCExp(toRefNode(e3.to), x,Gen); });
  const e6 = e5.reduce(concat);
  
  if(isCompoundExp(exp.rator)){
      const e1=ToMermaidCExp(n2,exp.rator,Gen);
      return [e2].concat(e3,e4,e6,e1);
  }
  return [e2].concat(e3,e4,e6);
}

const ToMermaidIfExp=(n:Node,exp:IfExp,Gen:_Gen):Edge[]=>
{
    const i1=Gen.IfExp_Gen("IfExp");
    const n1=makeNodeDecl(i1,"[IfExp]");
    const e1=makeEdge(toRefNode(n) ,makeEdgeLabel("--->"),n1);
    
    const n2=ToNode(exp,Gen);
    const e2=makeEdge(toRefNode(n1),makeEdgeLabel("--->|test|"),n2);

    const n3=ToNode(exp,Gen);
    const e3=makeEdge(toRefNode(n1),makeEdgeLabel("--->|then|"),n3);
    
    const n4=ToNode(exp,Gen);
    const e4=makeEdge(toRefNode(n1),makeEdgeLabel("--->|alt|"),n4);

    const etest=ToMermaidCExp(n2,exp.test,Gen);
    const ethen=ToMermaidCExp(n3,exp.then,Gen);
    const ealt=ToMermaidCExp(n4,exp.alt,Gen);
   
    return [e1].concat(e2,e3,e4,etest,ethen,ealt);
}


const ToMermaidProcExp=(n:Node,exp:ProcExp,Gen:_Gen):Edge[]=>{
    const i1=Gen.Args_Gen("Args");
    const e1=makeEdge(toRefNode(n),makeEdgeLabel("--->|Args|"),makeNodeDecl(i1,"[:]"));
   
    const i2=Gen.Body_Gen("Body");
    const e2=makeEdge(toRefNode(n) ,makeEdgeLabel("--->|Body|"),makeNodeDecl(i2,"[:]"));
   
    const eargs=exp.args.map(function(x){ return ToMermadiVarDecl(toRefNode(e1.to), x,Gen); });
    const c1 = eargs.reduce(concat);

    const ebody=exp.body.map(function(x){ return ToMermaidCExp(toRefNode(e2.to), x,Gen); });
    const c2 = ebody.reduce(concat);

    return [e1].concat(e2,c1,c2);
}

const ToMermaidLetExp=(n:Node,exp:LetExp,Gen:_Gen):Edge[]=>{
    const i=Gen.Binding_Gen("Binding");
    const e1=makeEdge(toRefNode(n) ,makeEdgeLabel("--->|Binding|"),makeNodeDecl(i,"[:]"));
    
    const i2=Gen.Body_Gen("Body");
    const e2=makeEdge(toRefNode(n) ,makeEdgeLabel("--->|Body|"),makeNodeDecl(i2,"[:]"));
   
    const eBinding=exp.bindings.map(function(x){ return ToBind(e1.to, x,Gen); });
    const c1 = eBinding.reduce(concat);
   
    const ebody=exp.body.map(function(x){ return ToMermaidCExp(toRefNode(e2.to), x,Gen); });
    const c2 = ebody.reduce(concat);
    return [e1].concat(e2,c1,c2);
}

const ToMermaidBind=(n:Node,exp:Binding[],Gen:_Gen):Edge[]=>{
    const i=Gen.Binding_Gen("Binding");
    const n1=makeNodeDecl(i,"[:]");
    const e1=makeEdge(toRefNode(n) ,makeEdgeLabel("--->|Binding|"),n1);
    const e2=exp.map(function(x){ return ToBind(n1, x,Gen); });
    const c = e2.reduce(concat);
    return [e1].concat(c);
}

const ToBind=(n:Node,exp:Binding,Gen:_Gen):Edge[]=>{
    const e1=ToMermadiVarDecl(n,exp.var,Gen);
    const e2=ToMermaidCExp(n,exp.val,Gen);
    return e1.concat(e2);
}

const ToMermaidSetExp=(n:Node,exp:SetExp,Gen:_Gen):Edge[]=>{
    const i1=Gen.SetExp_Gen("SetExp");
    const n1=makeNodeDecl(i1,"[SetExp]");
    const e1=makeEdge(toRefNode(n) ,makeEdgeLabel("--->"),n1);
    const e2=ToMermaidVarRef(n1,exp.var,Gen);
    const e3=ToMermaidCExp(n1,exp.val,Gen);
    const c = [e1].concat(e2);
    return c.concat(e3);
}

const ToMermaidLetrecExp=(n:Node,exp:LetrecExp,Gen:_Gen):Edge[]=>{
    const i=Gen.Binding_Gen("Binding");
    const e1=makeEdge(toRefNode(n) ,makeEdgeLabel("--->|Binding|"),makeNodeDecl(i,"[:]"));
    
    const i2=Gen.Body_Gen("Body");
    const e2=makeEdge(toRefNode(n) ,makeEdgeLabel("--->|Body|"),makeNodeDecl(i2,"[:]"));
   
    const eBinding=exp.bindings.map(function(x){ return ToBind(e1.to, x,Gen); });
    const c1 = eBinding.reduce(concat);
   
    const ebody=exp.body.map(function(x){ return ToMermaidCExp(toRefNode(e2.to), x,Gen); });
    const c2 = ebody.reduce(concat);
    return [e1].concat(e2,c1,c2);
    }

const ToMermaidLitExp=(n:Node,exp:LitExp,Gen:_Gen):Edge[]=>{
   return isSExp(exp.val) ? ToSExpValue(n,exp.val,Gen):
    [];    
}

const  ToSExpValue=(n:Node, exp:SExpValue,Gen:_Gen):Edge[]=>{
    return isNumber(exp)? [makeEdge(toRefNode(n) ,makeEdgeLabel("--->"),ToNode(exp,Gen))]:
    isBoolean(exp)? [makeEdge(toRefNode(n) ,makeEdgeLabel("--->"),ToNode(exp,Gen))]:
    isString(exp)? [makeEdge(toRefNode(n) ,makeEdgeLabel("--->"),ToNode(exp,Gen))]: 
    isPrimOp(exp)? [makeEdge(toRefNode(n) ,makeEdgeLabel("--->"),ToNode(exp,Gen))]:
    isSymbolSExp(exp)? [makeEdge(toRefNode(n) ,makeEdgeLabel("--->"),ToNode(exp,Gen))]: 
    isEmptySExp(exp)? [makeEdge(toRefNode(n) ,makeEdgeLabel("--->"),ToNode(exp,Gen))]:
    isCompoundSExp(exp)? ToCompoundSExp(n,exp,Gen):
    [];
}



const ToCompoundSExp=(n:Node,exp:CompoundSExp,Gen:_Gen):Edge[]=>{
    const i=Gen.CompoundSExp_Gen(exp.tag);
    const e=makeEdge(toRefNode(n),makeEdgeLabel("--->"),makeNodeDecl(i,"[("+exp.tag+")]"))
    const e1=ToSExpValue(toRefNode(e.to) ,exp.val1,Gen);
    const e2=ToSExpValue(toRefNode(e.to) ,exp.val2,Gen);
    const c = [e].concat(e1);
    return c.concat(e2);
}



const ToMermaidVarRef=(n:Node,exp:VarRef,Gen:_Gen):Edge[]=>{
    const i1=Gen.VarRef_Gen("VarRef");
    const n1=makeNodeDecl(i1,"[VarRef("+exp.var+")]");
    const e1=makeEdge(toRefNode(n) ,makeEdgeLabel("--->"),n1);
    return [e1]; 
}

const ToMermadiVarDecl=(n:Node,exp:VarDecl,Gen:_Gen):Edge[]=>{
    const i1=Gen.VarDecl_Gen("VarDecl");
    const n1=makeNodeDecl(i1,"[VarDecl("+exp.var+")]");
    const e1=makeEdge(toRefNode(n),makeEdgeLabel("--->"),n1);
    return [e1]; 
    
}




/////2.3
export const unparseMermaid = (exp: Graph): Result<string>=>{
    if(isAtomicGraph(exp.body)){
        const s = exp.tag + " " + exp.var.var.var + "\n" + exp.body.var.id + exp.body.var.label;
        return makeOk(s);
    }
    if(isCompoundGraph(exp.body)){
        const str = exp.body.body.map(EdgeToString);
        const out = str.reduce(concat);
        const s = (exp.tag + " " + exp.var.var.var + "\n").concat(out);
        return makeOk(s);
    }
    return makeFailure("error");
}

const EdgeToString = (e:Edge) :string =>{
    if(isNodeDecl(e.from) && isNodeDecl(e.to)){
        return (e.from.id + e.from.label + " " + e.Label.var + " " + e.to.id + e.to.label + "\n");
    }
    if(isNodeRef(e.from) && isNodeDecl(e.to)){
        return (e.from.id + " " + e.Label.var + " " + e.to.id + e.to.label + "\n");
    }
    return "";
}

export const L4toMermaid = (concrete: string): Result<string>=>{
    const a =p(concrete);
    if(isOk(a)){
        const g =parseL4Exp(a.value);
        if(isOk(g))
        {
            const d = mapL4toMermaid(g.value);
            if(isOk(d)){
                return unparseMermaid(d.value);
            }
        }
        else{
            const p = parseL4Program(a.value);
            if(isOk(p))
            {
                const d = mapL4toMermaid(p.value);
            if(isOk(d)){
                return unparseMermaid(d.value);
                }
            }
        }
    }
    return makeFailure("error");
}
