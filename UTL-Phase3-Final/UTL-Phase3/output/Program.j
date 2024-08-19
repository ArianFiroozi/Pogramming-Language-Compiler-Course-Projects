.class public UTL
.super java/lang/Object
.field vars Ljava/util/ArrayList
.field inits Ljava/util/ArrayList
.field starts Ljava/util/ArrayList
.field functions Ljava/util/ArrayList
.field programMainDeclaration Ljava/util/MainDeclaration
.field balance I
.field tick_counts I

.method public static OnInit(LTrade;)V
.limit stack 128
.limit locals 128
		new Order
		dup
		ldc "SELL"
		bipush 100
		bipush 100
		bipush 10
		invokespecial Program/Order(V;I;I;I;)Order
		astore_3
		aload_0
		getfield balance I
		iconst_1
		iadd
		istore 4
		aload_0
		iconst_0
		putfield tick_counts I
		return
.end method

.method public static OnInit(LTrade;)V
.limit stack 128
.limit locals 128
		new Order
		dup
		ldc "BUY"
		bipush 200
		bipush 50
		bipush 5
		invokespecial Program/Order(V;I;I;I;)Order
		astore_3
		new Order
		dup
		ldc "SELL"
		bipush 100
		bipush 100
		bipush 10
		invokespecial Program/Order(V;I;I;I;)Order
		astore 4
		return
.end method

.method public static OnStart(LTrade;)V
.limit stack 128
.limit locals 128
		aload_1
		invokevirtual LTrade/Bid()V
		fstore_2
		aload_1
		invokevirtual LTrade/Ask()V
		istore_3
		bipush 100
		fstore 4
		bipush 250
		fstore 5
		bipush 20
		fstore 6
		new Order
		dup
		ldc "BUY"
		fload 4
		fload 5
		fload 6
		invokespecial Program/Order(V;F;F;F;)Order
		astore 7
		fload 5
		fload 6
		fdiv
		fstore 8
		return
.end method

.method public static OnStart(LTrade;)V
.limit stack 128
.limit locals 128
		bipush 100
		invokevirtual Program/GetCandle(I;)V
		return
.end method

.method public static Main()V
.limit stack 128
.limit locals 128
		ldc "admin"
		astore_1
		aload_1
		ldc "password"
		invokestatic Program/Connect(Ljava/lang/String;Ljava/lang/String;)V
		ldc "USDETH"
		invokestatic Program/Observe(Ljava/lang/String;)V
		astore_2
		ldc "IRRETH"
		invokestatic Program/Observe(Ljava/lang/String;)V
		astore_3
		return
.end method
