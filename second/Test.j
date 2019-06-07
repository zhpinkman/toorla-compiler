.class public Test
.super java/lang/Object
	.field public Test I
	.method public <init>()V
		aload_0
		invokespecial java/lang/Object/<init>()V
		return
	.end method

	.method public toString()Ljava/lang/String;
		ldc "Ahmadhossein Yazdani"
		areturn
	.end method

	.method private len(I)I
		.limit locals 2
		.limit stack 100
		iload 1
		ldc 3
		if_icmplt returnTwoStat
		ldc 3
		ireturn
		returnTwoStat:
		ldc 2
		ireturn
	.end method

	.method public static main([Ljava/lang/String;)V
		.limit locals 10
		.limit stack 100
		new Child
		dup
		invokespecial Child/<init>()V
		astore_1
		aload 1
		ldc 2
		ldc 5678
		invokevirtual Parent/print(II)I
		pop
		new Test
		dup
		invokespecial Test/<init>()V
		ldc 2
		invokevirtual Test/len(I)I
		anewarray Test
		dup
		ldc 0
		new Test
		dup
		invokespecial Test/<init>()V
		aastore
		dup
		ldc 1
		new Test
		dup
		invokespecial Test/<init>()V
		aastore
		astore_1
		getstatic java/lang/System/out Ljava/io/PrintStream;
		aload_1
		invokestatic java/util/Arrays/toString([Ljava/lang/Object;)Ljava/lang/String;
		invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
		getstatic java/lang/System/out Ljava/io/PrintStream;
		ldc_w 666.6
		invokevirtual java/io/PrintStream/println(F)V
		return
	.end method