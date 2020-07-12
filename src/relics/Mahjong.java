package relics;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import basemod.abstracts.CustomSavable;

import cards.mahjong.*;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import mymod.TestMod;
import utils.Mahjong.MahjongHand;
import utils.Mahjong.MahjongWall;
import utils.MiscMethods;

public class Mahjong extends AbstractTestRelic implements MiscMethods, CustomSavable<Mahjong> {
    public static final String ID = "Mahjong";
    /**
     * 麻将 牌山
     */
    private MahjongWall mahjongWall = null;
    /**
     * 麻将 当前遗物手牌内容
     */
    private MahjongHand hand = null;
    /**
     * 麻将 当前遗物杠牌内容
     */
    private ArrayList<AbstractMahjongCard> kong = new ArrayList<>(4);

    /**
     * 麻将 当前遗物宝牌指示牌
     */
    private  ArrayList<AbstractMahjongCard> doraHint = new ArrayList<>(5);
    /**
     * 麻将 当前遗物里宝牌
     */
    private  ArrayList<AbstractMahjongCard> uraHint = new ArrayList<>(5);
    /**
     * 麻将 当前遗物 牌河历史记录的卡牌ID
     */
    private final ArrayList<Integer> history = new ArrayList<>();

    private static Random randomGenerator;

    /** 听牌 */
    private boolean ready = false;
    /** 立直 */
    private boolean reach = false;

    private int turns = 0;

    private static boolean saveLater = false;

    public Mahjong() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    public String getUpdatedDescription() {
        if (!isObtained)
            return DESCRIPTIONS[0];
        if (this.counter == 0)
            return DESCRIPTIONS[0];
        StringBuilder desc = new StringBuilder(DESCRIPTIONS[0] + " NL 现在是第 #b" + this.counter + " 巡。");
        if (this.doraHint.size() > 0) {
            desc.append("宝牌指示牌为: ");
            for (AbstractMahjongCard hint : doraHint) {
                desc.append(" #y").append(hint.name).append(" 、");
            }
            desc = new StringBuilder(desc.substring(0, desc.length() - 1) + "。");
        }
        return desc.toString();
    }

    public void updateDescription(PlayerClass c) {
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
        initializeTips();
    }

    public void onEquip() {
        this.reset();
        saveLater = true;
    }

    public static void setRng() {
        randomGenerator = new Random(Settings.seed + ((Integer) AbstractDungeon.floorNum).longValue());
        TestMod.info("rng 已设置");
    }


    private void reset() {
        TestMod.info("Mahjong开始reset");
        this.mahjongWall = new MahjongWall(randomGenerator);
        this.kong.clear();
        this.kangID.clear();
        this.doraHint.clear();
        this.uraHint.clear();
        this.history.clear();
        this.counter = 0;
        this.turns = 0;
        if (AbstractDungeon.floorNum > 0) {
            this.clearAndSetNewHand();
            this.doraHint();
        }
        TestMod.info("Mahjong reset完毕");
    }

    private void clearAndSetNewHand() {

        TestMod.info("Mahjong Hand分配完毕");
    }


    private int countLeft() {
        return this.mahjongWall.getCurrentLeft() - 14 + 1 + this.kong.size();
    }

    /**
     * 翻出一张dora指示牌
     */
    public void doraHint() {
        while (this.doraHint.size() < 1 + this.kong.size())
            this.doraHint.add(this.mahjongWall.DrawCard());
    }

    private ArrayList<AbstractMahjongCard> uraHint() {
        while (this.uraHint.size() < 1 + this.kong.size() && this.reach)
            this.uraHint.add(this.mahjongWall.DrawCard());
        return this.uraHint;
    }

    private void updateDora() {
        this.doraHint();
        // TODO
    }
    /** 是否听牌 */
    private boolean isReady() {
        return false
    }

    private void kang(int color, int num) {
        switch (color) {
            case 0:
                this.kong.add(new MahjongWs(num));
            case 1:
                this.kong.add(new MahjongPs(num));
            case 2:
                this.kong.add(new MahjongSs(num));
            case 3:
                this.kong.add(new MahjongZs(num));
        }
        // TODO

        this.updateDora();
    }

    private boolean checkMultiple() {
        // TODO
        return false;
    }

    public void atBattleStart() {
        this.history.clear();
        // TODO
        TestMod.info("开始战斗");
        if (saveLater) {
            saveLater = false;
        }
    }


    private long historySeed() {
        Long seed = 0L;
        for (Integer i : this.history) {
            seed += i.longValue() + 1L;
            seed *= i.longValue() + 1L;
        }
        return seed;
    }

    public void atTurnStart() {
        this.counter++;
        if (this.counter == 0)
            this.counter++;
        this.updateDescription(AbstractDungeon.player.chosenClass);
        if (this.turns < this.counter) {
            this.turns = this.counter;
            this.saveTurns();
        } else if (this.turns == this.counter) {
            // rng.nextBoolean();
        }

        // TODO
        this.addToBot(new MakeTempCardInHandAction(this.mahjongWall.DrawCard()));
        if (this.countLeft() < 4)
            this.reset();
        else {
            this.mahjongWall.DrawCard();
            this.mahjongWall.DrawCard();
            this.mahjongWall.DrawCard();
        }
    }

    public void onVictory() {
//        this.save();
    }


    @Override
    public Mahjong onSave() {
        return this;
    }

    @Override
    public void onLoad(Mahjong mahjong) {

        if (AbstractDungeon.player.hasRelic(TestMod.makeID(ID))) {
            Mahjong current = (Mahjong) AbstractDungeon.player.getRelic(TestMod.makeID(ID));
            TestMod.info("Mahjong开始load");
            this.kong = mahjong.kong;
            if (current.mahjongWall.isEmpty() || current.mahjongWall.isFull()) {
                saveLater = true;
                TestMod.info("saveLater变为true");
                return;
            }

            TestMod.info("Mahjong load完毕");
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<Mahjong>(){}.getType();
    }



}