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
      ldc 3
      newarray int
      astore_1
      aload_1
      ldc 0
      ldc 0
      iastore
      aload_1
      ldc 1
      ldc 1
      iastore
      aload_1
      ldc 2
      ldc 2
      iastore
      getstatic java/lang/System/out Ljava/io/PrintStream;
      aload_1
      invokestatic java/util/Arrays/toString([Ljava/lang/Object;)Ljava/lang/String;
      invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
      ldc 0
      ireturn
   .end method
