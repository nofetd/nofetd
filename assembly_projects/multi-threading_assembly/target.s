section	.rodata	
    max: dd 65535
    div: dd 100

section .bss
    x_target resq 1                    
    y_target resq 1  
    rand resw 1                  

section .text  
  global newCord
  global x_target
  global y_target
  global targetInit

  extern SPT
  extern scheduler_off
  extern drone_idx
  extern resume
  extern CORS
  extern get_rand
  extern printf
  extern fprintf 
  extern sscanf
  extern malloc 
  extern calloc 
  extern free

targetInit:
    mov eax, create
    mov [SPT], esp 
    mov esp, [ebx + 4] 
    push eax 
    pushfd 
    pushad 
    mov [ebx + 4], esp 
    mov esp, [SPT]
    ret

create:                  ; Initialise the target
    call newCord
    mov esi , [CORS]
    mov eax , [drone_idx]     
    mov ebx , [esi + (4*eax)]            
    call resume
    jmp create

newCord:
    push ebp
    mov ebp , esp
    pushad
    call get_rand      ; EAX = random(2 BYTE) 0x1234
    mov cx, ax
    mov word [rand], cx
    fild dword [rand]
    fidiv dword [max]                      ; EAX = EAX \ MAXINT
    fimul dword [div]                       ; EAX = EAX * 100
    fstp qword [x_target]                  ; X = (EAX / MAXINT) * 100 -> [0,100]
    call get_rand      
    mov cx, ax
    mov word [rand], cx
    fild dword [rand]
    fidiv dword [max]               ; EAX = EAX \ MAXINT
    fimul dword [div]                    ; EAX = EAX * 100
    fstp qword [y_target]         ; Y = (EAX / MAXINT) * 100 -> [0,100]
    popad
    pop ebp
    ret    
