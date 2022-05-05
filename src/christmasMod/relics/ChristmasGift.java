package christmasMod.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import christmasMod.mymod.ChristmasMod;
import testmod.relics.AbstractTestRelic;

public class ChristmasGift extends AbstractTestRelic {
	public static final String ID = "ChristmasGift";
	public static final String DESCRIPTION = "每回合开始将一张随机的圣诞礼物加入手牌。每 消耗 一张牌恢复 #b1 点生命值。";
	
	public ChristmasGift() {
		super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
	}
	
	public void onExhaust(final AbstractCard card) {
		p().heal(1);
		this.flash();
    }
	
	public void atTurnStart() {
		this.addToBot(new MakeTempCardInHandAction(ChristmasMod.randomGift(false)));
    }
	
}