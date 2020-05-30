package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import mymod.TestMod;

public class Maize extends MyRelic {
	public static final String ID = "Maize";
	public static final String IMG = TestMod.relicIMGPath(ID);
	private static final int AMOUNT = 5;
	public static final String DESCRIPTION = "你在同一回合内打出 #b" + AMOUNT + " 张 #y技能牌 时，获得 #b1 层 #y无实体 。";
	
	public Maize() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.MAGICAL);
	}

	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void atTurnStart() {
		this.counter = 0;
	}

	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (card.type == AbstractCard.CardType.SKILL) {
			this.counter++;
			if (this.counter == AMOUNT) {
				this.show();
				AbstractPlayer p = AbstractDungeon.player;
				AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new IntangiblePlayerPower(p, 1), 1));
			}
		}
	}

	public void onVictory() {
		this.counter = -1;
	}

}