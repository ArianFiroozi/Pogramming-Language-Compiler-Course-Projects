.class public Program
.super main/ast/node/Node
.field vars Ljava/util/ArrayList
.field inits Ljava/util/ArrayList
.field starts Ljava/util/ArrayList
.field functions Ljava/util/ArrayList
.field programMainDeclaration Ljava/util/MainDeclaration
		bipush 3
		istore 0
		
		.method public static foo(I;)I
		load 3y
		load 2x
		bipush 5
		iadd
		store ?null
		load 3y
		bipush 0
		idiv
		load 2x
		bipush 1
		store ?null
		load 2x
		bipush 4
		store ?null
		ifeq Label_8
		goto Label_End_8Label_8:
		Label_End_8:
		load 3y
		bipush 1
		idiv
		load 2x
		bipush 1
		store ?null
	Label_cond_15:
		null
		ifeq Label_end_15null
		goto Label_cond_15Label_end_15:
		bipush 0
		ireturn
		
		.method public static OnInit(L/trade?)V
		load nullt1
		astore 1
		
		.method public static OnInit(L/trade?)V
		load nullOnInit_t1$
		bipush 2
		astore 1
		
		.method public static OnStart(L/trade?)V
		load nullt1
		
		.method public static main()V
		bipush 4
		bipush 5
		iadd
		istore 1
		astore 2
		astore 3
