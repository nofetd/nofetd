section	.rodata	
    argumant_str_int: db "%d" , 0
    argumant_str_float: db "%f" , 0




section .bss
    current_arg_ptr resd 1

    amount resd 1
    cycles resd 1
    printer_steps resd 1
    destroy_distance resq 1 
    seed resd 1

    cors_num resd 1
    CORS resd 1

    target_off resd 1
    printer_off resd 1
    scheduler_off resd 1

    drone_array resd 1

    rand resw 1
    SPT resd 1                          
    SPMAIN resd 1

;      CORS -> [CO1 , CO2 , ...] : 4N b | COi -> [Funci , SPi , CO-Index] : 12 b | STKi -> [... , SPi] : 1 kb

section .data
    STKSIZE: dd 1024
    

section .text 
    
  global main
  global get_rand
  global CORS
  global amount
  global SPMAIN
  global SPT
  global drone_array
  global cycles
  global printer_steps
  global destroy_distance
  global scheduler_off  
  global target_off
  global printer_off

  extern initSchedulerCor
  extern initDroneCor
  extern targetInit
  extern init_print
  extern createDrone
  extern newCord
  extern printf
  extern fprintf 
  extern sscanf
  extern malloc 
  extern calloc 
  extern free



%macro define_array_of_cors 1
    push 4                  ; the size of each CORi pointer 
    push %1                 ; the amount of cors
    call calloc
    add esp , 8             ; EAX = return value from calloc
    mov [CORS] , eax        ; CORS = EAX
%endmacro

%macro get_argumant 2
    add dword [current_arg_ptr] , 4                 ; ECX = args[i]
    push %1
    push %2
    mov ecx , [current_arg_ptr]
    push dword [ecx]
    call sscanf
    add esp , 12
%endmacro 



get_rand:
    pushad
    mov ax ,  [rand]

    mov bx , 1                         ; 0000 0000 0000 0001
    and bx , ax
    mov cx , 4                         ; 0000 0000 0000 0100
    and cx , ax
    shr cx , 2
    xor bx , cx
    mov cx , 8                         ; 0000 0000 0000 1000
    and cx , ax
    shr cx , 3
    xor bx , cx
    mov cx , 32                        ; 0000 0000 0010 0000
    and cx , ax
    shr cx , 5
    xor bx , cx

    shl bx , 15
    shr ax , 1
    or ax , bx
    mov [rand] , ax

    popad
    mov eax , 0
    mov ax , [rand]
    ret        

  


main:
	push ebp
	mov ebp, esp	
	pushad

handle_args:
    mov ebx , 0
    mov edx , [ebp + 12]                 ; edx contains a pointer to the args
    mov ecx , edx 
    mov [current_arg_ptr] , ecx

    get_argumant amount , argumant_str_int
    get_argumant cycles , argumant_str_int
    get_argumant printer_steps , argumant_str_int
    get_argumant destroy_distance , argumant_str_float
    get_argumant seed , argumant_str_int


    mov ecx , [seed]          ; tranfer seed to edx
    mov [rand] , ecx          ; move the seed into the random number

    mov edx , [amount]
    add edx , 3
    mov [cors_num] , edx


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;INITIALISATION;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

init_array:                             ; Initialise co-routines -> Number of drones + target + scheduler + printer
    define_array_of_cors dword [cors_num]     ; Create the coroutines array
    
    mov ecx , 0
init_cors_struct:
    push ecx                            ; Save counter
    push 4                              ; the size of Func and SPP
    push 3                              ; Func , spp , index
    call calloc
    add esp , 8                         ; EAX = return value from calloc
    pop ecx
    mov ebx , [CORS]   
    mov [ebx + (4*ecx)] , eax
    mov [eax + 8] , ecx
    inc ecx
    cmp ecx, dword [cors_num]
    jl init_cors_struct

    mov ecx , 0
init_stacks_in_cors:
    push ecx
    push dword [STKSIZE]                      ; the size of each stack
    push 1                              ; Create a stack for each co routine      
    call calloc
    add esp , 8                         ; EAX = return value from calloc
    
    mov edi, [CORS]                     ; CORS[i] -> EBX
    pop ecx
    mov edx , [edi + (4*ecx)]
    mov ebx , eax
    add ebx , dword [STKSIZE]
    mov [edx + 4], ebx
    inc ecx

    cmp ecx, dword [cors_num]
    jl init_stacks_in_cors


init_drones:                            ; flag4b + x8b + y8b + angle8b + speed8b + targetDestroyed4b + index4b = 44b
    mov edx , [amount]
    push  44                          ; Each block is 40 bytes
    push edx                            ; edx -> the amount pof drones
    call calloc
    add esp , 8                         ; EAX = return value from calloc
    mov [drone_array] , eax             ; drone_array = EAX

init_target:                            ; Initialize the target
    call newCord

    mov ecx , 0
    mov esi , [CORS]
drones_creator:                         
    mov ebx , [esi + (4*ecx)]
    call createDrone
    inc ecx
    cmp ecx, [amount]
    jl drones_creator



    mov ecx , 0
    mov esi , [CORS]
init_cors:
    mov ebx , [esi + (4*ecx)]
    call initDroneCor
    inc ecx
    cmp ecx , dword [amount] 
    jl init_cors

    mov ebx , [esi + (4*ecx)]
    mov eax , 4
    mul ecx
    mov dword [target_off] , eax
    call targetInit

    inc ecx
    mov ebx , [esi + (4*ecx)]
    mov eax , 4
    mul ecx
    mov dword [printer_off] , eax
    call init_print

    inc ecx
    mov ebx , [esi + (4*ecx)]
    mov eax , 4
    mul ecx
    mov dword [scheduler_off] , eax
    call initSchedulerCor


end:
	popad			
	mov esp, ebp	
	pop ebp
    mov eax, 1                      ; call exit
    int 0x80    
