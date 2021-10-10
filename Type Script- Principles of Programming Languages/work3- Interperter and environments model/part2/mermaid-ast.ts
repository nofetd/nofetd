// ===========================================================
// AST type models

/*
;; =============================================================================
;; Scheme Parser
;; <graph> ::= <header> <graphContent> // Graph(dir: Dir, content: GraphContent)
;; <header> ::= graph (TD|LR)<newline> // Direction can be TD or LR
;; <graphContent> ::= <atomicGraph> | <compoundGraph>
;; <atomicGraph> ::= <nodeDecl>
;; <compoundGraph> ::= <edge>+
;; <edge> ::= <node> --><edgeLabel>? <node><newline> // <edgeLabel> is optional
                                                    // Edge(from: Node, to: Node, label?: string)
;; <node> ::= <nodeDecl> | <nodeRef>
;; <nodeDecl> ::= <identifier>["<string>"] // NodeDecl(id: string, label: string)
;; <nodeRef> ::= <identifier> // NodeRef(id: string)
;; <edgeLabel> ::= |<identifier>| // string
;;
;;
;;
*/




//mermaid
export type Node = NodeDecl | NodeRef;
export type GraphContent = AtomicGraph | CompoundGraph;
export type Direction = TD | LR ;

export interface Graph {tag: "Graph"; var: Header; body: GraphContent; }
export interface TD {tag: "TD"; var: string; }
export interface LR {tag: "LR"; var: string; }
export interface Header {tag: "Header"; var: Direction}
export interface AtomicGraph {tag: "AtomicGraph"; var: NodeDecl; }
export interface CompoundGraph {tag: "CompoundGraph"; body: Edge[];}
//export interface Arrow {tag: "Arrow"; var: string;}
export interface Edge {tag: "Edge"; from: Node; Label:EdgeLabel; to: Node; }
export interface NodeDecl {tag: "NodeDecl"; id: string; label: string; }
export interface NodeRef {tag: "NodeRef"; id: string; }
export interface EdgeLabel {tag: "EdgeLabel"; var: string;}
//Gen
export interface _Gen{
NumExp_Gen : (v:string)=>string,
BoolExp_Gen : (v:string)=>string,
StrExp_Gen : (v:string)=>string,
PrimOp_Gen : (v:string)=>string,
VarRef_Gen : (v:string)=>string,
VarDecl_Gen : (v:string)=>string,
Progrom_Gen : (v:string)=>string,
DefineExp_Gen : (v:string)=>string,
AppExp_Gen : (v:string)=>string,
IfExp_Gen : (v:string)=>string,
ProcExp_Gen : (v:string)=>string,
LetExp_Gen : (v:string)=>string,
LitExp_Gen : (v:string)=>string,
SetExp_Gen : (v:string)=>string,
Body_Gen : (v:string)=>string,
Args_Gen : (v:string)=>string,
Exseps_Gen : (v:string)=>string,
Rands_Gen : (v:string)=>string,
Number_Gen : (v:string)=>string,
String_Gen : (v:string)=>string,
Boolean_Gen : (v:string)=>string,
EmptySExp_Gen : (v:string)=>string,
CompoundSExp_Gen : (v:string)=>string,
Symbol_Gen : (v:string)=>string,
Binding_Gen : (v:string)=>string,
}

//mermaid
export const makeGraph = (v: Header, b: GraphContent): Graph => ({tag: "Graph", var: v, body: b});
export const makeTD = (v: string): TD => ({tag: "TD", var: v});
export const makeLR = (v: string): LR => ({tag: "LR", var: v});
export const makeHeader = (v: Direction): Header => ({tag: "Header", var: v});
export const makeAtomicGraph = (v: NodeDecl): AtomicGraph =>({tag: "AtomicGraph", var: v });
export const makeCompoundGraph = (b: Edge[]): CompoundGraph =>({tag: "CompoundGraph", body: b});
//export const makeArrow = (v: string): Arrow => ({tag: "Arrow", var: v });
export const makeEdge =  (f: Node, a: EdgeLabel, t: Node): Edge => ({tag: "Edge", from: f, Label: a, to: t }); 
export const makeNodeDecl = (v: string, l: string): NodeDecl => ({tag: "NodeDecl", id: v, label: l});
export const makeNodeRef = (v: string): NodeRef => ({tag: "NodeRef", id: v});
export const makeEdgeLabel = (v: string): EdgeLabel => ({tag: "EdgeLabel", var: v})

/*
    Purpose: create a generator of new symbols of the form v__n
    with n incremented at each call.
*/
export const makeVarGen = (): (v: string) => string => {
    let count: number = 0;
    return (v: string) => {
        count++;
        return `${v}__${count}`;
    };

};

//Gen
export const Make_Gen = ():_Gen =>({
    NumExp_Gen : makeVarGen(),
    BoolExp_Gen : makeVarGen(),
    StrExp_Gen : makeVarGen(),
    PrimOp_Gen : makeVarGen(),
    VarRef_Gen : makeVarGen(),
    VarDecl_Gen : makeVarGen(),
    Progrom_Gen : makeVarGen(),
    DefineExp_Gen : makeVarGen(),
    AppExp_Gen : makeVarGen(),
    IfExp_Gen : makeVarGen(),
    ProcExp_Gen : makeVarGen(),
    LetExp_Gen : makeVarGen(),
    LitExp_Gen : makeVarGen(),
    SetExp_Gen : makeVarGen(),
    Body_Gen : makeVarGen(),
    Args_Gen : makeVarGen(),
    Exseps_Gen : makeVarGen(),
    Rands_Gen : makeVarGen(),
    Number_Gen : makeVarGen(),
    String_Gen : makeVarGen(),
    Boolean_Gen : makeVarGen(),
    EmptySExp_Gen : makeVarGen(),
    CompoundSExp_Gen : makeVarGen(),
    Symbol_Gen : makeVarGen(),
    Binding_Gen : makeVarGen(),
});


//mermaid
export const isGraph = (x: any): x is Graph => x.tag === "Graph";
export const isTD = (x: any): x is TD => x.tag === "TD";
export const isLR = (x: any): x is LR => x.tag === "LR";
export const isHeader = (x: any): x is Header => x.tag === "Header";
export const isAtomicGraph = (x: any): x is AtomicGraph => x.tag === "AtomicGraph";
export const isCompoundGraph = (x: any): x is CompoundGraph => x.tag === "CompoundGraph";
export const isEdge =  (x: any): x is Edge => x.tag === "Edge"; 
export const isNodeDecl = (x: any): x is NodeDecl => x.tag === "NodeDecl";
export const isNodeRef =(x: any): x is NodeRef => x.tag === "NodeRef";
export const isEdgeLabel = (x: any): x is EdgeLabel => x.tag === "EdgeLabel";

export const isNode = (x: any): x is Node =>
    isNodeDecl(x) || isNodeRef(x);
export const isGraphContent = (x: any): x is GraphContent =>
    isAtomicGraph(x) || isCompoundGraph(x);
export const isDirection = (x: any): x is Direction =>
    isTD(x) || isLR(x);