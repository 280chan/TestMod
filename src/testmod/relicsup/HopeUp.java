package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import testmod.actions.HopeAction;
import testmod.mymod.TestMod;
import testmod.relics.Hope;
import testmod.utils.CounterKeeper;

public class HopeUp extends AbstractUpgradedRelic implements ClickableRelic, CounterKeeper {
	public static Random HPRng = new Random();
	public static Random cardRng = new Random();
	private static final int RATECARD = 1000;
	private boolean waitHandUpdate = false;
	private boolean canDraw;
	
	public HopeUp() {
		this.counter = 0;
	}
	
	public String getUpdatedDescription() {
		if (!isObtained || this.counter == 0)
			return DESCRIPTIONS[2];
		float rateHP = (Hope.RATE + this.counter * Hope.DELTA) / 1000f;
		return DESCRIPTIONS[0] + rateHP + DESCRIPTIONS[1];
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
		AbstractRoom r = AbstractDungeon.getCurrRoom();
		if (r.phase == RoomPhase.COMBAT && roll(HPRng)) {
        	show();
        	Hope.act(r);
        	return 0;
        }
		return damage;
    }
	
	private boolean roll(Random rng) {
		boolean result = false;
		boolean hp = rng.equals(HPRng);
		if (hp) {
			result = rng.random(Hope.RANGEHP) < Hope.RATE + this.counter * Hope.DELTA;
			if (!result)
				this.counter++;
			else
				this.counter /= 2;
		} else
			result = rng.random(Hope.RANGECARD) < RATECARD;
		this.updateDescription();
		return result;
	}
	
	public void atPreBattle() {
		HPRng = this.copyRNG(AbstractDungeon.miscRng);
		cardRng = this.copyRNG(AbstractDungeon.miscRng);
		this.canDraw = false;
	}

	public void atTurnStart() {
		this.canDraw = true;
	}
	
	public void onPlayerEndTurn() {
		this.canDraw = false;
		this.stopPulse();
	}
	
	public void onVictory() {
		this.stopPulse();
	}

	public void onRefreshHand() {
		this.waitHandUpdate = false;
		if (p().hand.isEmpty() && this.canDraw)
			this.beginLongPulse();
	}

	@Override
	public void onRightClick() {
		if (this.inCombat() && p().hand.isEmpty() && this.canDraw && !this.waitHandUpdate) {
			this.waitHandUpdate = true;
			this.stopPulse();
			if (roll(cardRng)) {
				TestMod.info("成功");
				show();
				this.addToTop(new HopeAction());
			} else {
				TestMod.info("失败");
			}
		}
	}
	
}