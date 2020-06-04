package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import powers.JusticePower;

public class Justice extends MyRelic {
	public static final String ID = "Justice";
	
	public Justice() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		this.addToTop(new ApplyPowerAction(p, p, new JusticePower(p, this)));
    }
	
	public void atTurnStart() {
		if (!isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		boolean had = false;
		for (AbstractPower po : p.powers)
			if (po instanceof JusticePower)
				had = true;
		if (!had)
			this.addToTop(new ApplyPowerAction(p, p, new JusticePower(p, this)));
    }
}