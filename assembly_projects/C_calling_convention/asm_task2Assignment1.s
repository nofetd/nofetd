section	.rodata			; we define (global) read-only variables in .rodata section
	format_string: db "%s", 10, 0	; format string

section .bss			; we define (global) uninitialized variables in .bss section
	an: resb 12		; enough to store integer in [-2,147,483,648 (-2^31) : 2,147,483,647 (2^31-1)]

section .text
	global convertor
	extern printf

convertor:
	push ebp
	mov ebp, esp	
	pushad			

	mov ecx, dword [ebp+8]	; get function argument (pointer to string)
	mov eax,0
	mov edx,0
    mov ebx,0
	conversion: 
		movzx ebx ,byte[ecx] ;45653
		inc ecx
		cmp byte[ecx],10
		je last
		sub ebx,'0'
		add eax,ebx 
		mov edx, 10  
        mul edx
	    jmp conversion 
	last:
	    sub ebx,'0'
		add eax,ebx   
		
		mov edx,0
		mov ebx,0
		mov ecx,0
	count:
	    cdq
		inc ecx
		mov ebx, 16 
        idiv ebx
		cmp edx,10
		jg Greater
		cmp edx,10
		je Greater  
		add edx,48
		jmp push
    Greater:
		add edx,55
	push:	
		push edx    
		cmp eax,0
		jnz count

		mov ebx,0
		mov edx,0 
	pop:
	    pop ebx
		mov [an+edx], ebx
		inc edx
		dec ecx
	    cmp ecx,0
		jne pop
    
	mov byte[an+edx],0
	push an			    ; call printf with 2 arguments -  
	push format_string	; pointer to str and pointer to format string
	call printf
	add esp, 8		    ; clean up stack after call

	popad			
	mov esp, ebp	
	pop ebp
	ret
