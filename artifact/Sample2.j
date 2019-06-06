.class public Sample2
.super Any
   .method public <init>()V
       aload_0 ; push this
       invokespecial java/lang/Object/<init>()V ; call super
       return
   .end method
   .method public static main()I
      .limit locals 10
      .limit stack 100
      getstatic java/lang/System/out Ljava/io/PrintStream;
      ldc "Hello World!"
      invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
      ldc 0
      ireturn
   .end method
