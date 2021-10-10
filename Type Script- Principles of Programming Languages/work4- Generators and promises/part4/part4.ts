import { reject } from "ramda";

function f(x:number): Promise<number> {
    const divisorPromise = (num: number): Promise<number> => {
        return new Promise<number>((resolve, reject) => {
            if (num==0) 
              reject("cant divide by 0");
            else
             resolve(1/x);
        });
    }
    const output = divisorPromise(x);
    output
        .then((content:number) => console.log("The division is successful, result:", content))
        .catch((err)=> console.error(err));
        return output;
      }

function g(x:number):number {
    return x*x;
}

function h(x:number):Promise<number> {
    return f(g(x));
}


const slower =<T,U> (values : readonly T[]): Promise<string> => {
    return new Promise<string>( (resolve, reject) =>{
      Promise.race([values[0],values[1]])
              .then((message) =>{
                  Promise.all([values[0],values[1]])
                    .then((values) => values[1] ===message ? resolve("(0,'"+values[0]+"')") : resolve("(1,'"+values[1]+"')"))
                    .catch((err) => reject(err));
              })
              .catch((err) => reject(err));
    }) 
}


const promise1 = new Promise(function(resolve,reject){
    setTimeout(resolve, 100, 'one');
});
const promise2 = new Promise(function(resolve,reject){
    setTimeout(resolve, 500, 'two');
});

slower([promise1,promise2]).then(function(value) {
    console.log(value);
});


'use strict';

//module.exports = race;

function race2<T>(arr:Promise<T>[]) {
  let resolve: Function, reject: Function;
  const clear = () => resolve = reject = Function.prototype;

  const winner = new Promise(function(res, rej) {
    resolve = rej;
    reject = res;
  });

  arr.forEach(function(promise) {
    promise.then(function(res) {
      resolve(res);
      clear();
    });

    promise.catch(function(err) {
      reject(err);
      clear();
    });
  });
  return winner;
}
