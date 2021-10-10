/* Question 3 */

export type Result<T> = {tag: 'OK', value: T} | {tag: 'Failure', message: string};

export const makeOk = (x: any):Result<any> =>
{
    let out:Result<any> = {tag:'OK', value:x};
    return out;
}
export const makeFailure = (x: string):Result<any> =>
{
    let out:Result<any> = {tag:'Failure', message:x};
    return out;
}

export const isOk = (res: Result<any>):boolean => 
{
    return(res.tag==='OK');
}
export const isFailure = (res: Result<any>):boolean => 
{
    return(res.tag==='Failure');
}

/* Question 4 */
export const bind = <T,U>(r: Result<T>, f:(x:T)=>Result<U>):Result<U> =>
{
    if(r.tag=='OK')
    {
        let newRes = f(r.value);
        return newRes;
    }
    else
    {
        return r;
    }

    
}

/* Question 5 */
interface User {
    name: string;
    email: string;
    handle: string;
}

const validateName = (user: User): Result<User> =>
    user.name.length === 0 ? makeFailure("Name cannot be empty") :
    user.name === "Bananas" ? makeFailure("Bananas is not a name") :
    makeOk(user);

const validateEmail = (user: User): Result<User> =>
    user.email.length === 0 ? makeFailure("Email cannot be empty") :
    user.email.endsWith("bananas.com") ? makeFailure("Domain bananas.com is not allowed") :
    makeOk(user);

const validateHandle = (user: User): Result<User> =>
    user.handle.length === 0 ? makeFailure("Handle cannot be empty") :
    user.handle.startsWith("@") ? makeFailure("This isn't Twitter") :
    makeOk(user);

export const naiveValidateUser = (u: User):Result<any> =>
{
    const Res1 = validateName(u);
    const Res2 = validateEmail(u);
    const Res3 = validateHandle(u);
    if(Res1.tag=='OK')
    {
        if(Res2.tag=='OK')
        {
            if(Res3.tag=='OK')
            {
                return {tag: 'OK', value:u};
            }
            else return {tag: 'Failure', message: Res3.message};
        }
        else return {tag: 'Failure', message: Res2.message};
    }
    else return {tag:'Failure', message: Res1.message};
}

export const monadicValidateUser = (u: User):Result<any> =>
{
    return bind(bind(validateName(u),validateEmail),validateHandle);
}