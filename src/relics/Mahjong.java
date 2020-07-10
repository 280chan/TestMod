package relics;

import java.util.ArrayList;
import java.util.Random;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import cards.mahjong.AbstractMahjongCard;
import mymod.TestMod;
import utils.Mahjong.MahjongWall;
import utils.MiscMethods;

public class Mahjong extends AbstractTestRelic implements MiscMethods {
    public static final String ID = "Mahjong";


    /** 麻将 牌山 */
    private MahjongWall mahjongWall = null;

    /** 麻将 默认杠牌数组 */
    public static final String[] KANG_NAME = {"KANG0", "KANG1", "KANG2", "KANG3"};
    /** 麻将 默认宝牌个数 */
    public static final String[] DORA_NAME = {"DORA0", "DORA1", "DORA2", "DORA3", "DORA4"};
    public static final String HAND_NAME = "Mahjong_HAND";
    public static final String SAVE_KANG = "MahjongKang";
    public static final String SAVE_TURN = "MahjongTurns";
    public static final String SAVE_REACH = "MahjongReach";

    /** 麻将 当前遗物牌山残余 */
    private final ArrayList<Integer> yama = new ArrayList<>();
    /** 麻将 当前遗物手牌内容 */
    private final ArrayList<AbstractMahjongCard> hand = new ArrayList<>();
    /** 麻将 当前遗物杠牌内容 */
    private final ArrayList<AbstractMahjongCard> kang = new ArrayList<>();

    /** 麻将 当前遗物宝牌指示牌 */
    private final ArrayList<AbstractMahjongCard> doraHint = new ArrayList<>();
    /** 麻将 当前遗物里宝牌 */
    private final ArrayList<AbstractMahjongCard> uraHint = new ArrayList<>();
    /** 麻将 当前遗物 牌河历史记录的卡牌ID */
    private final ArrayList<Integer> history = new ArrayList<>();
    /** 麻将 当前遗物已杠牌的ID
     * 一个杠存一个 */
    private final ArrayList<Integer> kangID = new ArrayList<>();

    private static Random randomGenerator;
    private int kangCount = 0;
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

    private void saveTurns() {
        TestMod.save(SAVE_TURN, this.turns);
    }


    private void save() {
        TestMod.info("Mahjong开始save");
        if (this.doraHint.isEmpty()) {
            this.reset();
        }
        TestMod.save(SAVE_KANG, this.kangCount);
        TestMod.save(SAVE_TURN, this.turns);
        TestMod.save(SAVE_REACH, this.reach);
        mahjongWall.Save();
        TestMod.info("Mahjong save完毕");
    }

    public static void load(int turns, boolean reach, int[] yama, int[] kangID, int[] hintID, int[] handID) {
        if (AbstractDungeon.player.hasRelic(TestMod.makeID(ID))) {
            Mahjong current = (Mahjong) AbstractDungeon.player.getRelic(TestMod.makeID(ID));
            TestMod.info("Mahjong开始load");
            if (current.total(yama) == 0 || current.total(yama) == current.total(YAMA_DEFAULT)) {
                saveLater = true;
                TestMod.info("saveLater变为true");
                return;
            }
            current.reach = reach;
            current.turns = turns;
            current.kangCount = kangID.length;
            current.yama.clear();
            current.mahjongWall.Load();

            TestMod.info("Mahjong load完毕");
        }
    }

    public void onEquip() {
        this.reset();
        saveLater = true;
    }

    public static void setRng() {
        randomGenerator = new Random(Settings.seed + ((Integer) AbstractDungeon.floorNum).longValue());
        TestMod.info("rng 已设置");
    }

    private int ran(int range, long seedFix) {
        for (long i = 0; i < seedFix; i++)
            randomGenerator.nextBoolean();
        return randomGenerator.nextInt(range);
    }

    private void reset() {
        TestMod.info("Mahjong开始reset");
        this.mahjongWall = new MahjongWall(randomGenerator);
        this.kang.clear();
        this.kangID.clear();
        this.doraHint.clear();
        this.uraHint.clear();
        this.history.clear();
        this.kangCount = 0;
        this.counter = 0;
        this.turns = 0;
        if (AbstractDungeon.floorNum > 0) {
            this.clearAndSetNewHand();
            this.doraHint();
        }
        TestMod.info("Mahjong reset完毕");
    }

    private void clearAndSetNewHand() {
        this.hand.clear();
        while (this.hand.size() < 13)
            this.hand.add(this.randomFromYama());
        for (int i = 0; i < 39; i++)
            this.randomFromYama();
        TestMod.info("Mahjong Hand分配完毕");
    }


    private int countLeft() {
        return this.total(this.yama) - 14 + 1 + this.kangCount;
    }

    private AbstractMahjongCard randomFromYama() {
        int index = this.ran(this.total(this.yama), this.historySeed());
        for (int i = 0; i < yama.size(); i++) {
            index -= yama.get(i);
            if (index < 0) {
                yama.set(i, yama.get(i) - 1);
                index = i;
                break;
            }
        }
        return AbstractMahjongCard.mahjong(index);
    }

    public ArrayList<AbstractMahjongCard> doraHint() {
        while (this.doraHint.size() < 1 + this.kangCount)
            this.doraHint.add(this.randomFromYama());
        return this.doraHint;
    }

    private ArrayList<AbstractMahjongCard> uraHint() {
        while (this.uraHint.size() < 1 + this.kangCount && this.reach)
            this.uraHint.add(this.randomFromYama());
        return this.uraHint;
    }

    private void updateDora() {
        this.doraHint();
        // TODO
    }

    private void kang(int color, int num) {

        // TODO
        this.kangCount++;
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
            this.save();
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
        this.addToBot(new MakeTempCardInHandAction(this.randomFromYama()));
        if (this.countLeft() < 4)
            this.reset();
        else {
            this.randomFromYama();
            this.randomFromYama();
            this.randomFromYama();
        }
    }

    public void onVictory() {
        this.save();
    }



}