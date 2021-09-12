package relics;

import java.util.ArrayList;
import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import utils.MiscMethods;

public class CyclicPeriapt extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "CyclicPeriapt";
	
	private static Color color = null;
	private ArrayList<UUID> used = new ArrayList<UUID>();
	
	public CyclicPeriapt() {
		super(ID, RelicTier.SHOP, LandingSound.MAGICAL);
		this.setTestTier(BAD);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (!c.purgeOnUse && !this.used.contains(c.uuid) && action.exhaustCard) {
			this.used.add(c.uuid);
			action.exhaustCard = false;
			c.name += "(已用)";
			this.show();
		}
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		if (!this.canUpdateHandGlow())
			return;
		this.stopPulse();
		ColorRegister cr = new ColorRegister(color, this);
		this.streamIfElse(AbstractDungeon.player.hand.group.stream(),
				c -> (c.exhaust || c.exhaustOnUseOnce) && !this.used.contains(c.uuid) && c.hasEnoughEnergy()
						&& c.cardPlayable(AbstractDungeon.getRandomMonster()),
				cr::addToGlowChangerList, cr::removeFromGlowList);
	}
	
	public void atPreBattle() {
		if (!this.used.isEmpty())
			this.used.clear();
    }
	
	public void onVictory() {
		this.used.clear();
		this.stopPulse();
    }
	
}