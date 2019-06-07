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
      new Test
      dup
      invokespecial Test/<init>()V
      astore_1
      aload_0
      ldc 3
      putfield Test/yyy I
      getstatic java/lang/System/out Ljava/io/PrintStream;
      aload_0
      getfield Test/yyy I
      invokevirtual java/io/PrintStream/println(I)V
      ldc 0
      ireturn
   .end method
