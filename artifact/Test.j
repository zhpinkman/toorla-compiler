.class public Test
.super Any
   .field public yyy I
   .method public <init>()V
       aload_0 ; push this
       invokespecial java/lang/Object/<init>()V ; call super
       return
   .end method
   .method public  main()I
      .limit locals 10
      .limit stack 100
      ldc 2
      istore_1
      continue_1 : 
      iload_1
      ldc 4
      if_icmpne L0_0
      iconst_1
      goto L0_exit
      L0_0 : iconst_0
      L0_exit : 
      ifne L1_0
      iconst_1
      goto L1_exit
      L1_0: iconst_0
      L1_exit : 
      if_eq break_1
      getstatic java/lang/System/out Ljava/io/PrintStream;
      iload_1
      invokevirtual java/io/PrintStream/println(I)V
      goto continue_1
      break_1 : 
      ldc 1
      iload_1
      iadd
      istore_1
   .end method
