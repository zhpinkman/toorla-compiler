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
      ldc 3
      istore_2
      iload_1
      iload_2
      if_icmpne L0_0
      iconst_1
      goto L0_exit
      L0_0 : iconst_0
      L0_exit : 
      if_eq L1_else
      ldc 4
      istore_1
      goto L1_exit
      L1_else : 
      ldc 5
      istore_2
      L1_exit : 
      ldc 0
      ireturn
   .end method
