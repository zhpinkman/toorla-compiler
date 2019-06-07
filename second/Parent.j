.class public Parent
.super java/lang/Object
	.field public x I
	.method public <init>()V
		aload_0
		invokespecial java/lang/Object/<init>()V
		return
	.end method

	.method public print(II)I
		.limit locals 10
		.limit stack 100
		getstatic java/lang/System/out Ljava/io/PrintStream;
		ldc "parent"
		invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
		ldc 2
		ireturn
		getstatic java/lang/System/out Ljava/io/PrintStream;
		ldc "parent"
		invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
		ldc 2
		ireturn
	.end method