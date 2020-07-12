package utils.Mahjong;

import basemod.abstracts.CustomSavable;
import cards.mahjong.AbstractMahjongCard;
import com.google.gson.Gson;
import mymod.TestMod;

import java.util.*;

public class MahjongWall   {



    class Wall  {
        public ArrayList<Integer> WallList;
        public Integer Length;

        Wall(ArrayList<Integer> wallList, Integer length) {
            this.WallList.addAll(wallList);
            this.Length = length;
        }

        Wall() {
        }

    }

    /**
     * 麻将 默认牌山
     */
    private final Wall DefaultWall = new Wall(
            new ArrayList<Integer>() {
                {
                    for (int i = 0; i < 37; i++) {
                        switch (i) {
                            case MahjongEnum.BambooFIVE:
                            case MahjongEnum.DotFIVE:
                            case MahjongEnum.CharacterFIVE:
                                add(i, 3);
                                break;
                            case MahjongEnum.BambooRED_FIVE:
                            case MahjongEnum.DotRED_FIVE:
                            case MahjongEnum.CharacterRED_FIVE:
                                add(i, 1);
                                break;
                            default:
                                add(i, 4);
                        }
                    }
                }
            }, 136
    );

    /**
     * 麻将 当前牌山
     */
    private Wall currentWall = null;
    /**
     * 麻将 当前遗物 牌河历史记录的卡牌ID
     */
    private final ArrayList<Integer> historyDiscardList = new ArrayList<>();

    private final Random randomGenerator;

    public MahjongWall(Random r) {
        this.randomGenerator = r;
        initialize();
    }

    private void initialize() {
        currentWall = new Wall(DefaultWall.WallList, DefaultWall.Length);
    }


    public AbstractMahjongCard DrawCard() {
        Integer index = generateRandomCard();
        if (index != null) {
            currentWall.WallList.set(index, currentWall.WallList.get(index) - 1);
            currentWall.Length--;
            historyDiscardList.add(index);
            return AbstractMahjongCard.mahjong(index);
        }
        return null;
    }


    public boolean isEmpty() {
        return this.currentWall.Length == 0;
    }

    public boolean isFull() {
        return this.currentWall.Length.equals(this.DefaultWall.Length);
    }

    public int getCurrentLeft() {return this.currentWall.Length;}


    private Integer generateRandomCard() {

        int index = this.randomIndex(this.currentWall.Length, this.historyDiscardSeed());
        for (int i = 0; i < currentWall.Length; i++) {
            index -= currentWall.WallList.get(i);
            if (index < 0) {
                currentWall.WallList.set(i, currentWall.WallList.get(i) - 1);
                return i;
            }
        }
        return null;
    }




    private int randomIndex(int range, long seedFix) {
        for (long i = 0; i < seedFix; i++)
            randomGenerator.nextBoolean();
        return randomGenerator.nextInt(range);
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
