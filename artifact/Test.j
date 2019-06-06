.class public Test
.super Any
   .method public <init>()V
       aload_0 ; push this
       invokespecial java/lang/Object/<init>()V ; call super
       return
   .end method
   .method public  main()I
      .limit locals 10
      .limit stack 100
      iconst_1
      ifeq 0_0
      iconst_0
      ifeq 0_0
      iconst_1
      goto 0_exit
      0_0: iconst_0
      getstatic java/lang/System/out Ljava/io/PrintStream;
      ldc 2
      invokevirtual java/io/PrintStream/println(I)V
      goto 1_exit
      1_else : 
      1_exit : 
      ldc 0
      ireturn
   .end method
