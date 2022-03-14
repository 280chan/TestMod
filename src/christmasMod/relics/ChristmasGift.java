package christmasMod.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import christmasMod.mymod.ChristmasMod;
import christmasMod.powers.GiftDamagedPower;
import testmod.relics.AbstractTestRelic;

public class ChristmasGift extends AbstractTestRelic {
	public static final String ID = "ChristmasGift";
	public static final String DESCRIPTION = "每回合开始将一张随机的圣诞礼物加入手牌。每 消耗 一张牌恢复 #b1 点生命值。";
	
	public ChristmasGift() {
		super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onCardDraw(AbstractCard c) {
		AbstractPlayer p = AbstractDungeon.player;
		if (GiftDamagedPower.hasThis(p) && c.type == CardType.STATUS) {
			p.hand.moveToExhaustPile(c);
			for (int i = 0; i < GiftDamagedPower.getThis(p).amount; i++)
				this.addToBot(new MakeTempCardInHandAction(ChristmasMod.randomGift(false)));
		}
	}
	
	public void onExhaust(final AbstractCard card) {
		AbstractDungeon.player.heal(1);
		this.flash();
    }
	
	public void atTurnStart() {
		this.addToBot(new MakeTempCardInHandAction(ChristmasMod.randomGift(false)));
    }
	
}