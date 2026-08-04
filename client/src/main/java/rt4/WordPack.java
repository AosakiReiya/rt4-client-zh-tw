package rt4;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public class WordPack {
	@OriginalMember(owner = "client!vl", name = "f", descriptor = "Lclient!na;")
	public static final JagString CABBAGE = JagString.parse("Cabbage");
	@OriginalMember(owner = "client!f", name = "W", descriptor = "Lclient!fi;")
	public static HuffmanCodec codec;

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!fi;I)V")
	public static void init(@OriginalArg(0) HuffmanCodec arg0) {
		codec = arg0;
	}

	@OriginalMember(owner = "client!lg", name = "a", descriptor = "(ZLclient!wa;Lclient!na;)I")
	public static int encode(@OriginalArg(1) Buffer arg0, @OriginalArg(2) JagString arg1) {
		@Pc(6) int local6 = arg0.offset;
		@Pc(14) byte[] local14 = arg1.method3148();
		// 改用 UTF-8 明文（不壓縮），使中文聊天可發送（伺服器 decryptPlayerChat 需對應改為讀 UTF-8）
		arg0.psmarts(local14.length);
		arg0.pdata(local14, local14.length);
		return arg0.offset - local6;
	}
}
