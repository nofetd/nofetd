section	.rodata			; we define (global) read-only variables in .rodata section
	format_integer: db "%d",10,0	; format integer

section .text
	global assFunc
	extern c_checkValidity
	extern printf

assFunc:
	push ebp
	mov ebp, esp	
	pushad	

	mov ecx,dword[ebp+8]
	mov edx,dword[ebp+12]

	push edx
	push ecx
	call c_checkValidity
	add esp,8
	cmp eax,'0'
	JZ add
	sub ecx, edx
	jmp end

add:	
	add ecx, edx

end:
	push ecx
	push format_integer
	call printf
	add esp,8	
	popad			
	mov esp, ebp	
	pop ebp
	ret
