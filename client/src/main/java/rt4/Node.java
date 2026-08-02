package rt4;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ab")
public class Node {

	@OriginalMember(owner = "client!ab", name = "a", descriptor = "J")
	public long key;

	@OriginalMember(owner = "client!ab", name = "d", descriptor = "Lclient!ab;")
	public Node nextNode;

	@OriginalMember(owner = "client!ab", name = "l", descriptor = "Lclient!ab;")
	public Node previousNode;

	@OriginalMember(owner = "client!ab", name = "a", descriptor = "(I)Z")
	public final boolean isLinked() {
		return this.previousNode != null;
	}

	@OriginalMember(owner = "client!ab", name = "b", descriptor = "(I)V")
	public final void unlink() {
		if (this.previousNode != null) {
			this.previousNode.nextNode = this.nextNode;
			this.nextNode.previousNode = this.previousNode;
			this.previousNode = null;
			this.nextNode = null;
		}
	}
}
