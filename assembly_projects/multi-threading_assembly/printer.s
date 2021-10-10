section .rodata
    target_location: db "Target location : (%f , %f)" , 10 , 0
    drone_info: db "id: %d | (%f , %f) | angle: %f | speed: %f | targets destroyed: %d " , 10 , 0

section .data
    curr_drone dd 0
    x_loc dd 0
    y_loc dd 0
    angle dd 0
    speed dd 0
    score dd 0
    id dd 0
;
section .bss


section .text
  global printerFunc
  global init_print

  extern CORS
  extern scheduler_off
  extern resume
  extern SPT
  extern amount
  extern printf
  extern fprintf 
  extern sscanf
  extern x_target
  extern y_target
  extern drone_array


init_print:
    mov eax , printerFunc
    mov [ebx] , eax
    mov [SPT], esp 
    mov esp, [ebx + 4] 
    push eax 
    pushfd 
    pushad 
    mov [ebx + 4], esp 
    mov esp, [SPT]
    ret



printerFunc:
    mov dword [curr_drone] , 0

    sub esp , 8
    fld qword [y_target]                        
    fstp qword [esp]

    sub esp , 8
    fld qword [x_target]                        
    fstp qword [esp]
                          
    push target_location
    call printf                                     ; prints the target location
    add esp, 20
    mov dword [id] , 0

print:

    mov esi, [drone_array]                          ; eax <- drone array
    mov eax, [curr_drone]                           ; ecx <- curr_drone
    mov ebx, 44                                 
    mul ebx
    add esi , eax
    mov ebx , 0
    cmp dword [esi], 1                                    ; checks if the drone is active
    je print_drone
    inc dword [curr_drone]
    mov eax, [curr_drone]
    mov ecx, [amount]                                      ; edx <- amount of drones
    mov edx, 0
    div ecx
    mov [curr_drone], edx
    cmp edx, 0                                      ; checking if we have reached the last drone
    je finish_printing
    jmp print

print_drone:                              ; ebx -> [flag  , x , y , angle , speed , score ]                      
    mov eax, [curr_drone]                       ; EAX = curr_drone
    inc eax                                     ; EAX = curr_drone + 1, EAX = id
    


    push dword [esi + 36]                                      

    sub esp , 8
    fld qword [esi + 28]                        
    fstp qword [esp]

    sub esp , 8
    fld qword [esi + 20]                        
    fstp qword [esp]

    sub esp , 8
    fld qword [esi + 12]                        
    fstp qword [esp]

    sub esp , 8
    fld qword [esi + 4]                        
    fstp qword [esp]
                    
    mov edx ,  dword [esi + 40]
    inc edx
    push edx
    
    push drone_info
    call printf
    add esp, 44


    

    inc dword[curr_drone]
    mov eax, [curr_drone]
    mov ecx, [amount]      ; edx <- amount of drones
    mov edx, 0
    div ecx
    mov [curr_drone], edx
    cmp edx, 0      ; checking if we have reached the last drone
    je finish_printing
    jmp print

finish_printing:
    mov eax , [scheduler_off]
    mov esi , [CORS]
    mov ebx , [esi +  eax]
    call resume
    jmp printerFunc




