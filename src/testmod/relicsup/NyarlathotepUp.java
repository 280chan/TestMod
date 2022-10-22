package testmod.relicsup;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import testmod.mymod.TestMod;
import testmod.patches.SwFPatch;
import testmod.relics.Nyarlathotep;

public class NyarlathotepUp extends AbstractUpgradedRelic {
	private static final String[] CARD_IDs = Nyarlathotep.CARD_IDs;
	private static final String[] POWER_IDs = Nyarlathotep.POWER_IDs;
	private static final String[] RELIC_IDs = Nyarlathotep.RELIC_IDs;
	private static final String[] RELIC_ON_PLAY_IDs = Nyarlathotep.RELIC_ON_PLAY_IDs;
	private static final ArrayList<String> CARD_LIST = Nyarlathotep.CARD_LIST;
	private static final ArrayList<String> POWER_LIST = Nyarlathotep.POWER_LIST;
	private static final ArrayList<String> RELIC_LIST = Nyarlathotep.RELIC_LIST;
	
	private boolean valid(AbstractCard c) {
		return Stream.of(CARD_IDs).anyMatch(c.cardID::equals) || CARD_LIST.stream().anyMatch(c.cardID::equals);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive && this.inCombat() && p().relics.stream().noneMatch(r -> r instanceof Nyarlathotep))
			SwFPatch.BingoExhaustPatch.exhaust = 0;
	}
	
	public void atPreBattle() {
		if (this.isActive && p().relics.stream().noneMatch(r -> r instanceof Nyarlathotep))
			SwFPatch.BingoExhaustPatch.exhaust = 0;
	}
	
	private static boolean isThis(AbstractRelic r) {
		return r instanceof NyarlathotepUp || r instanceof Nyarlathotep;
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		for (int i = 0; i < 2; i++) {
			this.triggerExhaustFor(c);
			this.triggerDiscardFor(c);
			this.triggerUsePowerCard(c, action);
		}
		this.show();
	}
	
	private void triggerExhaustFor(AbstractCard c) {
		p().relics.stream().filter(not(NyarlathotepUp::isThis)).forEach(r -> r.onExhaust(c));
		p().powers.forEach(p -> p.onExhaust(c));
		c.triggerOnExhaust();
		SwFPatch.BingoExhaustPatch.exhaust(true);
	}
	
	private void triggerDiscardFor(AbstractCard c) {
		c.triggerOnManualDiscard();
		++GameActionManager.totalDiscardedThisTurn;
		if (AbstractDungeon.actionManager.turnHasEnded)
			return;
		p().updateCardsOnDiscard();
		p().relics.stream().filter(not(NyarlathotepUp::isThis)).forEach(r -> r.onManualDiscard());
	}
	
	private String checkID(Object i) {
		if (i instanceof AbstractPower)
			return ((AbstractPower)i).ID;
		if (i instanceof AbstractRelic)
			return ((AbstractRelic)i).relicId;
		return ((AbstractCard)i).cardID;
	}
	
	private <T> Stream<T> get(ArrayList<T> list, String id) {
		return list.stream().filter(i -> id.equals(checkID(i)));
	}
	
	private void triggerUsePowerCard(AbstractCard c, UseCardAction a) {
		AbstractCard tmp = c.makeSameInstanceOf();
		tmp.type = CardType.POWER;
		Stream.concat(Stream.of(POWER_IDs), POWER_LIST.stream()).distinct().filter(p()::hasPower)
				.flatMap(id -> get(p().powers, id)).forEach(p -> p.onUseCard(tmp, a));
		Stream.concat(Stream.of(RELIC_IDs), RELIC_LIST.stream()).distinct().filter(p()::hasRelic)
				.flatMap(id -> get(p().relics, id)).forEach(r -> r.onUseCard(tmp, a));
		Stream.of(RELIC_ON_PLAY_IDs).filter(p()::hasRelic).flatMap(id -> get(p().relics, id)).forEach(
				r -> r.onPlayCard(tmp, (a.target instanceof AbstractMonster) ? (AbstractMonster) a.target : null));
		this.combatCards().filter(this::valid).forEach(b -> b.triggerOnCardPlayed(c));
		p().hand.group.forEach(b -> b.triggerOnOtherCardPlayed(c));
	}
	
	public void onExhaust(final AbstractCard c) {
		for (int i = 0; i < 2; i++) {
			this.triggerDiscardFor(c);
		}
		this.show();
	}
	
	public void onManualDiscard() {
		if (!p().discardPile.isEmpty()) {
			AbstractCard c = p().discardPile.getTopCard();
			for (int i = 0; i < 2; i++) {
				this.triggerExhaustFor(c);
			}
			this.show();
		}
	}
	
}