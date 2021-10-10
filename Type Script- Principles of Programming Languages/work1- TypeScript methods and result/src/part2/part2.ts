/* Question 1 */

import { pipe, any, concat, slice } from "ramda";

export const partition = <T>(predicate:(val: T) => boolean, arr:Array<T>):T[][] => 
{
    let a=arr.filter(predicate);
    
    function callback(el:T) {
        return a.indexOf(el)<0;        
    }
    const filtered = arr.filter(callback);
    return [a,filtered];
}

/* Question 2 */
export const mapMat = <T>(pre:any, arr:T[][]):T[][] =>
{
    const out:T[][]=[];
    for(let i=0; i<arr.length; i++)
    {
        out[i]=arr[i].map(pre);
    }
    return out;
}

/* Question 3 */
export const composeMany = <T>(foo:{(data:T) : T;}[]):Function =>
{
    let a= foo.reduce((prev,curr)=> pipe(curr,prev));
    return a;
}


/* Question 4 */
/* Remove the export!!*/
interface Languages {
    english: string;
    japanese: string;
    chinese: string;
    french: string;
}

interface Stats {
    HP: number;
    Attack: number;
    Defense: number;
    "Sp. Attack": number;
    "Sp. Defense": number;
    Speed: number;
}

interface Pokemon {
    id: number;
    name: Languages;
    type: string[];
    base: Stats;
}

export const maxSpeed = (arr:Array<Pokemon>):Array<Pokemon> =>
{
    const maxi = arr.reduce((prev,curr)=>(prev.base.Speed>curr.base.Speed) ? prev : curr);
    let func = (p:Pokemon)=> p.base.Speed === maxi.base.Speed;
    return arr.filter(func);
}

export const grassTypes = (arr:Array<Pokemon>):Array<string> =>
{
    let func1 =(p:Pokemon) =>
    {
        for(let i=0; i< p.type.length; i++)
        {
            if(p.type[i] === "Grass")
            {
                return p;
            }
        }
    }

    let a=arr.filter(func1);
    let func2 = (p:Pokemon)=> p.name.english;
    let b=a.map(func2);
    return b.sort();
}

export const uniqueTypes =(arr:Array<Pokemon>):Array<string>=>
{
    let func1 = (p:Pokemon) => p.type;
    let a = arr.map(func1);
    let b = a.reduce((prev,curr)=>(prev.concat(curr)));
    let c = b.filter((element,index,self) => index === self.indexOf(element));
    return c.sort();
}
