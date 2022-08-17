package testmod.relicsup;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ThousandKnivesUp extends AbstractUpgradedRelic implements ClickableRelic {
	private static Color color = null;
	private ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		if (this.inCombat())
			this.updateHandGlow();
	}
	
	private boolean checkGlow(AbstractCard c) {
		return checkCard(c) && c.hasEnoughEnergy() && c.cardPlayable(this.randomMonster());
	}
	
	private void updateHandGlow() {
		ColorRegister cr = new ColorRegister(color);
		if (p().hand.group.stream().anyMatch(this::checkGlow))
			this.beginLongPulse();
		else
			this.stopPulse();
		this.streamIfElse(p().hand.group.stream(), this::checkGlow, cr::addToGlowChangerList, cr::removeFromGlowList);
	}
	
	public void atPreBattle() {
		this.list.clear();
	}
	
	public void onVictory() {
		this.stopPulse();
		this.list.clear();
	}
	
	public void atTurnStart() {
		this.counter = -1;
	}
	
	private boolean checkCard(AbstractCard c) {
		return (c.costForTurn == 0 || c.freeToPlay()) && !c.isInAutoplay && c.type == CardType.ATTACK;
	}
	
	private int countCards() {
		return (int) this.combatCards().filter(this::checkCard).count();
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		if (checkCard(c)) {
			this.list.add(c.makeSameInstanceOf());
			this.atb(new GainBlockAction(p(), p(), countCards(), true));
			this.show();
		}
	}
	
	public void onCardDraw(final AbstractCard c) {
		if (checkCard(c)) {
			this.list.add(c.makeSameInstanceOf());
			p().gainGold(1);
			this.show();
		}
    }
	
	private void add(AbstractCard c) {
		this.att(new MakeTempCardInHandAction(c));
		this.list.remove(c);
		AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
		this.counter = -2;
	}

	@Override
	public void onRightClick() {
		if (!this.inCombat() || this.list.isEmpty() || this.counter == -2)
			return;
		this.addTmpActionToBot(() -> {
			CardGroup tmp = new CardGroup(CardGroupType.UNSPECIFIED);
			tmp.group = this.list;
			AbstractDungeon.gridSelectScreen.open(tmp, tmp.size(), true, this.DESCRIPTIONS[1]);
			addTmpActionToTop(() -> reverse(AbstractDungeon.gridSelectScreen.selectedCards).forEach(this::add));
    	});
	}

}