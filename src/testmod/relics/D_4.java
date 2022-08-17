package testmod.relics;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.apache.logging.log4j.util.TriConsumer;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.NightmarePower;
import com.megacrit.cardcrawl.random.Random;
import testmod.mymod.TestMod;
import testmod.powers.D_4Power;
import testmod.relicsup.D_4Up;

public class D_4 extends AbstractTestRelic {
	private static Situation nextSituation = null;
	public static final Situation[] SITUATIONS = Situation.values();
	public static final int LENGTH = SITUATIONS.length;
	private Random rng = null;
	private static final ArrayList<TriConsumer<AbstractCard, AbstractMonster, UseCardAction>> CONSUMER;

	static {
		CONSUMER = new ArrayList<TriConsumer<AbstractCard, AbstractMonster, UseCardAction>>();
		CONSUMER.add((c, m, a) -> exhaust(c, a));
		CONSUMER.add((c, m, a) -> regain(c));
		CONSUMER.add((c, m, a) -> dualPlay(c, m));
		CONSUMER.add((c, m, a) -> nightmare(c));
	}
	
	public static enum Situation {
		EXHAUST, REGAIN, DUALPLAY, NIGHTMARE;
		private Situation() {
		}
		public String toString() {
			return D_4Power.getString(this);
		}
	}
	
	public String getUpdatedDescription() {
		return this.isObtained && this.inCombat() ? DESCRIPTIONS[1] + nextSituation : DESCRIPTIONS[0];
	}
	
	private static void dualPlay(AbstractCard card, AbstractMonster m) {
		MISC.playAgain(card, m);
	}
	
	private static void regain(AbstractCard card) {
		int cost = card.costForTurn;
		if (cost == -1)
			cost = card.energyOnUse;
		else if (cost == -2)
			cost = 0;
		MISC.atb(new GainEnergyAction(cost));
	}
	
	private static void exhaust(AbstractCard card, UseCardAction action) {
		card.exhaustOnUseOnce = action.exhaustCard = true;
	}
	
	private static void nightmare(AbstractCard c) {
		MISC.atb(MISC.apply(MISC.p(), new NightmarePower(MISC.p(), 3, c)));
	}
	
	private static Consumer<AbstractCard> getConsumer(AbstractMonster m, UseCardAction a) {
		return c -> CONSUMER.get(nextSituation.ordinal()).accept(c, m, a);
	}
	
	public void onUseCard(final AbstractCard card, final UseCardAction action) {
		if (!card.isInAutoplay && isActive && this.relicStream(D_4Up.class).count() == 0) {
			AbstractMonster m = null;
			if (action.target != null && action.target instanceof AbstractMonster) {
				m = (AbstractMonster) action.target;
			}
			this.show();
			getConsumer(m, action).accept(card);
			this.setNextSituation();
		}
	}
	
	private int getRoll(int n) {
		return this.rng.random(n - 1);
	}
	
	private void setNextSituation() {
		nextSituation = SITUATIONS[getRoll(LENGTH)];
		this.tryUpdatePower();
		this.updateDescription(p().chosenClass);
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		this.rng = this.copyRNG(AbstractDungeon.miscRng);
		this.init();
    }
	
	public void onVictory() {
		if (!isActive)
			return;
		nextSituation = null;
		this.updateDescription(p().chosenClass);
	}
	
	private void init() {
		this.setNextSituation();
	}
	
	private void tryUpdatePower() {
		if (!D_4Power.hasThis(p())) {
			TestMod.info(this.name + ": Init or got fucked");
			p().powers.add(new D_4Power(p(), nextSituation));
		} else {
			boolean changed = false;
			for (int i = 0; i < p().powers.size(); i++) {
				AbstractPower power = p().powers.get(i);
				if (power instanceof D_4Power) {
					if (!changed) {
						changed = true;
						p().powers.set(i, new D_4Power(p(), nextSituation));
					} else {
						TestMod.info(this.name + ": WTF Dupe Power");
						p().powers.remove(i--);
					}
				}
			}
		}
	}
	
}