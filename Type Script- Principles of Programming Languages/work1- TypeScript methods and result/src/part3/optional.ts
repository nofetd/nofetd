/* Question 1 */

import { any } from "ramda";

export type Optional<T> = {tag: 'Some', value: T} | {tag: 'None'};
export const makeSome = (x: any) =>
{
    let out:Optional<any> = {tag: 'Some', value: x};
    return out;
}
export const makeNone = ():Optional<any> => 
{
    let out:Optional<any> = {tag: 'None'};
    return out;
}

export const isSome = (x: Optional<any>):boolean =>
{
    return (x.tag === 'Some');
}
export const isNone = (x: Optional<any>):boolean =>
{
    return(x.tag === 'None');
}
/* Question 2 */
export const bind = <T,U>(o: Optional<T>, f:(x:T)=>Optional<U>):Optional<U> =>
{
    if(o.tag=='Some')
    {
        let newOpt = f(o.value);
        return newOpt;
    }
    else
    {
        let out:Optional<U> = {tag: 'None'};
        return out;
    }
}