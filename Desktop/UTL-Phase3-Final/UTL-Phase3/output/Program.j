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
		invokevirtual Program/Order(V;I;I;I;)Order
		astore_3
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
		invokevirtual Program/Order(V;I;I;I;)Order
		astore_3
		new Order
		dup
		ldc "SELL"
		bipush 100
		bipush 100
		bipush 10
		invokevirtual Program/Order(V;I;I;I;)Order
		astore 4
		return
.end method

.method public static OnStart(LTrade;)V
.limit stack 128
.limit locals 128
		aload_2
		invokevirtual LTrade/Bid()V
		fstore_3
		aload_2
		invokevirtual LTrade/Ask()V
		istore 4
		bipush 100
		fstore 5
		bipush 250
		fstore 6
		bipush 20
		fstore 7
		new Order
		dup
		ldc "BUY"
		fload 5
		fload 6
		fload 7
		invokevirtual Program/Order(V;F;F;F;)Order
		astore 8
		fload 6
		fload 7
		fdiv
		fstore 9
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
		invokevirtual Program/Connect(Ljava/lang/String;Ljava/lang/String;)V
		ldc "USDETH"
		invokevirtual Program/Observe(Ljava/lang/String;)V
		astore_2
		ldc "IRRETH"
		invokevirtual Program/Observe(Ljava/lang/String;)V
		astore_3
		return
.end method
