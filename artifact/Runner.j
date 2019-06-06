.class public Runner
.super java/lang/Object
.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 1000
.limit locals 100
new Test
dup
invokespecial Test/<init>()V
astore_1
aload 1
invokevirtual Test/main()I
istore_0
return
.end method