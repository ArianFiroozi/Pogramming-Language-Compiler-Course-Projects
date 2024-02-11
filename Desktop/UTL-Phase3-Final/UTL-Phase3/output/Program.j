
.class public Program

.super main/ast/node/Node

.field vars Ljava/util/ArrayList

.field inits Ljava/util/ArrayList

.field starts Ljava/util/ArrayList

.field functions Ljava/util/ArrayList

.field programMainDeclaration Ljava/util/MainDeclaration
		iconst_3
		istore 0

.method public static foo(I;)I
		load 3y
		load 3y
		ineg
		istore 3
		load 3y
		bipush 5
		istore 3
		load 3y
		iinc
		load 3y
		ifeq Label_9
		pop
		iconst_0
		goto Label_9_End
	Label_9:
		pop
		iconst_1
	Label_9_End:
		iconst_0
		if_icmpeq Label_9
		iconst_0
		goto Label_9_End:
	Label_9:
		iconst_1
		if_icmplt Label_9_End:
		ifeq Label_False_9
		iconst_1
		ifeq Label_False_9
		iconst_1
		goto Label_9_End
	Label_False_9:
		iconst_0
	Label_9_End:
		load 2x
		iconst_1
		istore 2
		load 2x
		bipush 4
		istore 2
		ifeq Label_9
		goto Label_9_EndLabel_9:
		Label_9_End:
	Label_Cond_16:
		load 3y
		iconst_1
		if_icmpeq Label_16
		iconst_0
		goto Label_16_End:
	Label_16:
		iconst_1
		if_icmplt Label_16_End:
		ifeqLabel_16_End
		load 2x
		iconst_1
		istore 2
		gotoLabel_Cond_16
	Label_16_End:
		iconst_0
		ireturn

.method public static OnInit(L/trade?)V
		iconst_3
		invokevirtual Program/foo(I;)I
		bipush 100
		bipush 100
		bipush 10
		invokevirtual Program/Order(?)?
		astore 3

.method public static OnInit(L/trade?)V
		iconst_2
		astore 3

.method public static OnStart(L/trade?)V

.method public static main()V
		bipush 4
		bipush 5
		iadd
		istore 1
		ldc "username"
		ldc "password"
		invokevirtual Program/Connect(?)?
		ldc ""
		invokevirtual Program/Observe(?)?
		astore 2
		ldc ""
		invokevirtual Program/Observe(?)?
		astore 3
