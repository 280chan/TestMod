package testmod.relics;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.localization.UIStrings;
import testmod.relicsup.CyclicPeriaptUp;

public class CyclicPeriapt extends AbstractTestRelic {
	private static final UIStrings UI = MISC.uiString();
	
	private static Color color = null;
	private ArrayList<UUID> used = new ArrayList<UUID>();

	public static Color setColorIfNull(Supplier<Color> c) {
		if (color == null)
			color = c.get();
		return color;
	}
	
	private void initColor() {
		if (color == null)
			color = CyclicPeriaptUp.setColorIfNull(this::initGlowColor);
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (!c.purgeOnUse && !this.used.contains(c.uuid) && action.exhaustCard) {
			this.used.add(c.uuid);
			action.exhaustCard = false;
			c.name += UI.TEXT[0];
			this.show();
		}
	}
	
	public void onRefreshHand() {
		this.initColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		if (!this.inCombat())
			return;
		this.stopPulse();
		colorRegister(color).addRelic(this).addPredicate(c -> (c.exhaust || c.exhaustOnUseOnce)
				&& !this.used.contains(c.uuid) && c.hasEnoughEnergy() && c.cardPlayable(this.randomMonster()))
				.updateHand();
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