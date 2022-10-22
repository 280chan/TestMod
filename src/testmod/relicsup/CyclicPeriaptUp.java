package testmod.relicsup;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import testmod.relics.CyclicPeriapt;

public class CyclicPeriaptUp extends AbstractUpgradedRelic implements ClickableRelic {
	private static Color color = null;
	private boolean active = true;

	public static Color setColorIfNull(Supplier<Color> c) {
		if (color == null)
			color = c.get();
		return color;
	}
	
	private void initColor() {
		if (color == null)
			color = CyclicPeriapt.setColorIfNull(this::initGlowColor);
	}
	
	public void onEquip() {
		if (this.inCombat()) {
			this.atPreBattle();
			this.counter = 0;
		}
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (!c.purgeOnUse && this.active && action.exhaustCard) {
			action.exhaustCard = false;
			p().gainGold((int) this.relicStream(CyclicPeriaptUp.class).peek(r -> r.counter++).count());
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
		colorRegister(color).addPredicate(c -> (c.exhaust || c.exhaustOnUseOnce) && c.hasEnoughEnergy()
				&& c.cardPlayable(this.randomMonster())).updateHand();
	}
	
	public void atPreBattle() {
		if (this.active)
			this.beginLongPulse();
	}
	
	public void onVictory() {
		this.stopPulse();
		int tmp = this.relicStream(CyclicPeriaptUp.class).mapToInt(r -> r.counter).sum();
		if (this.isActive && tmp > 9)
			p().heal(tmp / 10);
		this.counter %= 10;
	}

	@Override
	public void onRightClick() {
		if (this.active = !this.active) {
			this.beginLongPulse();
		} else {
			this.stopPulse();
		}
	}
	
}