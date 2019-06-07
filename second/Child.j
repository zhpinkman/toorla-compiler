.class public Child
.super Parent
	.method public <init>()V
		aload_0
		invokespecial Parent/<init>()V
		return
	.end method

	.method public print(II)I
		.limit locals 10
		.limit stack 100
		getstatic java/lang/System/out Ljava/io/PrintStream;
		iload_2
		invokevirtual java/io/PrintStream/println(I)V
		ldc 2
		; new java/lang/Exception
		; dup
		; invokespecial java/lang/Exception/<init>()V
		; athrow
		ireturn
	.end method