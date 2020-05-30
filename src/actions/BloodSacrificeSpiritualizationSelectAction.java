package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BloodSacrificeSpiritualizationSelectAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_FAST;
	private CardGroup g;
	private AbstractPlayer p;

	public BloodSacrificeSpiritualizationSelectAction(AbstractPlayer p) {
		this.actionType = ActionType.SPECIAL;
		this.duration = DURATION;
		this.p = p;
	}

	private void init() {
		this.g = new CardGroup(CardGroupType.UNSPECIFIED);
        this.g.group.addAll(this.p.drawPile.group);
        this.g.group.addAll(this.p.hand.group);
        this.g.group.addAll(this.p.discardPile.group);
        for (AbstractCard c : this.p.hand.group)
        	c.beginGlowing();
        this.amount = this.p.maxHealth / 10;
        if (this.amount <= 0)
        	this.amount = 1;
	}
	
	@Override
	public void update() {
		if (this.duration == DURATION) {
			this.init();
			if (this.g.group.isEmpty()) {
				this.isDone = true;
				return;
			}
			String info = "跳过或失去" + this.amount + "点生命来选择1张牌尝试永久升级并打出(亮边的为手牌)";
			AbstractDungeon.gridSelectScreen.open(g, 1, info, false, false, true, false);
			AbstractDungeon.overlayMenu.cancelButton.show("跳过");
		} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			this.p.damage(new DamageInfo(p, this.amount, DamageType.HP_LOSS));
			AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
			this.upgrade(c);
			AbstractDungeon.actionManager.addToTop(new BloodSacrificeSpiritualizationPlayAction(p, getSource(c), c));
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.isDone = true;
		}
		tickDuration();
	}
	
	private void upgrade(AbstractCard card) {
		if (card.canUpgrade()) {
			card.upgrade();
			for (AbstractCard c : this.p.masterDeck.group) {
				if (c.uuid.equals(card.uuid) && c.canUpgrade()) {
					c.upgrade();
					return;
				}
			}
		}
	}

	private static CardGroup getSource(AbstractCard c) {
		AbstractPlayer p = AbstractDungeon.player;
		CardGroup[] groups = {p.discardPile, p.hand, p.drawPile};
		for (CardGroup g : groups)
			if (g.contains(c)) {
				System.out.println("来自于" + g.type);
				return g;
			}
		System.out.println("为什么找不到" + c.name + "？？？");
		return null;
	}
	
}
