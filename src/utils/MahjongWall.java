package utils;

public class MahjongWall implements SaveDataTransfer {
    /** 麻将 牌山默认种类 */
    public static final String[] YAMA_NAME = {"w0", "w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "p0", "p1",
            "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9",
            "z1", "z2", "z3", "z4", "z5", "z6", "z7"};
    /** 麻将 牌山默认种类对应的默认数量 */
    public static final int[] YAMA_DEFAULT = {1, 4, 4, 4, 4, 3, 4, 4, 4, 4, 1, 4, 4, 4, 4, 3, 4, 4, 4, 4, 1, 4, 4, 4,
            4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};
    public MahjongWall() {
    }
    public MahjongWall(String value){

    }

    @Override
    public String Marshal() {
        return null;
    }

    @Override
    public void UnMarshal() {

    }
}
