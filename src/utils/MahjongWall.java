package utils;

import cards.mahjong.AbstractMahjongCard;

import java.util.*;

public class MahjongWall implements SaveDataTransfer {
    /**
     * 麻将 牌山默认种类
     */
    public static final Map WallDefault = new HashMap<String, Integer>() {
        {
            put("1", 1);
        }
    };
    /**
     * 麻将默认牌山数量
     */
    public static final int DEFAULT_WALL_LENGTH = 136;

    private TreeMap<String, Integer> currentWall;
    private int currentWallLength;
    /** 麻将 当前遗物 牌河历史记录的卡牌ID */
    private final ArrayList<Integer> historyDiscardList = new ArrayList<>();

    private final Random randomNumberGenerate;

    public MahjongWall(Random randomNumberGenerate) {
        this.randomNumberGenerate = randomNumberGenerate;
        initialize();
    }

    private void initialize() {
        currentWall = new TreeMap<>();
        WallDefault.forEach((key, value) -> {
            currentWall.put((String) key, (Integer) value);
        });
        currentWallLength = DEFAULT_WALL_LENGTH;
    }


    public AbstractMahjongCard Draw() {
        return null;
    }


    private String generateRandomCard() {
        int index = this.randomIndex(this.currentWallLength, this.historyDiscardSeed());
        currentWall.higherEntry().getValue()
                // todo  https://leetcode-cn.com/problems/random-pick-with-weight/solution/lun-pan-du-suan-fa-by-joyboy/
    }

    @Override
    public String Marshal() {
        return null;
    }

    @Override
    public void UnMarshal() {

    }

    private int randomIndex(int range, long seedFix) {
        for (long i = 0; i < seedFix; i++)
            randomNumberGenerate.nextBoolean();
        return randomNumberGenerate.nextInt(range);
    }

    private long historyDiscardSeed() {
        long seed = 0L;
        for (Integer i : this.historyDiscardList) {
            seed += i.longValue() + 1L;
            seed *= i.longValue() + 1L;
        }
        return seed;
    }
}
