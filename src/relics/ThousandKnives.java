package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mymod.TestMod;
import utils.MiscMethods;

public class ThousandKnives extends MyRelic implements MiscMethods {
	public static final String ID = "ThousandKnives";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "每当你抽到 #b0 耗能的攻击牌时，将一张其复制品放入手牌。每当你打出 #b0 耗能的攻击牌时，将一张其复制品放入弃牌堆。打出时，手牌中每有一张 #b0 耗能的攻击牌，获得 #b1 格挡。";//遗物效果的文本描叙。
	
	private static Color color = null;
	
	public ThousandKnives() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.SOLID);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (checkCard(c) && c.hasEnoughEnergy() && c.cardPlayable(AbstractDungeon.getRandomMonster())) {
				this.addToGlowChangerList(c, color);
				active = true;
			} else
				this.removeFromGlowList(c, color);
		}
		if (active)
			this.beginLongPulse();
		else
			this.stopPulse();
	}
	
	public void onVictory() {
		this.stopPulse();
	}
	
	private static boolean checkCard(AbstractCard c) {
		return (c.costForTurn == 0 || c.freeToPlayOnce) && !c.isInAutoplay && c.type == CardType.ATTACK;
	}
	
	private int countCards() {
		int count = 0;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (checkCard(c)) {
				count++;
			}
		}
		return count;
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		if (this.isActive && checkCard(c)) {
			AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, countCards(), true));
			AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(c.makeStatEquivalentCopy(), 1));
			this.show();
		}
	}
	
	public void onCardDraw(final AbstractCard c) {
		if (this.isActive && checkCard(c)) {
			AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(c.makeStatEquivalentCopy()));
			this.show();
		}
    }

}