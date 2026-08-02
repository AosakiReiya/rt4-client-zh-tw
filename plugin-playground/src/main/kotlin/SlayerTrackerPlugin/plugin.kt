package SlayerTrackerPlugin
    
import plugin.Plugin
import plugin.annotations.PluginMeta
import plugin.api.API
import plugin.api.FontColor
import plugin.api.FontType
import plugin.api.TextModifier
import rt4.Sprite
import java.awt.Color
import java.lang.Exception

class plugin : Plugin() {
    val boxColor = 6116423
    val posX = 5
    val posY = 30
    val boxWidth = 90
    val boxHeight = 30
    val boxOpacity = 160
    val textX = 65
    val textY = 50
    val spriteX = 7
    val spriteY = 30

    var slayerTaskID = -1
    var slayerTaskAmount = 0
    var curSprite: Sprite? = null

    override fun Draw(deltaTime: Long) {
        if (slayerTaskAmount == 0 || slayerTaskID == -1) return

        API.FillRect(posX, posY, boxWidth, boxHeight, boxColor, boxOpacity)
        curSprite?.render(spriteX, spriteY)
        API.DrawText(
            FontType.SMALL,
            FontColor.fromColor(Color.WHITE),
            TextModifier.LEFT,
            slayerTaskAmount.toString(),
            textX,
            textY
        )
    }

    override fun OnVarpUpdate(id: Int, value: Int) {
        if (id == 2502) {
            slayerTaskID = value and 0x7F
            slayerTaskAmount = (value shr 7) and 0xFF
            setSprite()
        }
    }

    override fun OnLogout() {
        slayerTaskID = -1
        slayerTaskAmount = 0
        curSprite = null
    }

    private fun setSprite() {
        try {
            val itemId: Int = when (slayerTaskID) {
                0 -> 4144
                1 -> 4149
                2 -> 9008
                3 -> 10176
                4 -> 4135
                5 -> 4139
                6 -> 14072
                7 -> 948
                8 -> 12189
                9 -> 3098
                10 -> 1747
                11 -> 4141
                12 -> 1751
                13 -> 11047
                14 -> 2349
                15 -> 9008
                16 -> 4521
                17 -> 4134
                18 -> 8900
                19 -> 4520
                20 -> 4137
                21 -> 1739
                22 -> 7982
                23 -> 10149
                24 -> 8141
                25 -> 6637
                26 -> 6695
                27 -> 8132
                28 -> 4145
                29 -> 7500
                30 -> 1422
                31 -> 6105
                32 -> 6709
                33 -> 1387
                34 -> 28
                35 -> 4147
                36 -> 552
                37 -> 6722
                38 -> 10998
                39 -> 9016
                40 -> 2402
                41 -> 1753
                42 -> 7050
                43 -> 8137
                44 -> 12570
                45 -> 8133
                46 -> 4671
                47 -> 4671
                48 -> 1159
                49 -> 4140
                50 -> 2351
                51 -> 4142
                52 -> 7778
                53 -> 8139
                54 -> 7160
                55 -> 4146
                56 -> 2402
                57 -> 9007
                58 -> 2359
                59 -> 6661
                60 -> 10997
                61 -> 12201
                62 -> 12570
                63 -> 7420
                64 -> 4148
                65 -> 4818
                66 -> 6109
                67 -> 4138
                68 -> 8134
                69 -> 4136
                70 -> 9032
                71 -> 12055
                72 -> 7576
                73 -> 10634
                74 -> 1165
                75 -> 6811
                76 -> 553
                77 -> 8135
                78 -> 11732
                79 -> 10284
                80 -> 13923
                81 -> 2353
                82 -> 9105
                83 -> 10591
                84 -> 8136
                85 -> 4143
                86 -> 1549
                87 -> 4519
                88 -> 24
                89 -> 10535
                90 -> 571
                91 -> 2952
                92 -> 958
                93 -> 7594
                else -> -1
            }

            val sprite = API.GetObjSprite(itemId, 1, false, 1, 1)

            curSprite = sprite
        } catch (ignored: Exception){}
    }

    //Check the source of plugin.Plugin for more methods you can override! Happy hacking! <3
    //There are also many methods to aid in plugin development in plugin.api.API
}
