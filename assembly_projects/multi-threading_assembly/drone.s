
%macro generate_values 2
   
    call get_rand                      ; EAX = random(2 BYTE) 0x1234
    mov cx, ax
    mov word [random_number], cx
    fild dword [random_number]
    fidiv dword [MAX_INT]                       ; EAX = EAX \ MAXINT
    fimul %1                                    ; EAX = EAX * init_values first arg
    fstp %2                                    ; Y = (EAX / MAXINT) * 100 -> [0,100]

%endmacro

%macro get_drone_stat_ptr 1
    mov %1 , [drone_array]
    mov eax , [drone_idx]
    mov ebx , 44
    mul ebx
    add %1 , eax
%endmacro

%macro range_test 2
    fild dword [ZERO]
    fcomip                              ; compare with zero
    jc %%test_upper_limit           ; jg
    fild %1
    fadd
    
    %%test_upper_limit:
    fild %1
    fcomip                              ; compare with zero
    jnc %%save_data           ; jg
    fild %1
    fsubp
    %%save_data:
    fstp %2
%endmacro

section	.rodata	
    print_num: db "Single value: %f ", 10,  0	        ; format string sor fprinf
    print_cor: db "Drone print  : (%f , %f) , angle: %f , speed: %f", 10, 0	        ; format string sor fprinf
    winner: db "The drone [ %d ] is close enough!" , 10 , 0
    dist: db "The distance of drone [ %d ] from the target is %f ", 10 , 0
    
    ZERO: dd 0
    ONE_EIGHTY: dd 180
    MAX_INT: dd 65535
    board: dd 100
    MAX_ANGLE: dd 360
    MAX_angle: dd 120
    MAX_speed: dd 20
    speed_RANGE: dd 10
    angle_RANGE: dd 60


section .bss
  
    drone_idx resq 1



    angle resq 1
    speed resq 1

    random_number resw 1                  



section .text  
  global createDrone
  global initDroneCor
  global drone_idx

  extern resume
  extern drone_array
  extern get_rand
  extern CORS
  extern x_target
  extern y_target
  extern destroy_distance
  extern SPT
  extern scheduler_off
  extern amount
  extern printf
  extern fprintf 
  extern sscanf
  extern malloc 
  extern calloc 
  extern free

initDroneCor:
    mov eax, mayDestroy
    mov [SPT], esp 
    mov esp, [ebx + 4] 
    push eax 
    pushfd 
    pushad 
    mov [ebx + 4], esp 
    mov esp, [SPT]
    ret


createDrone:                  ; Initialise the drones
    push ebp
	mov ebp, esp	
	pushad

    mov edx , ebx
    add edx , 8
    mov ecx , [edx]

    mov [drone_idx] , ecx

    get_drone_stat_ptr esi
    mov dword [esi] , 1

    generate_values dword [board], qword [esi + 4]      ; X
    generate_values dword [board], qword [esi + 12]     ; Y
    generate_values dword [MAX_ANGLE], qword [esi + 20]      ; ANGLE
    generate_values dword [board], qword [esi + 28]      ; SPPED

    mov dword [esi + 36] , 0
    mov ecx , dword [drone_idx]
    mov dword [esi + 40] , ecx

    call updateAngleSpeed
    call moveDrone

    popad
    pop     ebp             	; Restore caller state
    ret     


mayDestroy:

    mov edx , ebx
    add edx , 8
    mov ecx , [edx]
    mov [drone_idx] , ecx


    get_drone_stat_ptr esi
    
    finit
    ; ------------------------------  check position -----------------------------
    fld qword [esi + 4]
    fsub qword [x_target]
    fmul st0

    fld qword [esi + 12]
    fsub qword [y_target]
    fmul st0

    fadd
    fsqrt
    ; ------------------------------  check position -----------------------------

    fld dword [destroy_distance]
    fcomip                              ; compare with given distance
    jc update_stats  
          
    inc dword [esi + 36]                ; score ++
    mov edi , [CORS]
    mov eax , [amount]
    mov ebx , [edi + (4*eax)]
    call resume
    
    update_stats:               
    call updateAngleSpeed       
    call moveDrone           ; new_x = cos(angle)*speed | new_y = sin(angle)*speed     

    mov esi , [CORS]
    mov eax , [scheduler_off]
    mov ebx , [esi + eax]
    call resume
    jmp mayDestroy

moveDrone:
    finit
    fld qword [esi + 20]
    fldpi
    fmulp
    fild dword [ONE_EIGHTY]
    fdivp
    fsincos

    fld qword [esi + 28]
    fmulp 
    fld qword [esi + 4]
    faddp

    range_test dword [board] ,  qword [esi + 4]

    fld qword [esi + 28]
    fmulp
    fld qword [esi + 12]
    faddp

    range_test dword [board] , qword [esi + 12]
    ret


updateAngleSpeed:
    generate_values dword [MAX_angle], qword [angle]  
    generate_values dword [MAX_speed], qword [speed]   
    finit
    fld qword [angle]
    fild dword [angle_RANGE]
    fsubp
    fld qword [esi + 20]
    fadd 

    range_test dword [MAX_ANGLE] , qword [esi + 20]
    
    finit
    fld qword [speed]
    fild dword [speed_RANGE]
    fsubp
    fld qword [esi + 28]
    fadd 

    fild dword [ZERO]
    fcomip                              ; compare with zero
    jc test_speed_upper_limit           ; jg
    fild dword [ZERO]
    
    test_speed_upper_limit:
    fild dword [board]
    fcomip                              ; compare with zero
    jnc save_speed           ; jg
    fild dword [board]
    save_speed:
    fstp qword [esi + 28] 

    ret

