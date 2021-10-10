function* gen1() {
    yield 3;
    yield 6;
    yield 9;
    yield 12;
}

function* gen2() {
    yield 8;
    yield 10;
}

function* take(n:number, generator:Generator) {
    for (let x of generator) {
        if (n <= 0) return;
        n--;
        yield x;
    }
}

function* braid(gen1: ()=> Generator, gen2: ()=> Generator){
    let select=1;
    const g1 = gen1();
    const g2 = gen2();
    let gen1Done=0;
    let gen2Done=0;
    while(1){
        if(select === 1){
            let ans=g1.next();
             if(!ans.done){
                yield ans.value;
             }
             else{
                gen1Done=1;
             }
            if(gen2Done==0){
                select=2;  
             }
             if(gen2Done==1&&gen1Done==1){
                 return;
             }
        }
       if(select===2){
            let ans=g2.next();
            if(!ans.done){
                yield ans.value;
            }
            else{
                gen2Done=1;
            }
             if(gen1Done==0){
                 select=1;  
              }
              if(gen2Done==1&&gen1Done==1){
                return;
            }
          }
        }
}

function* biased(gen1: ()=> Generator, gen2: ()=> Generator){
    let select=1;
    const g1 = gen1();
    const g2 = gen2();
    let times=0;
    let gen1Done=0;
    let gen2Done=0;
    while(1){
        if(select === 1){
            let ans=g1.next();
            times++;
             if(!ans.done){
                yield ans.value;
             }
             else{
                gen1Done=1;
             }
            if(gen2Done==0 && times==2){
                select=2;
                times=0;  
             }
             if(gen2Done==1&&gen1Done==1){
                 return;
             }
        }
       if(select===2){
            let ans=g2.next();
            if(!ans.done){
                yield ans.value;
            }
            else{
                gen2Done=1;
            }
             if(gen1Done==0){
                 select=1;  
              }
              if(gen2Done==1&&gen1Done==1){
                return;
            }
          }
        }
}

for(let n of take(4, braid(gen1, gen2))){
    console.log(n);
}
for (let n of take(4, biased(gen1,gen2))) {
    console.log(n);
    }
    
