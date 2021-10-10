section .rodata
    winner: db "The winner is drone number : %d , with a score of %d !" , 10 , 0

section .data
    MAX_INT: dd 214748364
   

section .bss
  drones resd 1
  curr resd 1
  counter resd 1
  low resd 1

section .text
    global initSchedulerCor
    global resume
    global next
    
    extern printer_steps
    extern drone_array
    extern SPT
    extern SPMAIN
    extern printf
    extern CORS
    extern amount
    extern printer_off
    extern cycles

initSchedulerCor:
    mov eax, schedFunc
    mov [SPT], esp 
    mov esp, [ebx + 4] 
    push eax 
    pushfd 
    pushad 
    mov [ebx + 4], esp 
    mov esp, [SPT]
    call next


schedFunc:
    mov dword [curr] , ebx
    mov dword [counter] , 0
    mov esi , [CORS]
    mov edx , [amount]
    mov [drones] , edx


main_loop:
    mov eax , [counter]
    mov edx , 0
    div dword [amount]                   ; (i / N)
    mov eax , edx                           ; eax = (i % N)
    mov ebx , 44                            ; get offset for        
    push edx        
    mul ebx                                 ; 44 * (i % N)
    pop edx
    mov edi , [drone_array]
    add edi , eax
    cmp dword [edi] , 1
    jne check_print
    mov ebx , [esi + (4*edx)]
    call resume

    check_print:
        mov eax , [counter]
        mov edx , 0
        mov ebx , [printer_steps]
        div ebx 
        cmp edx , 0
        jne check_rounds
        mov edx , [printer_off]
        mov ebx , [esi + edx]
        call resume

    check_rounds:
        mov eax , [counter]
        cmp eax , 0                         ; dont eliminate on first loop
        je end_of_loop
        mov edx , 0
        mov ebx , [amount]
        div ebx                         ; eax = (i / N) | edx = (i % N)
        cmp edx , 0
        jne end_of_loop
        mov edx , 0
        mov ebx , [cycles]
        div ebx 
        cmp edx , 0
        jne end_of_loop
        call eliminate

end_of_loop:
    inc dword [counter]
    cmp dword [drones] , 1
    jne main_loop

    call winning

    mov esp , [SPT]
    ret



eliminate:
    pushad
    mov edx , [MAX_INT]
    mov [low] , edx
    mov esi , [drone_array]
    mov ecx , 0

    check_if_active:
    mov edi , esi
    mov eax , ecx
    mov ebx , 44
    push edx
    mul ebx
    pop edx
    add edi , eax

    cmp dword [edi] , 1
    jne next_drone
    mov ebx , [edi + 36]                    ; ebx = score of drone i
    cmp ebx , dword [low] 
    jge next_drone
    mov [low] , ebx
    mov edx , ecx                           ; edx -> drone with the lowest score 

    next_drone: 
    mov ebx , [amount]
    dec ebx
    cmp ecx , ebx
    jge end_of_elimination
    inc ecx
    jmp check_if_active

    end_of_elimination:
    dec dword [drones]
    mov eax , edx
    mov ebx , 44
    mul ebx
    add esi , eax
    mov dword [esi] , 0
    popad
    ret
    


winning:
    pushad
    mov esi , [drone_array]
    mov ecx , 0

    check_if_active_winner:
    mov edi , esi
    mov eax , ecx
    mov ebx , 44
    mul ebx
    add edi , eax
    cmp dword [edi] , 1
    je found_winner
    inc ecx
    jmp check_if_active_winner

    found_winner:
    inc ecx
    pushad
    push dword [edi + 36]
    push ecx
    push winner
    call printf
    add esp , 12
    popad

    popad
    ret



resume:; save state of current co-routine
     pushfd
     pushad
     mov edx, [curr]             ; CURR -> the struct of the current co-routine
     mov [edx+4], esp               ; save current ESP

   next:                      ; load ESP for resumed co-routine
     mov esp, [ebx + 4]              ; Get to the wanted routine (in ebx)
     mov [curr], ebx              ; declare it as current routine
     popad                       ; restore resumed co-routine state
     popfd
     mov ebx , [curr]
     ret



    
