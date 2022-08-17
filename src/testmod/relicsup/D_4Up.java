package testmod.relicsup;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.apache.logging.log4j.util.TriConsumer;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.NightmarePower;
import testmod.relics.D_4;
import testmod.relics.D_4.Situation;

public class D_4Up extends AbstractUpgradedRelic implements ClickableRelic {
	private static Situation nextSituation = Situation.EXHAUST;
	private static final ArrayList<TriConsumer<AbstractCard, AbstractMonster, UseCardAction>> CONSUMER;
	
	static {
		CONSUMER = new ArrayList<TriConsumer<AbstractCard, AbstractMonster, UseCardAction>>();
		CONSUMER.add((c, m, a) -> exhaust(c, a));
		CONSUMER.add((c, m, a) -> regain(c));
		CONSUMER.add((c, m, a) -> dualPlay(c, m));
		CONSUMER.add((c, m, a) -> nightmare(c));
	}
	
	public void onEquip() {
		this.counter = 2;
		this.updateDescription();
	}
	
	public String getUpdatedDescription() {
		return this.isObtained ? DESCRIPTIONS[0] + DESCRIPTIONS[1] + nextSituation : DESCRIPTIONS[0];
	}
	
	private static void dualPlay(AbstractCard c, AbstractMonster m) {
		MISC.getIdenticalList(MISC, amount()).forEach(i -> i.playAgain(c, m));
	}
	
	private static void regain(AbstractCard c) {
		int cost = c.costForTurn;
		if (c.freeToPlay() || cost == -2)
			cost = 0;
		else if (cost == -1)
			cost = c.energyOnUse;
		if (cost > 0)
			MISC.atb(new GainEnergyAction((cost - 1) * amount()));
	}
	
	private static void exhaust(AbstractCard c, UseCardAction action) {
		c.exhaustOnUseOnce = action.exhaustCard = true;
	}
	
	private static void nightmare(AbstractCard c) {
		MISC.atb(MISC.apply(MISC.p(), new NightmarePower(MISC.p(), 3 * amount(), c)));
	}
	
	private static Consumer<AbstractCard> getConsumer(AbstractMonster m, UseCardAction a) {
		return c -> CONSUMER.get(nextSituation.ordinal()).accept(c, m, a);
	}
	
	private static int amount() {
		return (int) (MISC.relicStream(D_4Up.class).count() + MISC.relicStream(D_4.class).count());
	}
	
	public void onUseCard(final AbstractCard card, final UseCardAction action) {
		if (!card.isInAutoplay && isActive) {
			AbstractMonster m = null;
			if (action.target != null && action.target instanceof AbstractMonster)
				m = (AbstractMonster) action.target;
			this.show();
			if (amount() > 0)
				getConsumer(m, action).accept(card);
		}
	}
	
	private void change(int input) {
		this.counter = input;
	}

	@Override
	public void onRightClick() {
		int tmp = this.counter = (this.counter + 1) % 4;
		this.relicStream(D_4Up.class).forEach(r -> r.change(tmp));
		nextSituation = D_4.SITUATIONS[this.counter];
		this.updateDescription();
	}
	
}