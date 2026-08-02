package rt4;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ih")
public final class LinkedList {

	@OriginalMember(owner = "client!ih", name = "p", descriptor = "Lclient!ab;")
	private Node aClass3_110;

	@OriginalMember(owner = "client!ih", name = "m", descriptor = "Lclient!ab;")
	public final Node aClass3_109 = new Node();

	@OriginalMember(owner = "client!ih", name = "<init>", descriptor = "()V")
	public LinkedList() {
		this.aClass3_109.previousNode = this.aClass3_109;
		this.aClass3_109.nextNode = this.aClass3_109;
	}

	@OriginalMember(owner = "client!ih", name = "a", descriptor = "(I)V")
	public final void clear() {
		while (true) {
			@Pc(5) Node local5 = this.aClass3_109.nextNode;
			if (local5 == this.aClass3_109) {
				this.aClass3_110 = null;
				return;
			}
			local5.unlink();
		}
	}

	@OriginalMember(owner = "client!ih", name = "b", descriptor = "(I)Lclient!ab;")
	public final Node tail() {
		@Pc(7) Node local7 = this.aClass3_109.previousNode;
		if (this.aClass3_109 == local7) {
			this.aClass3_110 = null;
			return null;
		} else {
			this.aClass3_110 = local7.previousNode;
			return local7;
		}
	}

	@OriginalMember(owner = "client!ih", name = "a", descriptor = "(ZLclient!ab;)V")
	public final void addTail(@OriginalArg(1) Node arg0) {
		if (arg0.previousNode != null) {
			arg0.unlink();
		}
		arg0.nextNode = this.aClass3_109;
		arg0.previousNode = this.aClass3_109.previousNode;
		arg0.previousNode.nextNode = arg0;
		arg0.nextNode.previousNode = arg0;
	}

	@OriginalMember(owner = "client!ih", name = "a", descriptor = "(ILclient!ab;)V")
	public final void addHead(@OriginalArg(1) Node arg0) {
		if (arg0.previousNode != null) {
			arg0.unlink();
		}
		arg0.nextNode = this.aClass3_109.nextNode;
		arg0.previousNode = this.aClass3_109;
		arg0.previousNode.nextNode = arg0;
		arg0.nextNode.previousNode = arg0;
	}

	@OriginalMember(owner = "client!ih", name = "d", descriptor = "(I)Lclient!ab;")
	public final Node prev() {
		@Pc(13) Node local13 = this.aClass3_110;
		if (this.aClass3_109 == local13) {
			this.aClass3_110 = null;
			return null;
		} else {
			this.aClass3_110 = local13.previousNode;
			return local13;
		}
	}

	@OriginalMember(owner = "client!ih", name = "a", descriptor = "(B)Lclient!ab;")
	public final Node removeHead() {
		@Pc(3) Node local3 = this.aClass3_109.nextNode;
		if (this.aClass3_109 == local3) {
			return null;
		} else {
			local3.unlink();
			return local3;
		}
	}

	@OriginalMember(owner = "client!ih", name = "e", descriptor = "(I)Lclient!ab;")
	public final Node next() {
		@Pc(12) Node local12 = this.aClass3_110;
		if (local12 == this.aClass3_109) {
			this.aClass3_110 = null;
			return null;
		} else {
			this.aClass3_110 = local12.nextNode;
			return local12;
		}
	}

	@OriginalMember(owner = "client!ih", name = "f", descriptor = "(I)Lclient!ab;")
	public final Node head() {
		@Pc(3) Node local3 = this.aClass3_109.nextNode;
		if (this.aClass3_109 == local3) {
			this.aClass3_110 = null;
			return null;
		} else {
			this.aClass3_110 = local3.nextNode;
			return local3;
		}
	}
}
