package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mymod.TestMod;
import utils.MiscMethods;

public class Muramasa extends MyRelic implements MiscMethods {
	public static final String ID = "Muramasa";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "每当你抽到或打出 #b0 耗能的攻击牌次数合计为 #b2 时，抽 #b1 张牌。";

	private static Color color = null;
	
	public Muramasa() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	private void tryDo(AbstractCard c) {
		if (c.type == CardType.ATTACK && (c.costForTurn == 0 || c.freeToPlayOnce)) {
			counter++;
			if (counter == 2) {
				counter = 0;
				this.show();
				AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1));
			}
		}
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction useCardAction) {
		if (!isActive)
			return;
		tryDo(c);
	}
	
	public void onCardDraw(final AbstractCard c) {
		if (!isActive)
			return;
		tryDo(c);
    }
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (c.type == CardType.ATTACK && (c.costForTurn == 0 || c.freeToPlayOnce) && c.hasEnoughEnergy() && c.cardPlayable(AbstractDungeon.getRandomMonster())) {
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
}