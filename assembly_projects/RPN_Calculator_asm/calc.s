%macro start 1 			
	push ebp 			        
    mov ebp, esp 
    sub esp, %1 			
%endmacro

%macro get_number 0
    mov dword [num_counter] , 0     
    mov edx,  [stack]               
    mov ecx , [stack_pointer]       
    mov eax , [edx + (4*ecx)]       
%%loop_num:
    mov edx , 0                     
    mov dl , [eax]                  
    push edx                        
    inc dword [num_counter]         
    inc eax                         
    mov ebx , [eax]                 
    mov eax , ebx                   
    cmp dword eax , 0               
    jne %%loop_num
%endmacro

%macro end_calc 0			
	push dword[operator_count]		
	call itoh
	add esp, 4
	push eax				   
	push string_format	          
	call printf	
	add esp ,8				
	mov ebx,dword[operator_count] 
   	mov eax,1
  	int 0x80
	nop
%endmacro 

%macro ending 2
	mov     eax, %1   		
    add     esp, %2          	
    pop     ebp             	
    ret                     	
%endmacro

%macro get_link_ptr 0
    push 5        			
	call calloc           		
	add esp, 4
%endmacro

%macro print_prompt 0			
	push calc 		
	call printf
	add esp, 4
	push dword[stdout]
    call fflush
    add esp, 4
%endmacro

%macro get_input 0
    mov dword [buffer] , 0     
    push dword [stdin]          
    push dword 80              
    push dword buffer           
    call fgets                  
    add esp, 12  
    cmp dword[debug], 1
    jne %%no_debug
    push eax              
    push debug_input_str          
    push dword[stderr]
    call fprintf
    add esp, 12
    %%no_debug:
%endmacro

%macro free_mem 1
	mov edx,  [stack]                              
	mov ecx , %1                    
	mov ebx , [edx + (4*ecx)]                      
	free_number ebx
%endmacro

%macro free_number 1
	mov ebx, %1
	mov dword[num_counter], 0
%%clean_next_digit:
	inc dword[num_counter]
	push ebx
	inc dword ebx
	mov eax, [ebx]
	mov ebx, eax
	cmp ebx, 0
	jne %%clean_next_digit
%%finish_clean:
	dec dword[num_counter]
	call free
	add esp, 4
	cmp dword[num_counter], 0
	jne %%finish_clean
%endmacro


section	.rodata			; we define (global) read-only variables in .rodata section
    missing_args: db "Error: Insufficient Number of Arguments on Stack" , 10 , 0
    overflow_string: db "Operand Stack Overflow", 10, 0
    debug_input_str: db "DEBUG MODE: %s" ,0
	debug_result_str: db "DEBUG MODE: result pushed to stack is: %X" ,10, 0
    calc: db "Calc: ", 0
    string_format: db "%s", 10, 0	
    hexa_format: db "%X" , 0      
    new_line: db  10 , 0
    


section .bss
    next: resd 1            
    buffer:        resb 80        
    stack: resd 1                   
    stack_pointer: resd 1           
    stack_size: resd 1             
    operator_count: resd 1	
    num_counter: resd 1        
    backward_str: resb 34
    final_str: resb 34
    carry: resd 1           
    debug: resd 1               
	first_num: resd 1				

section .text
  align 16
  global main
  global options
  global quit
  global poping
  global my_calc
  global num_input
  global itoh
  global duplicate
  global num
  global add_func
  global or
  global and
  extern printf
  extern fprintf 
  extern fflush
  extern malloc 
  extern calloc 
  extern free 
  extern getchar 
  extern fgets
  extern stdin 
  extern stdout
  extern stderr

ascii_to_num:       
        mov eax, 0
        mov ebx, 0
        mov bl, [ecx]

    next_ascii_char:
        cmp bl , '9'
        jg letter
        sub bl, '0'			
        jmp continue_ascii_num
    letter:
        sub bl , 55    
    continue_ascii_num:        
        add eax, ebx 		
        inc ecx				
        mov bl, [ecx]		
        cmp bl, 0			
        jne multiply_numbers
        ret		
    multiply_numbers:    
        mov edx, 16			
        mul edx				
        jmp next_ascii_char

or:
    start 0 
    inc dword[operator_count]                       
    cmp dword[stack_pointer] , 0         
    jne continue_first_or                           
    push missing_args                 
    call printf
    add esp,4                 
    jmp end_or                           
    
continue_first_or:
   
    dec dword[stack_pointer]                        
    mov edx,  [stack]                               
    mov ecx , [stack_pointer]                       
    mov eax , [edx + (4*ecx)]                       

    cmp dword[stack_pointer] , 0         
    jne continue_second_or                           
    push missing_args                 
    call printf
    add esp,4              
    jmp end_or

continue_second_or:

    dec dword[stack_pointer]                        
    mov edx,  [stack]                               
    mov ecx , [stack_pointer]                       
    mov ebx , [edx + (4*ecx)]                              
loop_or:
    
    mov dl , [eax]
    or [ebx] , dl                                                        

continue_or:    
    inc eax
    cmp dword[eax], 0                             
    je end_or
    inc ebx
    cmp dword[ebx], 0                             
    je chain_or
    
    mov eax, [eax]                              
    mov ebx, [ebx]                              

    jmp loop_or                                    

chain_or:  
    mov ecx, [eax + 1]
    mov [ebx + 1], ecx                              
    jmp end_or

end_or:
    inc dword[stack_pointer] 
	free_mem dword[stack_pointer]	


    ending eax, 0
and:

    start 0
    
    inc dword[operator_count]                       

    cmp dword[stack_pointer] , 0         
    jne continue_first_and                           
    push missing_args                 
    call printf
    add esp,4                  
    jmp end_and                               
    
continue_first_and:
   
    dec dword[stack_pointer]                       
    mov edx,  [stack]                              
    mov ecx , [stack_pointer]                      
    mov eax , [edx + (4*ecx)]                      

    cmp dword[stack_pointer] , 0         
    jne continue_second_and                           
    push missing_args                 
    call printf
    add esp,4                  
    jmp end_and

continue_second_and:

    dec dword[stack_pointer]                        
    mov edx,  [stack]                             
    mov ecx , [stack_pointer]                       
    mov ebx , [edx + (4*ecx)]                          
                                    
loop_and:
    
    mov dl , [eax]
    and [ebx] , dl                                                        

continue_and:    
    inc eax
    cmp dword[eax], 0                              
    je empty_eax
    inc ebx
    cmp dword[ebx], 0                               
    je empty_ebx
    
    mov eax, [eax]                             
    mov ebx, [ebx]                             

    jmp loop_and                                  

empty_eax:
    inc  ebx                                         
    mov dword[ebx], 0
    jmp end_and

empty_ebx:
    inc  eax                                          
    mov dword[eax], 0
    jmp end_and

end_and:
    inc dword[stack_pointer] 
	free_mem dword[stack_pointer]

    ending eax, 0

options:                          

    start 0

    mov ecx , [buffer]                   
    
    cmp ecx , 0xA71              
    je choose_quit
    
    cmp ecx , 0xA2B                
    je choose_add                   

    cmp ecx , 0xA70                
    je choose_pop

    cmp ecx , 0xA64                
    je choose_dup

    cmp ecx , 0xA26             
    je choose_and

    cmp ecx , 0xA7C                
    je choose_or

    cmp ecx , 0xA6E                
    je choose_num
    
    jmp num_input_func                    
    jmp next_action

choose_quit:
    call quit  
    jmp next_action

choose_pop:
    call poping
    jmp next_action

choose_or:
    call or
    jmp next_action

choose_and:
    call and
    jmp next_action

num_input_func:
    call num_input
    jmp next_action

choose_dup:
    call duplicate
    jmp next_action

choose_add:
    call add_func
    jmp next_action

choose_num:
    call num
    jmp next_action

next_action:
    ending eax, 0

quit:                               
    start 0

mem_clean:
	cmp dword[stack_pointer], 0
	jle continue_quit
	dec dword[stack_pointer]                      
	free_mem dword[stack_pointer]
	jmp mem_clean

continue_quit:
	push dword[stack]
	call free
	add esp, 8


    end_calc

    ending eax, 0

add_func:
    start 0
    inc dword[operator_count]                      
    cmp dword[stack_pointer], 2
	jge continue_adding
	push missing_args
	call printf
	add esp, 4             
    jmp finish_add                                
    
continue_adding:
   
    dec dword[stack_pointer]                      
    mov edx,  [stack]                              
    mov ecx , [stack_pointer]                       
    mov eax , [edx + (4*ecx)]                      

    dec dword[stack_pointer]                       
    mov edx,  [stack]                               
    mov ecx , [stack_pointer]                       
    mov ebx , [edx + (4*ecx)]                       
    
              
    mov dl, 0
    mov dword[carry],0                                      
loop_add:
    mov dl , [eax]
    add dl, [carry]                             
    add [ebx] , dl                                    
    cmp dword[debug], 1
    jne no_debug
    push eax
    push dword[ebx]	                 
    push debug_result_str          
    push dword[stderr]
    call fprintf
    add esp, 12
    pop eax
no_debug:
    mov dword[carry], 0                      
    mov dl, [ebx]
    cmp dl, 16                                   	
    jge remainder_add

continue_add:    
    inc eax
    cmp dword[eax], 0                             
    je eax_finished
    inc ebx
    cmp dword[ebx], 0                              
    je chain_add
    mov eax, [eax]                              	
    mov ebx, [ebx]                             		
    jmp loop_add                                   
chain_add:
    mov ecx, dword[eax]							
    cmp dword[carry], 1                     
    je remainder_chain
no_remainder_chain:    
    mov dword[ebx], ecx                             
    jmp end_add_func
remainder_chain:    
	mov dl, [ecx]
	cmp dl ,15								
	jne next_not_F
	mov dl, 0
	mov [ecx], dl
	inc ecx
	mov ecx, dword[ecx]
	jmp remainder_chain
next_not_F:	
    mov dl, 1                            	
    add [ecx] ,dl                          
    mov ecx , dword[eax]
    mov dword[ebx], ecx            
    mov dword[carry], 0
    jmp end_add_func
remainder_add:
    mov dword[carry] ,1                                
    mov dl, [ebx]
    sub dl, 16                             
    mov [ebx], dl
    jmp continue_add
eax_finished:
    cmp dword[carry], 0                   
    je end_add_func             

    inc ebx                                    
    cmp dword[ebx], 0                         
    je empty_ebx_next    
    mov ebx, [ebx]                              
    
    mov dl, [ebx]
    cmp dl, 15                              
    je full_next
    
    mov dl, 1
    add [ebx], dl                              
    mov dword[carry], 0
    jmp end_add_func

full_next:
    mov dl, 0
    mov [ebx], dl
    jmp eax_finished

empty_ebx_next:
    mov ecx ,1
    push ecx
    get_link_ptr                                 
    pop ecx 
    mov dl, 1
    mov [eax] , dl                           
    mov dword [eax + 1], 0                     
    mov [ebx], eax
    mov dword[carry], 0
    jmp end_add_func

end_add_func:
    inc dword[stack_pointer]
	free_mem dword[stack_pointer]    
finish_add:
    ending eax,0

poping:

        start 0
        inc dword[operator_count]              
        cmp dword[stack_pointer], 0            
        je print_not_enough
        mov dword [num_counter] , 0        
        dec dword [stack_pointer]              
        mov ecx , [stack_pointer]
        mov edx , [stack]
        mov eax , [edx + (4*ecx)]
    next_number:
        mov edx , 0
        mov dl , [eax]
        push edx
        inc dword [num_counter]
        add dword eax , 1
	je print_number
        mov ebx , [eax] 
        mov eax , ebx
        cmp dword eax , 0
        jne next_number

    print_number:
        push hexa_format
        call printf
        add esp, 8
        dec dword[num_counter]
        cmp dword[num_counter], 0
        jne print_number

        push new_line
        call printf
        add esp, 4
        jmp end_pop
        
    print_not_enough:
        push missing_args
        call printf
        add esp, 4
        jmp loop

    end_pop:
	free_mem dword[stack_pointer]
        ending eax, 0


duplicate:

    start 0
    inc dword [operator_count]                             

    cmp dword[stack_pointer] , 0         
    jne next_check_duplicate                           
    push missing_args                 
    call printf
    add esp,4                   
    jmp end_duplicate                                    
    
    next_check_duplicate:  
	mov ebx, [stack_pointer]    
    	cmp ebx, [stack_size]
    	jnz continue_duplicate       
    	push overflow_string 		
    	call printf
    	add esp, 4
    	jmp end_duplicate    
    
    continue_duplicate:
        mov dword [next] , 0
        dec dword [stack_pointer]                         
        get_number                                       

    new_link:
        inc ecx                                            
        push ecx
        get_link_ptr                                                    
        pop ecx
        pop edx  
        cmp ecx, 1                                         
        jg not_first_duplicate

        mov [eax] , dl                                    
        mov dword [eax + 1], 0                             
        mov [next], eax                              
        jmp next_digit_duplicate                   

    not_first_duplicate:
        mov [eax] , dl                                               
        mov ebx , [next]                              
        mov dword [eax + 1] , ebx                           
        mov [next], eax                    
    next_digit_duplicate:                       
        dec dword [num_counter]                 
        cmp dword [num_counter], 0              
        jne new_link                            
    end_dup:                                                    
        inc dword [stack_pointer]                                           
        mov ecx , [stack_pointer]               
        mov edx , [stack]                       
        mov [edx + (4*ecx) ], eax                     
        inc dword [stack_pointer]                           
        mov dword [next] , 0




    end_duplicate:
        ending eax, 0

num:
        start 0

        inc dword [operator_count]            
        cmp dword[stack_pointer] , 0         
    	jne not_empty_num                          
    	push missing_args                 
	call printf
   	add esp,4          
    not_empty_num:
        mov dword [num_counter] , 0           
        dec dword [stack_pointer]              
        mov ecx , [stack_pointer]
        mov edx , [stack]                     
        mov eax , [edx + (4*ecx)]             

    counting_next_number:
        mov edx , 0
        mov dl , [eax]                         
        inc dword [num_counter]               
        mov eax , [eax + 1]                                 
        cmp dword eax , 0                     
        jne counting_next_number
        mov ebx, [num_counter]              
        mov [buffer], ebx                      
        mov edx, 0                      
        mov dl, [buffer]                        
        mov dword[num_counter], 0                   
    another_chain_number:               
        cmp dl, 0                                    
        je another_end_conversion             
        inc dword [num_counter]

    another_create_chain:
        cmp dl, '9'                
        jle another_decimal_conversion

        sub dl, 55                 
        jmp another_create_link 
    
    another_decimal_conversion:
        sub dl, '0'

    another_create_link:                   
        push edx
        get_link_ptr                      
        pop edx
        cmp dword [num_counter] ,  1      
        sub dl, 208
        jge another_not_first_num

        mov [eax] , dl                     
        mov dword [eax + 1] , 0            
        mov [next], eax               
        inc dword[num_counter]
        jmp another_move_digit             
        
    another_not_first_num:
        mov [eax] , dl
        mov ebx , [next]
        mov dword [eax + 1] , ebx
        mov [next], eax

    another_move_digit:
        mov ecx , [num_counter]            
        mov dl, [buffer + ecx]             
        jmp another_chain_number           
        another_end_conversion:  
	pushad
	free_mem dword[stack_pointer]
	popad
        mov ecx , [stack_pointer]              
        mov edx , [stack]                      
        mov [edx + (4*ecx) ], eax              
        inc dword [stack_pointer]              
        mov dword [next], 0               
        mov dword [num_counter], 0            


        
    end_num:
        ending eax, 0
    
itoh:					
	start 4	
	
	mov eax, [ebp+8]    		
	mov ebx, 0			
	mov ecx, 16			

start_conversion:			
	
	mov edx, 0			
	div ecx				
	
	cmp edx, 10			
	jge irregular_hex		

	add edx , '0'			
	mov dword[backward_str + ebx] ,edx 
	jmp continue_conversion	
	
irregular_hex:
	
	add edx , 55			
	mov dword[backward_str + ebx] , edx 
	jmp continue_conversion	

continue_conversion:
	
	cmp eax, 0			
	je reverse_string		
	
	inc ebx 			
	jmp start_conversion		

reverse_string:				
	
	mov eax ,0					
	mov edx ,0 			
	
reverse_loop:
	
	cmp ebx, -1			
	je finish
	
	mov edx, dword[backward_str + ebx]
	mov dword[final_str + eax], edx 
	dec ebx
	inc eax
	jmp reverse_loop	

finish:
	
	mov dword[final_str + eax], 0	
	mov eax, final_str
	ending eax, 4

my_calc:

    start 4

    loop:
        
        print_prompt             

        get_input                                  

        call options            

    jmp loop

ending eax, 4

num_input:

    start 4
    mov ebx, [stack_pointer]    
    cmp ebx, [stack_size]
    jnz add_number_to_stack       
    push overflow_string 		
    call printf
    add esp, 4
    jmp end_num_input  

add_number_to_stack:
    mov edx, 0
    mov dl, [buffer]              
    mov ecx , 0
	mov dword[first_num] ,1 
handle_num_input:
	
	leading_zeros:					
		cmp dl, '0'
		jne chain_number
		inc dword[num_counter]
		mov eax , [num_counter]
        mov dl, [buffer + eax]
		jmp leading_zeros

    chain_number:                
        cmp dl, 10                  
        je end_conversion              
        inc dword [num_counter]

    create_chain:
        cmp dl, '9'               
        jle decimal_conversion

        sub dl, 55                 
        jmp create_link 
    
    decimal_conversion:
        sub dl, '0'

    create_link:                    
        push edx
        get_link_ptr
        pop edx
        cmp dword [first_num] ,  1    
        jne not_first_num
        
        mov [eax] , dl                 
        mov dword [eax + 1] , 0
        mov [next], eax
        jmp move_digit

    not_first_num:
        mov [eax] , dl
        mov ebx , [next]
        mov dword [eax + 1] , ebx
        mov [next], eax

    move_digit:
        mov ecx , [num_counter]
        mov dl, [buffer + ecx]
		mov dword[first_num], 0
        jmp chain_number
    
    end_conversion:  
        cmp dword[num_counter], 0
        je end_num_input

        mov ecx , [stack_pointer]
        mov edx , [stack]
        mov [edx + (4*ecx) ], eax
        inc dword [stack_pointer]
        mov dword [next], 0
        mov dword [num_counter], 0

end_num_input:

    ending eax, 4

main:                       
	push ebp
	mov ebp, esp	
	pushad

    mov dword [stack_pointer], 0
    mov dword [next] , 0
    mov dword [num_counter] , 0
    mov dword[debug], 0 


define_stack:
        mov edx , [ebp + 8]                                         
        cmp edx, 1                                                
        je default_size_stack
        cmp edx , 2
        je single_arg
        cmp edx, 3                                               
        je both_args


single_arg:
    mov edx, [ebp + 12]                                            
    add edx, 4                                                      
    mov ecx, [edx]
    mov dl, [ecx]
    cmp dl, '-'                                                    
    jne specified_size_stack

    mov dword[debug], 1                                          
    jmp default_size_stack



both_args:
    mov dword[debug], 1                                              
    jmp specified_size_stack

    default_size_stack:
        mov dword [stack_size] , 5
        jmp initialize_stack

    specified_size_stack:   
        mov edx , [ebp + 12]
        add edx , 4
        mov ecx , [edx]
        call ascii_to_num 
        mov [stack_size], eax
        
    initialize_stack:
        mov eax, [stack_size]
        push 4
        push eax
        call calloc    
        mov [stack], eax
        add esp, 4

    call my_calc




end_calc

	

