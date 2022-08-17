package testmod.relicsup;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import testmod.relics.Prudence;

public class PrudenceUp extends AbstractUpgradedRelic {
	public static final ArrayList<AbstractCard> CARDS = new ArrayList<AbstractCard>();
	
	public void onRefreshHand() {
		if (!this.isActive)
			return;
		if (Prudence.color == null)
			Prudence.color = this.initGlowColor();
		if (this.inCombat())
			this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		for (AbstractCard c : p().hand.group) {
			if ((EnergyPanel.totalCount >= c.costForTurn || !CARDS.contains(c)) && this.calCanPlay(c)) {
				this.addToGlowChangerList(c, Prudence.color);
				this.addHardLockGlow(c);
				active = true;
			} else
				this.removeFromGlowList(c, Prudence.color);
		}
		if (active)
			this.beginLongPulse();
		else
			this.stopPulse();
	}
	
	public void atPreBattle() {
		CARDS.clear();
	}
	
	public void onVictory() {
		this.stopPulse();
	}
	
	private boolean calCanPlay(AbstractCard c) {
		AbstractMonster m = AbstractDungeon.getMonsters().hoveredMonster;
		if (m == null) {
			m = AbstractDungeon.getMonsters().monsters.stream().filter(c::cardPlayable).findAny().orElse(null);
		}
		return c.cardPlayable(m) && !canPlay(c, m);
	}
	
	public void onUseCard(AbstractCard c, UseCardAction a) {
		if (c.type == CardType.CURSE || c.type == CardType.STATUS)
			a.exhaustCard = true;
		boolean flag = calCanPlay(c);
		if (!CARDS.contains(c) && c.costForTurn > EnergyPanel.totalCount && !c.freeToPlay() && !c.isInAutoplay
				&& !(p().hasPower("Corruption") && c.type == AbstractCard.CardType.SKILL)) {
			flag = true;
			CARDS.add(c);
		}
		if (flag)
			this.show();
	}
	
	private boolean canPlay(AbstractCard c, AbstractMonster m) {
		if (c.type == CardType.STATUS) {
			if (p().hasRelic("Medical Kit")) {
				return true;
			}
			if (!c.cardID.equals("Slimed")) {
				return false;
			}
		}
		if (c.type == CardType.CURSE) {
			if (p().hasRelic("Blue Candle")) {
				return true;
			}
			if (!c.cardID.equals("Pride")) {
				return false;
			}
		}
		if (AbstractDungeon.actionManager.turnHasEnded) {
			return false;
		} else if (p().hasPower("Entangled") && c.type == CardType.ATTACK) {
			return false;
		} else if (c.freeToPlay()) {
			return true;
		}
		if (p().relics.stream().anyMatch(r -> !r.canPlay(c)) || p().blights.stream().anyMatch(b -> !b.canPlay(c))
				|| p().hand.group.stream().anyMatch(card -> !card.canPlay(c)))
			return false;
		return c.costForTurn <= EnergyPanel.totalCount && c.canUse(p(), m);
	}
}